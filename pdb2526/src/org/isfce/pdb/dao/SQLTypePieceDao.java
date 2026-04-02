package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isfce.pdb.model.TypePiece;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SQLTypePieceDao implements ITypePieceDao {
	private static String SQL_GET_FROM_ID = """
			select t.CODE_TYP,t.NOM_TYP,t.HUMIDE_TYP from TTYPE_PIECE t
			where t.CODE_TYP= ?
			""";
	private static String SQL_GET_LISTE = """
			select t.CODE_TYP,t.NOM_TYP,t.HUMIDE_TYP from TTYPE_PIECE t
			order by t.CODE_TYP
			""";
	private static String SQL_INSERT = """
			INSERT INTO TTYPE_PIECE (CODE_TYP, NOM_TYP, HUMIDE_TYP)
			VALUES (?,?,?)
			""";
	private DAOFactory factory;
	private Connection connexion;

	public SQLTypePieceDao(DAOFactory factory) {
		this.factory = factory;
		this.connexion = factory.getConnection();
	}

	@Override
	public Optional<TypePiece> getFromID(String id) {
		TypePiece obj = null;
		try (PreparedStatement ps = connexion.prepareStatement(SQL_GET_FROM_ID)) {
			ps.setString(1, id.trim().toUpperCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				obj = new TypePiece(rs.getString("CODE_TYP").trim(), rs.getString("NOM_TYP"),
						rs.getBoolean("HUMIDE_TYP"));
				log.debug("Un type de pièce est créé: " + obj);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		;
		return Optional.ofNullable(obj);
	}

	@Override
	public List<TypePiece> getListe(String regExpr) {
		List<TypePiece> liste = new ArrayList<>(20);
		try (Statement st = connexion.createStatement()) {
			ResultSet rs = st.executeQuery(SQL_GET_LISTE);
			while (rs.next()) {
				liste.add(new TypePiece(rs.getString("CODE_TYP").trim(), rs.getString("NOM_TYP"),
						rs.getBoolean("HUMIDE_TYP")));
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		;
		return liste;
	}

	@Override
	public TypePiece insert(TypePiece obj) throws Exception {
		try (PreparedStatement ps = connexion.prepareStatement(SQL_INSERT)) {
			ps.setString(1, obj.getCode().trim().toUpperCase());
			ps.setString(2, obj.getNom().trim());
			ps.setBoolean(3, obj.isHumide());
			ps.executeUpdate();
			if (!this.connexion.getAutoCommit())
				this.connexion.commit();
			log.debug("Ajout d'un TypePièce: " + obj);
		} catch (SQLException e) {
			log.error("Insertion non validée: " + e);
			if (!this.connexion.getAutoCommit())
				this.connexion.rollback();
			this.factory.dispatchException(e, "TTYPE_PIECE");
		}
		return obj;
	}

}

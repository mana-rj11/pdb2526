package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SQLPieceDao implements IPieceDao {
	private static String SQL_GET_FROM_ID = """
			SELECT NOM_PIE, DESCRIPTION_PIE, ETAGE_PIE, FKTYPE_PIE,FKINSTALLATION_PIE
			FROM TPIECE WHERE NUM_PIE = ?
			""";
	private static String SQL_GET_LISTE = """

			""";
	private static String SQL_INSERT = """
			INSERT INTO TPIECE (NOM_PIE, DESCRIPTION_PIE, ETAGE_PIE, FKTYPE_PIE, FKINSTALLATION_PIE)
			VALUES (?,?,?,?,?)
			""";
	private DAOFactory factory;
	private Connection connexion;

	public SQLPieceDao(DAOFactory factory) {
		this.factory = factory;
		this.connexion = factory.getConnection();
	}

	@Override
	public Optional<Piece> getFromID(Integer id) {
		Piece obj = null;
		try (PreparedStatement ps = connexion.prepareStatement(SQL_GET_FROM_ID)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String typeP = rs.getString("FKTYPE_PIE");
				TypePiece tp = factory.getTypePieceDAO().getFromID(typeP).get();// A corriger
//@formatter:off 
				obj = Piece.builder()
						.id(id)
						.nom(rs.getString("NOM_PIE"))
						.description(rs.getString("DESCRIPTION_PIE"))
						.etage(rs.getBigDecimal("ETAGE_PIE").setScale(1))
						.typePiece(tp)
						.installation(rs.getInt("FKINSTALLATION_PIE"))
						.build();
//@formatter:on
				log.debug("Une pièce est chargée: " + obj);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return Optional.ofNullable(obj);
	}

	@Override
	public Piece insert(Piece obj) throws Exception {
		assert obj != null && obj.getId() == null : "L'objet doit exister sans ID ";
		try (PreparedStatement ps = connexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, obj.getNom().trim());
			ps.setString(2, obj.getDescription().trim());
			ps.setBigDecimal(3, obj.getEtage());
			ps.setString(4, obj.getTypePiece().getCode());
			ps.setInt(5, obj.getInstallation());
			int nb = ps.executeUpdate();
			if (nb == 1) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
					if (!this.connexion.getAutoCommit())
						this.connexion.commit();
				} else
					log.error("L'insert n'a pas retounée l'ID auto généré");
			} else
				log.error("L'insert n'a pas fonctionné");
		} catch (SQLException e) {
			log.error("Insertion non validée: " + e);
			if (!this.connexion.getAutoCommit())
				this.connexion.rollback();
			this.factory.dispatchException(e, "PIECE");
		}
		return obj;
	}

	/*
	 * @Override public TypePiece insert(TypePiece obj) throws Exception { try
	 * (PreparedStatement ps = connexion.prepareStatement(SQL_INSERT)) {
	 * ps.setString(1, obj.getCode().trim().toUpperCase()); ps.setString(2,
	 * obj.getNom().trim()); ps.setBoolean(3, obj.isHumide()); ps.executeUpdate();
	 * if (!this.connexion.getAutoCommit()) this.connexion.commit();
	 * log.debug("Ajout d'un TypePièce: " + obj); } catch (SQLException e) {
	 * log.error("Insertion non validée: " + e); if
	 * (!this.connexion.getAutoCommit()) this.connexion.rollback();
	 * this.factory.dispatchException(e, "TTYPE_PIECE"); } return obj; }
	 */
}

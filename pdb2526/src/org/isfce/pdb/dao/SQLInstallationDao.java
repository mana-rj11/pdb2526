package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Adresse;
import org.isfce.pdb.model.Installation;

public class SQLInstallationDao implements IInstallationDao {
	
	private static final Logger logger = Logger.getLogger(SQLInstallationDao.class.getName());
	private final DAOFactory factory;
	
	public SQLInstallationDao(DAOFactory factory) {
		this.factory = factory;
	}
	
	private Installation mapResultSet(ResultSet rs) throws SQLException {
		Adresse adresse = new Adresse(
			rs.getString("ADRESSE_INS"),
			rs.getInt("CP_INS"),
			rs.getString("VILLE_INS")
		);
		return Installation.builder()
			.id(rs.getInt("NUM_INS"))
			.date(rs.getDate("DATE_INS").toLocalDate())
			.Installateur(rs.getString("INSTALLATEUR_INS"))
			.proprietaire(rs.getString("PROPRIETAIRE_INS"))
			.adresse(adresse)
			.build();
	}
	
	@Override
	public Optional<Installation> getFromId(int id) throws InstallationException {
		String sql = "SELECT * FROM TINSTALLATION WHERE NUM_INS = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					logger.info("Installation trouvée : " + id);
					return Optional.of(mapResultSet(rs));
				}
				return Optional.empty();
			}
		} catch (Exception e) {
			factory.dispatchException(e, "getFromId Installation " + id);
			return Optional.empty();
		}
	}
	
	@Override 
	public List<Installation> getListe() throws InstallationException {
		String sql = "SELECT * FROM TINSTALLATION ORDER BY DATE_INS DESC";
		Connection connect = factory.getConnection();
		List<Installation> liste = new ArrayList<>();
		try (PreparedStatement ps = connect.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					liste.add(mapResultSet(rs));
				}
				logger.info("Liste installations chargée : " + liste.size());
		} catch (Exception e) {
			factory.dispatchException(e, "getListe Installation");
		}
		return liste;
	}
	
	@Override 
	public boolean update(Installation obj) throws InstallationException {
		String sql = "UPDATE TINSTALLATION SET DATE_INS=?, INSTALLATEUR_INS=?, PROPRIETAIRE_INS=? WHERE NUM_INS=?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setDate(1, java.sql.Date.valueOf(obj.getDate()));
			ps.setString(2, obj.getInstallateur());
			ps.setString(3, obj.getProprietaire());
			ps.setInt(4, obj.getId());
			int rows = ps.executeUpdate();
			logger.info("Installation mise à jour : " + obj.getId());
			return rows > 0;
		} catch (Exception e) {
			factory.dispatchException(e, "update Installation " + obj.getId());
			return false;
		}
	}
}

package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Localisation;

/**
 * Implémentation SQL du DAO Localisation pour Firebird
 * Clé composite : FKELEMENT_LOC + FKPIECE_LOC
 */
public class SQLLocalisationDao implements ILocalisationDao {
	
	private static final Logger logger = Logger.getLogger(SQLLocalisationDao.class.getName());
	private final DAOFactory factory;
	
	public SQLLocalisationDao(DAOFactory factory) {
		this.factory = factory;
	}
	
	private Localisation mapResultSet(ResultSet rs) throws Exception {
		return new Localisation(
			rs.getDouble("X_LOC"),
			rs.getDouble("Y_LOC"),
			rs.getDouble("A_LOC"), 
			rs.getBoolean("PLACE_LOC")
		);
	}
	
	@Override
	public Optional<Localisation> getFromId(int idElement) throws InstallationException {
		String sql = "SELECT * FROM TLOCALISATION WHERE FKELEMENT_LOC = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, idElement);
			// ps.setInt(2, id.getIdPiece());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					logger.info("Localisation trouvée : " + idElement);
					return Optional.of(mapResultSet(rs));
				}
				return Optional.empty();
			}
		} catch (Exception e) {
			factory.dispatchException(e, "getFromId Localisation " + idElement);
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<Integer> getPieceIdFromElement(int idElement) throws InstallationException {
		String sql = "SELECT FKPIECE_LOC FROM TLOCALISATION WHERE FKELEMENT_LOC = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, idElement);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(rs.getInt("FKPIECE_LOC"));
				}
				return Optional.empty();
			}
		} catch (Exception e) {
			factory.dispatchException(e, "getPieceIdFromElement Localisation " + idElement);
			return Optional.empty();
		}
	}
	
	
	@Override
	public Localisation insert(int idElement, int idPiece, Localisation obj) throws InstallationException {
		String sql = "INSERT INTO TLOCALISATION " + "(FKELEMENT_LOC, FKPIECE_LOC, X_LOC, Y_LOC, A_LOC, PLACE_LOC) "
				   + "values (?, ?, ?, ?, ?, ?)";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, idElement);
			ps.setInt(2, idPiece);
			ps.setDouble(3, obj.getX());
			ps.setDouble(4, obj.getY());
			ps.setDouble(5, obj.getAngle());
			ps.setBoolean(6, obj.isPlace());
			ps.executeUpdate();
			logger.info("Localisation insérée : element=" + idElement + " piece=" + idPiece);
			return obj;
		} catch (Exception e) {
			factory.dispatchException(e, "[INS] Localisation " + idElement);
			return obj;
		}
	}
	
	@Override
	public boolean update(int idElement, Localisation obj) throws InstallationException {
		String sql = "UPDATE TLOCALISATION " + "SET X_LOC = ?, Y_LOC = ?, A_LOC = ?, PLACE_LOC = ? "
				   + "WHERE FKELEMENT_LOC = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setDouble(1, obj.getX());
			ps.setDouble(2, obj.getY());
			ps.setDouble(3, obj.getAngle());
			ps.setBoolean(4, obj.isPlace());
			ps.setInt(5, idElement);
			// ps.setInt(6, id.getIdPiece());
			int rows = ps.executeUpdate();
			logger.info("Localisation mise à jour : " + idElement);
			return rows > 0;
		} catch (Exception e) {
			factory.dispatchException(e, "update Localisation" + idElement);
			return false;
		}
	}
	
	@Override
	public boolean delete(int idElement) throws InstallationException {
		String sql = "DELETE FROM TLOCALISATION WHERE FKELEMENT_LOC = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, idElement);
			// ps.setInt(2, id.getIdPiece());
			int rows = ps.executeUpdate();
			logger.info("Localisation supprimée : " + idElement);
			return rows > 0;
		} catch (Exception e) {
			factory.dispatchException(e, "delete Localisation"+ idElement);
			return false;
		}
	}

}

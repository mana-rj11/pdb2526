package org.isfce.pdb.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Plan;

/**
 * Implémentation SQL du DAO Plan pour Firebird
 */
public class SQLPlanDao implements IPlanDao {
	
	private static final Logger logger = Logger.getLogger(SQLPlanDao.class.getName());
	private final DAOFactory factory;
	
	public SQLPlanDao(DAOFactory factory) {
		this.factory = factory;
	}
	
	private Plan mapResultSet(ResultSet rs) throws Exception {
			String fichier = rs.getString("NOM_PLA").trim();
			String nom = fichier.replace(".png", "");
			// gere le cas ou ETAGE_PLA est null en base
		    BigDecimal etage = rs.getBigDecimal("ETAGE_PLA");  // etage ajouté
		    if (etage == null) etage = BigDecimal.ZERO;
		    etage = etage.setScale(1);
		    return new Plan(rs.getInt("ID_PLA"), nom, fichier, etage);	// etage ajouté

	}
	
	@Override
	public Optional<Plan> getFromId(int id) throws InstallationException{
		String sql = "SELECT * FROM TPLAN WHERE ID_PLA = ?";
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					logger.info("Plan trouvé : " + id);
					return Optional.of(mapResultSet(rs));
				}
				return Optional.empty();
			}
		} catch (Exception e) {
			factory.dispatchException(e, "getFromId Plan " + id);
			return Optional.empty();
		}
	}
	
	@Override
	public List<Plan> getListePlanFromInstallation(int installation) throws InstallationException {
		String sql = "SELECT * FROM TPLAN WHERE FKINSTALLATION_PLA = ?";
		Connection connect = factory.getConnection();
		List<Plan> liste = new ArrayList<>();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, installation);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					liste.add(mapResultSet(rs));
				}
			}
			logger.info("Plans chargés pour installation " + installation + " : " + liste.size());
		} catch (Exception e) {
			factory.dispatchException(e, "getListePlanFromInstallation " + installation);
		}
		return liste;
	}
	

	@Override
	public Plan insert(Plan obj, int installation) throws InstallationException {
		String sql = "INSERT INTO TPLAN (NOM_PLA, FKINSTALLATION_PLA, ETAGE_PLA) VALUES (?, ?, ?)"; // 3e paramètre
		Connection connect = factory.getConnection();
		try (PreparedStatement ps = connect.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, obj.getFichier());
			ps.setInt(2, installation); // sera fourni par la façade
			ps.setBigDecimal(3, obj.getEtage());	// passe l'etage
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					logger.info("Plan inséré avec ID :" + id);
					return new Plan(id, obj.getNom(), obj.getFichier(), obj.getEtage()); // etage ajouté
				}
			}
		} catch (Exception e) {
			factory.dispatchException(e, "[INS] Plan" + obj.getFichier());
		}
		return obj;
	}
}


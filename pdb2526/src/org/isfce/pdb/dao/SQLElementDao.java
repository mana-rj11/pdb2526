package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Appareil;
import org.isfce.pdb.model.Element;


/**
 * Implémentation SQL du DAO Element pour Firebird
 * @param factory
 */
public class SQLElementDao implements IElementDao {
	
	private static final Logger logger = Logger.getLogger(SQLElementDao.class.getName());
	private final DAOFactory factory;
	
	public SQLElementDao(DAOFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public List<Element> getListeFromInstallation(int installation) throws InstallationException {
		String sql = "SELECT * FROM TELEMENT WHERE FKINSTALLATION_ELE = ? ORDER BY CODE_ELE, ORDRE_ELE";
		Connection connect = factory.getConnection();
		List<Element> liste = new ArrayList<>();
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, installation);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String codeAppareil = rs.getString("FKAPPAREIL_ELE").trim();
					Appareil appareil = factory.getAppareilDAO()
							 .getFromId(codeAppareil)
							 .orElseThrow(() -> new InstallationException("Appareil introuvable : " + codeAppareil));
					Element element = new Element(
							rs.getInt("ID_ELE"),
							appareil,
							rs.getInt("QT_ELE"),
							rs.getString("CODE_ELE").trim(),
							rs.getString("INFO_ELE"),
							rs.getInt("ORDRE_ELE")
							
					);
					liste.add(element);
				}
			}
			logger.info("Elements chargés pour installation " + installation + " : " + liste.size());
		} catch (InstallationException e) {
			throw e;
		} catch (Exception e) {
			factory.dispatchException(e, "getListeFromInstallation " + installation);
		}
		return liste;
	}
}

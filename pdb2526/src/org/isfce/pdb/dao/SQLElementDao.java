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
import org.isfce.pdb.model.Localisation;


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
	
	/**
	 * Représentation brute d'une ligne TELEMENT avant résolution de l'Appareil
	 * Nécessaire pour fermer le ResultSet avant d'appeler une autre DAO sur 
	 * la meme connexion (sinon Firebird ferme implicitement le curseur) 
	 */
	private record RawElement(int id, String codeAppareil, int qt, String code, String info, int ordre) {
	}
	
	
	@Override
	public List<Element> getListeFromInstallation(int installation) throws InstallationException {
		String sql = "SELECT * FROM TELEMENT WHERE FKINSTALLATION_ELE = ? ORDER BY CODE_ELE, ORDRE_ELE";
		Connection connect = factory.getConnection();
		List<RawElement> brut = new ArrayList<>();
		
		// on lit toutes les lignes brutes, puis on ferme le ResultSet 
		try (PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setInt(1, installation);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					brut.add(new RawElement(
							rs.getInt("ID_ELE"),
							rs.getString("FKAPPAREIL_ELE").trim(),
							rs.getInt("QT_ELE"),
							rs.getString("CODE_ELE").trim(),
							rs.getString("INFO_ELE"),
							rs.getInt("ORDRE_ELE")));
				}
			}
		} catch (Exception e) {
			factory.dispatchException(e, "getListeFromInstallation " + installation);
			return new ArrayList<>();
		}
		
		// 2 resolution de l'Appareil, curseur ...
		List<Element> liste = new ArrayList<>();
		for (RawElement r : brut) {
			Appareil appareil = factory.getAppareilDAO()
					.getFromId(r.codeAppareil())
					.orElseThrow(() -> new InstallationException("Appareil introuvable : " + r.codeAppareil()));
			Localisation localisation = factory.getLocalisationDAO()
					.getFromId(r.id())
					.orElse(new Localisation(0, 0, 0, false));
			liste.add(new Element(r.id(), appareil, r.qt(), r.code(), r.info(), r.ordre(), localisation));
		}
		logger.info("Elements chargés pour installation " + installation + " : " + liste.size());
		return liste;
	}
}
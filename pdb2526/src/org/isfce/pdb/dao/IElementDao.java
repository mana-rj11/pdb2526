package org.isfce.pdb.dao;

import java.util.List;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Element;

/**
 * Interface DAO pour Element
 * Une seule méthode : charger tous les éléments d'une installation
 */
public interface IElementDao {
	
	List<Element> getListeFromInstallation(int installation) throws InstallationException;

}

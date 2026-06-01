package org.isfce.pdb.dao;

import java.util.Optional;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.LocID;
import org.isfce.pdb.model.Localisation;


/**
	 * Interface DAO pour Localisation
	 * Clé composite : LocID (idElement + idPiece)
	 * getFromId, insert, update, delete
	 */
public interface ILocalisationDao {
	
	
	Optional<Localisation> getFromId(LocID id) throws InstallationException;
	
	Localisation insert(LocID id, Localisation obj) throws InstallationException;
	
	boolean update(LocID id, Localisation obj) throws InstallationException;
	
	boolean delete(LocID id) throws InstallationException;
}


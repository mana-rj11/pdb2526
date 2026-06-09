package org.isfce.pdb.dao;

import java.util.Optional;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Localisation;


/**
	 * Interface DAO pour Localisation
	 * Clé composite : LocID (idElement + idPiece)
	 * getFromId, insert, update, delete
	 */
public interface ILocalisationDao {
	
	
	Optional<Localisation> getFromId(int idElement) throws InstallationException;
	
	Localisation insert(int idElement, int idPiece, Localisation obj) throws InstallationException;
	
	boolean update(int idElement, Localisation obj) throws InstallationException;
	
	boolean delete(int idElement) throws InstallationException;
}


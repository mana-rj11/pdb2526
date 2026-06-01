package org.isfce.pdb.dao;

import java.util.List;
import java.util.Optional;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Installation;

/**
 * Interface DAO pour Installation
 * getFromId, getListe (tri date desc), update
 */
public interface IInstallationDao {
	
	Optional<Installation> getFromId(int id) throws InstallationException;
	
	List<Installation> getListe() throws InstallationException;
	
	boolean update(Installation obj) throws InstallationException;

}

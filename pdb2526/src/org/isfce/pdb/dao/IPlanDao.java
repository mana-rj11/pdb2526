package org.isfce.pdb.dao;

import java.util.List;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Plan;

/**
 * Interface DAO pour Plan
 * getListePlanFromInstallation : tous les plans d'une installation
 * insert : ajouter un nouveau plan
 */
public interface IPlanDao {
	
	List<Plan> getListePlanFromInstallation(int installation) throws InstallationException;
	
	Plan insert(Plan obj) throws InstallationException;
}

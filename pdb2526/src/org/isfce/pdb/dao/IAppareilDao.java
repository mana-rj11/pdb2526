package org.isfce.pdb.dao;

import java.util.Optional;

import org.isfce.pdb.model.Appareil;

public interface IAppareilDao {
	Optional<Appareil> getFromId(String id);

}

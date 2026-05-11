package org.isfce.pdb.dao;

import java.util.Optional;

import org.isfce.pdb.model.Svg;

public interface ISvgDao {
	Optional<Svg> getFromId(String id);

}

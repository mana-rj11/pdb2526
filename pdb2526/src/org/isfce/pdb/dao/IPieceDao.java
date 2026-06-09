package org.isfce.pdb.dao;

import java.util.List;

import org.isfce.pdb.model.Piece;

public interface IPieceDao extends IDAO<Piece, Integer> {
	
	List<Piece> getListeFromInstallation(Integer installation);

}

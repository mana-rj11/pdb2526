package org.isfce.pdb.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import org.isfce.pdb.model.TypePiece;

public class SQLTypePieceDao implements ITypePieceDao {
	//private DAOFactory factory; //pas besoin pour les TypePiece
	private Connection connexion;

	public SQLTypePieceDao(DAOFactory factory) {
		//this.factory = factory;
		this.connexion = factory.getConnection();
	}

	@Override
	public Optional<TypePiece> getFromID(String id) {
		// TODO Auto-generated method stub
		return ITypePieceDao.super.getFromID(id);
	}

	@Override
	public List<TypePiece> getListe(String regExpr) {
		// TODO Auto-generated method stub
		return ITypePieceDao.super.getListe(regExpr);
	}

}

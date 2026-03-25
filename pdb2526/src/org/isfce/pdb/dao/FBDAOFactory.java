package org.isfce.pdb.dao;

import java.sql.Connection;

import org.isfce.pdb.exceptions.InstallationException;

public class FBDAOFactory extends DAOFactory {
	private Connection connexion;
	private ITypePieceDao daoTypePiece = null;

	public FBDAOFactory(Connection connexion) {
		this.connexion = connexion;
	}

	@Override
	public ITypePieceDao getTypePieceDAO() {
		if (daoTypePiece == null)
			daoTypePiece = new SQLTypePieceDao(this);
		return daoTypePiece;
	}

	@Override
	public Connection getConnection() {
		return connexion;
	}

	@Override
	protected void dispatchException(Exception e, String detail) throws InstallationException {
		throw new InstallationException(" Problème ");
	}

}

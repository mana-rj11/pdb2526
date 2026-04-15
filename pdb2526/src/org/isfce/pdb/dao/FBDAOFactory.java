package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.isfce.pdb.exceptions.CheckException;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.exceptions.PKException;

public class FBDAOFactory extends DAOFactory {
	private Connection connexion;
	private ITypePieceDao daoTypePiece = null;
	private IPieceDao daoPiece = null;

	public FBDAOFactory(Connection connexion) {
		this.connexion = connexion;
	}

	@Override
	public ITypePieceDao getTypePieceDAO() {
		if (daoTypePiece == null)
			daoTypePiece =  new CacheTypePieceDao(new SQLTypePieceDao(this));
		return daoTypePiece;
	}

	@Override
	public IPieceDao getPieceDAO() {
		if (daoPiece == null)
			daoPiece = new SQLPieceDao(this);
		return daoPiece;
	}

	@Override
	public Connection getConnection() {
		return connexion;
	}

	@Override
	protected void dispatchException(Exception e, String detail) throws InstallationException {
		SQLException exc = (SQLException) e;

		throw switch (exc.getErrorCode()) {
		case 335544665 -> new PKException(e.getMessage(), detail);
		case 335544347 -> new CheckException(e.getMessage(), detail);

		default -> new InstallationException(" Problème "+exc);

		};

	}

	private static String findNom(String erreur) {
		int i = erreur.indexOf(".\"") + 2;
		int j = erreur.indexOf("\"", i);
		return erreur.substring(i, j - 4);
	}

}

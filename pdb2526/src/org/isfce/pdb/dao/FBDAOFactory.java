package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.isfce.pdb.exceptions.CheckException;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.exceptions.PKException;

public class FBDAOFactory extends DAOFactory {
	private Connection connexion;
	private ITypePieceDao daoTypePiece = null;
	private ISvgDao daoSvg = null;
	private IPieceDao daoPiece = null;
	private IAppareilDao daoAppareil = null;
	private IInstallationDao daoInstallation = null;
	private IElementDao daoElement = null;
	private IPlanDao daoPlan = null;
	private ILocalisationDao daoLocalisation = null;

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
	
	public ISvgDao getSvgDao() {
		if (daoSvg == null)
			daoSvg = new CacheSvgDao(new SQLSvgDao(this));
		return daoSvg;
	}
	
	@Override
	public IAppareilDao getAppareilDAO() {
		if (daoAppareil == null) 
			daoAppareil = new CacheAppareilDao(new SQLAppareilDao(this));
		return daoAppareil;
	}
	
	@Override
	public IInstallationDao getInstallationDAO() {
		if (daoInstallation == null)
			daoInstallation = new SQLInstallationDao(this);
		return daoInstallation;
	}
	
	@Override
	public IElementDao getElementDAO() {
		if (daoElement == null)
			daoElement = new SQLElementDao(this);
		return daoElement;
	}
	
	@Override
	public IPlanDao getPlanDAO() {
		if (daoPlan == null)
			daoPlan = new SQLPlanDao(this);
		return daoPlan;
	}
	
	@Override
	public ILocalisationDao getLocalisationDAO() {
		if (daoLocalisation == null)
			daoLocalisation = new SQLLocalisationDao(this);
		return daoLocalisation;
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

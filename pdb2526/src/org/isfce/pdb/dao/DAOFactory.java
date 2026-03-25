package org.isfce.pdb.dao;

import java.sql.Connection;

import org.isfce.pdb.exceptions.InstallationException;

/**
 * Fabrique abstraite
 * 
 * @author Didier
 *
 */
public abstract class DAOFactory {
//Type de persistances
	public enum TypePersistance {
		FIREBIRD, H2, POSTGRESQL
	}

//DAO que doit fournir chaque fabrique concrète
	public abstract ITypePieceDao getTypePieceDAO();

	// Méthode statique que génère des fabriques concrètes
	public static DAOFactory getDAOFactory(TypePersistance typeP, Connection connect) {
		switch (typeP) {
		case FIREBIRD:
			return new FBDAOFactory(connect);
		case H2:
			return null;//new H2DAOFactory(connect);
		case POSTGRESQL:
			return null;//new PostgreSqlDAOFactory(connect);

		default:
			return null;
		}
	}

  // Retourne la connection SQL
	public abstract Connection getConnection();

  // Permet de transformer une exception SQL vers une exception Propre à mon application
	protected abstract void dispatchException(Exception e,String detail) throws InstallationException;
	

}

package org.isfce.pdb.services;

import java.util.List;
import java.util.Optional;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.view.bundle.I18N;

public class Facade {
	
	// private static final Logger logger = Logger.getLogger(Facade.class.getName());

	private DAOFactory factory;
	private Installation installation;
	// pièces de l'installation
	// private List<Piece> pieces = new ArrayList<Piece>();
	// élements de l'installation
	// private List<Element> elements;

	public Facade(DAOFactory factory) {
		this.factory=factory;
	}
	
	// -----------------------------------------------------------
	// Installation
	// -----------------------------------------------------------
	
	/**
	 * charge une installation et mémorise ses pièces et éléments
	 */
	
	public void chargeInstallation(int id) throws InstallationException {
		installation = factory.getInstallationDAO().getFromId(id)
				.orElseThrow(() -> new InstallationException(I18N.getString("err.install.inconnue")));
	}
	
	/**
	 * Retourne l'installation courante
	 * @return installtion
	 * @throws InstallationException
	 */
	
	public Installation getCurrentInstallation() throws InstallationException {
		if (installation == null)
			throw new InstallationException(I18N.getString("err.noInstall"));
		return installation;
	}
	
	
	// -----------------------------------------------------------
	// Plan
	// -----------------------------------------------------------
	
	/**
	 * Ajoute un nouveau plan \u00e0 l'installation courante
	 */
	public Plan insertPlan(Plan plan) throws InstallationException {
		return factory.getPlanDAO().insert(plan, getCurrentInstallation().getId());
	}
	
	// -----------------------------------------------------------
	// TypePiece
	// -----------------------------------------------------------
	
	
	/**
	 * Retourne tous les types de pièces
	 */
	public List<TypePiece> getTypePiece() {
		return factory.getTypePieceDAO().getListe(null);
	}
	
	/**
	 * Ajoute d'une nouvelle pièce  
	 */
	public void insertPiece(Piece piece) throws InstallationException {
		try {
			factory.getPieceDAO().insert(piece);
			// pieces.add(piece);
		} catch (Exception e) {
			if (e instanceof InstallationException exception)
				throw exception;
		}
		
	}
	
	// -----------------------------------------------------------
	// Pièces 
	// -----------------------------------------------------------
	
	public List<Piece> getListePieces() {
		if (installation != null) 
			return factory.getPieceDAO().getListeFromInstallation(installation.getId());	
		else
			return List.of();	
	}
	
	/**
	 * Suppression d'une pièce
	 * 
	 */
	public boolean deletePiece(Piece obj) throws InstallationException {
		boolean ok = false;
		try {
			ok = factory.getPieceDAO().delete(obj);
		} catch (Exception e) {
			if (e instanceof InstallationException exc)
				throw exc;
		}
		return ok;
	}
	
	/**
	 * Mise à jour d'une pièce
	 */
	public boolean updatePiece(Piece piece) throws InstallationException {
		boolean ok = false;
		try {
			ok = factory.getPieceDAO().update(piece);
		} catch (Exception e) {
			if (e instanceof InstallationException exc)
				throw exc;
		}
		return ok;
	}
	
	/**
	 * Charge une pièce par son id
	 */
	public Optional<Piece> getPiece(Integer id) {
		return factory.getPieceDAO().getFromID(id);
	}
	
	
}
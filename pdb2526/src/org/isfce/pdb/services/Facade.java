package org.isfce.pdb.services;

import java.util.List;
import java.util.logging.Logger;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.model.LocID;
import org.isfce.pdb.model.Localisation;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;

public class Facade {
	
	private static final Logger logger = Logger.getLogger(Facade.class.getName());

	private DAOFactory factory;
	private Installation installation;
	//pièces de l'installation
	private List<Piece> pieces;
	//élements de l'installation
	private List<Element> elements;

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
		installation = factory.getInstallationDAO()
				.getFromId(id)
				.orElseThrow(() -> new InstallationException("Installation introuvable : " + id));
		pieces = factory.getPieceDAO().getListe(null);
		elements = factory.getElementDAO().getListeFromInstallation(id);
		logger.info("Installation chargée : " + id
				+ " | pièces : " + pieces.size()
				+ " | éléments : " + elements.size());
	}
	
	public Installation getInstallation() {
		return installation;
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
	
	// -----------------------------------------------------------
	// Pièces 
	// -----------------------------------------------------------
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	/**
	 * Ajoute une pièce à l'installation courante 
	 */
	public Piece addPiece(Piece piece) throws InstallationException {
		try {
			Piece created = factory.getPieceDAO().insert(piece);
			pieces.add(created);
			logger.info("Pièce ajoutée : " + created.getNom());
			return created;
		} catch (InstallationException e) {
			throw e;
		} catch (Exception e) {
			throw new InstallationException("Erreur ajout pièce : " + e.getMessage());
		}
		
	}
	
	// -----------------------------------------------------------
	// Eléments
	// -----------------------------------------------------------
	
	public List<Element> getElements() {
		return elements;
	}
	
	// -----------------------------------------------------------
	// Plans
	// -----------------------------------------------------------
	
	/**
	 * Retourne tous les plans de l'utilisation courante
	 */
	public List<Plan> getPlans() throws InstallationException {
		return factory.getPlanDAO()
				.getListePlanFromInstallation(installation.getId());
	}
	
	/**
	 * Ajoute un plan à l'utilisation courante
	 */
	public Plan addPlan(String nomFichier) throws InstallationException {
		Plan plan = new Plan(0, nomFichier);
		return factory.getPlanDAO().insert(plan);
	}
	
	// -----------------------------------------------------------
	// Localisation
	// -----------------------------------------------------------
	
	public Localisation getLocalisation(LocID id) throws InstallationException {
		return factory.getLocalisationDAO()
				.getFromId(id)
				.orElse(null);
	}
	
	public Localisation addLocalisation(LocID id, Localisation loc) throws InstallationException {
		return factory.getLocalisationDAO().insert(id, loc);
	}
	
	public boolean updateLocalisation(LocID id, Localisation loc) throws InstallationException {
		return factory.getLocalisationDAO().update(id, loc);
	}
	
	public boolean deleteLocalisation(LocID id) throws InstallationException {
		return factory.getLocalisationDAO().delete(id);
	}

}

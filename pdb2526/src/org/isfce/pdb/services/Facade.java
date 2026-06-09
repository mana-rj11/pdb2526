package org.isfce.pdb.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Adresse;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.model.LocID;
import org.isfce.pdb.model.Localisation;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.view.bundle.I18N;

public class Facade {
	
	private static final Logger logger = Logger.getLogger(Facade.class.getName());

	private DAOFactory factory;
	private Installation installation;
	//pièces de l'installation
	private List<Piece> pieces = new ArrayList<Piece>();
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
		if (id == 1) {
			installation = Installation.builder()
					.adresse(new Adresse("Rue J buedts", 1040, "Etterbeek"))
					.date(LocalDate.of(2026, 5, 12))
					.id(1)
					.Installateur("moi")
					.proprietaire("truc").build();
				// .getFromId(id)
			//.orElseThrow(() -> new InstallationException("Installation introuvable : " + id));
			//pieces = factory.getPieceDAO().getListe(null);
			//elements = factory.getElementDAO().getListeFromInstallation(id);
			//logger.info("Installation chargée : " + id
			//	+ " | pièces : " + pieces.size()
			//	+ " | éléments : " + elements.size());
		} else 
			throw new InstallationException(I18N.getString("err.noInstall"));
	}
	
	public Installation getCurrentInstallation() throws InstallationException {
		if (installation == null)
			throw new InstallationException(I18N.getString("err.noInstall"));
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
	
	public List<Piece> getListePieces() {
		return pieces;
	}
	
	/**
	 * Ajoute une pièce à l'installation courante 
	 */
	public void insertPiece(Piece piece) throws InstallationException {
		try {
			factory.getPieceDAO().insert(piece);
			pieces.add(piece);
		} catch (Exception e) {
			if (e instanceof InstallationException exception)
				throw exception;
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

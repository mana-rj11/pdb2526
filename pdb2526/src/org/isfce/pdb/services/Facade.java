package org.isfce.pdb.services;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
// import java.util.Collection;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.model.Localisation;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.view.bundle.I18N;


/**
 * Facade couche de service entre les ves et les DAO
 * Centralise l'accès aux données et gère la publication des événements
 */
public class Facade {
	

	private DAOFactory factory;
	private Installation installation;
	private List<Element> elementsCache = null;
	
	
	// publication des changements
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	// constantes pour les événements
	public static final String EVT_PIECE_AJOUTEE = "pieceAjoutee";
	public static final String EVT_PIECE_SUPPRIMEE = "pieceSupprimee";
	public static final String EVT_PIECE_MODIFIEE = "pieceModifiee";
	public static final String EVT_PLAN_AJOUTEE = "planAjoute";
	public static final String EVT_ELEMENT_ASSIGNEE = "elementAssigne";
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

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
		// charge le cache des éléments
		elementsCache = factory.getElementDAO().getListeFromInstallation(installation.getId());
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
		Plan p = factory.getPlanDAO().insert(plan, getCurrentInstallation().getId());
		pcs.firePropertyChange(EVT_PLAN_AJOUTEE, null, p);   // PUBLIE
		return p;
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
	
	public List<Installation> getListeInstallations() throws InstallationException {
		return factory.getInstallationDAO().getListe();
	}
	
	/**
	 * Ajoute d'une nouvelle pièce  
	 */
	public void insertPiece(Piece piece) throws InstallationException {
		try {
			factory.getPieceDAO().insert(piece);
			pcs.firePropertyChange(EVT_PIECE_AJOUTEE, null, piece); // PUBLIE
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
			if (ok) pcs.firePropertyChange(EVT_PIECE_SUPPRIMEE, obj, null);  // PUBLIE
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
			if (ok) pcs.firePropertyChange(EVT_PIECE_MODIFIEE, null, piece);	// PUBLIE
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
	
	// -----------------------------------------------------------
	// Elements / Localisation 
	// -----------------------------------------------------------
	
	/**
	 * Retourne tous les éléments de l'installation courante
	 */
	public List<Element> getListeElements() throws InstallationException {
		if (elementsCache == null)
			elementsCache = factory.getElementDAO().getListeFromInstallation(getCurrentInstallation().getId());
		return factory.getElementDAO().getListeFromInstallation(getCurrentInstallation().getId());
	}
	
	/**
	 * Retourne l'id de la puissance actuellement assignée en un élément,
	 * ou null si l'id element n'est pas assignée avec aucune puissance
	 */
	public Integer getPieceIdAssignee(Element element) throws InstallationException {
		return factory.getLocalisationDAO().getPieceIdFromElement(element.getId()).orElse(null);
	}
	
	/**
	 * Retourne la piece dans laquelle un element est assignee, si elle existe 
	 */
	public Optional<Piece> getPieceDeElement(Element element) throws InstallationException {
		Integer idPiece = getPieceIdAssignee(element);
		if (idPiece == null)
			return java.util.Optional.empty();
		return getListePieces().stream()
				.filter(p -> p.getId().equals(idPiece))
				.findFirst();
	}
	
	/**
	 * Assigne un element à une pièce (delete + insert localisation)
	 */
	public void assignerPiece(Element element, Piece piece) throws InstallationException {
		Optional<Localisation> existante = factory.getLocalisationDAO().getFromId(element.getId());
		if (existante.isPresent())
			factory.getLocalisationDAO().delete(element.getId());
		Localisation nouvelle = new Localisation(0, 0, 0, false);
		factory.getLocalisationDAO().insert(element.getId(), piece.getId(), nouvelle);
		elementsCache = null;	// Invalide le cache
		pcs.firePropertyChange(EVT_ELEMENT_ASSIGNEE, null, element); // PUBLIE
	}
	
	/**
	 * Sauvegarde uniquement les localisation modifiées 
	 */
	public void sauvegarderImplantation(Collection<Element> elements) throws InstallationException {
		for (Element e : elements) {
			if (e.getLocalisation() != null) {
				factory.getLocalisationDAO().update(e.getId(), e.getLocalisation());
			}
		}
		elementsCache = null;	// invalide le cache
	}
	
	/**
	 * Retourne tous les plans de l'installation courante
	 */
	public List<Plan> getListePlans() throws InstallationException {
		return factory.getPlanDAO().getListePlanFromInstallation(getCurrentInstallation().getId());
	}
	
	/**
	 * Retourne les éléments associés à un plan via JOIN SQL
	 */
	public List<Element> getElementsPlan(Plan plan) throws InstallationException {
		return factory.getElementDAO().getListeFromPlan(plan.getId());
	}
	
	public java.util.Properties getProperties() {
		return org.isfce.pdb.view.bundle.I18N.getInstance().getProperties();
	}
	
}
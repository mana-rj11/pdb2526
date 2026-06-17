package org.isfce.pdb.view.plan;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Plan;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VuePlanController {
	//pseudo classe pour les erreurs
	private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
	
	@FXML 
	private TextField ztNom;
	
	private MainController ctrl;
	private Stage stage;
	
	// Event Listener on Button.onAction
	@FXML
	public void actionAnnuler(ActionEvent event) {
		this.stage.close();
	}
	
	// Event listener on Button.onAction
	@FXML 
	public void actionValider(ActionEvent event) {
		Plan plan;
		// V\u00e9rifie la validit\u00e9 des encodages
		boolean bad = checkData();
		if (!bad) {
			try {
				plan = new Plan(0, ztNom.getText().trim());
				this.ctrl.getFacade().insertPlan(plan);
				this.stage.close();
			} catch (InstallationException e) {
				ctrl.showErreur(e.getMessage());
			}
		}
	}
	
	/**
	 * V\u00e9rifie la validit\u00e9 des champs et change la pseudo-classe "errorClass" en
	 * fonction
	 * 
	 * @return true si les donn\u00e9es sont incorrectes
	 */
	private boolean checkData() {
		boolean bad = false;
		boolean erreur;
		erreur = ztNom.getText().isBlank();
		ztNom.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;
		return bad;
	}
	
	/**
	 * Permet de fournir l'acc\u00e8s aux donn\u00e9es
	 * 
	 * @param ctrl
	 * @param stage
	 */
	public void setUpt(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
	}
}

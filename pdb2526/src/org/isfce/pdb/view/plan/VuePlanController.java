package org.isfce.pdb.view.plan;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Plan;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class VuePlanController {
	//pseudo classe pour les erreurs
	private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
	
	@FXML 
	private TextField ztNom;
	
	private MainController ctrl;
	private Stage stage;
	@FXML
	private Spinner<Integer> spEtage; // ajout etage 
	
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
				String fichier = ztNom.getText().trim();
				String nom = fichier.replace(".png", "");
				BigDecimal etage = new BigDecimal(spEtage.getValue()).setScale(1);	// il récupère l'etage du spinner
				plan = new Plan(0, nom, fichier, etage);	// etage ajouté
				this.ctrl.getFacade().insertPlan(plan);
				this.stage.close();
			} catch (InstallationException e) {
				ctrl.showErreur(e.getMessage());
			}
		}
	}
	
	@FXML
	void actionParcourir(ActionEvent event) {
		// ouvre l'explorateur de fichier pour sélectionner une image
		FileChooser fc = new FileChooser();
		fc.setTitle("Selectionner un plan");
		// filtre pour n'afficher que les images
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
		// ouvre la boîte du dialogue
		File file = fc.showOpenDialog(stage);
		
		// si l'user a sélectionné un fichier, met le nom dans le champ
		if (file != null) {
			try {
				// récupère le dossier de destination (imagesPath + installationId)
				String basePath = ctrl.getFacade().getProperties().getProperty("imagesPath")
					+ ctrl.getFacade().getCurrentInstallation().getId() + "/";
				Path destination = Path.of(basePath + file.getName());
				
				// copie le fichier dans le répertoire de l'installation
				Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
				log.info("Fichier copié vers : " + destination);
				
				// mettre le nom du fichier dans le champ
				ztNom.setText(file.getName());
			} catch (Exception e) {
				ctrl.showErreur("Erreur lors de la copie : " + e.getMessage());
				log.error("Copie fichier échouée : " + e.getMessage());
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
	public void setUp(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
		// Initialise le Spinner : étages de 0 à 10, défaut 0
		spEtage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
	}
}


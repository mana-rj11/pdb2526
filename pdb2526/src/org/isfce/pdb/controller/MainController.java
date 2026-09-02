package org.isfce.pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.dao.DAOFactory.TypePersistance;
import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.uri.Databases;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.services.Facade;
import org.isfce.pdb.view.bundle.I18N;
import org.isfce.pdb.view.element.VueListeElementsController;
import org.isfce.pdb.view.piece.VueListePiecesController;
import org.isfce.pdb.view.piece.VuePieceController;
import org.isfce.pdb.view.plan.VueImplantationController;
import org.isfce.pdb.view.plan.VuePlanController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MainController extends Application {
//Facade
	private Facade facade;
//Factory
	private DAOFactory factory;

//MainStage
	private Stage mainStage;
	
// property pour savoir si une installation est chargée
	private BooleanProperty installationChargee = new SimpleBooleanProperty(false);

//Lancement de l'application
	@Override
	public void start(Stage mainStage) {
		// Locale
		Locale.setDefault(Locale.FRENCH);
		// mémorise la fénêtre principale
		this.mainStage = mainStage;
		
		/*
		 * connexion à la base de données création de la fabrique
		 */
		factory = connexionToDatabase();
		
		
		// facade
		facade = new Facade(factory);

		BorderPane cp = new BorderPane();

		// Liste de boutons
		VBox leftPane = new VBox();
		leftPane.setFillWidth(true);
		leftPane.setPadding(new Insets(10));
		
		Button bt1 = new Button("Charge une installation");
		leftPane.getChildren().add(bt1);
		// bt1 
		bt1.setOnAction(this::actionChargeInstallation);
		bt1.setMaxWidth(Double.MAX_VALUE);
			

		// bt2 
		Button bt2 = new Button(I18N.getString("bt.cree.piece"));
		leftPane.getChildren().add(bt2);
		bt2.setOnAction(this::actionCreePiece);
		bt2.setMaxWidth(Double.MAX_VALUE);
		bt2.disableProperty().bind(installationChargee.not());

		// 
		Button bt3 = new Button(I18N.getString("bt.liste.piece"));
		leftPane.getChildren().add(bt3);
		bt3.setOnAction(this::actionListePieces);
		bt3.setMaxWidth(Double.MAX_VALUE);
		bt3.disableProperty().bind(installationChargee.not());
		
		// bt4..
		Button bt4 = new Button(I18N.getString("bt.cree.plan"));
		leftPane.getChildren().add(bt4);
		bt4.setOnAction(this::actionCreePlan);
		bt4.setMaxWidth(Double.MAX_VALUE);
		bt4.disableProperty().bind(installationChargee.not());
		
		// bt5..
		Button bt5 = new Button(I18N.getString("bt.element"));
		leftPane.getChildren().add(bt5);
		bt5.setOnAction(this::actionListeElements);
		bt5.setMaxWidth(Double.MAX_VALUE);
		bt5.disableProperty().bind(installationChargee.not());
		
		// bt6..
		Button bt6 = new Button(I18N.getString("bt.implantation"));
		leftPane.getChildren().add(bt6);
		bt6.setOnAction(this::actionImplantation);
		bt6.setMaxWidth(Double.MAX_VALUE);
		bt6.disableProperty().bind(installationChargee.not());
		
		cp.setLeft(leftPane);
		
		bt1.setTooltip(new Tooltip("Sélectionner une installation à charger"));
		bt2.setTooltip(new Tooltip("Créer une nouvelle pièce pour l'installation"));
		bt3.setTooltip(new Tooltip("Voir et modifier la liste des pièces"));
		bt4.setTooltip(new Tooltip("Ajouter un nouveau plan d'étage"));
		bt5.setTooltip(new Tooltip("Voir les éléments et les assigner aux pièces"));
		bt6.setTooltip(new Tooltip("Placer les éléments sur les plans"));
		
		
		Scene scene = new Scene(cp, 500, 400);
		mainStage.setScene(scene);
		mainStage.setTitle("Projet PDB 2526");
		mainStage.show();

	}

	/**
	 * 
	 * 
	 */
	private void showAddPiece() {
		// bundle
		// ResourceBundle bundle;
		// Crée une stage
		Stage stage = new Stage();
		// indique sa stage parent
		stage.initOwner(mainStage);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setX(100);
		stage.setY(50);

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/isfce/pdb/view/piece/VuePiece.fxml"));

		try {
			ResourceBundle bundle = I18N.getInstance().getGlobalBundle();
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("piece.titre"));
		} catch (Exception e) {
			log.error("Imposible de charger le bundle pour la VuePiece" + e.getMessage());
			// showErreur("Impossible de charger le buddle ");
			stage.setTitle("Vue Piece");
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur

		try {
			AnchorPane root = loader.load();
			VuePieceController ctrl = loader.getController();
			ctrl.setUp(this, stage);
			/*
			// récupère le ctrl (après l'initialisation)
			VuePieceController ctrl = loader.getController();
			// fourni au controleur l'accès à la fabrique et sa stage
			ctrl.setUp(this, stage);
			*/
			// charge le Pane dans la Stage
			Scene scene = new Scene(root);
			URL cssUrl = getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css");
			if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
			// scene.getStylesheets().add(getClass().getResource("./view/css/pdb2526.css").toExternalForm());
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Imposible de charger la vue Piece");
			showErreur("Impossible de charger la vue Piece: " + e.getMessage());
		}
		stage = null;
	}
	
	/**
	 * Affiche le formulaire d'ajout d'un Plan
	 */
	private void showAddPlan() {
		// crée une stage
		Stage stage = new Stage();
		// indique  sa stage parent
		stage.initOwner(mainStage);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setX(100);
		stage.setY(50);
		// creer un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/isfce/pdb/view/plan/VuePlan.fxml"));
		try {
			ResourceBundle bundle = I18N.getInstance().getGlobalBundle();
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale 
			stage.setTitle(bundle.getString("plan.titre"));
		} catch (Exception e) {
			log.error("Imposible de charger le bundle pour la VuePlan" + e.getMessage());
			stage.setTitle("Vue Plan");
		}
		// charge la vue a partir du loader
		// et initialise son contenu en appelant la méthode setUpdu controller 
		try {
			AnchorPane root = loader.load();
			VuePlanController ctrl = loader.getController();
			ctrl.setUp(this, stage);
			// charge le Pane dans la stage
			Scene scene = new Scene(root);
			URL cssUrl = getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css");
			if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Impossible de charger la vue Plan", e);
			showErreur("Impossobile de charger la vue Plan:" + e.getMessage());
		}
		stage = null;
	}
	
	public void showListePieces() {
		// bundle 
		// ResourceBundle bundle;
		// creer une stage 
		Stage stage = new Stage();
		// indiquer sa stage parent
		stage.initOwner(mainStage);
		// stage.initModality(Modality.APPLICATION_MODAL);
		stage.setX(100);
		stage.setY(50);
		
		// Creer un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/org/isfce/pdb/view/piece/VueListePieces.fxml"));
		try {
			ResourceBundle bundle = I18N.getInstance().getGlobalBundle();
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("piece.liste.titre"));
		} catch (Exception e) {
			log.error("Impossible de charger le bundle VueListePieces : " + e.getMessage());
			stage.setTitle("Vue Liste Pieces");
		}
		
		try {
			AnchorPane root = loader.load();
			VueListePiecesController ctrl = loader.getController(); 
			ctrl.setUp(this, stage);
			Scene scene = new Scene(root);
			URL cssUrl = getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css");
			if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
			// scene.getStylesheets().add(getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			log.error("Impossible de charger la vue ListePiece");
			showErreur("Impossible de charger la vue ListePieces: " + e.getMessage());
		}
		stage = null;
				
	}
	
	/**
	 * Affiche la liste des éléments
	 */
	public void showListeElements() {
		Stage stage = new Stage();
		stage.initOwner(mainStage);
		stage.setX(100);
		stage.setY(50);
		
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/org/isfce/pdb/view/element/VueListeElements.fxml"));
		try {
			ResourceBundle bundle = I18N.getInstance().getGlobalBundle();
			loader.setResources(bundle);
			stage.setTitle(bundle.getString("liste.element.titre"));
		} catch (Exception e) {
			log.error("Impossible de charger le bundle VueListeElements : " + e.getMessage());
			stage.setTitle("Vue Liste Elements");
		}
		
		try {
			BorderPane root = loader.load();
			VueListeElementsController ctrl = loader.getController();
			ctrl.setUp(this, stage);
			Scene scene = new Scene(root);
			URL cssUrl = getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css");
			if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			log.error("Impossible de charger la vue ListeElements");
			showErreur("Impossible de charger la vue ListeElements: " + e.getMessage());
		}
		stage = null;
	}
	
	/**
	 * Affiche la vue d'implantation graphique
	 */
	public void showImplantation() {
		Stage stage = new Stage();
		stage.initOwner(mainStage);
		stage.setX(100);
		stage.setY(50);
		
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/org/isfce/pdb/view/plan/VueImplantation.fxml"));
		try { 
			ResourceBundle bundle = I18N.getInstance().getGlobalBundle();
			loader.setResources(bundle);
			stage.setTitle(bundle.getString("bt.implantation"));
		} catch (Exception e) {
			log.error("Impossible de charger le bundle VueImplantation : " + e.getMessage());
			stage.setTitle("Vue Implantation");
		}
		
		try {
			BorderPane root = loader.load();
			stage.setScene(new Scene(root));
			VueImplantationController ctrl = loader.getController();
			ctrl.setUp(this, stage);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Impossible de charger la vue Implantation");
			showErreur("Impossible de charger la vue Implantation: " + e.getMessage());
		}
		stage = null;
	}
	

	/**
	 * Boite de confirmation
	 * 
	 * @param message
	 * @return
	 */
	public boolean showConfirmation(String message) {
		Alert a = new Alert(AlertType.CONFIRMATION, message);
		Optional<ButtonType> result = a.showAndWait();
		return result.get() == ButtonType.OK;
	}

	/**
	 * Vue pour afficher les messages d'erreur
	 * 
	 * @param message
	 */
	public void showErreur(String message) {
		Alert a = new Alert(AlertType.ERROR, message);
		a.showAndWait();
	}


	/**
	 * Point d'entrée principal de l'application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public Facade getFacade() {
		return facade;
	}
	
	/**
	 * Connexion à la base de données sur base du contenu du fichier
	 * "connexionPDB2526.properties"
	 */
	private DAOFactory connexionToDatabase() {
		DAOFactory factory = null;
		// Connexion à la BD
		try {
			ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
					"./ressources/connexionPDB2526.properties", Databases.FIREBIRD));
			Connection connect = ConnexionSingleton.getConnexion();
			log.info("Connexion établie");
			// Crée la factory Firebird
			factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, connect);
		} catch (Exception e) {
			showErreur("Problème de connexion");
			// Quitte l'application
			Platform.exit();
		}
		return factory;
	}
	
	public void actionChargeInstallation(ActionEvent event) {
		try {
			// charge la liste des installations depuis la BD
			List<Installation> installations = facade.getListeInstallations();
			
			if (installations.isEmpty()) {
				showErreur("Aucune installation trouvée en base de données");
				return;
			}
			
			// crée un dialog avec une ListeView
			Dialog<Installation> dialog = new Dialog<>();
			dialog.setTitle("Sélection d'une installation");
			dialog.setHeaderText("Double-cliquez sur une installation pour la charger");
			// style cohérent avec l'app
			dialog.getDialogPane().setStyle("-fx-font-family: Georgia;");
			
			// ListeView des installations
			ListView<Installation> lvInstallations = new ListView<>();
			lvInstallations.setItems(FXCollections.observableArrayList(installations));
			lvInstallations.setPrefSize(500, 300);
			// style cohérent avec l'app
			lvInstallations.setStyle(
				"-fx-font-size: 13px;" +
				"-fx-selection-bar: #4FC3F7;" +
				"-fx-selection-bar-non-focused: #B3E5FC;");
			
			// affichage personnalisé de chaque ligne 
			lvInstallations.setCellFactory(lv -> new ListCell<Installation>() {
				@Override
				protected void updateItem(Installation inst, boolean empty) {
					super.updateItem(inst, empty);
					if (empty || inst == null) {
						setText(null);
					} else {
						setText("Installation n°" + inst.getId()
							+ " | " + inst.getDate()
							+ " | " + inst.getProprietaire()
							+ " | " + inst.getAdresse().getRue()
							+ ", " + inst.getAdresse().getCp()
							+ " " + inst.getAdresse().getVille());
						
						// style de base 
						// String bgColor = getIndex() % 2 == 0 ? "#E1F5FE" : "white";
					// 	setStyle("-fx-padding: 8; -fx-font-family: Georgia; -fx-font-size: 13px; -fx-background-color: " + bgColor + ";");
						
						// setStyle(baseStyle + "-fx-background-color: " + bgColor + ";");
						// setTextFill(Color.BLACK);
	
					}
				}
			}); 
					
			// double clic pour charger
			lvInstallations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && lvInstallations.getSelectionModel().getSelectedItem() != null) {
					dialog.setResult(lvInstallations.getSelectionModel().getSelectedItem());
					dialog.close();
				}
			});
			
			dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
			dialog.getDialogPane().setContent(lvInstallations);
			
			// récupère le resultat via OK aussi
			dialog.setResultConverter(bt -> {
				if (bt == ButtonType.OK)
					return lvInstallations.getSelectionModel().getSelectedItem();
				return null;
			});
			
			// double clic pour charger
	        lvInstallations.setOnMouseClicked(e -> {
	            if (e.getClickCount() == 2 && lvInstallations.getSelectionModel().getSelectedItem() != null) {
	                dialog.setResult(lvInstallations.getSelectionModel().getSelectedItem());
	                dialog.close();
	            }
	        });

	        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
	        dialog.getDialogPane().setContent(lvInstallations);
	        dialog.getDialogPane().getStylesheets().add(
	        	getClass().getResource("/org/isfce/pdb/view/css/pdb2526.css").toExternalForm());
	        

	        dialog.setResultConverter(bt -> {
	            if (bt == ButtonType.OK)
	                return lvInstallations.getSelectionModel().getSelectedItem();
	            return null;
	        });
			
			// affiche et traite le résultat 
			Optional<Installation> result = dialog.showAndWait();
			result.ifPresent(inst -> {
				try {
					facade.chargeInstallation(inst.getId());
					installationChargee.set(true);
					facade.chargeInstallation(inst.getId());
					installationChargee.set(true);
					// met a jour le titre de la fenêtre
					mainStage.setTitle("Projet PDB 2526 - Installation n°" + inst.getId()
						+ " | " + inst.getProprietaire()
						+ " | " + inst.getAdresse().getVille());
				} catch (InstallationException ex) {
					showErreur(ex.getMessage());
				}
			});
			
		} catch (InstallationException e) {
			showErreur(e.getMessage());
		}
	}
			
			
	
	public void actionCreePiece(ActionEvent event) {
		showAddPiece();
	}
	
	public void actionListePieces(ActionEvent event) {
		showListePieces();
	}
	
	public void actionCreePlan(ActionEvent event) {
		showAddPlan();
	}
	
	public void actionListeElements(ActionEvent event) {
		showListeElements();
	}
	
	public void actionImplantation(ActionEvent event) {
		showImplantation();
	}

}



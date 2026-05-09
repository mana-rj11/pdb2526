package org.isfce.pdb.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.dao.DAOFactory.TypePersistance;
import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.uri.Databases;
import org.isfce.pdb.services.Facade;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		Button bt1 = new Button("Charge une installation");
		leftPane.getChildren().add(bt1);
		// bt1 
		bt1.setOnAction(ev -> {
			
		});

		// bt2 
		Button bt2 = new Button("");
		leftPane.getChildren().add(bt2);
		bt2.setOnAction(ev -> {
			
		});

		// 
		Button bt3 = new Button("");
		leftPane.getChildren().add(bt3);
		bt3.setOnAction(ev -> {
			
		});

		cp.setLeft(leftPane);

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
		ResourceBundle bundle;
		// Crée une stage
		Stage stage = new Stage();
		// indique sa stage parent
		stage.initOwner(mainStage);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setX(100);
		stage.setY(50);

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/piece/VuePiece.fxml"));

		try {
			bundle = ResourceBundle.getBundle("view.piece.bundle.VuePiece");
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("titre"));
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Piece" + e.getMessage());
			showErreur("Impossible de charger le buddle ");
			stage.setTitle("Vue Piece");
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur
		AnchorPane root;
		try {
			root = loader.load();
			/*
			// récupère le ctrl (après l'initialisation)
			VuePieceController ctrl = loader.getController();
			// fourni au controleur l'accès à la fabrique et sa stage
			ctrl.setUp(this, stage);
			*/
			// charge le Pane dans la Stage
			Scene scene = new Scene(root);
			scene.getStylesheets().add("./view/css/pdb2526.css");
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Imposible de charger la vue Piece");
			showErreur("Impossible de charger la vue Piece: " + e.getMessage());
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

}

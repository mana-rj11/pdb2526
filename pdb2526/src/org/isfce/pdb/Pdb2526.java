package org.isfce.pdb;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Pdb2526 extends Application {

	@Override
	public void start(Stage primaryStage) {
		BorderPane cp = new BorderPane();
		Label titre = new Label("PDB2526");
		titre.getStyleClass().add("titre-label");
		titre.setMaxWidth(Double.MAX_VALUE);
		HBox hb=new HBox(titre);
		hb.setAlignment(Pos.CENTER);
		hb.getStyleClass().add("top-bar");
		HBox.setHgrow(titre, Priority.ALWAYS);
		cp.setTop(hb);
		Scene scene = new Scene(cp);
		scene.getStylesheets().add(getClass().getResource("pdb2526.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setWidth(300);
		primaryStage.setHeight(200);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}

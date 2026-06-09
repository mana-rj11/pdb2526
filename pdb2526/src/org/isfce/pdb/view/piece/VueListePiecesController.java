package org.isfce.pdb.view.piece;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class VueListePiecesController implements Initializable {
	@FXML
	private Button btAjouterPiece;
	@FXML
	private Button btQuitter;
	@FXML
	private Button btRecharger;
	@FXML
	private Button btValider;
	
	@FXML
	private TableColumn<Piece, String> colDescription;
	@FXML
	private TableColumn<Piece, BigDecimal> colEtage;
	@FXML
	private TableColumn<Piece, String> colNom;
	@FXML
	private TableColumn<?, ?> colOperation;
	@FXML
	private TableColumn<Piece, TypePiece> colTypePiece;
	@FXML
	private TableView<Piece> tblPieces;
	
	private Stage stage;
	private MainController ctrl;
	private ObservableList<Piece> obsPieces;
	
	@FXML
	void actionAjouterPiece(ActionEvent event) {
		
	}
	
	@FXML
	void actionQuitter(ActionEvent event) {
		stage.close();
	}
	
	@FXML
	void actionRecharger(ActionEvent event) {
		
	}
	
	@FXML
	void actionValider(ActionEvent event) {
		
	}
	
	public void setUp(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
		List<Piece> listePiece = ctrl.getFacade().getListePieces();
		obsPieces = FXCollections.observableArrayList(listePiece);
		tblPieces.setItems(obsPieces);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		colNom.setCellValueFactory(
			p -> new ReadOnlyStringWrapper(p.getValue().getNom()));
		colDescription.setCellValueFactory(
			p -> new ReadOnlyStringWrapper(p.getValue().getDescription()));
		colTypePiece.setCellValueFactory(
			p -> new SimpleObjectProperty<TypePiece>(p.getValue().getTypePiece()));
		colEtage.setCellValueFactory(
			p -> new SimpleObjectProperty<BigDecimal>(p.getValue().getEtage()));
	}
	
}

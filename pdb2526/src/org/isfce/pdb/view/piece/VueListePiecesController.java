package org.isfce.pdb.view.piece;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.services.Facade;
import org.isfce.pdb.view.bundle.I18N;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j

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
	private TableColumn<Piece, Void> colOperation;
	@FXML
	private TableColumn<Piece, TypePiece> colTypePiece;
	@FXML
	private TableColumn<Piece, String> colPlan;  // colonne PLAN
	
	@FXML
	private TableView<Piece> tblPieces;
	
	private Stage stage;
	private MainController ctrl;
	private ObservableList<Piece> obsPieces;
	private ObservableList<TypePiece> typePieces =
		FXCollections.observableArrayList();
	private Map<Integer, Piece> mapUpdate = new HashMap<>();
	private BooleanProperty update = new SimpleBooleanProperty(false);
	private Facade facade;
	
	@FXML
	void actionAjouterPiece(ActionEvent event) {
		ctrl.actionCreePiece(event);
	}
	
	@FXML
	void actionQuitter(ActionEvent event) {
		fermeture();
		stage.close();
	}
	
	private void fermeture() {
		mapUpdate.clear();
		update.set(false);
		obsPieces.clear();
	}
	
	@FXML
	void actionRecharger(ActionEvent event) {
		obsPieces.setAll(facade.getListePieces());
		mapUpdate.clear();
		update.set(false);	// rechargement complet depuis la BD
		
		// anciene version 
		// for (Piece piece : mapUpdate.values()) {
			// int pos = obsPieces.indexOf(piece);
			// facade.getPiece(piece.getId())
				// .ifPresent(p -> obsPieces.set(pos, p));
		}
	
	@FXML
	void actionValider(ActionEvent event) {
		List<Integer> listeId = new ArrayList<>(mapUpdate.keySet());
		for (Integer id : listeId) {
			try {
				facade.updatePiece(mapUpdate.get(id));
				mapUpdate.remove(id);
			} catch (InstallationException e) {
				log.error("Can't update pièce: " + e.getMessage());
			}
		}
		if (mapUpdate.isEmpty())
			update.set(false);
	}
	
	public void setUp(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
		this.facade = ctrl.getFacade();
		this.typePieces.clear();
		this.typePieces.addAll(facade.getTypePiece());
		List<Piece> listePiece = ctrl.getFacade().getListePieces();
		obsPieces = FXCollections.observableArrayList(listePiece);
		tblPieces.setItems(obsPieces);
		update.set(false);
		mapUpdate.clear();
		stage.setOnCloseRequest(_ -> fermeture());
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
		colPlan.setCellValueFactory(p -> new ReadOnlyStringWrapper(
			    p.getValue().getPlan() != null 
			        ? p.getValue().getPlan().getNom() + " (ét." + p.getValue().getPlan().getEtage() + ")"
			        : "Aucun plan"));	// ajouté
		colPlan.setEditable(true);
		
		tblPieces.setEditable(true);
		
		// colonne NOM éditable
		colNom.setEditable(true);
		colPlan.setEditable(true);
		colNom.setCellFactory(_ -> new TextFieldTableCell<Piece, String>(new DefaultStringConverter()) {
			private TextField textField;
			
			@Override 
			public void startEdit() {
				super.startEdit();
				if (textField == null) {
					textField = (TextField) getGraphic();
					textField.focusedProperty().addListener((_, _, newV) -> {
						if (!newV) commitEdit(textField.getText());
					});
				}
			}
			
			@Override
			public void commitEdit(String newValue) {
				String oldValue = getItem();
				if (Objects.equals(oldValue, newValue)) {
					super.cancelEdit();
					return;
				}
				super.commitEdit(newValue);
			}
			
			@Override
			public void cancelEdit() {
				if (textField != null)
					commitEdit(textField.getText());
				else 
					super.cancelEdit();
			}
		});
	colNom.setOnEditCommit(e -> {
		Piece p = e.getRowValue();
		p.setNom(e.getNewValue());
		mapUpdate.put(p.getId(), p);
		update.set(true);
	});
	
	// colonne DESCRIPTION éditable
	colDescription.setEditable(true);
	colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
	colDescription.setOnEditCancel(e -> { 
		Piece p = e.getRowValue();
		p.setDescription(e.getNewValue());
		mapUpdate.put(p.getId(), p);
		update.set(true);
	});
	
	// colonne ETAGE avec Spinner
	colPlan.setEditable(true);
	colEtage.setEditable(true);
	colEtage.setCellFactory(_ -> new TableCell<>() {
		private final Spinner<Double> spinner = new Spinner<>(-5.0, 10.0, 0.0, 0.5);
		private boolean updatingByPrgm = false;
		{
			spinner.setEditable(true);
			spinner.valueProperty().addListener((_, _, newVal) -> {
				if (updatingByPrgm || getIndex() < 0) return;
				Piece piece = getTableView().getItems().get(getIndex());
				piece.setEtage(BigDecimal.valueOf(newVal)
						.setScale(1, RoundingMode.HALF_UP));
				mapUpdate.put(piece.getId(), piece);
				update.set(true);
			});
		}
		@Override
		protected void updateItem(BigDecimal item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				updatingByPrgm = true;
				spinner.getValueFactory().setValue(item.doubleValue());
				updatingByPrgm = false;
				setGraphic(spinner);
			}
		}	
	});
	
	// colonne TYPE DE PIECE avec Combobox
	StringConverter<TypePiece> converterType = new StringConverter<>() {
		@Override
		public TypePiece fromString(String str) {
			return typePieces.stream()
				.filter(tp -> tp.getCode().equals(str))
				.findFirst().get();
		}
		@Override
		public String toString(TypePiece t) {
			return t.getNom();
		}
	};
	colTypePiece.setCellFactory(
		ComboBoxTableCell.forTableColumn(converterType, typePieces));
	colTypePiece.setOnEditCancel(e -> {
		Piece p = e.getRowValue();
		p.setTypePiece(e.getNewValue());
		mapUpdate.put(p.getId(), p);
		update.set(true);
	});
	
	// colonne OPERATION avec bouton supprimer 
	colOperation.setSortable(false);
	colOperation.setEditable(false);
	colPlan.setEditable(true);
			
	colOperation.setCellFactory(_ -> new TableCell<>() {
		final Button btDel = new Button();
		final ImageView iconDel = new ImageView(new Image(
			getClass().getResource("/org/isfce/pdb/view/icon/trash.png").toExternalForm()));
		final Pane paneBt = new StackPane();
		{
			btDel.setGraphic(iconDel);
			paneBt.setPadding(new Insets(2));
			paneBt.getChildren().add(btDel);
			btDel.setOnAction(_ -> {
				Piece obj = getTableRow().getItem();
				if (obj != null) {
					mapUpdate.remove(obj.getId());
					update.set(!mapUpdate.isEmpty());
					try {
						if (facade.deletePiece(obj))
							obsPieces.remove(obj);
					} catch (InstallationException e1) {
						ctrl.showErreur(I18N.getString(e1.getMessage()));
					}
				}
			});
		}
		@Override
		protected void updateItem(Void item, boolean empty) {
			if (!empty) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setGraphic(paneBt);
			} else {
				setGraphic(null);
			}
			super.updateItem(item, empty);
		}
	});
	
	// boutons actif uniquement si modification
	btValider.disableProperty().bind(update.not());
	// btRecharger.disableProperty().bind(update.not());
	}
	
}

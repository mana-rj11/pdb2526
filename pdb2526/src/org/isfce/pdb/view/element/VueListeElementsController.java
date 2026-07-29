package org.isfce.pdb.view.element;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.services.Facade;

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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class VueListeElementsController implements Initializable {
	
	@FXML
	private Button btQuitter;
	@FXML 
	private Button btRecharger;
	
	@FXML 
	private TableColumn<Element, String> colCode;
	@FXML
	private TableColumn<Element, String> colInfo;
	@FXML 
	private TableColumn<Element, String> colAppareil;
	@FXML 
	private TableColumn<Element, Integer> colQt;
	@FXML 
	private TableColumn<Element, Piece> colPiece;
	@FXML
	private TableView<Element> tblElements;
	
	private Stage stage;
	private MainController ctrl;
	private Facade facade;
	private ObservableList<Element>obsElements;
	private ObservableList<Piece> listePieces = FXCollections.observableArrayList();
	// numerise la piece actuellement assignée dans chaque élement (par id d'un element)
	private Map<Integer, Piece> pieceAssignee = new HashMap<>();
	
	@FXML
	void actionQuitter(ActionEvent event) {
		stage.close();
	}
	
	@FXML
	void actionRecharger(ActionEvent event) {
		chargerDonnees();
	}
	
	
	private void chargerDonnees() {
		try {
			listePieces.clear();
			listePieces.addAll(facade.getListePieces());
			
			List<Element> listeElement = facade.getListeElements();
			pieceAssignee.clear();
			for (Element element : listeElement) {
				Integer idPiece = facade.getPieceIdAssignee(element);
				if (idPiece != null) {
					listePieces.stream()
							.filter(piece -> piece.getId().equals(idPiece))
							.findFirst()
							.ifPresent(piece -> pieceAssignee.put(element.getId(), piece));
				}
			}
			obsElements = FXCollections.observableArrayList(listeElement);
			tblElements.setItems(obsElements);
			tblElements.refresh();
		} catch (InstallationException e) {
			ctrl.showErreur(e.getMessage());
		}
	}
	
	public void setUp(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
		this.facade = ctrl.getFacade();
		chargerDonnees();
		// ecoute les changements de pièces pour rafraichir la liste 
		facade.addPropertyChangeListener(evt -> {
			if (evt.getPropertyName().equals(Facade.EVT_PIECE_AJOUTEE)
				|| evt.getPropertyName().equals(Facade.EVT_PIECE_SUPPRIMEE)) {
				// Rafraichit la liste des pièces dans la combobox
				javafx.application.Platform.runLater(() -> {
					try {
						listePieces.setAll(facade.getListePieces());
					} catch (Exception e) {
						log.error("Erreur refresh pièces: " + e.getMessage());
					}
				});
			}
				
		});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		colCode.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getCode()));
		colInfo.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getInfo()));
		colAppareil.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getAppareil().getNom()));
		colQt.setCellValueFactory(p -> new SimpleObjectProperty<Integer>(p.getValue().getQt()));
		
		colPiece.setCellValueFactory(p -> new SimpleObjectProperty<Piece>(pieceAssignee.get(p.getValue().getId())));
		
		StringConverter<Piece> converterPiece = new StringConverter<>() {
			@Override
			public Piece fromString(String str) {
				return listePieces.stream()
						.filter(piece -> piece.getNom().equals(str))
						.findFirst().orElse(null);
			}
			@Override
			public String toString(Piece piece) {
				return piece != null ? piece.getNom() : "";
			}
		};
		colPiece.setCellFactory(ComboBoxTableCell.forTableColumn(converterPiece, listePieces));
		colPiece.setEditable(true);
		colPiece.setOnEditCommit(e -> {
			Element element = e.getRowValue();
			Piece piece = e.getNewValue();
			if (piece == null)
				return;
			try {
				facade.assignerPiece(element, piece);
				pieceAssignee.put(element.getId(), piece);
			} catch (InstallationException ex) {
				ctrl.showErreur(ex.getMessage());
			}
		});
		
		tblElements.setEditable(true);
	}

}

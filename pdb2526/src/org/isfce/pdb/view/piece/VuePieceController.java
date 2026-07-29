package org.isfce.pdb.view.piece;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;

import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class VuePieceController implements Initializable {
	// pseudo classe pour les erreurs
	private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

	@FXML
	private TextField ztNom;
	@FXML
	private TextField ztDescription;
	@FXML
	private Spinner<Double> spEtage;
	@FXML
	private ComboBox<TypePiece> cbTypePiece;
	@FXML 
	private ComboBox<Plan> cbPlan;	// plan ajouté

	// private ResourceBundle bundle;

	private MainController ctrl;

	private Stage stage;

	// Event Listener on Button.onAction
	@FXML
	public void actionAnnuler(ActionEvent event) {
		this.stage.close();
	}

	// Event Listener on Button.onAction
	@FXML
	public void actionValider(ActionEvent event) {
		// Piece piece;
		// Vérifie la validité des encodages
		boolean bad = checkData();
		if (!bad) {
			// Création de l'objet
			try {
				Piece piece = Piece.builder()
					.nom(ztNom.getText().trim())
					.description(ztDescription.getText().trim())
					.etage(BigDecimal.valueOf(spEtage.getValue()).setScale(1, RoundingMode.HALF_UP))
					.typePiece(cbTypePiece.getValue())
					.installation(ctrl.getFacade().getCurrentInstallation().getId())
					.plan(cbPlan.getValue())
					.build();
				this.ctrl.getFacade().insertPiece(piece);
				this.stage.close();
			} catch (InstallationException e) {
				ctrl.showErreur(e.getMessage());
			}
		}
		}

	/**
	 * Vérifie la validité des champs et champ la pseudo-classe "errorClass" en
	 * fonction
	 * 
	 * @return true si les données sont correctes
	 */
	private boolean checkData() {
		boolean bad = false;
		boolean erreur;

		erreur = ztNom.getText().isBlank();
		ztNom.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;

		erreur = ztDescription.getText().isBlank();
		ztDescription.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;

		erreur = !(cbTypePiece.getValue() instanceof TypePiece);
		cbTypePiece.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;
		
		// vérifie le plan 
		erreur = cbPlan.getValue() == null;
		cbPlan.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;
		
		return bad;
	}

	/**
	 * Permet de fournir l'accès aux données
	 * 
	 * @param ctrl
	 * @param stage
	 */
	public void setUp(MainController ctrl, Stage stage) {
		this.stage = stage;
		this.ctrl = ctrl;
		List<TypePiece> listeTypePiece = ctrl.getFacade().getTypePiece();
		cbTypePiece.setItems(FXCollections.observableArrayList(listeTypePiece));
		try {
			cbPlan.setItems(FXCollections.observableArrayList(ctrl.getFacade().getListePlans()));
		} catch (InstallationException e) {ctrl.showErreur(e.getMessage());}
		cbPlan.setConverter(new StringConverter<Plan>() {
			
		    public String toString(Plan p) { 
		        return p != null ? p.getNom() + " (ét." + p.getEtage() + ")" : ""; 
		    }
		    @Override
		    public Plan fromString(String s) { return null; }
		}); // charge les plans dans la combobox
		
		cbTypePiece.setConverter(new StringConverter<TypePiece>() {
			@Override
			public String toString(TypePiece tp) {
				return tp != null ? tp.getNom() : "";
			}
			@Override
			public TypePiece fromString(String s) {
				return listeTypePiece.stream()
					.filter(tp -> tp.getNom().equals(s))
					.findFirst().orElse(null);
			}
		});
		}
	
		

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		// this.bundle = bundle;
		// Etage
		SpinnerValueFactory<Double> vf = new SpinnerValueFactory.DoubleSpinnerValueFactory(-5.0, 10.0, 0, 0.5);
		spEtage.setValueFactory(vf);

		// Type de pièce
		cbTypePiece.setEditable(true);

		cbTypePiece.setConverter(new StringConverter<TypePiece>() {

			@Override
			public String toString(TypePiece t) {
				if (t != null)
					return t.getNom();
				return "";
			}

			@Override // pas nécessaire si pas éditable
			public TypePiece fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});

	}
}


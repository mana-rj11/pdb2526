package org.isfce.pdb.view.plan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.isfce.pdb.controller.MainController;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.services.Facade;
import org.isfce.pdb.view.bundle.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VueImplantationController implements Initializable {

	@FXML
	private Button btAnnuler;

	@FXML
	private Button btValider;

	@FXML
	private Canvas canvas;

	@FXML
	private ScrollPane scpCanvas;

	@FXML
	private Group grpPane;

	@FXML
	private Pane pane;

	@FXML
	private ComboBox<Plan> cbPlans;
	
	@FXML 
	private ComboBox<Piece> cbPieces;

	@FXML
	private ListView<Element> lstElements;

	/* pas besoin pour l'instant */
	private Stage stage;
	private MainController ctrl;

	private Facade facade;
	
	//toggle pour afficher/masquer les noms
	private boolean afficherNomsPieces = true;

	private Optional<Plan> oPlanCharge = Optional.empty();
	// associe à un plan son canvas
	private Map<Integer, Canvas> mapPlanCanvas = new HashMap<>();
	// associe à un plan ses ElementView
	private Map<Integer, List<ElementView>> mapPlanNodes = new HashMap<>();
	// associe à un plan la liste observable de ses éléments
	private Map<Integer, ObservableList<Element>> mapPlanElements = new HashMap<>();
	// associe à un plan une Map (id élément -> Pièce) pour le filtre par pièce
	private Map<Integer, Map<Integer, Piece>> mapPlanElementPiece = new HashMap<>();
	// map pour stocker les text des noms de pièces par plan
	private Map<Integer, List<Text>> mapPlanPieceNames = new HashMap<>();
	
	private Set<Integer> elementsModifies = new HashSet<>();
	
	// Zoom
	private double zoom = 1.0;		// niveau de zoom actuel
	private double zoomMin = 0.3;	// zoom minimum
	private double zoomMax = 3.0; 	// zoom maximum

	@FXML
	void actionQuitter(ActionEvent event) {
		//TODO  à ajuster conf.quitter.sans.sauver
		if (ctrl.showConfirmation(I18N.getString("conf.quitter.sans.sauver")))
			stage.hide();
	}

	@FXML
	void actionValider(ActionEvent event) {
		//TODO gérer la sauvegarde
		if (ctrl.showConfirmation(I18N.getString("conf.save"))) {
			try {
				// Ne sauvegarde que les éléments modifiés
				// java.util.List<Element> tousLesElements = new java.util.ArrayList<>();
				List<Element> aMettre = new ArrayList<>();
				for (var listeElements : mapPlanElements.values()) {
					for (Element el : listeElements) {
						if (elementsModifies.contains(el.getId()))
							aMettre.add(el);
					}
				}
				ctrl.getFacade().sauvegarderImplantation(aMettre);
				// sauvegarde les positions des noms de pièces
				for (var pieces : mapPlanElementPiece.values()) {
					Set<Integer> dejaVu = new HashSet<>();
					for (Piece piece : pieces.values()) {
						if (piece != null && dejaVu.add(piece.getId())) {
							if (piece.getXNom() > 0 || piece.getYNom() > 0)
								ctrl.getFacade().updatePiece(piece);
						}
					}
				}
				elementsModifies.clear(); // reinitialise après sauvegarde
				stage.hide();
			} catch (InstallationException e) {
				ctrl.showErreur(e.getMessage());
			} catch (Exception e) {
				ctrl.showErreur(e.getMessage());
			}
		}
	}

	@FXML
	void actionChargePlan(ActionEvent event) {

		// Charge le plan sélectionné
		oPlanCharge = Optional.ofNullable(cbPlans.getValue());
		if (oPlanCharge.isPresent()) {
			Plan planCharge = oPlanCharge.get();
			// charge le canvas du plan
			canvas = mapPlanCanvas.get(planCharge.getId());

			pane.getChildren().clear();// vide le Pane
			pane.getChildren().add(canvas);// place le dessin de l'étage
			// ajuste le canvas à la taille de l'image
			pane.setPrefSize(canvas.getWidth(), canvas.getHeight());

			// On spécifie les éléments à la "List" lstElements
			lstElements.setItems(mapPlanElements.get(planCharge.getId()));

			// dessine les éléments déjà placés
			pane.getChildren().addAll(mapPlanNodes.get(planCharge.getId()));
			// crée les nom des pièces
			if(!mapPlanPieceNames.containsKey(planCharge.getId())) {
				mapPlanPieceNames.put(planCharge.getId(), creePieceNames(planCharge.getId()));
			}
			// affiche les nom si le toggle est activé
			if (afficherNomsPieces) {
				pane.getChildren().addAll(mapPlanPieceNames.get(planCharge.getId()));
			}
			
			// met à jour le filtre par pièce pour ce plan
			ObservableList<Piece> piecesDuPlan = FXCollections.observableArrayList();
			piecesDuPlan.add(null); // toutes les pièces
			piecesDuPlan.addAll(mapPlanElementPiece.get(planCharge.getId()).values().stream().distinct().toList());
			cbPieces.setItems(piecesDuPlan);
			cbPieces.getSelectionModel().select(null); // pas de filtre par défaut
			// reinitialise le zoom
			zoom = 1.0;
			zoomer();

		}

	}
	
	@FXML 
	void actionFiltrePiece(ActionEvent event) {
		if (oPlanCharge.isEmpty())
			return;
		Piece filtre = cbPieces.getValue();
		ObservableList<Element> tousLesElements = mapPlanElements.get(oPlanCharge.get().getId());
		if (filtre == null) {
			lstElements.setItems(tousLesElements);
		} else {
			Map<Integer, Piece> elementPieceMap = mapPlanElementPiece.get(oPlanCharge.get().getId());
			ObservableList<Element> filtres = FXCollections.observableArrayList(
					tousLesElements.stream()
							.filter(e -> filtre.equals(elementPieceMap.get(e.getId())))
							.toList());
			lstElements.setItems(filtres);
		}
	}
	/**
	 * Active/désactive l'affichage des noms de pièces
	 */
	@FXML
	void actionToggleNoms(ActionEvent event) {
		afficherNomsPieces = !afficherNomsPieces;
		if (oPlanCharge.isPresent()) {
			List<Text> names = mapPlanPieceNames.get(oPlanCharge.get().getId());
			if (names != null) {
				if (afficherNomsPieces)
					pane.getChildren().addAll(names);
				else
					pane.getChildren().removeAll(names);
			}
		}
	}
	
	@FXML
	void actionZoomIn(ActionEvent event) {
		zoom = Math.min(zoomMax, zoom + 0.1);
		zoomer();
	}
	
	@FXML
	void actionZoomOut(ActionEvent event) {
		zoom = Math.max(zoomMin, zoom - 0.1);
		zoomer();
	}
	
	@FXML
	void actionZoomReset(ActionEvent event) {
		zoom = 1.0;
		zoomer();
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
		this.facade = ctrl.getFacade();

		// liste observable des Plans valide (avec une image), pour la comboBox
		ObservableList<Plan> obsPlan = FXCollections.observableArrayList();
		// Crée la liste des images dans des canvas (couche de fond)
		mapPlanCanvas.clear();
		String basePath;// chemin de base des images
		try {
			// liste des plans
			List<Plan> listePlan = facade.getListePlans();
			// format de base: chemin/installationId/
			basePath = facade.getProperties().getProperty("imagesPath") + facade.getCurrentInstallation().getId() + "/";
			// ligne temporaire
			// System.out.println("DEBUG basePath=[" + basePath + "]");

			// Crée les canvas pour les plans qui possèdent un fichier lisible
			for (Plan p : listePlan) {
				Path path = Path.of(basePath + p.getFichier());
				// temporaire
				// System.out.println("DEBUG plan=" + p.getFichier() + " path=[" + path + "] exists=" + Files.exists(path));
				if (Files.exists(path)) {
					InputStream stream;
					try {
						stream = Files.newInputStream(path);
						Image image = new Image(stream);
						// Crée un canvas de la taille de l'image
						Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
						canvas.getGraphicsContext2D().drawImage(image, 0, 0);
						mapPlanCanvas.put(p.getId(), canvas);
						obsPlan.add(p);
					} catch (IOException e) {
						ctrl.showErreur("Fichier introuvable: " + p.getFichier());
						log.error(p.getFichier());
						cbPlans.getItems().remove(p);
					}
				}
				// Zoom avec CTRL + molette de la souris
				scpCanvas.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
					if (event.isControlDown()) {
						double delta = event.getDeltaY() > 0 ? 0.1 : -0.1;
						zoom = Math.max(zoomMin, Math.min(zoomMax, zoom + delta));
						zoomer();
						event.consume();
					}
				});
			}
			
			cbPlans.setConverter(new StringConverter<Plan>() {
				@Override 
				public String toString(Plan plan) {
					return plan != null ? plan.getNom() : "";
				}
				@Override
				public Plan fromString(String s) {
					return null;
				}
			});
				
			
			// mets la liste observable de la comboBox cbPlans
			cbPlans.setItems(obsPlan);
			
			// Crée une Map qui associe à chaque plan sa liste observable
			// d'éléments
			for (Plan plan : obsPlan) {
				mapPlanElements.put(plan.getId(),
						FXCollections.observableArrayList(facade.getElementsPlan(plan)));
			}

			// Pour les éléments qui ont une localisation,
			// on crée la map qui associe au plan une liste d'ElementView
			for (Plan plan : obsPlan) {
				List<ElementView> nodes = new ArrayList<ElementView>();
				Map<Integer, Piece> elementPieceMap = new HashMap<>();
				//pour chaque élément du plan
				for (Element element : mapPlanElements.get(plan.getId())) {
					//pour chaque élement déjà placé, on crée un ElementView
					if (element.getLocalisation().isPlace())
						nodes.add(creeElementView(element));
					facade.getPieceDeElement(element).ifPresent(p -> elementPieceMap.put(element.getId(), p));
				}
				// pour un plan on associe sa liste d'ElementView
				mapPlanNodes.put(plan.getId(), nodes);
				mapPlanElementPiece.put(plan.getId(), elementPieceMap);
			}
			//Action sur la touche R (rotation) et DEL (suppression)
			stage.getScene().setOnKeyPressed(event -> {
				Element elem = lstElements.getSelectionModel().getSelectedItem();
				if (elem == null || elem.getLocalisation() == null || !elem.getLocalisation().isPlace())
					return;
				//recherche l'ElementView
				var oElementView = mapPlanNodes.get(oPlanCharge.get().getId()).stream()
						.filter(ev -> ev.getElement().getId().equals(elem.getId())).findFirst();

				if (event.getCode() == KeyCode.R) {
					oElementView.get().rotate90();
					event.consume();
				} else if (event.getCode() == KeyCode.DELETE) {
					// supprime l'elementView du pane
					pane.getChildren().remove(oElementView.get());
					// supprime l'elementView de la liste des ElementViews du plan								 
					mapPlanNodes.get(oPlanCharge.get().getId()).remove(oElementView.get());
					//indique que l'élément n'est plus placé																
					oElementView.get().getElement().getLocalisation().setPlace(false);
					elementsModifies.add(elem.getId()); // marque comme modifié
					lstElements.refresh(); // met a jour le style visuel
					event.consume();
				}
			});
			
			// désélectionne les ElementView de l'ancien Plan si on change de plan
			cbPlans.valueProperty().addListener((_, oldV, _) -> {
				if (oldV != null) {
					mapPlanNodes.get(oldV.getId()).forEach(ev -> ev.setSelected(false));

				}
			});

		} catch (InstallationException e) {
			ctrl.showErreur(I18N.getString("err.noInstall"));
		}
	}

	/**
	 * Permet de déplacer un élementView
	 * 
	 * @param elementView
	 */
	private void moveBehaviour(ElementView elementView) {
		// mémorise la différence entre la position de la souris sur la scene et
		// la translation de l'élement sur le pane
		final Delta delta = new Delta();

		elementView.setOnMousePressed(e -> {
			// décalage entre la position de la souris sur la scene et la
			// translation dans le Pane 
			// Multiple par le zomm pour le delta
			delta.x = e.getSceneX() - elementView.getTranslateX();
			delta.y = e.getSceneY() - elementView.getTranslateY();
			elementView.toFront();

			// sélectionne l'élément dans la liste et indirectement selectionne son ElementView dans le pane 
			//==> le déplacement graphique sélectionne l'élément ds la liste
			lstElements.getSelectionModel().select(elementView.getElement());
			e.consume();
			
		});

		elementView.setOnMouseDragged(e -> {
			// Calcule la nouvelle translation
			double x = e.getSceneX() / zoom - delta.x;
			double y = e.getSceneY() / zoom - delta.y;
			

			elementView.setTranslateX(x);
			elementView.setTranslateY(y);

			e.consume();
		});

		elementView.setOnMouseReleased(e -> {

			Element elt = elementView.getElement();
			elt.getLocalisation().setX(elementView.getTranslateX());
			elementsModifies.add(elt.getId()); // marque comme modifié
			elt.getLocalisation().setY(elementView.getTranslateY());

			e.consume();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Défini le look des cellules de la liste d'éléments et le drag and drop vers le Canvas
		lstElements.setCellFactory(_ -> {
			ListCell<Element> cell = new ListCell<>() {
				@Override
				protected void updateItem(Element element, boolean empty) {
					super.updateItem(element, empty);

					if (empty || element == null) {
						setText(null);
						setStyle("");
					} else {
						setText(element.getCode() + ":" + element.getAppareil().getNom());
						if (element.getLocalisation() != null && element.getLocalisation().isPlace()) {
							setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
						} else {
							setStyle("");
						}
					}
				}
			};

			// On Drag
			cell.setOnDragDetected(ev -> {

				Element element = cell.getItem();
				// si l'élément est déjà placé on quitte
				if (element == null || element.getLocalisation().isPlace())
					return;
				//On mémorise l'id de l'élément qu'on veut positionner placer
				Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
				ClipboardContent cc = new ClipboardContent();
				cc.putString(element.getId().toString());// garde l'identifiant de l'élément
				db.setContent(cc);

				ev.consume();
			});

			return cell;
		});
		// Drag Over sur le Pane
		pane.setOnDragOver(ev -> {

			if (ev.getGestureSource() != pane && ev.getDragboard().hasString()) {

				ev.acceptTransferModes(TransferMode.COPY);
			}

			ev.consume();
		});
		// Drop
		pane.setOnDragDropped(ev -> {

			Dragboard db = ev.getDragboard();

			if (!db.hasString())
				return;
			// recherche l'élément que l'on pose sur le dessin à partir de son id dans le DragBoard
			var oElement = lstElements.getItems().stream()
					.filter(elem -> elem.getId().equals(Integer.parseInt(db.getString()))).findFirst();
			if (oElement.isPresent()) {
				// spécifie ses nouvelles coordonnées et crée son ElementView
				Element element = oElement.get();
				element.getLocalisation().setX(ev.getX());
				element.getLocalisation().setY(ev.getY());
				ElementView elementView = creeElementView(element);
				pane.getChildren().add(elementView);// rajoute l'elementView au pane actif
				// ajoute l'elementView aux elements du plan
				mapPlanNodes.get(oPlanCharge.get().getId()).add(elementView);
				element.getLocalisation().setPlace(true);//indique qu'il est placé
				elementsModifies.add(element.getId()); // marque comme modifié
				lstElements.refresh(); // met a jour le style visuel

				elementView.setSelected(true);//sélectionne l'élément que l'on vient de mettre
			}

			ev.setDropCompleted(true);
			ev.consume();
		});

		lstElements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// Pour l'élément selectionné, s'il est placé, on active la boundingBox de son ElementView associé
		lstElements.getSelectionModel().selectedItemProperty().addListener((_, oldElem, newElem) -> {
			if (oldElem != null && oPlanCharge.isPresent()) {
				// recherche son ElementView s'il existe et le désélectionne
				mapPlanNodes.get(oPlanCharge.get().getId()).stream()
						.filter(ev -> ev.getElement().getId().equals(oldElem.getId())).findFirst()
						.ifPresent(ev -> ev.setSelected(false));
			}

			if (newElem != null && oPlanCharge.isPresent()) {
				// recherche son ElementView s'il existe et le sélectionne
				mapPlanNodes.get(oPlanCharge.get().getId()).stream()
						.filter(ev -> ev.getElement().getId().equals(newElem.getId())).findFirst()
						.ifPresent(ev -> ev.setSelected(true));
			}
		});

		// permet de déselectionné l'élément lorsque l'on clique sur le canvas dans une zone sans ElementView
		pane.setOnMouseClicked(ev -> {
			if (ev.getTarget() instanceof Canvas)
				lstElements.getSelectionModel().clearSelection();
		});
		
		cbPieces.setConverter(new javafx.util.StringConverter<Piece>() {
			@Override
			public String toString(Piece piece) {
				return piece == null ? I18N.getString("filtre.toutes.pieces") : piece.getNom();
			}
			@Override
			public Piece fromString(String string) {
				return null;
			}
		});

	}
	
	

	/**
	 * Crée un ElementView à partir d'un élément et le positionne sur le pane s'il est déjà placé 
	 * On lui associe aussi les évènements 
	 * 		de Rotation via les touches CTRL+R
	 * 		de suppression de l'ElementView via Delete (on ne supprime pas l'élément!) 
	 * 		de déplacement sur le pane
	 * 
	 * @param element
	 * @return
	 */
	private ElementView creeElementView(Element element) {
		assert element != null && element.getLocalisation() != null
				: "L'élément ne peut pas être à null et doit avoir une localisation";
		ElementView elementView = new ElementView(element);
		elementView.setTranslateX(element.getLocalisation().getX());
		elementView.setTranslateY(element.getLocalisation().getY());
		moveBehaviour(elementView);
		return elementView;
	}
	
	/**
	 * Crée les Text des noms de pièces pour un plan donné
	 */
	private List<Text> creePieceNames(int planId) {
		List<Text> names = new ArrayList<>();
		// récupère les pièces distinctes de ce plan
		Map<Integer, Piece> pieces = mapPlanElementPiece.get(planId);
		if (pieces == null) return names;
		
		Set<Integer> dejaVu = new HashSet<>();
		for (Piece piece : pieces.values()) {
			if (piece != null && dejaVu.add(piece.getId())) {
				Text txt = new Text(piece.getNom());
				txt.setFont(Font.font("Georgia", javafx.scene.text.FontWeight.BOLD, 16));
				txt.setFill(Color.BLACK);
				txt.setFill(Color.web("#623BFF"));	// Bleu
				// txt.setStroke(Color.WHITE); 		// contour blanc pour la lisibilité
				txt.setStrokeWidth(0.4);
				txt.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);");
				// position : utilise les coordonnées sauvegardées ou (50, 50) par défaut
				double x = piece.getXNom() > 0 ? piece.getXNom() : 20;
				double y = piece.getYNom() > 0 ? piece.getYNom() : 30 + (dejaVu.size() * 25);
				txt.setTranslateX(x);
				txt.setTranslateY(y);
				
				// drag-and-drop pour repositionner le nom
				final Piece p = piece;
				txt.setOnMousePressed(ev -> {
					txt.setUserData(new double[]{
						ev.getSceneX() - txt.getTranslateX(),
						ev.getSceneY() - txt.getTranslateY()
					});
					ev.consume();
				});
				txt.setOnMouseDragged(ev -> {
					double[] delta = (double[]) txt.getUserData();
					txt.setTranslateX(ev.getSceneX() - delta[0]);
					txt.setTranslateY(ev.getSceneY() - delta[1]);
					ev.consume();
				});
				txt.setOnMouseReleased(ev -> {
					// sauvegarde la position dans l'objet Piece
					p.setXNom(txt.getTranslateX());
					p.setYNom(txt.getTranslateY());
					elementsModifies.add(-p.getId());	// négatif pour distinguer des éléments
					ev.consume();
				});
				
				txt.setCursor(javafx.scene.Cursor.MOVE);
				names.add(txt);
			}
		}
		return names;
	}
	
	/**
	 * Applique le zoom sur le plan
	 */
	private void zoomer() {
		pane.setScaleX(zoom);
		pane.setScaleY(zoom);
	}

}

//Utilisé pour le déplacement d'un ElementView
class Delta {
	double x;
	double y;
};

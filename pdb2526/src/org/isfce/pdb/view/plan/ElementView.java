package org.isfce.pdb.view.plan;

import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Svg;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import lombok.Getter;
import lombok.ToString;

/**
 * Représentation graphique de l'élément SVG avec son texte
 */
@ToString(exclude = { "svgPath", "svg" })
public class ElementView extends Group {
	private static final Color COLOR_ELEMENT = Color.BLACK;
	private static final Color COLOR_TEXT = Color.NAVY;
	private static final Color COLOR_BOX_ON = Color.LIGHTCORAL;
	private static final int SVG_SIZE = 35;
	private static final double SVG_LINE_WIDTH = 0.1;
	private static final double BOX_LINE_WIDTH = 0.3;
	private static final Font TEXT_FONT_SIZE = Font.font("Verdana", 10);

	@Getter
	private final Element element;
	private final SVGPath svgPath;// objet graphique
	private final Svg svg;// objet svg métier
	private final Rectangle box;
	private final Text text;
	private boolean selected = false;

	private final Rotate rotate;

	/**
	 * Création d'un ElementView à partir d'un Element
	 * Spécifie son SVG, sa boundingBox lors d'une sélection et le texte
	 * spécifie le pivot de rotation
	 * @param element
	 */
	public ElementView(Element element) {
		assert element != null : " L'élément ne peut pas être à null";
		//pour la transformation sur base de l'angle et du pivot de rotation
		rotate = new Rotate(0, 0, 0);

		this.element = element;
		//Création du SVG
		this.svgPath = new SVGPath();
		this.svg = this.element.getAppareil().getSvg();
		double svgScale = svg.getScale(SVG_SIZE);
		this.svgPath.setContent(svg.svg());
		this.svgPath.setStrokeWidth(SVG_LINE_WIDTH);
		this.svgPath.setFill(COLOR_ELEMENT);
		this.svgPath.setStroke(COLOR_ELEMENT);
		this.svgPath.setScaleX(svgScale);
		this.svgPath.setScaleY(svgScale);
		//création du rectangle de sélection autour du SVG
		this.box = new Rectangle(this.svg.x(), this.svg.y(), this.svg.width(), this.svg.height());
		this.box.setScaleX(svgScale);
		this.box.setScaleY(svgScale);
		this.box.setFill(Color.TRANSPARENT);
		this.box.setStroke(Color.TRANSPARENT);
		this.box.setStrokeWidth(BOX_LINE_WIDTH);

		rotate.setPivotX(svgPath.getLayoutX());
		rotate.setPivotY(svgPath.getLayoutY() / 2);

		//Place le texte en haut de la Box 
		Bounds b = box.getBoundsInParent();
		this.text = new Text(element.getCode());
		this.text.setFont(TEXT_FONT_SIZE);
		this.text.setFill(COLOR_TEXT);
		this.text.setX(b.getCenterX());
		this.text.setY(b.getMinY() - 5);

		getChildren().addAll(svgPath, box, text);
		getTransforms().add(rotate);

		//s'il est déjà placé on précise son angle initial
		if (element.getLocalisation().isPlace())
			rotate.setAngle(element.getLocalisation().getAngle());

	}

	/**
	 * Rotation par 90°
	 * Adapte la transformation, ne pas appeler directement setRotation
	 */
	public void rotate90() {
		double angle = (rotate.getAngle() + 90) % 360;
		rotate.setAngle(angle);
		element.getLocalisation().setAngle(angle);
	}

	/**
	 * Permet d'afficher ou non la BoundingBox qui visualise l'objet sélectionné
	 */
	public void setSelected(boolean selected) {
		if (this.selected == selected)
			return;
		if (selected)
			box.setStroke(COLOR_BOX_ON);
		else
			box.setStroke(Color.TRANSPARENT);
		this.selected = selected;
	}

}

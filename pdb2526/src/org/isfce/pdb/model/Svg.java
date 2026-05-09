package org.isfce.pdb.model;

import java.io.Serializable;

/**
 * @author Didier
 * width et height tjs positifs
 */
public record Svg(String id,String svg, double x, double y, double width, double height) {
	/**
	 * calcule le rapport à avoir pour obtenir l'image sur une taille "size"
	 * 
	 * @param size (Le rapport sera précisé via la largeur)
	 * @return rappport
	 */
	public double getScale(int size) {
		// Au cas ou la haut et larg =0 ==> retour 1
		if (height + width == 0)
			return 1;
		return size / width;// Math.max(width, height);
	}

	public double getHeightForScale(double scale) {
		return height * scale;
	}

	public double getWidthForScale(double scale) {
		return width * scale;
	}
}

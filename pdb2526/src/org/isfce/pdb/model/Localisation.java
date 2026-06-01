package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Représente la position d'un élément sur un plan 
 * x, y : position sur le plan
 * a : angle de rotation (0 par défaut = horizontal)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Localisation {
	private double x;
	private double y;
	private double a;
}

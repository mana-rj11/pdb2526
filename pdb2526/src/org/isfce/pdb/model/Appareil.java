package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "svg")
@AllArgsConstructor
@EqualsAndHashCode
public final class Appareil {
	public enum Classe {
		APPAREIL, DISJONCTEUR, DISJONCTEURDIFF, INTERRUPTEUR, PRISE, 
		LAMPE, COMPTEUR, TELERUPTEUR, BOUTON,APPAREILLAGE,TRANSFORMATEUR;		
	};

	private final String code;
	private final String nom;
	private final Svg svg;
	private final Classe classe;
}

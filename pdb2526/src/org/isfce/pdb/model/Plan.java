package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Plan {
	private final int id;
	private final String nom;
	private final String fichier;
    private final int etage; // etage ajouté


}

package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * Clé composite pour TLOCALISATION
 * FKELEMENT_LOC + FKPIECE_LOC
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LocID {
	private final int idElement;
	private final int idPiece;
}

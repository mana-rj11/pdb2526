package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TypePiece {
	private final String code;
	private final String nom;
	private final boolean humide;
}

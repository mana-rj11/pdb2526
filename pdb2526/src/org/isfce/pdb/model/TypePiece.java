package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TypePiece implements Cloneable {
	private final String code;
	private final String nom;
	private final boolean humide;
	
	@Override
	public TypePiece clone() {
		try {
			return (TypePiece) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException (e);
		}
	}
}

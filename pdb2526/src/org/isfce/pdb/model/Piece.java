package org.isfce.pdb.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
@Builder
@ToString(exclude = {"typePiece","installation","description"})
@EqualsAndHashCode
@Getter
public class Piece {
	private Integer id;
	private String nom;
	private String description;
	private BigDecimal etage;
	private TypePiece typePiece;
	private final Integer installation;
}

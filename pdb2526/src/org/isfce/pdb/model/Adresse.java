package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Didier
 */
@Data
@AllArgsConstructor
public class Adresse {
	private String rue;
	private int cp;
	private String ville;
}

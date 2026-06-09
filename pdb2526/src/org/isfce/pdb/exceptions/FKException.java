package org.isfce.pdb.exceptions;

import lombok.Getter;

/**
 * Exception levée lors d'une violation de clé étrangère 
 * Code Firebird : 335544466
 */
public class FKException extends InstallationException {
	
	private static final long serialVersionUID = 1L;
	
	@Getter 
	private final String champ;
	
	public FKException(String message, String champ) {
		super(message);
		this.champ = champ;
	}
}

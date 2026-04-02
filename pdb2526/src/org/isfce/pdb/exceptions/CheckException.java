package org.isfce.pdb.exceptions;

import lombok.Getter;

public class CheckException extends InstallationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	private final String champ;

	public CheckException(String message, String champ) {
		super(message);
		this.champ = champ;
	}
}

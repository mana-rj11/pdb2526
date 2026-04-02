package org.isfce.pdb.exceptions;

import lombok.Getter;

public class PKException extends InstallationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	private final String champ;

	public PKException(String message, String champ) {
		super(message);
		this.champ = champ;
	}
}

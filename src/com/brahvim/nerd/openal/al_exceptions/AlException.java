package com.brahvim.nerd.openal.al_exceptions;

import org.lwjgl.openal.AL10;

public class AlException extends NerdAbstractOpenAlException {

	private static final long serialVersionUID = -565443463464L;

	public AlException(final int p_alErrorCode) {
		super(AL10.alGetString(p_alErrorCode), p_alErrorCode);
	}

}

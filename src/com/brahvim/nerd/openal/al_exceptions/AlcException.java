package com.brahvim.nerd.openal.al_exceptions;

import org.lwjgl.openal.ALC10;

/**
 * Thrown when `alcGetError()` checks report an error.
 */
public class AlcException extends NerdAbstractOpenAlException {

	public AlcException(final long p_deviceId, final int p_alErrorCode) {
		super(ALC10.alcGetString(p_deviceId, ALC10.alcGetError(p_alErrorCode)), p_alErrorCode);
	}

}

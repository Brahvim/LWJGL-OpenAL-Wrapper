package com.brahvim.nerd.openal.al_exceptions;

/**
 * Thrown when:
 * - An error occurs within this library.
 * - An error reported by OpenAL using return values occurs.
 * - An error not reported by any OpenAL {@code *GetError()} functions occurs.
 */
public class AlUnflaggedException extends RuntimeException {

	public AlUnflaggedException(final String p_message) {
		super(p_message);
	}

}

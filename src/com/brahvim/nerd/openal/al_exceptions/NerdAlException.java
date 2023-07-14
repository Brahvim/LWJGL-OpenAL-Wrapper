package com.brahvim.nerd.openal.al_exceptions;

/**
 * Thrown when:
 * - An error occurs within this library.
 * - An error not reported by any of OpenAL's `*GetError()` functions occurs.
 */
public class NerdAlException extends RuntimeException {

	public NerdAlException(final String p_message) {
		super(p_message);
	}

}

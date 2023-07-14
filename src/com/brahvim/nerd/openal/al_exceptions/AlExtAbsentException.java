package com.brahvim.nerd.openal.al_exceptions;

/**
 * Thrown when an OpenAL extension required by the library is absent in the
 * OpenAL implementation. Should not occur if you use OpenAL Soft.
 */
public class AlExtAbsentException extends RuntimeException {

	public AlExtAbsentException(final String p_extName, final String p_problem) {
		super("Issue with extension " + p_extName + " : " + p_problem);
	}

	public AlExtAbsentException(final String p_extName) {
		super("OpenAL Extension \"" + p_extName + "\" does not exist!");
	}

}

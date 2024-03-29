package com.brahvim.nerd.openal.al_exceptions;

import org.lwjgl.openal.AL10;

/**
 * Thrown when {@code alGetError()} checks report an error.
 */
public class AlException extends AbstractAlException {

    public AlException(final int p_alErrorCode) {
        super(AL10.alGetString(p_alErrorCode), p_alErrorCode);
    }

}

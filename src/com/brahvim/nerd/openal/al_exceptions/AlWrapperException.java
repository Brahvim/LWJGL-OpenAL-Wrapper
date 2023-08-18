package com.brahvim.nerd.openal.al_exceptions;

import com.brahvim.nerd.openal.objects.NerdAl;

/**
 * Wrapper objects can be constructed by giving them a {@link NerdAl} instance,
 * and a native object's ID. This exception is thrown when said ID is invalid.
 */
public class AlWrapperException extends RuntimeException {

}

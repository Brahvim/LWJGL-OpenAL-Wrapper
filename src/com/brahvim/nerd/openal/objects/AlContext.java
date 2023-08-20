package com.brahvim.nerd.openal.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_exceptions.AlException;
import com.brahvim.nerd.openal.al_exceptions.AlUnflaggedException;
import com.brahvim.nerd.openal.al_exceptions.AlWrapperException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;

public class AlContext extends AlNativeResource<Long> {

    // region Fields.
    protected static final Vector<AlContext> ALL_INSTANCES = new Vector<>(1);

    // protected final ALCapabilities AL_CAPABILITIES;
    // protected final ALCCapabilities ALC_CAPABILITIES;
    // endregion

    // region Constructors.
    public AlContext(final NerdAl p_alMan) {
        this(p_alMan, new AlContextSettings());
    }

    public AlContext(final NerdAl p_alMan, final long p_id) throws AlWrapperException {
        super(p_alMan);
        super.id = p_id;

        final long currentContextId = ALC10.alcGetCurrentContext();

        try {
            if (!ALC10.alcMakeContextCurrent(p_id)) {
                throw new AlWrapperException();
            }
        } finally {
            ALC10.alcMakeContextCurrent(currentContextId);
        }

        // this.ALC_CAPABILITIES = ALC.createCapabilities(this.getDevice().getId());
        // this.AL_CAPABILITIES = AL.createCapabilities(this.ALC_CAPABILITIES);

        AlContext.ALL_INSTANCES.add(this);
    }

    public AlContext(final NerdAl p_alMan, final AlContextSettings p_settings) {
        super(p_alMan);
        super.id = this.createNativeContext(p_settings);

        // this.ALC_CAPABILITIES = ALC.createCapabilities(this.getDevice().getId());
        // this.AL_CAPABILITIES = AL.createCapabilities(this.ALC_CAPABILITIES);

        AlContext.ALL_INSTANCES.add(this);
    }

    protected long createNativeContext(AlContextSettings p_settings) {
        // Finally, a `null` check!:

        if (p_settings == null) {
            // System.err.println(
            // "`AlContext::AlContext(NerdAl, AlContext.AlContextSettings)` received a
            // `null` settings object.");
            p_settings = new AlContextSettings();
        }

        final long toRet = ALC10.alcCreateContext(this.getDevice().getId(), p_settings.asAttribArray());
        // ^^^ `toRet` will be `0` if the call fails.
        this.checkAlcError();

        // Placing the check into a boolean to check for errors right away!
        final boolean ctxVerifiedStatus = ALC10.alcMakeContextCurrent(toRet);
        this.checkAlcError();

        if (toRet == 0 || !ctxVerifiedStatus) {
            super.dispose();
        }

        return toRet;
    }
    // endregion

    // region Instance collection queries.
    public static int getNumInstances() {
        return AlContext.ALL_INSTANCES.size();
    }

    public static List<AlContext> getAllInstances() {
        return new ArrayList<>(AlContext.ALL_INSTANCES);
    }
    // endregion

    // region Getters.
    public AlDevice getDevice() {
        return super.MAN.getDevice();
    }

    // public ALCapabilities getLwjglAlCapabilities() {
    // return this.AL_CAPABILITIES;
    // }

    // public ALCCapabilities getLwjglAlcCapabilities() {
    // return this.ALC_CAPABILITIES;
    // }
    // endregion

    // region OpenAL listener manipulation.
    // region C-style OpenAL listener getters.
    public int getListenerInt(final int p_alEnum) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return Integer.MIN_VALUE;
        }
        return AL10.alGetListeneri(p_alEnum);
    }

    public float getListenerFloat(final int p_alEnum) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return -Float.MAX_VALUE;
        }
        return AL10.alGetListenerf(p_alEnum);
    }

    // Vectors in OpenAL are not large and can be allocated on the stack just fine.
    public int[] getListenerIntVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new int[0];
        }
        AL11.alGetListeneriv(p_alEnum, intBuffer);
        MemoryStack.stackPop();

        return intBuffer.array();
    }

    public float[] getListenerFloatVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(p_vecSize);
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new float[0];
        }
        AL10.alGetListenerfv(p_alEnum, floatBuffer);
        MemoryStack.stackPop();

        return floatBuffer.array();
    }

    public int[] getListenerIntTriplet(final int p_alEnum) {
        MemoryStack.stackPush();
        final IntBuffer intBuffer = MemoryStack.stackMallocInt(3);
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new int[0];
        }
        AL11.alGetListeneriv(p_alEnum, intBuffer);
        MemoryStack.stackPop();

        return intBuffer.array();
    }

    public float[] getListenerFloatTriplet(final int p_alEnum) {
        MemoryStack.stackPush();
        final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new float[0];
        }
        AL10.alGetListenerfv(p_alEnum, floatBuffer);
        MemoryStack.stackPop();

        return floatBuffer.array();
    }
    // endregion

    // region C-style OpenAL listener setters.
    public void setListenerInt(final int p_alEnum, final int p_value) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListeneri(p_alEnum, p_value);
        super.MAN.checkAlError();
    }

    public void setListenerFloat(final int p_alEnum, final float p_value) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListenerf(p_alEnum, p_value);
        super.MAN.checkAlError();
    }

    public void setListenerIntVector(final int p_alEnum, final int... p_values) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL11.alListeneriv(p_alEnum, p_values);
        super.MAN.checkAlError();
    }

    public void setListenerFloatVector(final int p_alEnum, final float... p_values) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListenerfv(p_alEnum, p_values);
        super.MAN.checkAlError();
    }

    public void setListenerIntTriplet(final int p_alEnum, final int... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`AlSource::setIntTriplet(AlContext p_ctx, )` cannot take an array of size other than `3`!");
        }

        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL11.alListener3i(p_alEnum, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
    }

    public void setListenerIntTriplet(final int p_alEnum, final int p_i1, final int p_i2,
            final int p_i3) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL11.alListener3i(p_alEnum, p_i1, p_i2, p_i3);
        super.MAN.checkAlError();
    }

    public void setListenerFloatTriplet(final int p_alEnum, final float... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`AlSource::setFloatTriplet()` cannot take an array of a size other than `3`!");
        }

        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListener3f(p_alEnum, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
    }

    public void setListenerFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListener3f(p_alEnum, p_f1, p_f2, p_f3);
        super.MAN.checkAlError();
    }
    // endregion

    // region Listener getters.
    public float getListenerMetersPerUnit() {
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return -Float.MAX_VALUE;
        }
        return AL10.alGetListenerf(EXTEfx.AL_METERS_PER_UNIT);
    }

    public float getListenerGain() {
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return -Float.MAX_VALUE;
        }
        return AL10.alGetListenerf(AL10.AL_GAIN);
    }

    public float[] getListenerPosition() {
        // MemoryStack.stackPush();
        final float[] floatArray = new float[3];
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new float[0];
        }
        AL10.alGetListenerfv(AL10.AL_POSITION, floatArray);
        // MemoryStack.stackPop();

        return floatArray;
        // return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
    }

    public float[] getListenerVelocity() {
        // MemoryStack.stackPush();
        final float[] floatArray = new float[3];
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new float[0];
        }
        AL10.alGetListenerfv(AL10.AL_VELOCITY, floatArray);
        // MemoryStack.stackPop();

        return floatArray;
        // return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
    }

    public float[] getListenerOrientation() {
        // MemoryStack.stackPush();
        final float[] floatArray = new float[3];
        super.MAN.makeContextCurrent();
        this.checkAlcError();

        if (this.hasDisposed) {
            return new float[0];
        }
        AL10.alGetListenerfv(AL10.AL_ORIENTATION, floatArray);
        // MemoryStack.stackPop();

        return floatArray;
        // return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
    }
    // endregion

    // region Listener setters.
    public void setListenerGain(final float p_value) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListenerf(AL10.AL_GAIN, p_value);
        super.MAN.checkAlError();
    }

    public void setMetersPerUnit(final float p_value) {
        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListenerf(EXTEfx.AL_METERS_PER_UNIT, p_value);
        super.MAN.checkAlError();
    }

    // region `float...` overloads for listener vectors.
    public void setListenerPosition(final float... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`AlSource::setListenerPosition()` cannot take an array of a size other than `3`!");
        }

        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListener3f(AL10.AL_POSITION, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
    }

    public void setListenerVelocity(final float... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`AlSource::setListenerVelocity()` cannot take an array of a size other than `3`!");
        }

        super.MAN.makeContextCurrent();
        this.checkAlcError();
        AL10.alListener3f(AL10.AL_VELOCITY, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
    }

    public void setListenerOrientation(final float... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`AlSource::setListenerOrientation()` cannot take an array of a size other than `3`!");
        }

        final float[] values = new float[3];

        // The usual case is for `y` to be `1`, and the rest to be `0`.
        // This should keep that logic significantly fast:
        values[0] = p_values[0] == 0.0f ? 0.0f : p_values[0] > 0.0f ? 1.0f : -1.0f;
        values[1] = p_values[1] == 0.0f ? 0.0f : p_values[1] > 0.0f ? 1.0f : -1.0f;
        values[2] = p_values[2] == 0.0f ? 0.0f : p_values[2] > 0.0f ? 1.0f : -1.0f;

        try { // Need to put this in a try-catch block...
            this.setListenerFloatVector(AL10.AL_ORIENTATION, values);
        } catch (final AlException e) { // TOO MANY FALSE POSITIVES!
            // System.out.printf("""
            // Setting the listener's orientation to `%s` failed!
            // Values: `[%.7f, %.7f, %.7f]`.""", p_value, values[0], values[1], values[2]);
        }
    }
    // endregion
    // endregion
    // endregion

    public int checkAlcError() {
        final int errorCode = ALC10.alcGetError(this.getDevice().getId());

        if (errorCode != 0) {
            throw new AlcException(this.getDevice().getId(), errorCode);
        }

        return errorCode;
    }

    @Override
    protected void disposeImpl() {
        // Unlink the current context object:
        if (!ALC10.alcMakeContextCurrent(0)) {
            throw new AlUnflaggedException("Could not change the OpenAL context (whilst disposing one)!");
        }

        this.checkAlcError();

        // *Actually* destroy the context object:
        ALC10.alcDestroyContext(super.id);

        this.checkAlcError();
        AlContext.ALL_INSTANCES.remove(this);
    }

}

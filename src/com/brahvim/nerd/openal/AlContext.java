package com.brahvim.nerd.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_exceptions.AlException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.al_exceptions.NerdAlException;

public class AlContext extends AlNativeResource<Long> {

	public static class AlContextSettings {

		// OpenAL default values:
		public int frequency = 44100, monoSources = 32, stereoSources = 8, refresh = 40;
		public boolean sync; // `false` by default for OpenAL.

		public int[] asAttribArray() {
			return new int[] {
					ALC10.ALC_FREQUENCY, this.frequency,
					ALC11.ALC_MONO_SOURCES, this.monoSources,
					ALC11.ALC_STEREO_SOURCES, this.stereoSources,
					ALC10.ALC_REFRESH, this.refresh,
					ALC10.ALC_SYNC, this.sync ? ALC10.ALC_TRUE : ALC10.ALC_FALSE
			};
		}

	}

	// region Fields.
	protected static final Vector<AlContext> ALL_INSTANCES = new Vector<>();

	private final long deviceId;
	private final AlDevice device;
	private final ArrayList<AlBuffer<?>> buffers;
	// endregion

	// region Constructors.
	public AlContext(final NerdAl p_alMan) {
		this(p_alMan, new AlContext.AlContextSettings());
	}

	public AlContext(final NerdAl p_alMan, final AlContext.AlContextSettings p_settings) {
		super(p_alMan);

		this.buffers = new ArrayList<>();
		this.device = p_alMan.getDevice();
		this.deviceId = this.device.getId();
		super.id = this.createCtx(p_settings);

		AlContext.ALL_INSTANCES.add(this);
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlContext.ALL_INSTANCES.size();
	}

	public static ArrayList<AlContext> getAllInstances() {
		return new ArrayList<>(AlContext.ALL_INSTANCES);
	}
	// endregion

	// region Getters.
	public AlDevice getDevice() {
		return this.device;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AlBuffer<?>> getBuffers() {
		return (ArrayList<AlBuffer<?>>) this.buffers.clone();
	}
	// endregion

	// region OpenAL listener manipulation.
	// region C-style OpenAL listener getters.
	public int getListenerInt(final long p_ctxId, final int p_alEnum) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return Integer.MIN_VALUE;
		return AL10.alGetListeneri(p_alEnum);
	}

	public float getListenerFloat(final long p_ctxId, final int p_alEnum) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(p_alEnum);
	}

	// Vectors in OpenAL are not large and can be allocated on the stack just fine.
	public int[] getListenerIntVector(final long p_ctxId, final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return new int[0];
		AL11.alGetListeneriv(p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public float[] getListenerFloatVector(final long p_ctxId, final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(p_vecSize);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(p_alEnum, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
	}

	public int[] getListenerIntTriplet(final long p_ctxId, final int p_alEnum) {
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(3);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return new int[0];
		AL11.alGetListeneriv(p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public float[] getListenerFloatTriplet(final long p_ctxId, final int p_alEnum) {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(p_alEnum, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}
	// endregion

	// region C-style OpenAL listener setters.
	public void setListenerInt(final long p_ctxId, final int p_alEnum, final int p_value) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListeneri(p_alEnum, p_value);
		super.MAN.checkAlError();
	}

	public void setListenerFloat(final long p_ctxId, final int p_alEnum, final float p_value) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListenerf(p_alEnum, p_value);
		super.MAN.checkAlError();
	}

	public void setListenerIntVector(final long p_ctxId, final int p_alEnum, final int... p_value) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListeneriv(p_alEnum, p_value);
		super.MAN.checkAlError();
	}

	public void setListenerFloatVector(final long p_ctxId, final int p_alEnum, final float... p_values) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListenerfv(p_alEnum, p_values);
		super.MAN.checkAlError();
	}

	public void setListenerIntTriplet(final long p_ctxId, final int p_alEnum, final int... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setIntTriplet(AlContext p_ctx, )` cannot take an array of size other than `3`!");

		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListener3i(p_alEnum, p_value[0], p_value[1], p_value[2]);
		super.MAN.checkAlError();
	}

	public void setListenerIntTriplet(final long p_ctxId, final int p_alEnum, final int p_i1, final int p_i2,
			final int p_i3) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListener3i(p_alEnum, p_i1, p_i2, p_i3);
		super.MAN.checkAlError();
	}

	public void setListenerFloatTriplet(final long p_ctxId, final int p_alEnum, final float... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setFloatTriplet()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListener3f(p_alEnum, p_value[0], p_value[1], p_value[2]);
		super.MAN.checkAlError();
	}

	public void setListenerFloatTriplet(final long p_ctxId, final int p_alEnum, final float p_f1, final float p_f2,
			final float p_f3) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListener3f(p_alEnum, p_f1, p_f2, p_f3);
		super.MAN.checkAlError();
	}
	// endregion

	// region Default listener getters.
	public float getListenerMetersPerUnit() {
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();

		if (this.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(EXTEfx.AL_METERS_PER_UNIT);
	}

	public float getListenerGain() {
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();

		if (this.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(AL10.AL_GAIN);
	}

	public float[] getListenerPosition() {
		// MemoryStack.stackPush();
		final float[] floatArray = new float[3];
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();

		if (this.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_POSITION, floatArray);
		// MemoryStack.stackPop();

		return floatArray;
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}

	public float[] getListenerVelocity() {
		// MemoryStack.stackPush();
		final float[] floatArray = new float[3];
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();

		if (this.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_VELOCITY, floatArray);
		// MemoryStack.stackPop();

		return floatArray;
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}

	public float[] getListenerOrientation() {
		// MemoryStack.stackPush();
		final float[] floatArray = new float[3];
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();

		if (this.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_ORIENTATION, floatArray);
		// MemoryStack.stackPop();

		return floatArray;
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}
	// endregion

	// region Default listener setters.
	public void setListenerGain(final float p_value) {
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();
		AL10.alListenerf(AL10.AL_GAIN, p_value);
		super.MAN.checkAlError();
	}

	public void setMetersPerUnit(final float p_value) {
		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();
		AL10.alListenerf(EXTEfx.AL_METERS_PER_UNIT, p_value);
		super.MAN.checkAlError();
	}

	// region `float...` overloads for listener vectors.
	public void setListenerPosition(final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setListenerPosition()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();
		AL10.alListener3f(AL10.AL_POSITION, p_values[0], p_values[1], p_values[2]);
		super.MAN.checkAlError();
	}

	public void setListenerVelocity(final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setListenerVelocity()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(super.id);
		this.checkAlcError();
		AL10.alListener3f(AL10.AL_VELOCITY, p_values[0], p_values[1], p_values[2]);
		super.MAN.checkAlError();
	}

	public void setListenerOrientation(final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setListenerOrientation()` cannot take an array of a size other than `3`!");

		final float[] values = new float[3];

		// The usual case is for `y` to be `1`, and the rest to be `0`.
		// This should keep that logic significantly fast:
		values[0] = p_values[0] == 0.0f ? 0.0f : p_values[0] > 0.0f ? 1.0f : -1.0f;
		values[1] = p_values[1] == 0.0f ? 0.0f : p_values[1] > 0.0f ? 1.0f : -1.0f;
		values[2] = p_values[2] == 0.0f ? 0.0f : p_values[2] > 0.0f ? 1.0f : -1.0f;

		try { // Need to put this in a try-catch block...
			this.setListenerFloatVector(super.id, AL10.AL_ORIENTATION, values);
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
		final int errorCode = ALC10.alcGetError(this.device.getId());

		if (errorCode != 0)
			throw new AlcException(this.deviceId, errorCode);

		return errorCode;
	}

	private long createCtx(AlContext.AlContextSettings p_settings) {
		// Finally, a `null` check!:

		if (p_settings == null) {
			// System.err.println(
			// "`AlContext(NerdAl, AlContextSettings)` received a `null` settings object.");
			p_settings = new AlContextSettings();
		}

		final long toRet = ALC10.alcCreateContext(this.deviceId, p_settings.asAttribArray());
		super.MAN.checkAlcError();

		// Placing the check into a boolean to check for errors right away!
		final boolean ctxVerifStatus = ALC10.alcMakeContextCurrent(toRet);
		super.MAN.checkAlcError();

		if (toRet == 0 || !ctxVerifStatus)
			super.dispose();

		return toRet;
	}

	@Override
	protected void disposeImpl() {
		// Unlink the current context object:
		if (!ALC10.alcMakeContextCurrent(0))
			throw new NerdAlException(
					"Could not change the OpenAL context (whilst disposing one)!");

		super.MAN.checkAlcError();

		// *Actually* destroy the context object:
		ALC10.alcDestroyContext(super.id);

		super.MAN.checkAlcError();
		AlContext.ALL_INSTANCES.remove(this);
	}

}

package com.brahvim.nerd.openal;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_buffers.AlOggBuffer;
import com.brahvim.nerd.openal.al_exceptions.AlException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.al_exceptions.NerdAbstractOpenAlException;

public class NerdAl {

	// region Fields.
	public static final float UNIT_SIZE_3D_PARK_SCENE = 100.0f;
	public static final float UNIT_SIZE_MOUSE_RELATIVE = 1.0f;
	public static final float UNIT_SIZE_2D_SCENE = 25.0f;

	public final long DEFAULT_CONTEXT_ID;
	public final AlContext DEFAULT_CONTEXT;

	public float unitSize = NerdAl.UNIT_SIZE_3D_PARK_SCENE;

	private ALCapabilities alCap;
	private ALCCapabilities alCtxCap;
	private /* `volatile` */ AlDevice device;
	private /* `volatile` */ AlContext context;
	// endregion

	// region Construction.
	public NerdAl() {
		this(AlDevice.getDefaultDeviceName());
	}

	public NerdAl(final String p_deviceName) {
		this.DEFAULT_CONTEXT = this.createAl(p_deviceName);
		this.DEFAULT_CONTEXT_ID = this.DEFAULT_CONTEXT.getId();
	}

	public NerdAl(final AlContext.AlContextSettings p_settings) {
		this.DEFAULT_CONTEXT = this.createAl(AlDevice.getDefaultDeviceName(), p_settings);
		this.DEFAULT_CONTEXT_ID = this.DEFAULT_CONTEXT.getId();
	}

	public NerdAl(final AlContext.AlContextSettings p_settings, final String p_deviceName) {
		this.DEFAULT_CONTEXT = this.createAl(p_deviceName, p_settings);
		this.DEFAULT_CONTEXT_ID = this.DEFAULT_CONTEXT.getId();
	}
	// endregion

	// region Listener functions.
	// region C-style OpenAL listener getters.
	public int getListenerInt(final long p_ctxId, final int p_alEnum) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return Integer.MIN_VALUE;
		return AL10.alGetListeneri(p_alEnum);
	}

	public float getListenerFloat(final long p_ctxId, final int p_alEnum) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(p_alEnum);
	}

	// Vectors in OpenAL are not large and can be allocated on the stack just fine.
	public int[] getListenerIntVector(final long p_ctxId, final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.context.hasDisposed)
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

		if (this.context.hasDisposed)
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

		if (this.context.hasDisposed)
			return new int[0];
		AL11.alGetListeneriv(p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public /* `float[]` */ float[] getListenerFloatTriplet(final long p_ctxId, final int p_alEnum) {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();

		if (this.context.hasDisposed)
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
		this.checkAlError();
	}

	public void setListenerFloat(final long p_ctxId, final int p_alEnum, final float p_value) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListenerf(p_alEnum, p_value);
		this.checkAlError();
	}

	public void setListenerIntVector(final long p_ctxId, final int p_alEnum, final int... p_value) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListeneriv(p_alEnum, p_value);
		this.checkAlError();
	}

	public void setListenerFloatVector(final long p_ctxId, final int p_alEnum, final float... p_values) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListenerfv(p_alEnum, p_values);
		this.checkAlError();
	}

	public void setListenerIntTriplet(final long p_ctxId, final int p_alEnum, final int... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setIntTriplet(AlContext p_ctx, )` cannot take an array of size other than `3`!");

		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListener3i(p_alEnum, p_value[0], p_value[1], p_value[2]);
		this.checkAlError();
	}

	public void setListenerIntTriplet(final long p_ctxId, final int p_alEnum, final int p_i1, final int p_i2,
			final int p_i3) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL11.alListener3i(p_alEnum, p_i1, p_i2, p_i3);
		this.checkAlError();
	}

	public void setListenerFloatTriplet(final long p_ctxId, final int p_alEnum, final float... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setFloatTriplet()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListener3f(p_alEnum, p_value[0], p_value[1], p_value[2]);
		this.checkAlError();
	}

	public void setListenerFloatTriplet(final long p_ctxId, final int p_alEnum, final float p_f1, final float p_f2,
			final float p_f3) {
		ALC10.alcMakeContextCurrent(p_ctxId);
		this.checkAlcError();
		AL10.alListener3f(p_alEnum, p_f1, p_f2, p_f3);
		this.checkAlError();
	}
	// endregion

	// region Default listener getters.
	public float getListenerMetersPerUnit() {
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(EXTEfx.AL_METERS_PER_UNIT);
	}

	public float getListenerGain() {
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return -Float.MAX_VALUE;
		return AL10.alGetListenerf(AL10.AL_GAIN);
	}

	public float[] getListenerPosition() {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_POSITION, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}

	public float[] getListenerVelocity() {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_VELOCITY, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}

	public float[] getListenerOrientation() {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();

		if (this.context.hasDisposed)
			return new float[0];
		AL10.alGetListenerfv(AL10.AL_ORIENTATION, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}
	// endregion

	// region Default listener setters.
	public void setListenerGain(final float p_value) {
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();
		AL10.alListenerf(AL10.AL_GAIN, p_value);
		this.checkAlError();
	}

	public void setMetersPerUnit(final float p_value) {
		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();
		AL10.alListenerf(EXTEfx.AL_METERS_PER_UNIT, p_value);
		this.checkAlError();
	}

	// region `float...` overloads for listener vectors.
	public void setListenerPosition(final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setListenerPosition()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();
		AL10.alListener3f(AL10.AL_POSITION, p_values[0], p_values[1], p_values[2]);
		this.checkAlError();
	}

	public void setListenerVelocity(final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setListenerVelocity()` cannot take an array of a size other than `3`!");

		ALC10.alcMakeContextCurrent(this.DEFAULT_CONTEXT_ID);
		this.checkAlcError();
		AL10.alListener3f(AL10.AL_VELOCITY, p_values[0], p_values[1], p_values[2]);
		this.checkAlError();
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
			this.setListenerFloatVector(this.DEFAULT_CONTEXT_ID, AL10.AL_ORIENTATION, values);
		} catch (final AlException e) { // TOO MANY FALSE POSITIVES!
			// System.out.printf("""
			// Setting the listener's orientation to `%s` failed!
			// Values: `[%.7f, %.7f, %.7f]`.""", p_value, values[0], values[1], values[2]);
		}
	}
	// endregion
	// endregion
	// endregion

	// region Getters and setters!...
	// region C-style OpenAL getters.
	public int getAlInt(final int p_alEnum) {
		final int toRet = AL10.alGetInteger(p_alEnum);
		this.checkAlError();
		return toRet;
	}

	public float getAlFloat(final int p_alEnum) {
		final float toRet = AL10.alGetFloat(p_alEnum);
		this.checkAlError();
		return toRet;
	}

	public int[] getAlIntVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(p_vecSize);
		AL10.alGetIntegerv(p_alEnum, buffer);
		MemoryStack.stackPop();

		this.checkAlError();
		return buffer.array();
	}

	public float[] getAlFloatVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final FloatBuffer buffer = MemoryStack.stackMallocFloat(p_vecSize);
		AL10.alGetFloatv(p_alEnum, buffer);
		MemoryStack.stackPop();

		this.checkAlError();
		return buffer.array();
	}
	// endregion

	// region Getters.
	// region OpenAL API getters.
	public float getDistanceModel() {
		return this.getAlFloat(AL10.AL_DISTANCE_MODEL);
	}

	public float getDopplerFactor() {
		return this.getAlFloat(AL10.AL_DOPPLER_FACTOR);
	}

	public float getSpeedOfSound() {
		return this.getAlFloat(AL11.AL_SPEED_OF_SOUND);
	}
	// endregion

	public AlDevice getDevice() {
		return this.device;
	}

	public AlContext getContext() {
		return this.context;
	}

	public long getDeviceId() {
		return this.device.getId();
	}

	public String getDeviceName() {
		return this.device.getName();
	}

	public long getContextId() {
		return this.context.getId();
	}

	public ALCapabilities getCapabilities() {
		return this.alCap;
	}

	public ALCCapabilities getContextCapabilities() {
		return this.alCtxCap;
	}
	// endregion

	// Only these three setters.
	// region OpenAL API setters.
	public void setDistanceModel(final int p_value) {
		AL10.alDistanceModel(p_value);
	}

	public void setDopplerFactor(final float p_value) {
		AL10.alDopplerFactor(p_value);
	}

	public void setSpeedOfSound(final float p_value) {
		AL11.alSpeedOfSound(p_value);
	}
	// endregion
	// endregion

	// region Error, and other checks.
	public static boolean isSource(final int p_id) {
		return AL10.alIsSource(p_id);
	}

	public static boolean isBuffer(final int p_id) {
		return AL10.alIsBuffer(p_id);
	}

	public static boolean isEffect(final int p_id) {
		return EXTEfx.alIsEffect(p_id);
	}

	public static boolean isFilter(final int p_id) {
		return EXTEfx.alIsFilter(p_id);
	}

	public static boolean isEffectSlot(final int p_id) {
		return EXTEfx.alIsAuxiliaryEffectSlot(p_id);
	}

	public static int errorStringToCode(final String p_errorString) {
		return AL10.alGetEnumValue(p_errorString.split("\"")[1]);
	}

	public static int errorStringToCode(final NerdAbstractOpenAlException p_exception) {
		return AL10.alGetEnumValue(p_exception.getAlcErrorString());
	}

	public int checkAlError() throws AlException {
		final int alError = AL10.alGetError();

		// `40964` is THE MOST annoying error.
		// Its error string is literally "No Error"!
		if (!(alError == 0 || alError == 40964))
			throw new AlException(alError);

		return alError;
	}

	public int checkAlcError() throws AlcException {
		final int alcError = ALC10.alcGetError(this.device.getId());

		if (alcError != 0)
			throw new AlcException(this.getDeviceId(), alcError);

		return alcError;
	}
	// endregion

	// region Loading sources from disk.
	public AlSource sourceFromOgg(final File p_file) {
		return new AlSource(this, new AlOggBuffer(this).loadFrom(p_file));
	}

	/**
	 * @deprecated We don't support WAV files for now.
	 */
	@Deprecated
	public AlSource sourceFromWav(final File p_file) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlWavBuffer(this).loadFrom(p_file));
	}

	public AlSource sourceFromOgg(final String p_filePath) {
		return new AlSource(this, new AlOggBuffer(this).loadFrom(p_filePath));
	}

	/**
	 * @deprecated We don't support WAV files for now.
	 */
	@Deprecated
	public AlSource sourceFromWav(final String p_filePath) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlWavBuffer(this).loadFrom(p_filePath));
	}
	// endregion

	// region State management.
	public void framelyCallback() {
		this.device.disconnectionCheck();

		for (final AlSource s : AlSource.ALL_INSTANCES)
			s.framelyCallback();
	}

	@SuppressWarnings("deprecation")
	public void scenelyDisposal() {
		for (int listId = 0; listId != 6; listId++) {
			final ArrayList<? extends AlNativeResource> list = switch (listId) {
				case 0 -> AlCapture.ALL_INSTANCES;
				case 1 -> AlFilter.ALL_INSTANCES;
				case 2 -> AlEffect.ALL_INSTANCES;
				case 3 -> AlAuxiliaryEffectSlot.ALL_INSTANCES; // Always after the other two!
				case 4 -> AlSource.ALL_INSTANCES; // Before the buffers!
				case 5 -> AlBuffer.ALL_INSTANCES; // ...After the sources.
				default -> null;
			};

			if (list == null)
				continue;

			for (int i = list.size() - 1; i > -1; i--)
				list.get(i).dispose();
		}
	}

	@SuppressWarnings("deprecation")
	public void completeDisposal() {

		for (int listId = 0; listId < 8; listId++) {
			final ArrayList<? extends AlNativeResource> list = switch (listId) {
				case 0 -> AlCapture.ALL_INSTANCES;
				case 1 -> AlFilter.ALL_INSTANCES;
				case 2 -> AlEffect.ALL_INSTANCES;
				case 3 -> AlAuxiliaryEffectSlot.ALL_INSTANCES; // Always after the other two!
				case 4 -> AlSource.ALL_INSTANCES; // Before the buffers!
				case 5 -> AlBuffer.ALL_INSTANCES; // ...After the sources.
				case 6 -> AlContext.ALL_INSTANCES;
				case 7 -> AlDevice.ALL_INSTANCES;
				default -> null;
			};

			if (list != null)
				for (int i = list.size() - 1; i > -1; i--)
					list.get(i).disposeForcibly();
		}
	}

	protected AlContext createAl(final String p_deviceName) {
		return this.createAl(p_deviceName, null);
	}

	protected AlContext createAl(final String p_deviceName, final AlContext.AlContextSettings p_contextSettings) {
		this.device = new AlDevice(this, p_deviceName);
		this.context = new AlContext(this, p_contextSettings);

		// LWJGL objects:
		this.alCtxCap = ALC.createCapabilities(this.getDeviceId());
		this.alCap = AL.createCapabilities(this.alCtxCap);

		this.checkAlError();
		this.checkAlcError();

		return this.context;
	}
	// endregion

}

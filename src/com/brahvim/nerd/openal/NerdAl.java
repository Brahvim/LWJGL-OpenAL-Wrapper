package com.brahvim.nerd.openal;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

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

/**
 * Wrapper to sit on top of an {@link AlContext} to allow access to
 * {@code al*()} functions.
 */
public class NerdAl {

	// region Fields.
	protected final Vector<AlNativeResource<?>> RESOURCES = new Vector<>(2), RESOURCES_TO_REMOVE = new Vector<>(2);

	protected long contextId;
	protected AlDevice device;
	protected AlContext context;
	protected ALCapabilities alCap;
	protected ALCCapabilities alCtxCap;
	// endregion

	// region Construction.
	public NerdAl() {
		this(AlDevice.getDefaultDeviceName());
	}

	public NerdAl(final AlDevice p_device) {
		this.context = this.createAl(p_device, new AlContext.AlContextSettings());
		this.contextId = this.context.getId();
	}

	public NerdAl(final AlContext p_context) {
		this.createAl(p_context);
	}

	public NerdAl(final String p_deviceName) {
		this.context = this.createAl(p_deviceName);
		this.contextId = this.context.getId();
	}

	public NerdAl(final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(AlDevice.getDefaultDeviceName(), p_settings);
		this.contextId = this.context.getId();
	}

	public NerdAl(final AlDevice p_device, final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(p_device, p_settings);
		this.contextId = this.context.getId();
	}

	public NerdAl(final String p_deviceName, final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(p_deviceName, p_settings);
		this.contextId = this.context.getId();
	}
	// endregion

	// Getters and setters!...:
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
	// region ...The three OpenAL API getters!
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

	// region ...Stuff in the `private` fields.
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
		return this.contextId;
	}

	public ALCapabilities getLwjglAlCapabilities() {
		return this.alCap;
	}

	public ALCCapabilities getLwjglAlContextCapabilities() {
		return this.alCtxCap;
	}
	// endregion

	// region ...The three OpenAL API setters!
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
		final int errorCode = AL10.alGetError();

		// `40964` is THE MOST annoying error.
		// Its error string is literally "No Error"!
		if (!(errorCode == 0 || errorCode == 40964))
			throw new AlException(errorCode);

		return errorCode;
	}

	public int checkAlcError() throws AlcException {
		return this.context.checkAlcError();
	}
	// endregion

	// region Loading sources from disk.
	public AlSource sourceFromOgg(final File p_file) {
		return new AlSource(this, new AlOggBuffer(this).loadFrom(p_file));
	}

	public AlSource sourceFromOgg(final String p_filePath) {
		return new AlSource(this, new AlOggBuffer(this).loadFrom(p_filePath));
	}

	/**
	 * @deprecated We don't support WAV files for now.
	 */
	@Deprecated
	public AlSource sourceFromWav(final File p_file) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlWavBuffer(this).loadFrom(p_file));
	}

	/**
	 * @deprecated We don't support WAV files for now.
	 */
	@Deprecated
	public AlSource sourceFromWav(final String p_filePath) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlWavBuffer(this).loadFrom(p_filePath));
	}

	/**
	 * @deprecated We don't support MP3 files for now.
	 */
	@Deprecated
	public AlSource sourceFromMp3(final File p_file) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlMp3Buffer(this).loadFrom(p_file));
	}

	/**
	 * @deprecated We don't support WAV files for now.
	 */
	@Deprecated
	public AlSource sourceFromMp3(final String p_filePath) {
		return new AlSource(this, new com.brahvim.nerd.openal.al_buffers.AlMp3Buffer(this).loadFrom(p_filePath));
	}
	// endregion

	// region State management.
	protected void makeContextCurrent() {
		ALC10.alcMakeContextCurrent(this.contextId);

	}

	public void framelyCallback() {
		AlSource.ALL_INSTANCES.removeAll(AlSource.INSTANCES_TO_REMOVE);

		for (final AlNativeResource<?> r : this.RESOURCES)
			r.framelyCallback();
	}

	public void disposeAllResources() {
		this.disposeResources(false, false);
	}

	public void disposeResources(final boolean p_alsoContexts, final boolean p_alsoDevices) {
		synchronized (this.RESOURCES) {
			for (int i = this.RESOURCES.size() - 1; i > -1; i--) {
				final AlNativeResource<?> resource = this.RESOURCES.get(i);

				if (resource instanceof AlContext)
					if (!p_alsoContexts)
						continue;

				if (resource instanceof AlDevice)
					if (!p_alsoDevices)
						continue;

				resource.disposeForcibly();
			}

			if (p_alsoContexts && p_alsoDevices)
				this.RESOURCES.clear();
		}
	}
	// endregion

	// region Initialization.
	protected AlContext createAl(final String p_deviceName) {
		return this.createAl(p_deviceName, null);
	}

	protected AlContext createAl(final String p_deviceName, final AlContext.AlContextSettings p_contextSettings) {
		return this.createAl(new AlDevice(this, p_deviceName), p_contextSettings);
	}

	protected AlContext createAl(final AlDevice p_device, final AlContext.AlContextSettings p_contextSettings) {
		this.device = p_device;
		this.context = new AlContext(this, p_contextSettings);

		// LWJGL objects:
		this.alCtxCap = ALC.createCapabilities(this.getDeviceId());
		this.alCap = AL.createCapabilities(this.alCtxCap);

		this.checkAlError();
		this.checkAlcError();

		return this.context;
	}

	protected AlContext createAl(final AlContext p_context) {
		this.context = p_context;
		return this.context;
	}
	// endregion

}

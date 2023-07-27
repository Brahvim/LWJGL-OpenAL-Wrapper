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
import com.brahvim.nerd.openal.al_exceptions.NerdAlException;

/**
 * Wrapper to sit on top of an {@link AlContext} to allow access to
 * {@code al*()} functions.
 */
public class NerdAl {

	// Test using `main()`:
	/*
	 * public static void main(final String[] p_args) {
	 * final NerdAl al = new NerdAl();
	 * System.out.println("Created a `NerdAl` instance!");
	 * al.makeContextCurrent();
	 * System.out.println("Exiting, ba-bye!");
	 * }
	 */

	// region Fields.
	protected final Vector<AlNativeResource<?>> RESOURCES = new Vector<>(2), RESOURCES_TO_REMOVE = new Vector<>(2);

	protected long contextId;
	protected AlDevice device;
	protected AlContext context;
	protected ALCapabilities alCap;
	protected ALCCapabilities alCtxCap;
	// endregion

	// region Construction.
	/**
	 * Creates OpenAL objects (an {@link AlDevice} and an {@link AlContext} on it)
	 * for the "default" physical device connected.
	 *
	 * @see AlDevice#changeEndpoint(String) Use this method to change the physical
	 *      device later without losing any objects already created.
	 */
	public NerdAl() {
		this(AlDevice.getDefaultPhysicalDeviceName());
	}

	/**
	 * Creates an abstract, OpenAL "device" object ({@link AlDevice} instance) given
	 * just the name of a physical device to connect to. Use
	 * {@link AlDevice#getPhysicalDevicesNames()} to get a list of physical devices
	 * to connect to.
	 */
	public NerdAl(final String p_deviceName) {
		this.context = this.createAl(p_deviceName);
		this.contextId = this.context.getId();
	}

	/**
	 * @param p_device is the abstract, OpenAL "device" object ({@link AlDevice}
	 *                 instance) to be used for making {@code alc*()} calls, and
	 *                 creating an {@link AlContext} object.
	 */
	public NerdAl(final AlDevice p_device) {
		this.context = this.createAl(p_device, new AlContext.AlContextSettings());
		this.contextId = this.context.getId();
	}

	/**
	 * Lets this {@link NerdAl} instance wrap the given {@link AlContext} object.
	 *
	 * @param p_context is the {@link AlContext} this {@link NerdAl} instance will
	 *                  wrap.
	 */
	public NerdAl(final AlContext p_context) {
		this.createAl(p_context);
	}

	/**
	 * Lets this {@link NerdAl} instance construct, then wrap an {@link AlContext}
	 * wrapper object given settings to initialize it.
	 *
	 * @param p_settings specifies initialization settings.
	 */
	public NerdAl(final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(AlDevice.getDefaultPhysicalDeviceName(), p_settings);
		this.contextId = this.context.getId();
	}

	/**
	 * Lets this {@link NerdAl} instance construct, then wrap an {@link AlContext}
	 * wrapper object constructed on the given {@link AlDevice}, given settings to
	 * initialize said {@link AlContext}.
	 *
	 * @param p_device   is the {@link AlDevice} to create a context in.
	 * @param p_settings specifies initialization settings for the
	 *                   {@link AlContext} to create.
	 */
	public NerdAl(final AlDevice p_device, final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(p_device, p_settings);
		this.contextId = this.context.getId();
	}

	/**
	 * Lets this {@link NerdAl} instance construct, then wrap an {@link AlContext}
	 * (given settings to initialize it) constructed on an
	 * {@link AlDevice}, which outputs to the physical device whose name is
	 * provided.
	 *
	 * @param p_deviceName is the name of the physical device the automatically
	 *                     created {@link AlDevice} will output to.
	 * @param p_settings   specifies initialization settings for the
	 *                     {@link AlContext} to create.
	 */
	public NerdAl(final String p_deviceName, final AlContext.AlContextSettings p_settings) {
		this.context = this.createAl(p_deviceName, p_settings);
		this.contextId = this.context.getId();
	}

	// region Initialization.
	/**
	 * Creates an abstract, OpenAL "device" object ({@link AlDevice} instance) given
	 * just the name of a physical device to connect to.
	 *
	 * @param p_deviceName is the name of the physical device.
	 * @return The {@link AlContext} this {@link NerdAl} instance will wrap.
	 */
	protected AlContext createAl(final String p_deviceName) {
		return this.createAl(p_deviceName, null);
	}

	/**
	 * @param p_deviceName      is the name of the physical device to connect to.
	 * @param p_contextSettings are the settings to create the {@link AlContext}
	 *                          object to wrap.
	 * @return The {@link AlContext} this {@link NerdAl} instance will wrap.
	 */
	protected AlContext createAl(final String p_deviceName, final AlContext.AlContextSettings p_contextSettings) {
		return this.createAl(new AlDevice(this, p_deviceName), p_contextSettings);
	}

	/**
	 * @param p_device          is the abstract, OpenAL "device" object
	 *                          ({@link AlDevice} instance) to be used for making
	 *                          {@code alc*()} calls.
	 * @param p_contextSettings are the settings to create the {@link AlContext}
	 *                          object to wrap.
	 * @return The {@link AlContext} this {@link NerdAl} instance will wrap.
	 */
	protected AlContext createAl(final AlDevice p_device, final AlContext.AlContextSettings p_contextSettings) {
		this.device = p_device;
		return this.createAl(new AlContext(this, p_contextSettings));
	}

	/**
	 * @param p_context is the {@link AlContext} to wrap.
	 * @return The very exact same {@link AlContext} object!
	 */
	protected AlContext createAl(final AlContext p_context) {
		this.context = p_context;

		// LWJGL objects. These fetch function pointers and stuff:
		this.alCtxCap = ALC.createCapabilities(this.getDeviceId());
		this.alCap = AL.createCapabilities(this.alCtxCap);

		// What happens when you don't create these? This!:
		/*
		 * """
		 * Exception in thread "main" java.lang.ExceptionInInitializerError
		 * at org.lwjgl.openal.AL$ICDStatic.get(AL.java:252)
		 * AL.java:252
		 * at org.lwjgl.openal.AL.getICD(AL.java:218)
		 * AL.java:218
		 * at org.lwjgl.openal.AL10.alGetError(AL10.java:143)
		 * AL10.java:143
		 * """
		 *
		 * ...
		 * <Insert errors referencing our code here!>
		 * ...
		 *
		 * """
		 * Caused by: java.lang.IllegalStateException: No ALCapabilities instance has
		 * been set at org.lwjgl.openal.AL$ICDStatic$WriteOnce.<clinit>(AL.java:262)
		 * AL.java:262
		 * ... 11 more
		 * """
		 */

		this.checkAlError();
		this.checkAlcError();

		this.contextId = this.context.getId();
		return this.context;
	}
	// endregion
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

	// region ...Stuff in the `protected` fields.
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
		return this.device.getPhysicalDeviceName();
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
		if (!ALC10.alcMakeContextCurrent(this.contextId))
			throw new NerdAlException("Could not change the OpenAL context! This should not happen...");
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

}

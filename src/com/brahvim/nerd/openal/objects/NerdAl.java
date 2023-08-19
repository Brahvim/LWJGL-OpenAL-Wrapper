package com.brahvim.nerd.openal.objects;

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
import com.brahvim.nerd.openal.al_exceptions.AbstractAlException;
import com.brahvim.nerd.openal.al_exceptions.AlException;
import com.brahvim.nerd.openal.al_exceptions.AlUnflaggedException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.null_objects.AlNullAuxiliaryEffectSlot;
import com.brahvim.nerd.openal.null_objects.AlNullBuffer;
import com.brahvim.nerd.openal.null_objects.AlNullBufferStream;
import com.brahvim.nerd.openal.null_objects.AlNullCapture;
import com.brahvim.nerd.openal.null_objects.AlNullContext;
import com.brahvim.nerd.openal.null_objects.AlNullDevice;
import com.brahvim.nerd.openal.null_objects.AlNullEffect;
import com.brahvim.nerd.openal.null_objects.AlNullFilter;
import com.brahvim.nerd.openal.null_objects.AlNullSource;

/**
 * Wrapper to sit on top of an {@link AlContext} to allow access to
 * {@code al*()} functions.
 */
public class NerdAl {

	// Test using `main()`:
	public static void main(final String[] p_args) {
		final NerdAl al1 = new NerdAl(), al2 = new NerdAl();
		System.out.println("Created a `NerdAl` instance!");
		al1.makeContextCurrent();
		al2.makeContextCurrent();

		final AlSource source = new AlSource(al2);
		source.setEffectSlot(new AlAuxiliaryEffectSlot(al2));
		source.playThenDispose();

		al1.disposeAllResources();
		al2.disposeAllResources();
		System.out.println("Exiting, ba-bye!");
	}

	/**
	 * An enum listing all OpenAL extensions this library uses. Any OpenAL drivers
	 * ("implementations") you use with this library, need to support them too.
	 */
	public enum NerdAlExtensionsInfo {

		/**
		 * Refers to {@code ALC_EXT_EFX}, the "OpenAL Effects Extension".
		 */
		EXT_EFFECTS("ALC_EXT_EFX"),

		/**
		 * Refers to {@code ALC_EXT_disconnect}, which comes primarily from OpenAL Soft.
		 */
		EXT_DISCONNECT("ALC_EXT_disconnect"),

		/**
		 * Refers to {@code ALC_ENUMERATION_EXT}, a standard extension to OpenAL that
		 * should be included with drivers for PC platforms.
		 */
		ENUMERATION_EXT("ALC_ENUMERATION_EXT"),

		/**
		 * 
		 */
		ENUMERATE_ALL_EXT("ALC_ENUMERATE_ALL_EXT");

		// region Instance stuff!
		private final String NAME;

		private NerdAlExtensionsInfo(final String p_extName) {
			this.NAME = p_extName;
		}

		public String getExtensionName() {
			return this.NAME;
		}
		// endregion

	}

	public class NerdAlDefaultObjects {

		// region Fields.
		public AlNullAuxiliaryEffectSlot auxiliaryEffectSlot;
		public AlNullBuffer buffer;
		public AlNullContext context;

		@SuppressWarnings("deprecation")
		public AlNullCapture capture;
		public AlNullDevice device;
		public AlNullEffect effect;
		public AlNullFilter filter;
		public AlNullSource source;
		public AlNullBufferStream stream;
		// endregion

		@SuppressWarnings("deprecation")
		protected NerdAlDefaultObjects() {
			// The constructors of these remove these objects from their respective class's
			// static lists, and we do the rest down there in the loop!:
			this.auxiliaryEffectSlot = new AlNullAuxiliaryEffectSlot(NerdAl.this);
			this.buffer = new AlNullBuffer(NerdAl.this);
			this.context = new AlNullContext(NerdAl.this);
			this.capture = new AlNullCapture(NerdAl.this);
			this.device = new AlNullDevice(NerdAl.this);
			this.effect = new AlNullEffect(NerdAl.this);
			this.filter = new AlNullFilter(NerdAl.this);
			this.source = new AlNullSource(NerdAl.this);
			this.stream = new AlNullBufferStream(NerdAl.this);

			// Remove these default objects from these!:
			for (final AlNativeResource<?> r : NerdAl.this.RESOURCES) {
				NerdAl.this.RESOURCES.remove(r);
				AlNativeResource.ALL_INSTANCES.remove(r);
			}
		}

	}

	// region Fields.
	public final NerdAl.NerdAlDefaultObjects DEFAULTS;
	protected final Vector<AlNativeResource<?>> RESOURCES = new Vector<>(2), RESOURCES_TO_REMOVE = new Vector<>(2);

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
	public NerdAl(final String p_physicalDeviceName) {
		this.context = this.createAl(p_physicalDeviceName);
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
	}

	/**
	 * @param p_device is the abstract, OpenAL "device" object ({@link AlDevice}
	 *                 instance) to be used for making {@code alc*()} calls, and
	 *                 creating an {@link AlContext} object.
	 */
	public NerdAl(final AlDevice p_device) {
		this.context = this.createAl(p_device, new AlContextSettings());
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
	}

	/**
	 * Lets this {@link NerdAl} instance wrap the given {@link AlContext} object.
	 *
	 * @param p_context is the {@link AlContext} this {@link NerdAl} instance will
	 *                  wrap.
	 */
	public NerdAl(final AlContext p_context) {
		this.createAl(p_context);
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
	}

	/**
	 * Lets this {@link NerdAl} instance construct, then wrap an {@link AlContext}
	 * wrapper object given settings to initialize it.
	 *
	 * @param p_settings specifies initialization settings.
	 */
	public NerdAl(final AlContextSettings p_settings) {
		this.context = this.createAl(AlDevice.getDefaultPhysicalDeviceName(), p_settings);
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
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
	public NerdAl(final AlDevice p_device, final AlContextSettings p_settings) {
		this.context = this.createAl(p_device, p_settings);
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
	}

	/**
	 * Lets this {@link NerdAl} instance construct, then wrap an {@link AlContext}
	 * (given settings to initialize it) constructed on an
	 * {@link AlDevice}, which outputs to the physical device whose name is
	 * provided.
	 *
	 * @param p_physicalDeviceName is the name of the physical device the
	 *                             automatically
	 *                             created {@link AlDevice} will output to.
	 * @param p_settings           specifies initialization settings for the
	 *                             {@link AlContext} to create.
	 */
	public NerdAl(final String p_physicalDeviceName, final AlContextSettings p_settings) {
		this.context = this.createAl(p_physicalDeviceName, p_settings);
		this.DEFAULTS = new NerdAl.NerdAlDefaultObjects();
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
		return this.createAl(p_deviceName, null); // `AlContext` constructor deals with the `null`.
	}

	/**
	 * @param p_deviceName      is the name of the physical device to connect to.
	 * @param p_contextSettings are the settings to create the {@link AlContext}
	 *                          object to wrap.
	 * @return The {@link AlContext} this {@link NerdAl} instance will wrap.
	 */
	protected AlContext createAl(final String p_deviceName, final AlContextSettings p_contextSettings) {
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
	protected AlContext createAl(final AlDevice p_device, final AlContextSettings p_contextSettings) {
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
		return this.context.getId();
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
	public boolean isSource(final int p_id) {
		this.makeContextCurrent();
		return AL10.alIsSource(p_id);
	}

	public boolean isBuffer(final int p_id) {
		this.makeContextCurrent();
		return AL10.alIsBuffer(p_id);
	}

	public boolean isEffect(final int p_id) {
		this.makeContextCurrent();
		return EXTEfx.alIsEffect(p_id);
	}

	public boolean isFilter(final int p_id) {
		this.makeContextCurrent();
		return EXTEfx.alIsFilter(p_id);
	}

	public boolean isEffectSlot(final int p_id) {
		this.makeContextCurrent();
		return EXTEfx.alIsAuxiliaryEffectSlot(p_id);
	}

	public static int errorStringToCode(final String p_errorString) {
		return AL10.alGetEnumValue(p_errorString.split("\"")[1]);
	}

	public static int errorStringToCode(final AbstractAlException p_exception) {
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
		if (!ALC10.alcMakeContextCurrent(this.context.getId()))
			throw new AlUnflaggedException("Could not change the OpenAL context! This should not happen...");
	}

	public void framelyCallback() {
		this.RESOURCES.removeAll(this.RESOURCES_TO_REMOVE);
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

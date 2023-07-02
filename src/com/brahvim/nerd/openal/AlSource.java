package com.brahvim.nerd.openal;

import java.io.File;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_buffers.AlMp3Buffer;
import com.brahvim.nerd.openal.al_buffers.AlNoTypeBuffer;
import com.brahvim.nerd.openal.al_buffers.AlOggBuffer;
import com.brahvim.nerd.openal.al_buffers.AlWavBuffer;

public class AlSource extends AlNativeResource {

	// region Fields.
	protected static final ArrayList<AlSource> ALL_INSTANCES = new ArrayList<>();

	private final int id;
	private final NerdAl alMan;
	private final AlContext context;

	// State tracking!:
	private boolean ppaused, paused;
	private boolean pstopped, stopped;
	private boolean pplaying, playing;
	private boolean plooping, looping;

	private AlBuffer<?> buffer;
	private AlDataStream stream;
	private AlAuxiliaryEffectSlot effectSlot;
	private boolean disposeOnPlay, disposeBufferOnPlay;
	private AlFilter directFilter, auxiliarySendFilter;
	// endregion

	// region Constructors.
	public AlSource(final NerdAl p_alMan) {
		this.alMan = p_alMan;
		this.context = this.alMan.getContext();
		ALC10.alcMakeContextCurrent(this.context.getId());

		this.alMan.checkAlcError();
		this.alMan.checkAlcError();
		this.id = AL10.alGenSources();
		this.alMan.checkAlError();

		AlSource.ALL_INSTANCES.add(this);
	}

	public AlSource(final AlSource p_source) {
		this.alMan = p_source.alMan;
		this.context = p_source.context;

		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		this.id = AL10.alGenSources();
		this.alMan.checkAlError();

		// region Transfer properties over (hopefully, the JIT inlines!):
		this.setBuffer(p_source.buffer);
		this.setGain(p_source.getGain());
		this.setMinGain(p_source.getMinGain());
		this.setMaxGain(p_source.getMaxGain());
		this.setPosition(p_source.getPosition());
		this.setVelocity(p_source.getVelocity());
		// this.setSourceType(p_source.getSourceType());
		this.setDirection(p_source.getDirection());
		this.setMaxDistance(p_source.getMaxDistance());
		this.setSampleOffset(p_source.getSampleOffset());
		this.setConeOuterGain(p_source.getConeOuterGain());
		this.setRolloffFactor(p_source.getRolloffFactor());
		this.attachDirectFilter(p_source.getDirectFilter());
		this.setConeOuterAngle(p_source.getConeOuterAngle());
		this.setConeInnerAngle(p_source.getConeInnerAngle());
		this.setConeOuterGainHf(p_source.getConeOuterGainHf());
		this.setPitchMultiplier(p_source.getPitchMultiplier());
		this.setReferenceDistance(p_source.getReferenceDistance());
		this.setRoomRolloffFactor(p_source.getRoomRolloffFactor());
		this.setAirAbsorptionFactor(p_source.getAirAbsorptionFactor());
		this.attachAuxiliarySendFilter(p_source.getAuxiliarySendFilter());
		this.setDirectFilterGainHfAuto(p_source.getDirectFilterGainHfAuto());
		this.setAuxiliarySendFilterGainAuto(p_source.getAuxiliarySendFilterGainAuto());
		this.setAuxiliarySendFilterGainHfAuto(p_source.getAuxiliarySendFilterGainHfAuto());
		// endregion

		AlSource.ALL_INSTANCES.add(this);
	}

	public AlSource(final NerdAl p_alMan, final AlBuffer<?> p_buffer) {
		this(p_alMan);
		this.setBuffer(p_buffer);
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlSource.ALL_INSTANCES.size();
	}

	public static ArrayList<AlSource> getAllInstances() {
		return new ArrayList<>(AlSource.ALL_INSTANCES);
	}
	// endregion

	// region ...literal "buffer distribution", :joy:
	@SuppressWarnings({ "unchecked", "deprecation" })
	public <T extends Buffer> AlBuffer<T> getBuffer() {
		final int BUFFER_ID = this.getInt(AL10.AL_BUFFER);
		if (BUFFER_ID == this.buffer.getId())
			return (AlBuffer<T>) this.buffer;

		if (this.buffer instanceof AlOggBuffer)
			return (AlBuffer<T>) this.buffer;
		else if (this.buffer instanceof com.brahvim.nerd.openal.al_buffers.AlWavBuffer)
			return (AlBuffer<T>) this.buffer;
		else
			return (AlBuffer<T>) new AlNoTypeBuffer(this.alMan, BUFFER_ID);
	}

	public AlSource setBuffer(final AlBuffer<?> p_buffer) {
		this.buffer = p_buffer;
		this.setInt(AL10.AL_BUFFER, this.buffer.getId());
		return this;
	}

	public void loadOggBuffer(final File p_file) {
		this.buffer = new AlOggBuffer(this.alMan).loadFrom(p_file);
	}

	/**
	 * @deprecated since it uses a deprecated API.
	 */
	@Deprecated
	public void loadWavBuffer(final File p_file) {
		this.buffer = new AlWavBuffer(this.alMan).loadFrom(p_file);
	}

	/**
	 * @deprecated since it uses a deprecated API.
	 */
	@Deprecated
	public void loadMp3Buffer(final File p_file) {
		this.buffer = new AlMp3Buffer(this.alMan).loadFrom(p_file);
	}
	// endregion

	// region C-style OpenAL getters.
	public int getInt(final int p_alEnum) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();

		if (super.hasDisposed)
			return Integer.MIN_VALUE;

		return AL10.alGetSourcei(this.id, p_alEnum);
	}

	public float getFloat(final int p_alEnum) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();

		if (super.hasDisposed)
			return -Float.MAX_VALUE;

		return AL10.alGetSourcef(this.id, p_alEnum);
	}

	// Vectors in OpenAL are not large and can be allocated on the stack just fine.
	public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);

		if (super.hasDisposed)
			return new int[0];

		AL10.alGetSourceiv(this.id, p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public float[] getFloatTriplet(final int p_alEnum) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		MemoryStack.stackPush();
		final FloatBuffer f1 = MemoryStack.stackMallocFloat(1),
				f2 = MemoryStack.stackMallocFloat(1),
				f3 = MemoryStack.stackMallocFloat(1);

		if (super.hasDisposed)
			return new float[0];

		AL10.alGetSource3f(this.id, p_alEnum, f1, f2, f3);
		MemoryStack.stackPop();

		return new float[] { f1.get(), f2.get(), f3.get() };
	}

	public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(p_vecSize);

		if (super.hasDisposed)
			return new float[0];

		AL10.alGetSourcefv(this.id, p_alEnum, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
	}

	public int[] getIntTriplet(final int p_alEnum) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(3);

		if (super.hasDisposed)
			return new int[0];

		AL10.alGetSourceiv(this.id, p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}
	// endregion

	// region C-style OpenAL setters.
	public AlSource setInt(final int p_alEnum, final int p_value) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourcei(this.id, p_alEnum, p_value);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setFloat(final int p_alEnum, final float p_value) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourcef(this.id, p_alEnum, p_value);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setIntVector(final int p_alEnum, final int... p_values) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL11.alSourceiv(this.id, p_alEnum, p_values);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setFloatVector(final int p_alEnum, final float... p_values) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourcefv(this.id, p_alEnum, p_values);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setIntTriplet(final int p_alEnum, final int... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setIntTriplet()` cannot take an array of size other than `3`!");

		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL11.alSource3i(this.id, p_alEnum, p_value[0], p_value[1], p_value[2]);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL11.alSource3i(this.id, p_alEnum, p_i1, p_i2, p_i3);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setFloatTriplet(final int p_alEnum, final float... p_value) {
		if (p_value.length != 3)
			throw new IllegalArgumentException(
					"`AlSource::setFloatTriplet()` cannot take an array of size other than `3`!");

		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSource3f(this.id, p_alEnum, p_value[0], p_value[1], p_value[2]);
		this.alMan.checkAlError();
		return this;
	}

	public AlSource setFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSource3f(this.id, p_alEnum, p_f1, p_f2, p_f3);
		this.alMan.checkAlError();
		return this;
	}
	// endregion

	// region Source getters.
	public int getId() {
		return this.id;
	}

	// region `int` getters.
	public int getSourceType() {
		return this.getInt(AL10.AL_SOURCE_TYPE);
	}

	public int getSourceState() {
		return this.getInt(AL10.AL_SOURCE_STATE);
	}

	public int getBuffersQueued() {
		return this.getInt(AL10.AL_BUFFERS_QUEUED);
	}

	public int getBuffersProcessed() {
		return this.getInt(AL10.AL_BUFFERS_PROCESSED);
	}

	public int getSecOffset() {
		return this.getInt(AL11.AL_SEC_OFFSET);
	}

	public int getSampleOffset() {
		return this.getInt(AL11.AL_SAMPLE_OFFSET);
	}

	public int getByteOffset() {
		return this.getInt(AL11.AL_BYTE_OFFSET);
	}
	// endregion

	// region `float` getters.
	public float getGain() {
		return this.getFloat(AL10.AL_GAIN);
	}

	public float getPitchMultiplier() {
		return this.getFloat(AL10.AL_PITCH);
	}

	public float getMaxDistance() {
		return this.getFloat(AL10.AL_MAX_DISTANCE);
	}

	public float getRolloffFactor() {
		return this.getFloat(AL10.AL_ROLLOFF_FACTOR);
	}

	public float getReferenceDistance() {
		return this.getFloat(AL10.AL_REFERENCE_DISTANCE);
	}

	public float getMinGain() {
		return this.getFloat(AL10.AL_MIN_GAIN);
	}

	public float getMaxGain() {
		return this.getFloat(AL10.AL_MAX_GAIN);
	}

	public float getConeOuterGain() {
		return this.getFloat(AL10.AL_CONE_OUTER_GAIN);
	}

	public float getConeInnerAngle() {
		return this.getFloat(AL10.AL_CONE_INNER_ANGLE);
	}

	public float getConeOuterAngle() {
		return this.getFloat(AL10.AL_CONE_OUTER_ANGLE);
	}
	// endregion

	// region State (`boolean`) getters.
	// These don't query their info directly from OpenAL since we already track
	// that, might help prevent race conditions by making sure that the rate at
	// which this data is updated/tracked is consistent!:

	public boolean isPaused() {
		// return this.getInt(AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
		return this.paused;
	}

	public boolean isLooping() {
		// return this.getInt(AL10.AL_SOURCE_STATE) == AL10.AL_LOOPING;
		return this.looping;
	}

	public boolean isStopped() {
		// return this.getInt(AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
		return this.stopped;
	}

	public boolean isPlaying() {
		// return this.getInt(AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
		return this.playing;
	}

	// Previous frame tracking:
	public boolean wasPaused() {
		return this.ppaused;
	}

	public boolean wasLooping() {
		return this.plooping;
	}

	public boolean wasStopped() {
		return this.pstopped;
	}

	public boolean wasPlaying() {
		return this.pplaying;
	}
	// endregion

	// region Triplet getters.
	public float[] getPosition() {
		return this.getFloatTriplet(AL10.AL_POSITION);
	}

	public float[] getVelocity() {
		return this.getFloatTriplet(AL10.AL_POSITION);
	}

	public float[] getDirection() {
		return this.getFloatTriplet(AL10.AL_POSITION);
	}
	// endregion
	// endregion

	// region Source setters.
	// region `int` setters.
	public AlSource setSecOffset(final int p_value) {
		this.setInt(AL11.AL_SEC_OFFSET, p_value);
		return this;
	}

	public AlSource setSampleOffset(final int p_value) {
		this.setInt(AL11.AL_SAMPLE_OFFSET, p_value);
		return this;
	}

	public AlSource setByteOffset(final int p_value) {
		this.setInt(AL11.AL_BYTE_OFFSET, p_value);
		return this;
	}
	// endregion

	// region `float` setters.
	public AlSource setGain(final float p_value) {
		this.setFloat(AL10.AL_GAIN, p_value);
		return this;
	}

	public AlSource setPitchMultiplier(final float value) {
		AL10.alSourcef(this.id, AL10.AL_PITCH, value);
		return this;
	}

	public AlSource setMaxDistance(final float p_value) {
		this.setFloat(AL10.AL_MAX_DISTANCE, p_value);
		return this;
	}

	public AlSource setRolloffFactor(final float p_value) {
		this.setFloat(AL10.AL_ROLLOFF_FACTOR, p_value);
		return this;
	}

	public AlSource setReferenceDistance(final float p_value) {
		this.setFloat(AL10.AL_REFERENCE_DISTANCE, p_value);
		return this;
	}

	public AlSource setMinGain(final float p_value) {
		this.setFloat(AL10.AL_MIN_GAIN, p_value);
		return this;
	}

	public AlSource setMaxGain(final float p_value) {
		this.setFloat(AL10.AL_MAX_GAIN, p_value);
		return this;
	}

	public AlSource setConeOuterGain(final float p_value) {
		this.setFloat(AL10.AL_CONE_OUTER_GAIN, p_value);
		return this;
	}

	public AlSource setConeInnerAngle(final float p_value) {
		this.setFloat(AL10.AL_CONE_INNER_ANGLE, p_value);
		return this;
	}

	public AlSource setConeOuterAngle(final float p_value) {
		this.setFloat(AL10.AL_CONE_OUTER_ANGLE, p_value);
		return this;
	}
	// endregion

	// region Triplet setters.
	public AlSource setPosition(final float[] p_value) {
		this.setFloatTriplet(AL10.AL_POSITION, p_value);
		return this;
	}

	public AlSource setPosition(final float p_x, final float p_y, final float p_z) {
		this.setFloatTriplet(AL10.AL_POSITION, p_x, p_y, p_z);
		return this;
	}

	public AlSource setVelocity(final float[] p_value) {
		this.setFloatTriplet(AL10.AL_VELOCITY, p_value);
		return this;
	}

	public AlSource setVelocity(final float p_x, final float p_y, final float p_z) {
		this.setFloatTriplet(AL10.AL_VELOCITY, p_x, p_y, p_z);
		return this;
	}

	public AlSource setDirection(final float[] p_value) {
		this.setFloatTriplet(AL10.AL_DIRECTION, p_value);
		return this;
	}

	public AlSource setDirection(final float p_x, final float p_y, final float p_z) {
		this.setFloatTriplet(AL10.AL_DIRECTION, p_x, p_y, p_z);
		return this;
	}
	// endregion
	// endregion

	// region Anything `EXTEfx`.
	public AlAuxiliaryEffectSlot getEffectSlot() {
		return this.effectSlot;
	}

	/**
	 * @return The older effect slot object (may be {@code null}).
	 */
	public AlAuxiliaryEffectSlot setEffectSlot(final AlAuxiliaryEffectSlot p_effectSlot) {
		final AlAuxiliaryEffectSlot toRet = this.effectSlot;
		this.effectSlot = p_effectSlot;
		final int EFFECT_SLOT_ID;

		if (this.effectSlot == null) {
			EFFECT_SLOT_ID = EXTEfx.AL_EFFECTSLOT_NULL;
			return toRet;
		} else {
			this.effectSlot.setSource(this);
			EFFECT_SLOT_ID = this.effectSlot.getId();
		}

		this.setIntTriplet(
				EXTEfx.AL_AUXILIARY_SEND_FILTER,
				EFFECT_SLOT_ID, 0,
				this.auxiliarySendFilter == null
						? EXTEfx.AL_FILTER_NULL
						: this.auxiliarySendFilter.getId());
		return toRet;
	}

	// region Methods for `AlFilter`s.
	public AlFilter getDirectFilter() {
		return this.directFilter;
	}

	public AlFilter detachDirectFilter() {
		final AlFilter toRet = this.directFilter;
		this.directFilter = null;
		this.setInt(EXTEfx.AL_DIRECT_FILTER, EXTEfx.AL_FILTER_NULL);

		return toRet;
	}

	/**
	 * @return The older filter object attached.
	 */
	public AlFilter attachDirectFilter(final AlFilter p_filter) {
		final AlFilter toRet = this.directFilter;
		this.directFilter = p_filter;
		this.setInt(EXTEfx.AL_DIRECT_FILTER, this.directFilter == null
				? EXTEfx.AL_FILTER_NULL
				: this.directFilter.getId());

		return toRet;
	}

	public AlFilter getAuxiliarySendFilter() {
		return this.auxiliarySendFilter;
	}

	public AlFilter detachAuxiliarySendFilter() {
		final AlFilter toRet = this.auxiliarySendFilter;
		this.auxiliarySendFilter = null;
		this.setInt(EXTEfx.AL_AUXILIARY_SEND_FILTER, EXTEfx.AL_FILTER_NULL);

		return toRet;
	}

	public AlFilter attachAuxiliarySendFilter(final AlFilter p_filter) {
		final AlFilter toRet = this.auxiliarySendFilter;
		this.auxiliarySendFilter = p_filter;
		this.setIntTriplet(
				EXTEfx.AL_AUXILIARY_SEND_FILTER,
				this.effectSlot.getId(), 0,
				this.auxiliarySendFilter == null
						? EXTEfx.AL_FILTER_NULL
						: this.auxiliarySendFilter.getId());

		return toRet;
	}
	// endregion

	// region `EXTEfx` property getters.
	public float getAirAbsorptionFactor() {
		return this.getFloat(EXTEfx.AL_AIR_ABSORPTION_FACTOR);
	}

	public float getRoomRolloffFactor() {
		return this.getFloat(EXTEfx.AL_ROOM_ROLLOFF_FACTOR);
	}

	public float getConeOuterGainHf() {
		return this.getFloat(EXTEfx.AL_CONE_OUTER_GAINHF);
	}

	public float getDirectFilterGainHfAuto() {
		return this.getFloat(EXTEfx.AL_DIRECT_FILTER_GAINHF_AUTO);
	}

	public float getAuxiliarySendFilterGainAuto() {
		return this.getFloat(EXTEfx.AL_AUXILIARY_SEND_FILTER_GAIN_AUTO);
	}

	public float getAuxiliarySendFilterGainHfAuto() {
		return this.getFloat(EXTEfx.AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO);
	}
	// endregion

	// region `EXTEfx` property setters.
	public AlSource setAirAbsorptionFactor(final float p_value) {
		this.setFloat(EXTEfx.AL_AIR_ABSORPTION_FACTOR, p_value);
		return this;
	}

	public AlSource setRoomRolloffFactor(final float p_value) {
		this.setFloat(EXTEfx.AL_ROOM_ROLLOFF_FACTOR, p_value);
		return this;
	}

	public AlSource setConeOuterGainHf(final float p_value) {
		this.setFloat(EXTEfx.AL_CONE_OUTER_GAINHF, p_value);
		return this;
	}

	public AlSource setDirectFilterGainHfAuto(final float p_value) {
		this.setFloat(EXTEfx.AL_DIRECT_FILTER_GAINHF_AUTO, p_value);
		return this;
	}

	public AlSource setAuxiliarySendFilterGainAuto(final float p_value) {
		this.setFloat(EXTEfx.AL_AUXILIARY_SEND_FILTER_GAIN_AUTO, p_value);
		return this;
	}

	public AlSource setAuxiliarySendFilterGainHfAuto(final float p_value) {
		this.setFloat(EXTEfx.AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO, p_value);
		return this;
	}
	// endregion
	// endregion

	// region Actual state management!
	public void play() {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourcePlay(this.id);
	}

	public void playThenDispose() {
		this.disposeOnPlay = true;
		this.play();
	}

	public void playThenDisposeWithBuffer() {
		this.disposeBufferOnPlay = true;
		this.playThenDispose();
	}

	public void loop(final boolean p_value) {
		this.setInt(AL10.AL_LOOPING, p_value ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void stop() {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceStop(this.id);
	}

	public void pause() {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourcePause(this.id);
	}

	public void rewind() {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceRewind(this.id);
	}

	public AlDataStream getStream() {
		return this.stream;
	}

	public AlSource setStream(final AlDataStream p_alDataStream) {
		this.stream = p_alDataStream;
		return this;
	}

	/* `package` */ void framelyCallback() {
		if (this.stream != null)
			this.stream.framelyCallback();

		this.ppaused = this.paused;
		this.pplaying = this.playing;
		this.pstopped = this.stopped;
		this.plooping = this.looping;

		this.paused = this.getSourceState() == AL10.AL_PAUSED;
		this.playing = this.getSourceState() == AL10.AL_PLAYING;
		this.stopped = this.getSourceState() == AL10.AL_STOPPED;
		this.looping = this.getSourceState() == AL10.AL_LOOPING;

		if (this.pplaying && !this.playing)
			if (this.disposeOnPlay) {
				if (this.disposeBufferOnPlay)
					this.disposeWithBuffer();
				else
					super.dispose();
			}
	}

	public void queueBuffers(final AlBuffer<?> p_buffer) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceQueueBuffers(this.id, p_buffer.getId());
	}

	public void queueBuffers(final AlBuffer<?>... p_buffers) {
		final int[] buffers = new int[p_buffers.length];
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceQueueBuffers(this.id, buffers);
	}

	public void unqueueBuffers(final AlBuffer<?> p_buffer) {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceUnqueueBuffers(this.id, new int[] { p_buffer.getId() });
	}

	public void unqueueBuffers(final AlBuffer<?>... p_buffers) {
		final int[] buffers = new int[p_buffers.length];

		for (int i = 0; i < buffers.length; i++)
			buffers[i] = p_buffers[i].getId();

		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alSourceUnqueueBuffers(this.id, buffers);
	}

	public void disposeWithBuffer() {
		super.dispose();
		this.buffer.dispose();
	}

	@Override
	protected void disposeImpl() {
		ALC10.alcMakeContextCurrent(this.context.getId());
		this.alMan.checkAlcError();
		AL10.alDeleteSources(this.id);
		this.alMan.checkAlError();
		AlSource.ALL_INSTANCES.remove(this);
	}
	// endregion

}

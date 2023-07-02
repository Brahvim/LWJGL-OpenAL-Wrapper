package com.brahvim.nerd.openal;

import java.io.File;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryStack;

// If "`BufferT`" sounds weird to you, check out: 
// https://stackoverflow.com/a/30146204/

public abstract class AlBuffer<BufferT extends Buffer> extends AlNativeResource {

	// region Fields.
	protected static final ArrayList<AlBuffer<?>> ALL_INSTANCES = new ArrayList<>();

	// No OpenAL implementation provides `AL_DATA`.
	// Storing it here!
	protected BufferT data;
	protected NerdAl alMan;
	protected int id, alFormat;
	// endregion

	// region Constructors.
	protected AlBuffer(final NerdAl p_alMan) {
		this.alMan = p_alMan;
		AlBuffer.ALL_INSTANCES.add(this);

		this.id = AL10.alGenBuffers();
		this.alMan.checkAlError();
	}

	@SuppressWarnings("unchecked")
	protected AlBuffer(final AlBuffer<?> p_buffer) {
		AlBuffer.ALL_INSTANCES.add(this);

		this.alMan = p_buffer.alMan;
		this.alFormat = p_buffer.alFormat;

		this.id = AL10.alGenBuffers();
		this.alMan.checkAlError();

		// These aren't set individually:
		// this.setBits(p_buffer.getBits());
		// this.setChannels(p_buffer.getChannels());
		this.setDataImpl(p_buffer.alFormat, (BufferT) p_buffer.getData(), p_buffer.getSampleRate());
		this.alMan.checkAlError();
	}

	protected AlBuffer(final NerdAl p_alMan, final int p_id) {
		if (!NerdAl.isBuffer(p_id))
			throw new IllegalArgumentException("`AlBuffer.AlBuffer(NerdAL, int)` received an invalid buffer ID.");

		AlBuffer.ALL_INSTANCES.add(this);
		this.alMan = p_alMan;
		this.id = p_id;
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlBuffer.ALL_INSTANCES.size();
	}

	public static ArrayList<AlBuffer<?>> getAllInstances() {
		return new ArrayList<>(AlBuffer.ALL_INSTANCES);
	}
	// endregion

	// region `abstract` methods (and overloads, with their implementations).
	public AlBuffer<?> loadFrom(final String p_path) {
		this.loadFrom(new File(p_path)); // Also invoke `AlNativeResource::shouldDispose()`.
		return this;
	}

	public AlBuffer<?> loadFrom(final File p_file) {
		super.shouldDispose(false);
		this.loadFromImpl(p_file);
		return this;
	}

	protected abstract AlBuffer<?> loadFromImpl(File p_file);

	public void setData(final int p_format, final BufferT p_buffer, final int p_sampleRate) {
		this.data = p_buffer;
		this.alFormat = p_format;
		this.setDataImpl(p_format, p_buffer, p_sampleRate);
		this.alMan.checkAlError();
	}

	protected abstract void setDataImpl(int p_format, BufferT p_buffer, int p_sampleRate);
	// endregion

	// region C-style OpenAL getters.
	public int getInt(final int p_alEnum) {

		if (super.hasDisposed)
			return Integer.MIN_VALUE;

		return AL10.alGetBufferi(this.id, p_alEnum);
	}

	public float getFloat(final int p_alEnum) {

		if (super.hasDisposed)
			return -Float.MAX_VALUE;

		return AL10.alGetBufferf(this.id, p_alEnum);
	}

	// Vectors in OpenAL are not large and can be allocated on the stack just fine.
	public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);

		if (super.hasDisposed)
			return new int[0];

		AL11.alGetBufferiv(this.id, p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(p_vecSize);

		if (super.hasDisposed)
			return new float[0];

		AL11.alGetBufferfv(this.id, p_alEnum, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
	}

	public int[] getIntTriplet(final int p_alEnum) {
		MemoryStack.stackPush();
		final IntBuffer intBuffer = MemoryStack.stackMallocInt(3);

		if (super.hasDisposed)
			return new int[0];

		AL11.alGetBufferiv(this.id, p_alEnum, intBuffer);
		MemoryStack.stackPop();

		return intBuffer.array();
	}

	public /* `float[]` */ float[] getFloatTriplet(final int p_alEnum) {
		MemoryStack.stackPush();
		final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);

		if (super.hasDisposed)
			return new float[0];

		AL11.alGetBufferfv(this.id, p_alEnum, floatBuffer);
		MemoryStack.stackPop();

		return floatBuffer.array();
		// return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
	}
	// endregion

	// region C-style OpenAL setters.
	public AlBuffer<BufferT> setInt(final int p_alEnum, final int p_value) {
		AL11.alBufferi(this.id, p_alEnum, p_value);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setFloat(final int p_alEnum, final float p_value) {
		AL11.alBufferf(this.id, p_alEnum, p_value);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setIntVector(final int p_alEnum, final int... p_values) {
		AL11.alBufferiv(this.id, p_alEnum, p_values);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setFloatVector(final int p_alEnum, final float... p_values) {
		AL11.alBufferfv(this.id, p_alEnum, p_values);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setIntTriplet(final int p_alEnum, final int... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`alBuffer::setIntTriplet()` cannot take an array of size other than `3`!");

		AL11.alBuffer3i(this.id, p_alEnum, p_values[0], p_values[1], p_values[2]);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
		AL11.alBuffer3i(this.id, p_alEnum, p_i1, p_i2, p_i3);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setFloatTriplet(final int p_alEnum, final float... p_values) {
		if (p_values.length != 3)
			throw new IllegalArgumentException(
					"`alBuffer::setFloatTriplet()` cannot take an array of size other than `3`!");

		AL11.alBuffer3f(this.id, p_alEnum, p_values[0], p_values[1], p_values[2]);
		this.alMan.checkAlError();
		return this;
	}

	public AlBuffer<BufferT> setFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
		AL11.alBuffer3f(this.id, p_alEnum, p_f1, p_f2, p_f3);
		this.alMan.checkAlError();
		return this;
	}
	// endregion

	// region Getters.
	public int getId() {
		return this.id;
	}

	public int getSize() {
		return this.getInt(AL10.AL_SIZE);
	}

	public int getBits() {
		return this.getInt(AL10.AL_BITS);
	}

	public int getChannels() {
		return this.getInt(AL10.AL_CHANNELS);
	}

	public BufferT getData() {
		return this.data;
	}

	public int getSampleRate() {
		return this.getInt(ALC10.ALC_FREQUENCY);
	}
	// endregion

	// region Setters.
	// `alBufferi()` is only for extensions!
	/*
	 * public AlBuffer<BufferT> setBits(final int p_bits) {
	 * AL11.alBufferi(this.id, AL10.AL_BITS, p_bits);
	 * return this;
	 * }
	 * 
	 * public AlBuffer<BufferT> setChannels(final int p_channels) {
	 * AL11.alBufferi(this.id, AL10.AL_CHANNELS, p_channels);
	 * return this;
	 * }
	 * 
	 * public AlBuffer<BufferT> setSampleRate(final int p_sampleRate) {
	 * AL11.alBufferi(this.id, AL10.AL_FREQUENCY, p_sampleRate);
	 * return this;
	 * }
	 */
	// endregion

	@Override
	protected void disposeImpl() {
		AL10.alDeleteBuffers(this.id);
		this.alMan.checkAlError();
		AlBuffer.ALL_INSTANCES.remove(this);
	}

}

package com.brahvim.nerd.openal.objects;

import java.io.File;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryStack;

// If "`BufferT`" sounds weird to you, check out: 
// https://stackoverflow.com/a/30146204/
public abstract class AlBuffer<BufferT extends Buffer> extends AlNativeResource<Integer> {

    // region Fields.
    protected static final Vector<AlBuffer<?>> ALL_INSTANCES = new Vector<>(0);

    // No OpenAL implementation provides `AL_DATA`.
    // Storing it here!
    protected int alFormat;
    protected BufferT data;
    // endregion

    // region Constructors.
    protected AlBuffer(final NerdAl p_alMan) {
        super(p_alMan);
        AlBuffer.ALL_INSTANCES.add(this);

        super.id = AL10.alGenBuffers();
        super.MAN.checkAlError();
    }

    @SuppressWarnings("unchecked")
    protected AlBuffer(final AlBuffer<?> p_buffer) {
        super(p_buffer.MAN);
        AlBuffer.ALL_INSTANCES.add(this);

        this.alFormat = p_buffer.alFormat;

        super.id = AL10.alGenBuffers();
        super.MAN.checkAlError();

        // These aren't set individually:
        // this.setBits(p_buffer.getBits());
        // this.setChannels(p_buffer.getChannels());
        this.setDataImpl(p_buffer.alFormat, (BufferT) p_buffer.getData(), p_buffer.getSampleRate());
        super.MAN.checkAlError();
    }

    protected AlBuffer(final NerdAl p_alMan, final int p_id) {
        super(p_alMan);

        if (!super.MAN.isBuffer(p_id)) {
            throw new IllegalArgumentException("`AlBuffer::AlBuffer(NerdAL, int)` received an invalid buffer ID.");
        }

        AlBuffer.ALL_INSTANCES.add(this);
        super.id = p_id;
    }
    // endregion

    // region Instance collection queries.
    public static int getNumInstances() {
        return AlBuffer.ALL_INSTANCES.size();
    }

    public static List<AlBuffer<?>> getAllInstances() {
        return new ArrayList<>(AlBuffer.ALL_INSTANCES);
    }
    // endregion

    // region `abstract` methods (and overloads, with their implementations).
    public AlBuffer<?> loadFrom(final String p_path) {
        this.loadFrom(new File(p_path)); // Also invoke `AlNativeResource::shouldDispose()`.
        return this;
    }

    public AlBuffer<?> loadFrom(final File p_file) {
        super.setDisposability(false);
        this.loadFromImpl(p_file);
        return this;
    }

    protected abstract AlBuffer<?> loadFromImpl(final File p_file);

    public void setData(final int p_format, final BufferT p_buffer, final int p_sampleRate) {
        this.data = p_buffer;
        this.alFormat = p_format;
        this.setDataImpl(p_format, p_buffer, p_sampleRate);
        super.MAN.checkAlError();
    }

    protected abstract void setDataImpl(int p_format, BufferT p_buffer, int p_sampleRate);
    // endregion

    // region C-style OpenAL getters.
    public int getInt(final int p_alEnum) {

        if (super.hasDisposed) {
            return Integer.MIN_VALUE;
        }

        return AL10.alGetBufferi(super.id, p_alEnum);
    }

    public float getFloat(final int p_alEnum) {

        if (super.hasDisposed) {
            return -Float.MAX_VALUE;
        }

        return AL10.alGetBufferf(super.id, p_alEnum);
    }

    // Vectors in OpenAL are not large and can be allocated on the stack just fine.
    public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final IntBuffer intBuffer = MemoryStack.stackMallocInt(p_vecSize);

        if (super.hasDisposed) {
            return new int[0];
        }

        AL11.alGetBufferiv(super.id, p_alEnum, intBuffer);
        MemoryStack.stackPop();

        return intBuffer.array();
    }

    public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(p_vecSize);

        if (super.hasDisposed) {
            return new float[0];
        }

        AL11.alGetBufferfv(super.id, p_alEnum, floatBuffer);
        MemoryStack.stackPop();

        return floatBuffer.array();
    }

    public int[] getIntTriplet(final int p_alEnum) {
        MemoryStack.stackPush();
        final IntBuffer intBuffer = MemoryStack.stackMallocInt(3);

        if (super.hasDisposed) {
            return new int[0];
        }

        AL11.alGetBufferiv(super.id, p_alEnum, intBuffer);
        MemoryStack.stackPop();

        return intBuffer.array();
    }

    public /* `float[]` */ float[] getFloatTriplet(final int p_alEnum) {
        MemoryStack.stackPush();
        final FloatBuffer floatBuffer = MemoryStack.stackMallocFloat(3);

        if (super.hasDisposed) {
            return new float[0];
        }

        AL11.alGetBufferfv(super.id, p_alEnum, floatBuffer);
        MemoryStack.stackPop();

        return floatBuffer.array();
        // return new PVector(floatBuffer.get(), floatBuffer.get(), floatBuffer.get());
    }
    // endregion

    // region C-style OpenAL setters.
    public AlBuffer<BufferT> setInt(final int p_alEnum, final int p_value) {
        AL11.alBufferi(super.id, p_alEnum, p_value);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setFloat(final int p_alEnum, final float p_value) {
        AL11.alBufferf(super.id, p_alEnum, p_value);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setIntVector(final int p_alEnum, final int... p_values) {
        AL11.alBufferiv(super.id, p_alEnum, p_values);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setFloatVector(final int p_alEnum, final float... p_values) {
        AL11.alBufferfv(super.id, p_alEnum, p_values);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setIntTriplet(final int p_alEnum, final int... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`alBuffer::setIntTriplet()` cannot take an array of size other than `3`!");
        }

        AL11.alBuffer3i(super.id, p_alEnum, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
        AL11.alBuffer3i(super.id, p_alEnum, p_i1, p_i2, p_i3);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setFloatTriplet(final int p_alEnum, final float... p_values) {
        if (p_values.length != 3) {
            throw new IllegalArgumentException(
                    "`alBuffer::setFloatTriplet()` cannot take an array of size other than `3`!");
        }

        AL11.alBuffer3f(super.id, p_alEnum, p_values[0], p_values[1], p_values[2]);
        super.MAN.checkAlError();
        return this;
    }

    public AlBuffer<BufferT> setFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
        AL11.alBuffer3f(super.id, p_alEnum, p_f1, p_f2, p_f3);
        super.MAN.checkAlError();
        return this;
    }
    // endregion

    // region Getters.
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
     * AL11.alBufferi(super.id, AL10.AL_BITS, p_bits);
     * return this;
     * }
     * 
     * public AlBuffer<BufferT> setChannels(final int p_channels) {
     * AL11.alBufferi(super.id, AL10.AL_CHANNELS, p_channels);
     * return this;
     * }
     * 
     * public AlBuffer<BufferT> setSampleRate(final int p_sampleRate) {
     * AL11.alBufferi(super.id, AL10.AL_FREQUENCY, p_sampleRate);
     * return this;
     * }
     */
    // endregion
    @Override
    protected void disposeImpl() {
        AL10.alDeleteBuffers(super.id);
        super.MAN.checkAlError();
        AlBuffer.ALL_INSTANCES.remove(this);
    }

}

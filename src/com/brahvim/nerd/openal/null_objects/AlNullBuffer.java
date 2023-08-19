package com.brahvim.nerd.openal.null_objects;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.brahvim.nerd.openal.objects.AlBuffer;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullBuffer extends AlBuffer<Buffer> implements AlNullObject {

    public AlNullBuffer(final NerdAl p_alMan) {
        super(p_alMan, 0);
        AlBuffer.ALL_INSTANCES.remove(this);
    }

    @Override
    protected void disposeImpl() {
    }

    @Override
    public int getBits() {
        return 0;
    }

    @Override
    public int getChannels() {
        return 0;
    }

    @Override
    public Buffer getData() {
        return ByteBuffer.allocate(0);
    }

    @Override
    public float getFloat(final int p_alEnum) {
        return 0;
    }

    @Override
    public float[] getFloatTriplet(final int p_alEnum) {
        return new float[3];
    }

    @Override
    public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
        return new float[p_vecSize];
    }

    @Override
    public int getInt(final int p_alEnum) {
        return 0;
    }

    @Override
    public int[] getIntTriplet(final int p_alEnum) {
        return new int[3];
    }

    @Override
    public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
        return new int[p_vecSize];
    }

    @Override
    public int getSampleRate() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    // @Override
    // public AlBuffer<?> loadFrom(final String p_path) {
    // throw new UnsupportedOperationException("`AlNullBuffer`s cannot load
    // files.");
    // }
    // @Override
    // public AlBuffer<?> loadFrom(final File p_file) {
    // throw new UnsupportedOperationException("`AlNullBuffer`s cannot load
    // files.");
    // }
    @Override
    protected AlBuffer<?> loadFromImpl(final File p_file) {
        throw new UnsupportedOperationException("`AlNullBuffer`s cannot load files.");
    }

    @Override
    public void setData(final int p_format, final Buffer p_buffer, final int p_sampleRate) {
    }

    @Override
    protected void setDataImpl(final int p_format, final Buffer p_buffer, final int p_sampleRate) {
    }

    @Override
    public AlBuffer<Buffer> setFloat(final int p_alEnum, final float p_value) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setFloatTriplet(final int p_alEnum, final float... p_values) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setFloatVector(final int p_alEnum, final float... p_values) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setInt(final int p_alEnum, final int p_value) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setIntTriplet(final int p_alEnum, final int... p_values) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
        return this;
    }

    @Override
    public AlBuffer<Buffer> setIntVector(final int p_alEnum, final int... p_values) {
        return this;
    }

    @Override
    protected void framelyCallback() {
    }

    // @Override
    // public NerdAl getAlMan() {
    // return super.getAlMan();
    // }
    // @Override
    // public Integer getId() {
    // return super.getId();
    // }
}

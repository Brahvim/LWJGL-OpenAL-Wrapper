package com.brahvim.nerd.openal.null_objects;

import java.nio.Buffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import com.brahvim.nerd.openal.objects.AlAuxiliaryEffectSlot;
import com.brahvim.nerd.openal.objects.AlBuffer;
import com.brahvim.nerd.openal.objects.AlBufferStream;
import com.brahvim.nerd.openal.objects.AlFilter;
import com.brahvim.nerd.openal.objects.AlSource;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullSource extends AlSource implements AlNullObject {

    public AlNullSource(final NerdAl p_alMan) {
        super(p_alMan, 0);
        AlSource.ALL_INSTANCES.remove(this);
    }

    @Override
    public AlFilter attachAuxiliarySendFilter(final AlFilter p_filter) {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    public AlFilter attachDirectFilter(final AlFilter p_filter) {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    public AlFilter detachAuxiliarySendFilter() {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    public AlFilter detachDirectFilter() {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    protected void disposeImpl() {
    }

    @Override
    public void disposeWithBuffer() {
    }

    @Override
    protected void framelyCallback() {
    }

    @Override
    public float getAirAbsorptionFactor() {
        return 0;
    }

    @Override
    public AlFilter getAuxiliarySendFilter() {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    public float getAuxiliarySendFilterGainAuto() {
        return 0;
    }

    @Override
    public float getAuxiliarySendFilterGainHfAuto() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Buffer> AlBuffer<T> getBuffer() {
        return (AlBuffer<T>) super.MAN.DEFAULTS.buffer;
    }

    @Override
    public int getBuffersProcessed() {
        return 0;
    }

    @Override
    public int getBuffersQueued() {
        return 0;
    }

    @Override
    public int getByteOffset() {
        return 0;
    }

    @Override
    public float getConeInnerAngle() {
        return 0;
    }

    @Override
    public float getConeOuterAngle() {
        return 0;
    }

    @Override
    public float getConeOuterGain() {
        return 0;
    }

    @Override
    public float getConeOuterGainHf() {
        return 0;
    }

    @Override
    public AlFilter getDirectFilter() {
        return super.MAN.DEFAULTS.filter;
    }

    @Override
    public float getDirectFilterGainHfAuto() {
        return 0;
    }

    @Override
    public float[] getDirection() {
        return new float[3];
    }

    @Override
    public AlAuxiliaryEffectSlot getEffectSlot() {
        return super.MAN.DEFAULTS.auxiliaryEffectSlot;
    }

    @Override
    public int getEffectSlotId() {
        return 0;
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
    public float getGain() {
        return 0;
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
    public float getMaxDistance() {
        return 0;
    }

    @Override
    public float getMaxGain() {
        return 0;
    }

    @Override
    public float getMinGain() {
        return 0;
    }

    @Override
    public float getPitchMultiplier() {
        return 0;
    }

    @Override
    public float[] getPosition() {
        return new float[3];
    }

    @Override
    public float getReferenceDistance() {
        return 0;
    }

    @Override
    public float getRolloffFactor() {
        return 0;
    }

    @Override
    public float getRoomRolloffFactor() {
        return 0;
    }

    @Override
    public int getSampleOffset() {
        return 0;
    }

    @Override
    public int getSecOffset() {
        return 0;
    }

    @Override
    public int getSourceState() {
        return AL10.AL_STOPPED;
    }

    @Override
    public int getSourceType() {
        return AL11.AL_UNDETERMINED;
    }

    @Override
    public AlBufferStream getStream() {
        return super.MAN.DEFAULTS.stream;
    }

    @Override
    public float[] getVelocity() {
        return new float[3];
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return true;
    }

    @Override
    public void loop(final boolean p_value) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void play() {
    }

    @Override
    public void playThenDispose() {
    }

    @Override
    public void playThenDisposeWithBuffer() {
    }

    @Override
    public void queueBuffers(final AlBuffer<?> p_buffer) {
    }

    @Override
    public void queueBuffers(final AlBuffer<?>... p_buffers) {
    }

    @Override
    public void rewind() {
    }

    @Override
    public AlSource setAirAbsorptionFactor(final float p_value) {
        return this;
    }

    @Override
    public AlSource setAuxiliarySendFilterGainAuto(final float p_value) {
        return this;
    }

    @Override
    public AlSource setAuxiliarySendFilterGainHfAuto(final float p_value) {
        return this;
    }

    @Override
    public AlSource setBuffer(final AlBuffer<?> p_buffer) {
        return this;
    }

    @Override
    public AlSource setByteOffset(final int p_value) {
        return this;
    }

    @Override
    public AlSource setConeInnerAngle(final float p_value) {
        return this;
    }

    @Override
    public AlSource setConeOuterAngle(final float p_value) {
        return this;
    }

    @Override
    public AlSource setConeOuterGain(final float p_value) {
        return this;
    }

    @Override
    public AlSource setConeOuterGainHf(final float p_value) {
        return this;
    }

    @Override
    public AlSource setDirectFilterGainHfAuto(final float p_value) {
        return this;
    }

    @Override
    public AlSource setDirection(final float[] p_value) {
        return this;
    }

    @Override
    public AlSource setDirection(final float p_x, final float p_y, final float p_z) {
        return this;
    }

    @Override
    public AlAuxiliaryEffectSlot setEffectSlot(final AlAuxiliaryEffectSlot p_effectSlot) {
        return super.MAN.DEFAULTS.auxiliaryEffectSlot;
    }

    @Override
    public AlSource setFloat(final int p_alEnum, final float p_value) {
        return this;
    }

    @Override
    public AlSource setFloatTriplet(final int p_alEnum, final float... p_value) {
        return this;
    }

    @Override
    public AlSource setFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
        return this;
    }

    @Override
    public AlSource setFloatVector(final int p_alEnum, final float... p_values) {
        return this;
    }

    @Override
    public AlSource setGain(final float p_value) {
        return this;
    }

    @Override
    public AlSource setInt(final int p_alEnum, final int p_value) {
        return this;
    }

    @Override
    public AlSource setIntTriplet(final int p_alEnum, final int... p_value) {
        return this;
    }

    @Override
    public AlSource setIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
        return this;
    }

    @Override
    public AlSource setIntVector(final int p_alEnum, final int... p_values) {
        return this;
    }

    @Override
    public AlSource setMaxDistance(final float p_value) {
        return this;
    }

    @Override
    public AlSource setMaxGain(final float p_value) {
        return this;
    }

    @Override
    public AlSource setMinGain(final float p_value) {
        return this;
    }

    @Override
    public AlSource setPitchMultiplier(final float value) {
        return this;
    }

    @Override
    public AlSource setPosition(final float[] p_value) {
        return this;
    }

    @Override
    public AlSource setPosition(final float p_x, final float p_y, final float p_z) {
        return this;
    }

    @Override
    public AlSource setReferenceDistance(final float p_value) {
        return this;
    }

    @Override
    public AlSource setRolloffFactor(final float p_value) {
        return this;
    }

    @Override
    public AlSource setRoomRolloffFactor(final float p_value) {
        return this;
    }

    @Override
    public AlSource setSampleOffset(final int p_value) {
        return this;
    }

    @Override
    public AlSource setSecOffset(final int p_value) {
        return this;
    }

    @Override
    public AlSource setStream(final AlBufferStream p_alDataStream) {
        return this;
    }

    @Override
    public AlSource setVelocity(final float[] p_value) {
        return this;
    }

    @Override
    public AlSource setVelocity(final float p_x, final float p_y, final float p_z) {
        return this;
    }

    @Override
    public void stop() {
    }

    @Override
    public void unqueueBuffers(final AlBuffer<?> p_buffer) {
    }

    @Override
    public void unqueueBuffers(final AlBuffer<?>... p_buffers) {
    }

}

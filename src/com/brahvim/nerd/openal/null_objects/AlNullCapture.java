package com.brahvim.nerd.openal.null_objects;

import java.nio.ByteBuffer;

import com.brahvim.nerd.openal.al_buffers.AlWavBuffer;
import com.brahvim.nerd.openal.objects.AlBuffer;
import com.brahvim.nerd.openal.objects.AlCapture;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullCapture extends AlCapture implements AlNullObject {

    public AlNullCapture(final NerdAl p_alMan) {
        super(p_alMan, "");
        AlCapture.ALL_INSTANCES.remove(this);
    }

    @Override
    protected void disposeImpl() {
    }

    // @Override
    // public Thread getCaptureThread() {
    // return Thread.currentThread(); // Superclass returns `null` here.
    // }
    @Override
    public ByteBuffer getCapturedData() {
        return ByteBuffer.allocate(0);
    }

    @Override
    public boolean isCapturing() {
        return false;
    }

    @Override
    public void startCapturing() {
    }

    @Override
    public void startCapturing(final int p_format) {
    }

    @Override
    public void startCapturing(final int p_sampleRate, final int p_format) {
    }

    @Override
    public synchronized void startCapturing(final int p_sampleRate, final int p_format, final int p_samplesPerBuffer) {
    }

    @Override
    protected synchronized void startCapturingImpl(
            final int p_sampleRate, final int p_format, final int p_samplesPerBuffer) {
    }

    @Override
    public AlWavBuffer stopCapturing(final AlWavBuffer p_buffer) {
        return (AlWavBuffer) (AlBuffer<?>) super.MAN.DEFAULTS.buffer;
    }

    @Override
    public synchronized ByteBuffer stopCapturing() {
        return ByteBuffer.allocate(0);
    }

    @Override
    public ByteBuffer storeIntoBuffer(final AlWavBuffer p_buffer) {
        return ByteBuffer.allocate(0);
    }

    @Override
    public AlWavBuffer storeIntoBuffer() {
        return (AlWavBuffer) (AlBuffer<?>) super.MAN.DEFAULTS.buffer;
    }

    @Override
    protected void framelyCallback() {
    }

}

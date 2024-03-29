package com.brahvim.nerd.openal.al_buffers;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import com.brahvim.nerd.openal.objects.AlBuffer;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlUnknownFormatBuffer extends AlBuffer<Buffer> {

    // region Constructors.
    public AlUnknownFormatBuffer(final NerdAl p_alMan) {
        super(p_alMan);
    }

    public AlUnknownFormatBuffer(final AlBuffer<?> p_buffer) {
        super(p_buffer);
    }

    public AlUnknownFormatBuffer(final NerdAl p_alMan, final int p_id) {
        super(p_alMan, p_id);
    }
    // endregion

    @Override
    protected AlBuffer<Buffer> loadFromImpl(final File p_file) {
        throw new UnsupportedOperationException("""
                `AlNativeBuffer` exists for types you may add yourself!
                `AlNativeBuffer::loadFrom(File)` has no idea what you're trying to do.
                """);
        // return switch (PApplet.getExtension(p_file.getAbsolutePath())) {
        // case "wav" ->
        // case "ogg": ->
        // default: // ?!??!?
        // }
    }

    @Override
    protected void setDataImpl(final int p_format, final Buffer p_buffer, final int p_sampleRate) {
        AL10.alBufferData(super.id, p_format, (ByteBuffer) p_buffer, p_sampleRate);
    }

}

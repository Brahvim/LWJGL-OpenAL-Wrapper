package com.brahvim.nerd.openal.null_objects;

import com.brahvim.nerd.openal.objects.AlBufferStream;
import com.brahvim.nerd.openal.objects.AlSource;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullBufferStream extends AlBufferStream implements AlNullObject {

    public AlNullBufferStream(final NerdAl p_alMan, final AlSource p_source) {
        super(p_alMan, p_source);
        super.USED_BUFFERS.trimToSize();
        super.UNUSED_BUFFERS.trimToSize();
        AlBufferStream.ALL_INSTANCES.remove(this);
    }

    @Override
    public synchronized void addBytes(final int p_alFormat, final byte[] p_bytes, final int p_sampleRate) {
    }

    @Override
    protected synchronized void framelyCallback() {
    }

    @Override
    public synchronized void stop() {
    }

}

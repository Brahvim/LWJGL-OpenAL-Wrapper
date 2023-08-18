package com.brahvim.nerd.openal.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.openal.AL10;

import com.brahvim.nerd.openal.al_buffers.AlOggBuffer;

/**
 * Allows for {@link AlBuffer}s ({@link AlOggBuffer}s for now) to be put
 * into the queue of an {@link AlSource}, essentially allowing for a stream of
 * audio buffers. Yes, this means reading from files as-we-go, or networking!
 *
 * <p>
 * Feel free to imagine... "Voice chat over UDP or WebRTC using OpenAL"!
 *
 * <h2>Currently undeveloped!...</h2>
 * ...Sorry. This should exist one day!
 */
public class AlBufferStream {

	// PS This class should also be a `AlNativeResource`, but without the "native"
	// part. Need to write a class for stuff that can be "disposed off" like this,
	// but without a handle to the thing operating. Not `java.io.Closeable`, though!

	// region Fields.
	protected static final Vector<AlBufferStream> ALL_INSTANCES = new Vector<>(0);

	protected final NerdAl MAN;
	protected final AlSource SOURCE;
	protected final ArrayList<AlOggBuffer> USED_BUFFERS = new ArrayList<>(3), UNUSED_BUFFERS = new ArrayList<>(5);
	// endregion

	public AlBufferStream(final NerdAl p_alMan, final AlSource p_source) {
		this.MAN = p_alMan;
		this.SOURCE = p_source;
		AlBufferStream.ALL_INSTANCES.add(this);
	}

	public synchronized void addBytes(final int p_alFormat, final byte[] p_bytes, final int p_sampleRate) {
		// This is fine - `ArrayList`s don't decrease their size anyway.
		if (this.UNUSED_BUFFERS.isEmpty())
			this.UNUSED_BUFFERS.add(new AlOggBuffer(this.MAN));

		final AlOggBuffer toQueue = this.UNUSED_BUFFERS.remove(0);
		toQueue.setData(p_alFormat,
				ByteBuffer.wrap(p_bytes).order(ByteOrder.nativeOrder()).asShortBuffer(),
				p_sampleRate);
		this.SOURCE.queueBuffers(toQueue);
		this.MAN.checkAlError();
		this.USED_BUFFERS.add(toQueue);
	}

	// Yo! Saw this class in the stack trace?
	// ...you might wanna check out the loop in this method!:
	protected synchronized void framelyCallback() {
		// For each buffer (backwards),
		for (int i = this.SOURCE.getBuffersProcessed() - 1; i != 0; i--) {
			final AlOggBuffer b = this.USED_BUFFERS.get(i);
			this.SOURCE.unqueueBuffers(b); // ..Tell the source to unqueue it,
			this.MAN.checkAlError();
			this.UNUSED_BUFFERS.add(this.USED_BUFFERS.remove(i)); // ..And add it to our pool!
		}
	}

	public synchronized void stop() {
		// Page `14` of the "OpenAL Programmer's Guide" mentions this
		// nice shortcut to remove all attached buffers from a source:
		this.SOURCE.setInt(AL10.AL_BUFFER, 0);
		this.MAN.checkAlError();

		// Should I actually be doing this...?
		this.MAN.checkAlError();
	}

	public List<AlOggBuffer> getAlBuffers() {
		return new ArrayList<>(this.USED_BUFFERS);
	}

}

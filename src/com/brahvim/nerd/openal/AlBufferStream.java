package com.brahvim.nerd.openal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
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
	protected static final Vector<AlBufferStream> ALL_INSTANCES = new Vector<>();

	private final NerdAl alMan;
	private final AlSource source;
	private final ArrayList<AlOggBuffer> buffers = new ArrayList<>(3), unusedBuffersPool = new ArrayList<>(5);
	// endregion

	public AlBufferStream(final NerdAl p_alMan, final AlSource p_source) {
		this.alMan = p_alMan;
		this.source = p_source;
		AlBufferStream.ALL_INSTANCES.add(this);
	}

	public synchronized void addBytes(final int p_alFormat, final byte[] p_bytes, final int p_sampleRate) {
		// This is fine - `ArrayList`s don't decrease their size anyway.
		if (this.unusedBuffersPool.isEmpty())
			this.unusedBuffersPool.add(new AlOggBuffer(this.alMan));

		final AlOggBuffer toQueue = this.unusedBuffersPool.remove(0);
		toQueue.setData(p_alFormat, ByteBuffer.wrap(p_bytes)
				.order(ByteOrder.nativeOrder()).asShortBuffer(),
				p_sampleRate);
		this.source.queueBuffers(toQueue);
		this.alMan.checkAlError();
		this.buffers.add(toQueue);
	}

	// Yo! Saw this class in the stack trace?
	// ...you might wanna check out the loop in this method!:
	protected void framelyCallback() {
		// For each buffer (backwards),
		for (int i = this.source.getBuffersProcessed() - 1; i != 0; i--) {
			final AlOggBuffer b = this.buffers.get(i);
			this.source.unqueueBuffers(b); // ..Tell the source to unqueue it,
			this.alMan.checkAlError();
			this.unusedBuffersPool.add(this.buffers.remove(i)); // ..And add it to our pool!
		}
	}

	public synchronized void stop() {
		// Page `14` of the "OpenAL Programmer's Guide" mentions this
		// nice shortcut to remove all attached buffers from a source:
		this.source.setInt(AL10.AL_BUFFER, 0);
		this.alMan.checkAlError();

		// Should I actually be doing this...?
		this.alMan.checkAlError();
	}

	public ArrayList<AlOggBuffer> getAlBuffers() {
		return new ArrayList<>(this.buffers);
	}

}

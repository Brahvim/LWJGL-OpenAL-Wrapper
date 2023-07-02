package com.brahvim.nerd.openal.al_buffers;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;

import com.brahvim.nerd.openal.AlBuffer;
import com.brahvim.nerd.openal.NerdAl;

public class AlOggBuffer extends AlBuffer<ShortBuffer> {

	protected static final ArrayList<AlOggBuffer> ALL_INSTANCES = new ArrayList<>();

	// region Constructors.
	public AlOggBuffer(final NerdAl p_alMan) {
		super(p_alMan);
		AlOggBuffer.ALL_INSTANCES.add(this);
	}

	public AlOggBuffer(final AlBuffer<?> p_buffer) {
		super(p_buffer);
		AlOggBuffer.ALL_INSTANCES.add(this);
	}

	public AlOggBuffer(final NerdAl p_alMan, final int p_id) {
		super(p_alMan, p_id);
		AlOggBuffer.ALL_INSTANCES.add(this);
	}
	// endregion

	// Free the buffer (or not) :D
	@Override
	protected void disposeImpl() {
		super.disposeImpl();
		LibCStdlib.free(super.data); // Yep, we literally made Java, C. "Welcome to javac!" :joy:
		AlOggBuffer.ALL_INSTANCES.remove(this);
	}

	@Override
	protected void setDataImpl(final int p_format, final ShortBuffer p_buffer, final int p_sampleRate) {
		AL10.alBufferData(super.id, p_format, p_buffer, p_sampleRate);
	}

	@Override
	protected AlOggBuffer loadFromImpl(final File p_file) {
		if (super.hasDisposed)
			throw new IllegalArgumentException("This `AlBuffer` was deallocated. Please use a new one!");

		// If we have any data, we free it:
		if (super.data != null) {
			LibCStdlib.free(super.data); // Yep, we literally made Java, C. "Welcome to javac!" :joy:
			super.data = null;
		}

		// A note about the use of `org.lwjgl.system.MemoryStack`:
		/*
		 * LWJGL's `MemoryStack` class allows for stack allocations.
		 * They're faster for this case - where we are allocating amounts of memory the
		 * stack can handle into buffers, which is why we use them here. Java would
		 * otherwise use the heap for any object, including these `java.nio.Buffer`
		 * subclass instances used here, which is slower for a case like this.
		 * 
		 * After all, the reason why we're using buffers is just that we're supposed to
		 * use pointers with C API calls to simulate multiple return values. Why not
		 * also make them faster to access?
		 */

		try {
			MemoryStack.stackPush();
			final IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);

			MemoryStack.stackPush();
			final IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);

			// The bigger data (the audio) we're loading. Definitely goes on the heap!

			final ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(
					p_file.getCanonicalPath(), channelsBuffer, sampleRateBuffer);

			if (rawAudioBuffer == null) {
				// System.err.println("STB failed to load audio data!");
				MemoryStack.stackPop();
				MemoryStack.stackPop();
			}

			// Give the OpenAL buffer the data.
			// (Sure, I'll just use a raw AL call...):
			AL10.alBufferData(super.id,
					super.alFormat = channelsBuffer.get() == 1
							? AL10.AL_FORMAT_MONO16
							: AL10.AL_FORMAT_STEREO16,
					rawAudioBuffer, sampleRateBuffer.get());

			// We're done. Remove the previous two allocations.
			MemoryStack.stackPop();
			MemoryStack.stackPop();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return this;
	}

}

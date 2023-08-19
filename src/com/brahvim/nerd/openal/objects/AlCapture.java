package com.brahvim.nerd.openal.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

import com.brahvim.nerd.openal.al_buffers.AlWavBuffer;
import com.brahvim.nerd.openal.al_exceptions.AlcException;

public class AlCapture extends AlNativeResource<Long> {

	// region Fields.
	protected static final Vector<AlCapture> ALL_INSTANCES = new Vector<>(0);

	// This is here literally just for naming threads!:
	protected static final AtomicInteger NUM_ACTIVE_INSTANCES = new AtomicInteger();

	protected final String physicalDeviceName;

	protected Thread captureThread;
	protected ByteBuffer capturedData = ByteBuffer.allocate(0);

	// Last capture info:
	protected int lastCapSampleRate = -1, lastCapFormat = -1;
	// endregion

	// region Constructors.
	public AlCapture(final NerdAl p_alMan) {
		this(p_alMan, AlCapture.getDefaultDeviceName());
		AlCapture.ALL_INSTANCES.add(this);
	}

	public AlCapture(final NerdAl p_alMan, final String p_deviceName) {
		super(p_alMan);
		AlCapture.ALL_INSTANCES.add(this);
		this.physicalDeviceName = p_deviceName;
	}
	// endregion

	// region Instance management.
	public static List<AlCapture> getAllInstances() {
		return new ArrayList<>(AlCapture.ALL_INSTANCES);
	}

	public static int getNumInstances() {
		return AlCapture.ALL_INSTANCES.size();
	}

	public static int getNumInstancesCurrentlyCapturing() {
		return AlCapture.NUM_ACTIVE_INSTANCES.get();
	}
	// endregion

	// region Capture queries.
	public AlWavBuffer stopCapturing(final AlWavBuffer p_buffer) {
		this.stopCapturing();
		this.storeIntoBuffer(p_buffer);
		return p_buffer;
	}

	public synchronized ByteBuffer stopCapturing() {
		if (this.captureThread == null || !this.captureThread.isAlive())
			return this.capturedData;

		this.captureThread.interrupt();

		ALC11.alcCaptureCloseDevice(super.id);
		super.MAN.checkAlcError();

		AlCapture.NUM_ACTIVE_INSTANCES.decrementAndGet();
		return this.capturedData;
	}

	public static String getDefaultDeviceName() {
		return ALC10.alcGetString(0, ALC11.ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER);
	}

	// region `startCapturing()` overloads.
	public void startCapturing() {
		this.startCapturing(44100, AL10.AL_FORMAT_MONO8, 1024);
	}

	public void startCapturing(final int p_format) {
		this.startCapturing(44100, p_format, 1024);
	}

	public void startCapturing(final int p_sampleRate, final int p_format) {
		this.startCapturing(p_sampleRate, p_format, p_format);
	}

	public synchronized void startCapturing(final int p_sampleRate, final int p_format, final int p_samplesPerBuffer) {
		if (this.isCapturing()) {
			System.err.printf(
					"An instance of `%s` cannot start capturing whilst already doing it!%n",
					this.getClass().getName());
			return;
		}

		AlCapture.NUM_ACTIVE_INSTANCES.incrementAndGet();
		this.startCapturingImpl(p_sampleRate, p_format, p_samplesPerBuffer);
	}

	protected synchronized void startCapturingImpl(
			final int p_sampleRate, final int p_format, final int p_samplesPerBuffer) {

		// region Preparing to capture.
		// Store the last ones.
		this.lastCapFormat = p_format;
		this.lastCapSampleRate = p_sampleRate;

		// Open the capture device,
		super.id = ALC11.alcCaptureOpenDevice(this.physicalDeviceName, p_sampleRate, p_format, p_samplesPerBuffer);
		super.MAN.checkAlcError();

		// Begin capturing!:
		ALC11.alcCaptureStart(super.id);
		super.MAN.checkAlcError();
		// endregion

		this.captureThread = new Thread(() -> {
			boolean deviceGotRemoved = false;
			ByteBuffer dataCaptured = ByteBuffer.allocate(0);

			// Capture till `stopCapturing()` is called:
			while (!Thread.interrupted()) {
				super.MAN.checkAlcError();

				final ByteBuffer SAMPLES_BUFFER = ByteBuffer.allocate(p_samplesPerBuffer);

				// for (int i = 0; i < p_samplesPerBuffer; i = ALC10.alcGetInteger(super.id,
				// ALC11.ALC_CAPTURE_SAMPLES))
				// System.out.printf("Captured `%d` samples.\n", i);

				ALC11.alcCaptureSamples(super.id, SAMPLES_BUFFER, p_samplesPerBuffer);

				// region Check if the device gets disconnected (cause of `ALC_INVALID_DEVICE`):
				try {
					super.MAN.checkAlcError();
				} catch (final AlcException e) {
					deviceGotRemoved = true;
					System.err.printf("""
							Audio capture device on thread "%s" has been disconnected amidst a session.
							Recording has stopped.""", this.captureThread.getName());
					this.captureThread.interrupt();
				}
				// endregion

				// Store the old data away:
				final byte[] oldData = dataCaptured.array();
				dataCaptured = ByteBuffer.allocate(oldData.length + p_samplesPerBuffer);
				dataCaptured.put(oldData);
				dataCaptured.put(SAMPLES_BUFFER);
			}

			// When interrupted, stop capturing:
			synchronized (this) {
				if (deviceGotRemoved)
					ALC11.alcCaptureStop(super.id);
				this.capturedData = dataCaptured;
			}
		});

		this.captureThread.setName("OpenAlCaptureThread#" + AlCapture.NUM_ACTIVE_INSTANCES);
		this.captureThread.start();
	}
	// endregion

	public Thread getCaptureThread() {
		return this.captureThread;
	}

	public boolean isCapturing() {
		// return this.captureThread == null ? false : this.captureThread.isAlive();
		return this.captureThread != null && this.captureThread.isAlive();
	}
	// endregion

	// region Using the captured data.
	public ByteBuffer getCapturedData() {
		return this.capturedData;
	}

	public ByteBuffer storeIntoBuffer(final AlWavBuffer p_buffer) {
		Objects.requireNonNull(p_buffer,
				"`AlCapture::storeIntoBuffer(AlWavBuffer)` cannot use a `null` buffer.");

		if (this.lastCapFormat == -1 || this.lastCapSampleRate == -1)
			return this.capturedData;

		p_buffer.setData(this.lastCapFormat,
				this.capturedData.order(ByteOrder.nativeOrder()).asIntBuffer(),
				this.lastCapSampleRate);
		return this.capturedData;
	}

	public AlWavBuffer storeIntoBuffer() {
		final AlWavBuffer toRet = new AlWavBuffer(super.MAN);
		toRet.setData(this.lastCapFormat,
				this.capturedData.order(ByteOrder.nativeOrder()).asIntBuffer(),
				this.lastCapSampleRate);
		return toRet;
	}
	// endregion

	@Override
	protected void disposeImpl() {
		ALC11.alcCaptureCloseDevice(super.id);
		AlCapture.ALL_INSTANCES.remove(this);
	}

}

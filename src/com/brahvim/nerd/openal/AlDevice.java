package com.brahvim.nerd.openal;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;

import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EXTDisconnect;
import org.lwjgl.openal.SOFTReopenDevice;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.al_exceptions.NerdAlException;

public class AlDevice extends AlNativeResource<Long> {

	// region Fields.
	protected static final Vector<AlDevice> ALL_INSTANCES = new Vector<>();

	protected String physicalDeviceName = "";
	protected Supplier<String> disconnectionCallback = AlDevice::getDefaultPhysicalDeviceName;
	// endregion

	// region Constructors.
	public AlDevice(final NerdAl p_alMan) {
		this(p_alMan, AlDevice.getDefaultPhysicalDeviceName());
	}

	public AlDevice(final NerdAl p_alMan, final String p_deviceName) {
		super(p_alMan);
		AlDevice.ALL_INSTANCES.add(this);

		this.physicalDeviceName = p_deviceName;
		super.id = ALC10.alcOpenDevice(this.physicalDeviceName);

		// Check for errors here because we can't call `NerdAl::checkAlcError()` yet:
		final int alcError = ALC10.alcGetError(super.id);
		if (alcError != 0)
			throw new AlcException(super.id, alcError);
	}
	// endregion

	// region `static` methods.
	// region Instance collection queries.
	public static int getNumInstances() {
		return AlDevice.ALL_INSTANCES.size();
	}

	public static ArrayList<AlDevice> getAllInstances() {
		return new ArrayList<>(AlDevice.ALL_INSTANCES);
	}
	// endregion

	public static String getDefaultPhysicalDeviceName() {
		return ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
	}

	public static List<String> getPhysicalDevicesNames() {
		return ALUtil.getStringList(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
	}
	// endregion

	// region Connection status.
	public void setDisconnectionCallback(final Supplier<String> p_callback) {
		this.disconnectionCallback = p_callback;
	}

	@Override
	public void framelyCallback() {
		this.resolveDisconnection();
	}

	protected void resolveDisconnection() {
		if (!this.isConnected())
			this.changeEndpoint(this.disconnectionCallback.get());
	}

	public void changeEndpoint(final String p_dvName) {
		if (!SOFTReopenDevice.alcReopenDeviceSOFT(super.id, p_dvName, new int[] { 0 }))
			throw new NerdAlException("`SOFTReopenDevice` failed...");
		this.physicalDeviceName = p_dvName;
	}

	// This uses device handles and not device names. Thus, no `static` version.
	public boolean isConnected() {
		// No idea why this bad stack read works.
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(1); // Stack allocation, "should" be "faster".
		ALC10.alcGetIntegerv(super.id, EXTDisconnect.ALC_CONNECTED, buffer);
		MemoryStack.stackPop();

		return buffer.get() == 1;
	}
	// endregion

	// region Getters (and the `isDefault()` query).
	public String getPhysicalDeviceName() {
		return this.physicalDeviceName;
	}

	@Override
	public NerdAl getAlMan() {
		return super.MAN;
	}

	// I don't want to hold a `boolean` for this...
	public boolean usesDefaultPhysicalDevice() {
		return this.physicalDeviceName.equals(AlDevice.getDefaultPhysicalDeviceName());
	}
	// endregion

	@Override
	protected void disposeImpl() {
		if (!ALC10.alcCloseDevice(super.id))
			throw new NerdAlException("Could not close OpenAL device!");

		super.id = 0L;
		// super.MAN.checkAlcErrors();
		AlDevice.ALL_INSTANCES.remove(this);
	}

}

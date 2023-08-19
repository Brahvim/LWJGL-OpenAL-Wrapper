package com.brahvim.nerd.openal.objects;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EXTDisconnect;
import org.lwjgl.openal.SOFTReopenDevice;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_exceptions.AlUnflaggedException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.null_objects.AlNullDevice;

public class AlDevice extends AlNativeResource<Long> {

    // region Fields.
    protected static final Vector<AlDevice> ALL_INSTANCES = new Vector<>(1);
    protected static final ConcurrentHashMap<String, AlDevice> PHYSICAL_DEVICES_TO_INSTANCES_MAP = new ConcurrentHashMap<>(
            1);

    // OpenAL provides no way to retrieve this, so do we:
    protected String physicalDeviceName = "";
    protected Supplier<String> disconnectionCallback = AlDevice::getDefaultPhysicalDeviceName;
    // endregion

    // region Constructors.
    protected AlDevice(final NerdAl p_alMan) {
        this(p_alMan, AlDevice.getDefaultPhysicalDeviceName());
    }

    protected AlDevice(final NerdAl p_alMan, final String p_physicalDeviceName) {
        super(p_alMan);
        AlDevice.ALL_INSTANCES.add(this);
        super.id = ALC10.alcOpenDevice(p_physicalDeviceName);
        this.checkForErrors();
    }

    protected AlDevice(final NerdAl p_alMan, final long p_id) {
        super(p_alMan);
        AlDevice.ALL_INSTANCES.add(this);

        super.id = p_id;
        this.physicalDeviceName = ALC10.alcGetString(super.id, ALC10.ALC_DEVICE_SPECIFIER);
        this.checkForErrors();
    }

    protected void checkForErrors() {
        // Check for errors here because we can't call `NerdAl::checkAlcError()` yet:
        final int alcError = ALC10.alcGetError(super.id);
        if (alcError != 0) {
            throw new AlcException(super.id, alcError);
        }
    }
    // endregion

    // region `static` methods.
    // region Instance collection queries.
    public static AlDevice getInstanceFromPhysicalDeviceName(final String p_physicalDeviceName) {
        var toRet = AlDevice.PHYSICAL_DEVICES_TO_INSTANCES_MAP.get(p_physicalDeviceName);

        if (toRet == null) {
            toRet = new AlNullDevice(null);
        }

        return toRet;
    }

    public static int getNumInstances() {
        return AlDevice.ALL_INSTANCES.size();
    }

    public static List<AlDevice> getAllInstances() {
        return new ArrayList<>(AlDevice.ALL_INSTANCES);
    }
    // endregion

    public static String getDefaultPhysicalDeviceName() {
        // The extension IS supported!:

        // if
        // (this.definesExtension(NerdAl.NerdAlExtensionsInfo.ENUMERATION_EXT.getExtensionName()))
        // throw new AlExtAbsentException("ALC_ENUMERATION_EXT");
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
        if (!this.isConnected()) {
            this.changeEndpoint(this.disconnectionCallback.get());
        }
    }

    public void changeEndpoint(final String p_dvName) {
        if (!SOFTReopenDevice.alcReopenDeviceSOFT(super.id, p_dvName, new int[] { 0 })) {
            throw new AlUnflaggedException("`SOFTReopenDevice` failed...");
        }
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
    public boolean definesExtension(final String p_alExtName) {
        return ALC10.alcIsExtensionPresent(super.id, p_alExtName);
    }

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
        if (!ALC10.alcCloseDevice(super.id)) {
            throw new AlUnflaggedException("Could not close OpenAL device!");
        }
        AlDevice.ALL_INSTANCES.remove(this);
    }

}

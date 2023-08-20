package com.brahvim.nerd.openal.null_objects;

import java.util.function.Supplier;

import com.brahvim.nerd.openal.objects.AlDevice;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullDevice extends AlDevice implements AlNullObject {

    public AlNullDevice(final NerdAl p_alMan) {
        super(p_alMan, 0);
        AlDevice.ALL_INSTANCES.remove(this);
    }

    @Override
    public void changeEndpoint(final String p_dvName) {
    }

    @Override
    protected void checkForErrors() {
    }

    @Override
    public boolean definesExtension(final String p_alExtName) {
        return false;
    }

    @Override
    protected void disposeImpl() {
    }

    @Override
    public void framelyCallback() {
    }

    // @Override
    // public String getPhysicalDeviceName() {
    // return "";
    // }
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    protected void resolveDisconnection() {
    }

    @Override
    public void setDisconnectionCallback(final Supplier<String> p_callback) {
    }

}

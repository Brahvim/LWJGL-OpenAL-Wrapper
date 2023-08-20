package com.brahvim.nerd.openal.null_objects;

import java.io.File;

import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import com.brahvim.nerd.openal.al_exceptions.AlException;
import com.brahvim.nerd.openal.al_exceptions.AlcException;
import com.brahvim.nerd.openal.objects.AlContext;
import com.brahvim.nerd.openal.objects.AlDevice;
import com.brahvim.nerd.openal.objects.AlSource;
import com.brahvim.nerd.openal.objects.NerdAl;

public class NullNerdAl extends NerdAl {

    private static final NullNerdAl INSTANCE = new NullNerdAl();

    protected NullNerdAl() {
        super((NullNerdAl) null);
    }

    public static NullNerdAl getInstance() {
        return NullNerdAl.INSTANCE;
    }

    @Override
    public int checkAlError() throws AlException {
        return 0;
    }

    @Override
    public int checkAlcError() throws AlcException {
        return 0;
    }

    // None of the `createAl()` methods are here.

    @Override
    public void disposeAllResources() {
    }

    @Override
    public void disposeResources(final boolean p_alsoContexts, final boolean p_alsoDevices) {
    }

    @Override
    public void framelyCallback() {
    }

    @Override
    public float getAlFloat(final int p_alEnum) {
        return 0;
    }

    @Override
    public float[] getAlFloatVector(final int p_alEnum, final int p_vecSize) {
        return new float[p_vecSize];
    }

    @Override
    public int getAlInt(final int p_alEnum) {
        return 0;
    }

    @Override
    public int[] getAlIntVector(final int p_alEnum, final int p_vecSize) {
        return new int[p_vecSize];
    }

    @Override
    public AlContext getContext() {
        return super.DEFAULTS.context;
    }

    @Override
    public long getContextId() {
        return 0;
    }

    @Override
    public AlDevice getDevice() {
        return super.DEFAULTS.device;
    }

    @Override
    public long getDeviceId() {
        return 0;
    }

    @Override
    public String getPhysicalDeviceName() {
        return super.DEFAULTS.device.getPhysicalDeviceName();
    }

    @Override
    public float getDistanceModel() {
        return 0;
    }

    @Override
    public float getDopplerFactor() {
        return 0;
    }

    @Override
    public ALCapabilities getLwjglAlCapabilities() {
        return null;
    }

    @Override
    public ALCCapabilities getLwjglAlContextCapabilities() {
        return super.getLwjglAlContextCapabilities();
    }

    @Override
    public float getSpeedOfSound() {
        return 0;
    }

    @Override
    public boolean isBuffer(final int p_id) {
        return super.isBuffer(p_id);
    }

    @Override
    public boolean isEffect(final int p_id) {
        return super.isEffect(p_id);
    }

    @Override
    public boolean isEffectSlot(final int p_id) {
        return super.isEffectSlot(p_id);
    }

    @Override
    public boolean isFilter(final int p_id) {
        return super.isFilter(p_id);
    }

    @Override
    public boolean isSource(final int p_id) {
        return super.isSource(p_id);
    }

    @Override
    protected void makeContextCurrent() {
        super.makeContextCurrent();
    }

    @Override
    public void setDistanceModel(final int p_value) {
    }

    @Override
    public void setDopplerFactor(final float p_value) {
    }

    @Override
    public void setSpeedOfSound(final float p_value) {
    }

    @Override
    public AlSource sourceFromMp3(final File p_file) {
        return super.DEFAULTS.source;
    }

    @Override
    public AlSource sourceFromMp3(final String p_filePath) {
        return super.DEFAULTS.source;
    }

    @Override
    public AlSource sourceFromOgg(final File p_file) {
        return super.DEFAULTS.source;
    }

    @Override
    public AlSource sourceFromOgg(final String p_filePath) {
        return super.DEFAULTS.source;
    }

    @Override
    public AlSource sourceFromWav(final File p_file) {
        return super.DEFAULTS.source;
    }

    @Override
    public AlSource sourceFromWav(final String p_filePath) {
        return super.DEFAULTS.source;
    }

}
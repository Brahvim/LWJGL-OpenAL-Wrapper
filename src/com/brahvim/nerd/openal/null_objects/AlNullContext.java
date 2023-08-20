package com.brahvim.nerd.openal.null_objects;

import com.brahvim.nerd.openal.objects.AlContext;
import com.brahvim.nerd.openal.objects.AlDevice;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullContext extends AlContext implements AlNullObject {

    public AlNullContext(final NerdAl p_alMan) {
        super(p_alMan, 0);
        AlContext.ALL_INSTANCES.remove(this);
    }

    @Override
    public int checkAlcError() {
        return 0;
    }

    @Override
    protected void disposeImpl() {
    }

    @Override
    public AlDevice getDevice() {
        return super.MAN.DEFAULTS.device;
    }

    @Override
    public float getListenerFloat(final int p_alEnum) {
        return 0;
    }

    @Override
    public float[] getListenerFloatTriplet(final int p_alEnum) {
        return new float[3];
    }

    @Override
    public float[] getListenerFloatVector(final int p_alEnum, final int p_vecSize) {
        return new float[p_vecSize];
    }

    @Override
    public float getListenerGain() {
        return 0;
    }

    @Override
    public int getListenerInt(final int p_alEnum) {
        return 0;
    }

    @Override
    public int[] getListenerIntTriplet(final int p_alEnum) {
        return new int[3];
    }

    @Override
    public int[] getListenerIntVector(final int p_alEnum, final int p_vecSize) {
        return new int[p_vecSize];
    }

    @Override
    public float getListenerMetersPerUnit() {
        return 0;
    }

    @Override
    public float[] getListenerOrientation() {
        return new float[3];
    }

    @Override
    public float[] getListenerPosition() {
        return new float[3];
    }

    @Override
    public float[] getListenerVelocity() {
        return new float[3];
    }

    @Override
    public void setListenerFloat(final int p_alEnum, final float p_value) {
    }

    @Override
    public void setListenerFloatTriplet(final int p_alEnum, final float... p_values) {
    }

    @Override
    public void setListenerFloatTriplet(final int p_alEnum, final float p_f1, final float p_f2, final float p_f3) {
    }

    @Override
    public void setListenerFloatVector(final int p_alEnum, final float... p_values) {
    }

    @Override
    public void setListenerGain(final float p_value) {
    }

    @Override
    public void setListenerInt(final int p_alEnum, final int p_value) {
    }

    @Override
    public void setListenerIntTriplet(final int p_alEnum, final int... p_values) {
    }

    @Override
    public void setListenerIntTriplet(final int p_alEnum, final int p_i1, final int p_i2, final int p_i3) {
    }

    @Override
    public void setListenerIntVector(final int p_alEnum, final int... p_values) {
    }

    @Override
    public void setListenerOrientation(final float... p_values) {
    }

    @Override
    public void setListenerPosition(final float... p_values) {
    }

    @Override
    public void setListenerVelocity(final float... p_values) {
    }

    @Override
    public void setMetersPerUnit(final float p_value) {
    }

    @Override
    protected void framelyCallback() {
    }

}

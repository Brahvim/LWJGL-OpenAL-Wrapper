package com.brahvim.nerd.openal.null_objects;

import com.brahvim.nerd.openal.objects.AlAuxiliaryEffectSlot;
import com.brahvim.nerd.openal.objects.AlEffect;
import com.brahvim.nerd.openal.objects.AlSource;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullAuxiliaryEffectSlot extends AlAuxiliaryEffectSlot implements AlNullObject {

    public AlNullAuxiliaryEffectSlot(final NerdAl p_alMan) {
        super(p_alMan, 0);
        AlAuxiliaryEffectSlot.ALL_INSTANCES.remove(this);
    }

    @Override
    protected void disposeImpl() {
    }

    @Override
    public AlEffect getEffect() {
        return super.MAN.DEFAULTS.effect;
    }

    @Override
    public float getFloat(final int p_alEnum) {
        return 0;
    }

    @Override
    public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
        return new float[p_vecSize];
    }

    @Override
    public float getGain() {
        return 0;
    }

    @Override
    public int getInt(final int p_alEnum) {
        return 0;
    }

    @Override
    public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
        return new int[p_vecSize];
    }

    @Override
    public AlSource getSource() {
        return super.MAN.DEFAULTS.source;
    }

    @Override
    public AlAuxiliaryEffectSlot setAutoSend(final boolean p_value) {
        return this;
    }

    @Override
    public AlEffect setEffect(final AlEffect p_effect) {
        return super.MAN.DEFAULTS.effect;
    }

    @Override
    public void setFloat(final int p_alEnum, final float p_value) {
    }

    @Override
    public void setFloatVector(final int p_alEnum, final float... p_values) {
    }

    @Override
    public AlAuxiliaryEffectSlot setGain(final float p_value) {
        return this;
    }

    @Override
    public void setInt(final int p_alEnum, final int p_value) {
    }

    @Override
    public void setIntVector(final int p_alEnum, final int... p_values) {
    }

    @Override
    public AlAuxiliaryEffectSlot setSource(final AlSource p_source) {
        return super.MAN.DEFAULTS.auxiliaryEffectSlot;
    }

    @Override
    protected void framelyCallback() {
    }

}

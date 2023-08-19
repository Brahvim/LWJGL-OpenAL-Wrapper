package com.brahvim.nerd.openal.al_ext_efx.al_effects;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;

import com.brahvim.nerd.openal.objects.AlEffect;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlCompressor extends AlEffect {

    public AlCompressor(final NerdAl p_NerdAl) {
        super(p_NerdAl);
    }

    @Override
    protected int getEffectType() {
        return EXTEfx.AL_EFFECT_COMPRESSOR;
    }

    public boolean getStatus() {
        return super.getInt(EXTEfx.AL_COMPRESSOR_ONOFF) == AL10.AL_TRUE;
    }

    public AlCompressor setStatus(final boolean p_value) {
        super.setInt(EXTEfx.AL_COMPRESSOR_ONOFF, p_value ? AL10.AL_TRUE : AL10.AL_FALSE);
        return this;
    }

}

package com.brahvim.nerd.openal.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

public abstract class AlFilter extends AlNativeResource<Integer> {

    protected static final Vector<AlFilter> ALL_INSTANCES = new Vector<>(0);

    protected AlFilter(final NerdAl p_alMan) {
        super(p_alMan);
        AlFilter.ALL_INSTANCES.add(this);
        super.MAN.RESOURCES.add(this);

        super.id = EXTEfx.alGenFilters();
        super.MAN.checkAlError();

        this.setInt(EXTEfx.AL_FILTER_TYPE, this.getName());
        super.MAN.checkAlError();
    }

    protected AlFilter(final NerdAl p_alMan, final int p_id) {
        super(p_alMan);
        AlFilter.ALL_INSTANCES.add(this);
        super.MAN.RESOURCES.add(this);

        super.id = p_id;
        super.MAN.checkAlError();

        this.setInt(EXTEfx.AL_FILTER_TYPE, this.getName());
        super.MAN.checkAlError();
    }

    public abstract int getName();

    // region Instance collection queries.
    public static int getNumInstances() {
        return AlFilter.ALL_INSTANCES.size();
    }

    public static List<AlFilter> getAllInstances() {
        return new ArrayList<>(AlFilter.ALL_INSTANCES);
    }
    // endregion

    // region C-style OpenAL getters.
    public int getInt(final int p_alEnum) {
        MemoryStack.stackPush();
        final IntBuffer buffer = MemoryStack.stackMallocInt(1);

        if (super.hasDisposed) {
            return Integer.MIN_VALUE;
        }

        EXTEfx.alGetFilteri(super.id, p_alEnum, buffer);

        MemoryStack.stackPop();
        super.MAN.checkAlError();

        return buffer.get();
    }

    public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final IntBuffer buffer = MemoryStack.stackMallocInt(p_vecSize);

        if (super.hasDisposed) {
            return new int[0];
        }

        EXTEfx.alGetFilteriv(super.id, p_alEnum, buffer);

        MemoryStack.stackPop();
        super.MAN.checkAlError();

        return buffer.array();
    }

    public float getFloat(final int p_alEnum) {
        MemoryStack.stackPush();
        final FloatBuffer buffer = MemoryStack.stackMallocFloat(1);

        if (super.hasDisposed) {
            return -Float.MAX_VALUE;
        }

        EXTEfx.alGetFilterf(super.id, p_alEnum, buffer);

        MemoryStack.stackPop();
        super.MAN.checkAlError();

        return buffer.get();
    }

    public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
        MemoryStack.stackPush();
        final FloatBuffer buffer = MemoryStack.stackMallocFloat(p_vecSize);

        if (super.hasDisposed) {
            return new float[0];
        }

        EXTEfx.alGetFilterfv(super.id, p_alEnum, buffer);

        MemoryStack.stackPop();
        super.MAN.checkAlError();

        return buffer.array();
    }
    // endregion

    // region C-style OpenAL setters.
    public void setInt(final int p_alEnum, final int p_value) {
        EXTEfx.alFilteri(super.id, p_alEnum, p_value);
        super.MAN.checkAlError();
    }

    public void setIntVector(final int p_alEnum, final int... p_values) {
        EXTEfx.alFilteriv(super.id, p_alEnum, p_values);
        super.MAN.checkAlError();
    }

    public void setFloat(final int p_alEnum, final float p_value) {
        EXTEfx.alFilterf(super.id, p_alEnum, p_value);
        super.MAN.checkAlError();
    }

    public void setFloatVector(final int p_alEnum, final float... p_values) {
        EXTEfx.alFilterfv(super.id, p_alEnum, p_values);
        super.MAN.checkAlError();
    }
    // endregion

    // region Mass source attachment.
    public void attachToSources(final int p_filterType, final AlSource... p_sources) {
        if (p_filterType == EXTEfx.AL_DIRECT_FILTER) {
            for (final AlSource s : p_sources) {
                if (s != null) {
                    s.attachDirectFilter(this);
                }
            }
        } else {
            for (final AlSource s : p_sources) {
                if (s != null) {
                    s.attachAuxiliarySendFilter(this);
                }
            }
        }
    }

    public void detachFromSources(final int p_filterType, final AlSource... p_sources) {
        if (p_filterType == EXTEfx.AL_DIRECT_FILTER) {
            for (final AlSource s : p_sources) {
                if (s != null) {
                    s.detachDirectFilter();
                }
            }
        } else {
            for (final AlSource s : p_sources) {
                if (s != null) {
                    s.detachAuxiliarySendFilter();
                }
            }
        }
    }
    // endregion

    @Override
    protected void disposeImpl() {
        EXTEfx.alDeleteFilters(super.id);
        AlFilter.ALL_INSTANCES.remove(this);
    }

}

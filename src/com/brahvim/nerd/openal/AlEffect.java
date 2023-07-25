package com.brahvim.nerd.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

public abstract class AlEffect extends AlNativeResource<Integer> {

	// region Fields.
	protected static final Vector<AlEffect> ALL_INSTANCES = new Vector<>();

	protected AlAuxiliaryEffectSlot slot;
	// endregion

	protected AlEffect(final NerdAl p_alMan) {
		super(p_alMan);
		AlEffect.ALL_INSTANCES.add(this);

		super.id = EXTEfx.alGenEffects();
		this.setInt(EXTEfx.AL_EFFECT_TYPE, this.getEffectType());
		super.MAN.checkAlError();
	}

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlEffect.ALL_INSTANCES.size();
	}

	public static ArrayList<AlEffect> getAllInstances() {
		return new ArrayList<>(AlEffect.ALL_INSTANCES);
	}
	// endregion

	// region Getters!
	protected abstract int getEffectType();

	public boolean isUsed() {
		return this.slot != null;
	}
	// endregion

	// region C-style OpenAL getters.
	public int getInt(final int p_alEnum) {
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(1);

		if (super.hasDisposed)
			return Integer.MIN_VALUE;

		EXTEfx.alGetEffecti(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.get();
	}

	public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(p_vecSize);

		if (super.hasDisposed)
			return new int[0];

		EXTEfx.alGetEffectiv(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.array();
	}

	public float getFloat(final int p_alEnum) {
		MemoryStack.stackPush();
		final FloatBuffer buffer = MemoryStack.stackMallocFloat(1);

		if (super.hasDisposed)
			return -Float.MAX_VALUE;

		EXTEfx.alGetEffectf(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.get();
	}

	public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final FloatBuffer buffer = MemoryStack.stackMallocFloat(p_vecSize);

		if (super.hasDisposed)
			return new float[0];

		EXTEfx.alGetEffectfv(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.array();
	}
	// endregion

	// region C-style OpenAL setters.
	public void setInt(final int p_alEnum, final int p_value) {
		EXTEfx.alEffecti(super.id, p_alEnum, p_value);
		if (this.slot != null)
			this.slot.setEffect(this);
		super.MAN.checkAlError();
	}

	public void setIntVector(final int p_alEnum, final int... p_values) {
		EXTEfx.alEffectiv(super.id, p_alEnum, p_values);
		if (this.slot != null)
			this.slot.setEffect(this);
		super.MAN.checkAlError();
	}

	public void setFloat(final int p_alEnum, final float p_value) {
		EXTEfx.alEffectf(super.id, p_alEnum, p_value);
		if (this.slot != null)
			this.slot.setEffect(this);
		super.MAN.checkAlError();
	}

	public void setFloatVector(final int p_alEnum, final float... p_values) {
		EXTEfx.alEffectfv(super.id, p_alEnum, p_values);
		if (this.slot != null)
			this.slot.setEffect(this);
		super.MAN.checkAlError();
	}
	// endregion

	public AlAuxiliaryEffectSlot getSlot() {
		return this.slot;
	}

	@Override
	protected void disposeImpl() {
		this.slot.setEffect(null);
		EXTEfx.alDeleteEffects(super.id);
		AlEffect.ALL_INSTANCES.remove(this);
	}

}

package com.brahvim.nerd.openal.null_objects;

import org.lwjgl.openal.EXTEfx;

import com.brahvim.nerd.openal.objects.AlFilter;
import com.brahvim.nerd.openal.objects.NerdAl;

public class AlNullFilter extends AlFilter implements AlNullObject {

	public AlNullFilter(final NerdAl p_alMan) {
		super(p_alMan, 0);
		AlFilter.ALL_INSTANCES.remove(this);
	}

	// Methods that just borrow themselves:
	/*
	 * 
	 * @Override
	 * public void attachToSources(final int p_filterType, final AlSource...
	 * p_sources) {
	 * }
	 * 
	 * @Override
	 * public void detachFromSources(final int p_filterType, final AlSource...
	 * p_sources) {
	 * }
	 */

	@Override
	protected void disposeImpl() {
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
	public int getInt(final int p_alEnum) {
		return 0;
	}

	@Override
	public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
		return new int[p_vecSize];
	}

	@Override
	public int getName() {
		return EXTEfx.AL_FILTER_NULL;
	}

	@Override
	public void setFloat(final int p_alEnum, final float p_value) {
	}

	@Override
	public void setFloatVector(final int p_alEnum, final float... p_values) {
	}

	@Override
	public void setInt(final int p_alEnum, final int p_value) {
	}

	@Override
	public void setIntVector(final int p_alEnum, final int... p_values) {
	}

	@Override
	protected void framelyCallback() {
	}

}

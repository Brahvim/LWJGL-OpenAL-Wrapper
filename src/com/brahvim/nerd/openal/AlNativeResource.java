package com.brahvim.nerd.openal;

import java.util.ArrayList;
import java.util.Vector;

public abstract class AlNativeResource /* implements Closeable */ {

	// region Fields and constructor.
	protected boolean hasDisposed, willDispose = true;

	protected static Vector<AlNativeResource> ALL_INSTANCES = new Vector<>();

	protected AlNativeResource() {
		AlNativeResource.ALL_INSTANCES.add(this);
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlNativeResource.ALL_INSTANCES.size();
	}

	public static ArrayList<AlNativeResource> getResourcesCopy() {
		return new ArrayList<>(AlNativeResource.ALL_INSTANCES);
	}
	// endregion

	// Yes, there's no `getId()` method here.
	// That's because OpenAL object IDs can be either of `long`s, or `int`s!

	public final boolean isDisposed() {
		return this.hasDisposed;
	}

	public final boolean setDisposability(final boolean p_value) {
		final boolean toRet = this.willDispose;
		this.willDispose = p_value;
		return toRet;
	}

	// @Override public void close() { this.dispose(); }
	public final void dispose() {
		if (!this.willDispose)
			return;

		// They're the same, so-uhh,
		this.disposeForcibly();
	}

	public final void disposeForcibly() {
		if (this.hasDisposed)
			return;

		this.hasDisposed = true;
		this.disposeImpl();
	}

	protected abstract void disposeImpl();

}

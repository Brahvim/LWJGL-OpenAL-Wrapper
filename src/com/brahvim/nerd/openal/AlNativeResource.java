package com.brahvim.nerd.openal;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public abstract class AlNativeResource<IdT extends Number> /* implements Closeable */ {

	// region Fields and constructor.
	protected final NerdAl MAN;

	protected IdT id;
	protected boolean hasDisposed, willDispose = true;

	protected static final Vector<AlNativeResource<?>> ALL_INSTANCES = new Vector<>(2);

	protected AlNativeResource(final NerdAl p_alMan) {
		this.MAN = Objects.requireNonNull(p_alMan, String.format(
				"Cannot construct any OpenAL object wrapper from `com.brahvim.nerd.openal` with a `null` `%s` instance.",
				NerdAl.class.getSimpleName()));
		this.MAN.RESOURCES.add(this);
		AlNativeResource.ALL_INSTANCES.add(this);
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlNativeResource.ALL_INSTANCES.size();
	}

	public static ArrayList<AlNativeResource<?>> getResourcesCopy() {
		return new ArrayList<>(AlNativeResource.ALL_INSTANCES);
	}
	// endregion

	public IdT getId() {
		return id;
	}

	public NerdAl getAlMan() {
		return this.MAN;
	}

	@SuppressWarnings("unchecked")
	public IdT getNullObjectId() {
		return (IdT) (Number) 0L;
	}

	protected void framelyCallback() {
	}

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

		this.disposeImpl();
		this.hasDisposed = true;
		this.MAN.RESOURCES.remove(this);
		AlNativeResource.ALL_INSTANCES.remove(this);
	}

	protected abstract void disposeImpl();

}

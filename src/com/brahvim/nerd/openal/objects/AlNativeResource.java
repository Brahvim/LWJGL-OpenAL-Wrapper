package com.brahvim.nerd.openal.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class AlNativeResource<IdT extends Number> /* implements Closeable */ {

    // region Fields and constructor.
    protected final NerdAl MAN;

    @SuppressWarnings("unchecked")
    protected IdT id = (IdT) (Number) 0;
    protected boolean hasDisposed, willDispose = true;

    protected static final Vector<AlNativeResource<?>> ALL_INSTANCES = new Vector<>(2);

    protected AlNativeResource(final NerdAl p_alMan) {
        this.MAN = p_alMan;
        this.MAN.RESOURCES.add(this);
        AlNativeResource.ALL_INSTANCES.add(this);
    }
    // endregion

    // region Instance collection queries.
    public static int getNumInstances() {
        return AlNativeResource.ALL_INSTANCES.size();
    }

    public static List<AlNativeResource<?>> getResourcesCopy() {
        return new ArrayList<>(AlNativeResource.ALL_INSTANCES);
    }
    // endregion

    public final IdT getId() {
        return this.id;
    }

    public final NerdAl getAlMan() {
        return this.MAN;
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
        if (!this.willDispose) {
            return;
        }

        // This method does what I want the exact same way, so-uhh...
        this.disposeForcibly();
    }

    @SuppressWarnings("unchecked")
    public final void disposeForcibly() {
        if (this.hasDisposed) {
            return;
        }

        this.disposeImpl();
        this.hasDisposed = true;
        this.MAN.RESOURCES.remove(this);
        AlNativeResource.ALL_INSTANCES.remove(this);

        this.id = (IdT) (Number) 0;
    }

    protected abstract void disposeImpl();

}

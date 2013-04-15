package org.codeliners.silvair.util;

import java.lang.ref.ReferenceQueue;

public class WeakReference<T> extends java.lang.ref.WeakReference<T> {
    public WeakReference(T referent) {
        super(referent);
    }

    public WeakReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public String toString() {
        T g = get();
        if (g == null) return null;
        return g.toString();
    }

    @Override
    public int hashCode() {
        T g = get();
        if (g == null) return super.hashCode();
        return g.hashCode();
    }

    @Override
    public boolean equals(Object anotherObject) {
        T g = get();
        if (g == null) return anotherObject == null;
        if (g.equals(anotherObject)) return true;
        if (anotherObject instanceof WeakReference)
            return g.equals((WeakReference) anotherObject);
        return super.equals(anotherObject);
    }
}

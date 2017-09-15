package net.warvale.bungee.data.runnable;

public interface QueryCallback<V extends Object, T extends Throwable> {
    void call(V result, T thrown);
}

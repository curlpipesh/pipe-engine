package me.curlpipesh.engine.plugin;

/**
 * @author c
 * @since 10/6/15.
 */
@SuppressWarnings("unused")
public interface Loadable {
    void onLoad();

    void onUnload();

    boolean isLoaded();

    void setLoaded(final boolean loaded);
}

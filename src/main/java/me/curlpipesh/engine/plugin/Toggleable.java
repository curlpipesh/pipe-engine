package me.curlpipesh.engine.plugin;

/**
 * @author c
 * @since 10/7/15.
 */
@SuppressWarnings("unused")
public interface Toggleable {
    void onEnable();

    void onDisable();

    boolean isEnabled();

    void setEnabled(final boolean enabled);
}

package me.curlpipesh.engine.plugin.module;

import me.curlpipesh.engine.plugin.Plugin;

/**
 * The base of all modules. A module is not intended to be used in a standalone
 * context, but is instead intended to be parented by a
 * {@link me.curlpipesh.engine.plugin.Plugin}.
 *
 * @author c
 * @since 7/10/15
 */
@SuppressWarnings("unused")
public interface Module {
    /**
     * Returns the name of this module.
     *
     * @return The name of this module. May not be null.
     */
    String getName();

    /**
     * Returns the description of this module.
     *
     * @return The description of this module. May not be null.
     */
    String getDescription();

    /**
     * Initializes this module. Registration of routes, file IO, and everything
     * else goes here.
     */
    void init();

    /**
     * Returns a String that represents the status of this module. This status
     * may be anything, from "Ok" to "47 potatoes eaten."
     *
     * @return The current status. May be null
     */
    String getStatus();

    /**
     * Sets the status for this module.
     *
     * @param status The status to set. May be null
     */
    void setStatus(final String status);

    default boolean isStatusShown() {
        return true;
    }

    /**
     * Intended to return the plugin that registered this module.
     *
     * @return The plugin that registered this module
     */
    Plugin getPlugin();

    /**
     * Whether or not this module is currently accepting events. With the
     * default implementation in {@link BasicModule}, this will always be true.
     *
     * @return Whether or not this module is currently accepting events.
     */
    boolean isEnabled();

    /**
     * Sets whether or not this module is currently accepting events. In the
     * default implementation in {@link BasicModule}, this will do nothing.
     *
     * @param enabled Whether or not the module should be accepting events.
     */
    void setEnabled(final boolean enabled);
}

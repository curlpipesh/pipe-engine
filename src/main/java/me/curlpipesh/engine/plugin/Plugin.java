package me.curlpipesh.engine.plugin;

import me.curlpipesh.engine.plugin.module.Module;

import java.util.List;

/**
 * The base of all plugins. An individual plugin is meant to have individual
 * {@link me.curlpipesh.engine.plugin.module.Module}s registered to it. Each
 * <tt>Module</tt> is then responsible for being the individual feature that a
 * given plugin provides.
 *
 * @author c
 * @since 7/10/15
 */
@SuppressWarnings("unused")
public interface Plugin extends Loadable, Toggleable {
    /**
     * Returns the name of this plugin. May not be null
     *
     * @return The name of this plugin
     */
    String getName();

    /**
     * Returns the author of this plugin. May be null
     *
     * @return The author of this plugin
     */
    String getAuthor();

    /**
     * Returns the description of this plugin. May be null
     *
     * @return The description of this plugin
     */
    String getDescription();

    /**
     * Returns the list of all modules that this plugin provides. May not be
     * null. May be empty.
     *
     * @return The list of all modules that this plugin provides.
     */
    List<Module> getProvidedModules();

    /**
     * Registers a new module for this plugin.
     *
     * @param module The module to register. May not be null.
     */
    void registerModule(final Module module);

    /**
     * Unregisters a module from this plugin.
     *
     * @param module The plugin to unregister. May not be null.
     */
    void unregisterModule(final Module module);

    /**
     * Returns the {@link PluginManifest} for this plugin.
     *
     * @return The {@link PluginManifest} for this plugin
     */
    PluginManifest getManifest();

    /**
     * Sets the {@link PluginManifest} for this plugin. This is intended to
     * be called only from {@link PluginManager#init(String)}. Note that after
     * invocation of this method, it is necessary to call
     * {@link #loadManifestData()} in order to update the stored data.
     *
     * @param manifest The new manifest for the plugin.
     */
    void setManifest(PluginManifest manifest);

    /**
     * Loads data from the {@link PluginManifest}. Intended to only be called
     * once.
     */
    void loadManifestData();

    /**
     * Finishes up whatever the plugin needs to do after onEnable(). This may
     * include anything from registering routes to adding event handlers. Note
     * that {@link BasicPlugin} uses this for:
     * <pre>
     *     Registering {@link Module} routes
     *     Initializting Modules
     *     Setting up all event listeners
     * </pre>
     */
    void finishEnabling();
}

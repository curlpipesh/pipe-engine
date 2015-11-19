package me.curlpipesh.engine.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.curlpipesh.engine.plugin.module.Module;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * The basic implementation of {@link Plugin}. This class will generally be the
 * one that you want to extend, instead of implementing <tt>Plugin</tt>.
 *
 * @author c
 * @since 7/11/15
 */
public abstract class BasicPlugin implements Plugin {
    @Getter
    @Setter
    private PluginManifest manifest;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private String author;

    @SuppressWarnings("FieldMayBeFinal")
    @Getter
    @Setter
    private boolean loaded = false;

    @SuppressWarnings("FieldMayBeFinal")
    @Getter
    @Setter
    private boolean enabled = false;

    @Getter
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Module> providedModules = new CopyOnWriteArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    private final Logger logger = Logger.getLogger(BasicPlugin.class.getSimpleName());

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onLoad() {}

    @Override
    public void onUnload() {}

    @Override
    public void loadManifestData() {
        name = manifest.getName();
        description = manifest.getDescription();
        author = manifest.getAuthor();
    }

    @Override
    public void registerModule(@NonNull final Module module) {
        if(!providedModules.contains(module)) {
            if(!providedModules.add(module)) {
                logger.warning(String.format("[%s] Unable to register module \"%s\"!", name, module.getName()));
            } else {
                logger.info(String.format("[%s] Registered module \"%s\"", name, module.getName()));
            }
        } else {
            logger.warning(String.format("[%s] Ignoring register for registered module \"%s\"!", name, module.getName()));
        }
    }

    @Override
    public void unregisterModule(@NonNull final Module module) {
        if(providedModules.contains(module)) {
            if(!providedModules.remove(module)) {
                logger.warning(String.format("[%s] Unable to unregister module \"%s\"!", name, module.getName()));
            } else {
                logger.info(String.format("[%s] Unregistered module \"%s\"", name, module.getName()));
            }
        } else {
            logger.warning(String.format("[%s] Ignoring unregister for non-registered module \"%s\"!", name, module.getName()));
        }
    }

    @Override
    public final void finishEnabling() {
        providedModules.forEach(Module::init);
    }
}

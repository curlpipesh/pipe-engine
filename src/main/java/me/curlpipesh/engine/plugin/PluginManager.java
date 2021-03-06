package me.curlpipesh.engine.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import me.curlpipesh.engine.plugin.serialization.ManifestDeserializer;
import me.curlpipesh.engine.plugin.util.ClassEnumerator;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * @author c
 * @since 7/11/15
 */
@SuppressWarnings("unused")
public class PluginManager {
    /**
     * The list of plugins that have been registered with the PluginManager
     * instance.
     */
    @Getter
    private final List<Plugin> plugins = new CopyOnWriteArrayList<>();
    
    private final Logger logger = Logger.getLogger(PluginManager.class.getSimpleName());

    private final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(PluginManifest.class, new ManifestDeserializer()).create();
    
    public void init(final String dir) {
        final File directory = new File(dir);
        if(!directory.exists()) {
            logger.warning("ERROR: Directory '" + dir + "' does not exist");
            return;
        }
        final File[] files = directory.listFiles();
        if(files == null) {
            logger.warning("Couldn't get files from plugin directory! Skipping loading...");
            return;
        }
        for(final File file : files) {
            if(file.getName().toLowerCase().endsWith(".jar")) {
                final List<Class<?>> classes;
                try {
                    classes = ClassEnumerator.getClassesFromJar(file, URLClassLoader.newInstance(new URL[]{
                            new URL("jar:file:" + file.getAbsoluteFile().getAbsolutePath() + "!/")
                    }, PluginManager.class.getClassLoader()));
                } catch(final Exception e) {
                    e.printStackTrace();
                    continue;
                }
                final JarFile jarFile;
                try {
                    jarFile = new JarFile(file);
                } catch(final IOException e) {
                    logger.warning("Error loading JAR (" + file.getName() + "):");
                    e.printStackTrace();
                    continue;
                }
                final ZipEntry entry = jarFile.getEntry("plugin.json");
                if(entry == null) {
                    logger.warning("No plugin.json in " + file.getName() + ", skipping.");
                    continue;
                }
                final InputStream manifestInputStream;
                try {
                    manifestInputStream = jarFile.getInputStream(entry);
                } catch(final IOException e) {
                    logger.warning("Error reading manifest in JAR (" + file.getName() + "):");
                    e.printStackTrace();
                    continue;
                }
                final String manifestContents = readFromInputStream(manifestInputStream);
                try {
                    manifestInputStream.close();
                } catch(final IOException e) {
                    logger.warning("Error reading manifest in JAR (" + file.getName() + "):");
                    e.printStackTrace();
                    continue;
                }
                final PluginManifest pluginManifest;
                try {
                    pluginManifest = gson.fromJson(manifestContents, PluginManifest.class);
                } catch(final IllegalArgumentException e) {
                    logger.warning("Error loading manifest from JAR (" + file.getName() + "):");
                    e.printStackTrace();
                    continue;
                }

                classes.stream().filter(p -> p.getName().equalsIgnoreCase(pluginManifest.getMainClass())).forEach(clazz -> {
                    try {
                        if(!Plugin.class.isAssignableFrom(clazz) || !isInstantiable(clazz)) {
                            logger.warning("Unable to load plugin \"" + pluginManifest.getName()
                                    + "\": No main class found");
                        }
                        final Plugin plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
                        plugin.setManifest(pluginManifest);
                        try {
                            plugin.onLoad();
                        } catch(final Exception e) {
                            logger.warning("Error loading plugin: " + clazz.getName());
                            e.printStackTrace();
                            return;
                        }
                        plugin.setLoaded(true);
                        plugins.add(plugin);
                    } catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        logger.info("Done!");

        plugins.forEach(p -> {
            try {
                p.loadManifestData();
                p.onEnable();
                p.finishEnabling();
                p.setEnabled(true);
                logger.info("Enabled plugin: " + p.getName());
            } catch(final Exception e) {
                logger.warning("Error enabling plugin (" + p.getClass().getName() + "):");
                e.printStackTrace();
            }
        });
    }

    private boolean isInstantiable(@NonNull final Class<?> clazz) {
        return !Modifier.isInterface(clazz.getModifiers())
                && !Modifier.isAbstract(clazz.getModifiers());
    }

    private String readFromInputStream(@NonNull final InputStream in) {
        final char[] buffer = new char[4096];
        final Reader reader = new InputStreamReader(in);
        final StringBuilder out = new StringBuilder();
        try {
            while(true) {
                final int rsz = reader.read(buffer, 0, buffer.length);
                if(rsz < 0) {
                    break;
                }
                out.append(buffer, 0, rsz);
            }
        } catch(final IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public void shutdown() {
        plugins.forEach(this::disablePlugin);
        plugins.forEach(this::unloadPlugin);
    }

    private void disablePlugin(final Plugin plugin) {
        plugin.onDisable();
    }

    private void unloadPlugin(final Plugin plugin) {
        plugin.onUnload();
        plugins.remove(plugin);
    }
}

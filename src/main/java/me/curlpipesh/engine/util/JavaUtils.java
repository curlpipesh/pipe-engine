package me.curlpipesh.engine.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * @author audrey
 * @since 11/17/15.
 */
@UtilityClass
public final class JavaUtils {
    public static boolean isRunningInJar() {
        final class ReferenceClass{}
        final File code = new File(ReferenceClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return code.isFile();
    }

    public static boolean isDebuggerAttached() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().toLowerCase().contains("jdwp");
    }
}

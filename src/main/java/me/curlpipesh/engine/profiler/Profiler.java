package me.curlpipesh.engine.profiler;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author audrey
 * @since 11/19/15.
 */
public final class Profiler {
    @Getter
    private static final Map<String, Long> sections;

    private static String currentSection = "";
    private static long startTime = 0L;

    static {
        sections = new LinkedHashMap<>();
    }

    public static void startSection(final String sectionName) {
        currentSection = sectionName;
        startTime = System.nanoTime();
    }

    public static void endSection() {
        final long endTime = System.nanoTime();
        sections.put(currentSection, endTime - startTime);
        currentSection = "";
    }

    public static void endStartSection(final String sectionName) {
        endSection();
        startSection(sectionName);
    }
}

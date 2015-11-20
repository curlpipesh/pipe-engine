package me.curlpipesh.engine.profiler;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author audrey
 * @since 11/19/15.
 */
public final class Profiler {
    @Getter
    private static final List<Section> sections = new LinkedList<>();
    private static final List<String> existingSections = new LinkedList<>();
    
    private static String currentSection = "";
    private static long startTime = 0L;
    
    public static void startSection(final String sectionName) {
        currentSection = sectionName;
        startTime = System.nanoTime();
        
    }
    
    public static void endSection() {
        final long endTime = System.nanoTime();
        if(!existingSections.contains(currentSection)) {
            existingSections.add(currentSection);
            final Section section = new Section(endTime - startTime, endTime - startTime, 1, currentSection);
            sections.add(section);
        } else {
            sections.parallelStream().filter(s -> s.section.equals(currentSection)).forEach(s -> {
                ++s.samples;
                s.totalTime += endTime - startTime;
                s.averageTime = TimeUnit.NANOSECONDS.toMillis(s.totalTime / s.samples);
            });
        }
        currentSection = "";
    }
    
    public static void endStartSection(final String sectionName) {
        endSection();
        startSection(sectionName);
    }
    
    public static class Section {
        @Getter
        private long averageTime;
        private long totalTime;
        private long samples;
        @Getter
        private final String section;
        
        public Section(final long averageTime, final long totalTime, final long samples, final String section) {
            this.averageTime = averageTime;
            this.totalTime = totalTime;
            this.samples = samples;
            
            this.section = section;
        }
    }
}

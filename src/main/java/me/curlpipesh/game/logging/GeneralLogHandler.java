package me.curlpipesh.game.logging;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class GeneralLogHandler extends Handler {
    @Override
    public synchronized void publish(final LogRecord record) {
        final String message = "[" + record.getLoggerName() + "] [" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                + "] [" + record.getLevel() + "] " + record.getMessage();
        if(record.getLevel().equals(Level.WARNING) || record.getLevel().equals(Level.SEVERE)) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
    }

    @Override
    public synchronized void flush() {
    }

    @Override
    public synchronized void close() throws SecurityException {
    }
}

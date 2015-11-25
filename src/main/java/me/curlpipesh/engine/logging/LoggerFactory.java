package me.curlpipesh.engine.logging;

import me.curlpipesh.engine.Engine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author audrey
 * @since 11/18/15.
 */
public class LoggerFactory {
    public static Logger getLogger(final Engine engine, final String name) {
        final Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(engine.isInTestMode() ? Level.INFO : Level.FINEST);
        logger.addHandler(new GeneralLogHandler());
        return logger;
    }

    @SuppressWarnings("unused")
    public static Logger getLogger(final boolean testMode, final String name) {
        final Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(testMode ? Level.INFO : Level.FINEST);
        logger.addHandler(new GeneralLogHandler());
        return logger;
    }
}

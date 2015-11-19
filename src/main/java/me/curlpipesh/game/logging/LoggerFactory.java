package me.curlpipesh.game.logging;

import me.curlpipesh.game.Game.GameState;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author audrey
 * @since 11/18/15.
 */
public class LoggerFactory {
    public static Logger getLogger(final GameState state, final String name) {
        final Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(state.isInTestMode() ? Level.INFO : Level.FINEST);
        logger.addHandler(new GeneralLogHandler());
        return logger;
    }
}

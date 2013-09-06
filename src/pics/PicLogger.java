package pics;

import java.util.logging.*;
import java.io.*;

/**
 * User: sortiz Date: Sep 14, 2004
 */
public class PicLogger {

  private static Logger log = null;


  private PicLogger() {} // Use getLog

  private static void makeLog() {
    log = Logger.getLogger(PicConst.LOG);
    //log.setUseParentHandlers(false); // do not send log info to the console

    // Make the logging directory if necessary
    File logDir = new File(PicConst.LOG_DIR);
    if (!logDir.exists()) logDir.mkdir();

    try {
      // Setup the general log file
      FileHandler generalFileHandler = new FileHandler(PicConst.LOG_DIR+"/general_log.%g", PicConst.LOGGER_MAX_FILESIZE, 50, true);
      generalFileHandler.setFormatter(new PicLogFormatter());
      generalFileHandler.setLevel(Level.ALL);
      log.addHandler(generalFileHandler);
    } catch (IOException e) {
      System.out.println("Error creating Logger! "+e.getMessage());
    }

  }

  /** Gets an instance of the log for use */
  public static Logger get() {
    if (log == null) makeLog();
    return log;
  }
  public static Logger getInstance() {
    return get();
  }

}

package pics;

import java.util.logging.*;
import java.text.SimpleDateFormat;

/**
 *  Format for log messages
 * User: sortiz Date: Sep 14, 2004
 */
public class PicLogFormatter extends Formatter {

  private SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm.ss");

  // Constructor
  public PicLogFormatter() { } // default mode = VERBOSE

  /** Formats the LogRecord into a string for output in the log file */
  public String format(LogRecord record) {
    StringBuffer retVal = new StringBuffer();
    retVal.append(df.format(new java.util.Date(record.getMillis()))+" ");
    retVal.append(record.getMessage());
    retVal.append("\n");
    return retVal.toString();
  } //format

}

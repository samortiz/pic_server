package pics;

/**
 * Constants for the Pic site
 */
public class PicConst {

  // Absolute File Paths
  public static final String NEW_DIR = "/home/share/pics/new";
  public static final String DB_DIR = "/home/share/pics/db";
  public static final String WEB_DIR = "/db";
  public static final String IMPORT_DIR = "/home/share/pics/import";
  public static final String LOG = "general";
  public static final String LOG_DIR = "/home/share/pics/log";
  public static final int LOGGER_MAX_FILESIZE = 10485760; // 10 MB Log file max

  // Relative File Paths
  public static final String FULL = "full";
  public static final String LARGE = "large";
  public static final String SMALL = "small";
  public static final String LARGE_SIZE="700";
  public static final String SMALL_SIZE="200";

  // JPG quality when rotating
  public static final String JPG_QUALITY_ROTATE = "90";
  public static final String JPG_QUALITY_SMALL  = "60";
  public static final String JPG_QUALITY_LARGE  = "60";

  // Thumbnail Constants
  public static final int PICS_PER_PAGE = 12;
  public static final int NUM_COLS = 4;

  // Colors
  public static final String LIGHT = "#F8FFF8";
  public static final String BACKGROUND = "#B0CAB0";
  public static final String HIGHLIGHT = "#C0C0D0";
  public static final String MED_LIGHT = "#A0A0C0";
  public static final String DARK = "#0F1F0F";

}

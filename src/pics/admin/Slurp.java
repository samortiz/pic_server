package pics.admin;

import pics.*;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

import util.MyException;

/**
 * User: sortiz Date: Aug 14, 2004
 */
public class Slurp {

  private static final boolean DEBUG = false;

  private File newDir = new File(PicConst.NEW_DIR);
  private File dbDir = new File(PicConst.DB_DIR);
  private File fullDir = new File(dbDir, PicConst.FULL);
  //private PicSession picSession = null;
  private int count = 0;

  public Slurp (PicSession picSession) {
    //this.picSession = picSession;

    SimpleDateFormat df_month = new SimpleDateFormat("yyyy_MM");
    SimpleDateFormat df_day = new SimpleDateFormat("MMM_dd");
    SimpleDateFormat df_db = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    File[] newFiles = newDir.listFiles();

    // Go through each of the new files
    if (newFiles != null) {
      for (int i=0; i<newFiles.length; i++) {
        File newFile = newFiles[i];

        // Verify that we want to process this file
        if (!newFile.exists() || !newFile.canRead() ||
            newFile.isDirectory() ||
            newFile.getName().startsWith("_")) {
          continue; // Go to the next file and leave this one alone
        }

        try {
          // Determine the Destination of this new file
          Date fileDate = new Date(newFile.lastModified());
          String relativePath = df_month.format(fileDate) + File.separator + df_day.format(fileDate);
          String relativeFile = relativePath + File.separator + newFile.getName();

          File fullPath = new File(fullDir, relativePath);
          File fullFile = new File(fullDir, relativeFile);
          // Make the directory if it doesn't exist
          if (!fullPath.exists()) {
            if (!fullPath.mkdirs()) throw new MyException("Error creating dir : "+fullPath.getAbsolutePath());
          }
          // Move the file to it's new destination
          if (!newFile.renameTo(fullFile)) {
            throw new MyException ("Error moving file from "+newFile.getAbsolutePath()+" to "+fullFile.getAbsolutePath());
          }

          boolean isMovie = relativeFile.toLowerCase().endsWith(".mov");

          // Update the database with the file info
          PicDb.update("insert into pic (file, name, description, created, movie) values " +
              " ('"+relativeFile+"', '', '', '"+df_db.format(fileDate)+"'::timestamp, "+isMovie+") ");
          if (DEBUG) System.out.println("insert into pic (file, name, description, created, movie) values " +
              " ('"+relativeFile+"', '', '', '"+df_db.format(fileDate)+"'::timestamp, "+isMovie+") ");

          StringBuffer messages = new StringBuffer();
          if (!isMovie) {
            PicUpdate.makeThumbnail(relativeFile, PicConst.SMALL, messages);
            PicUpdate.makeThumbnail(relativeFile, PicConst.LARGE, messages);
          }

          if (messages.length() > 0) {
            picSession.putMessage(messages.toString());
          }

          count += 1;

        } catch (Exception e) {
          picSession.putMessage("Error slurping file "+newFile.getName()+" Error : "+e.getMessage());
        }
      } // for
    }

  }


  public int getNewFileCount() {
    return count;
  } // getNewFileCount




}

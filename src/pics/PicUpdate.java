package pics;

import java.io.File;
import java.util.HashMap;

import util.*;
import javax.servlet.http.HttpServletRequest;

/**
 *  This class contains static methods for manipulating pictures,
 *  both in the file system and in the database
 * User: sortiz Date: Aug 21, 2004
 */
public class PicUpdate {

  private static final boolean DEBUG = false;

  /** This will look in the request for parameters "mode" and "pid".
   *  Further parameters may be required, if doing an update "name" and "description" will also be required
   *  the "mode" parameter should be one of the allowed functions "delete", "rotate" or "update"
   *
   * @param request  The request stream to get the parameters from
   * @param picView The view to update if there is structural change (deleted!)
   * @param messages Any messages sent back to the user (Success or failure messages) formatted in HTML
   * @param picSession The session of the current user.
   * @return false if there was an error, true otherwise (including if nothing was done)
   */
  public static boolean doUpdate(HttpServletRequest request, PicView picView, StringBuffer messages, PicSession picSession) {
    boolean success = true;
    String mode = request.getParameter("mode");
    String pid = Filters.escapeDbStr(request.getParameter("pid"));

    try {
      if (picSession.hasPermission("Admin") && (request.getParameter("mode") != null)) {
        if ("delete".equals(mode)) {
          success = PicUpdate.delete(pid, messages, picSession);
          picView.refreshView(); // remove the deleted picture

        } else if ("rotate".equals(mode)) {
          String dir = request.getParameter("dir");
          success = PicUpdate.rotate(pid, dir, messages, picSession);

        } else if ("update".equals(mode)) {
          String name = Filters.escapeDbStr(request.getParameter("name"));
          String description = Filters.escapeDbStr(request.getParameter("description"));
          String keywords = Filters.escapeDbStr(request.getParameter("keywords"));
          boolean hidden = "true".equals(request.getParameter("hidden"));
          boolean vertical = "true".equals(request.getParameter("vertical"));

          success = PicUpdate.update(pid, name, description, keywords, hidden, vertical, messages);

        } else if ("deletecomment".equals(mode)) {
          String pcid = Filters.escapeDbStr(request.getParameter("pcid"));
          PicDb.update("delete from pic_comment where pcid="+pcid);
        }
      }

      // Adding a comment
      String comment = Filters.escapeDbStr(request.getParameter("comment"));
      if ((comment != null) && (comment.length() > 0))  {
        addComment(pid, comment, picSession);
      }

    } catch (MyException e) {
      messages.append("<FONT COLOR=RED>Error doing picture "+mode+" error="+e.getMessage()+"</FONT><BR>");
      success = false;
    }

    return success;
  }



  /** Deletes a picture from the file system and the database.  This is permanent and cannot be undone!
   * @param pid The pid of the picture to delete.
   * @param messages Any error messages, or a success message if all went well.
   * @return true if all was successful and false otherwise
   */
  public static boolean delete(String pid, StringBuffer messages, PicSession picSession) throws MyException {
    if ((pid == null) || (pid.length() == 0)) {
      messages.append("<FONT COLOR=RED>No pid specified for deleting!</FONT><BR>");
      return false;
    }

    String file = PicDb.getSingleValue("select file from pic where pid="+pid);
    if (file.length() == 0) {
      messages.append("<FONT COLOR=RED> No file specified to delete! Pid="+pid+"</FONT><BR>");
      return false;
    }

    File picFile = new File(PicConst.DB_DIR+File.separator+PicConst.FULL, file);
    if (!picFile.exists() || !picFile.isFile()) {
      messages.append("<FONT COLOR=RED> File "+picFile.getAbsolutePath()+" does not exist!</FONT><BR>");
      return false;
    }
    if (!picFile.delete()) {
      messages.append("<FONT COLOR=RED>Unable to delete file : "+picFile.getAbsolutePath()+"</FONT><BR>");
      return false;
    }

    // Delete thumbnails
    File smallFile = new File(PicConst.DB_DIR+File.separator+PicConst.SMALL, file);
    if (smallFile.exists()) smallFile.delete();

    // Delete large size
    File largeFile = new File(PicConst.DB_DIR+File.separator+PicConst.LARGE, file);
    if (largeFile.exists()) largeFile.delete();

    PicDb.update("delete from pic where pid="+pid);
    messages.append("<FONT COLOR=GREEN>Deleted "+file+" OK!</FONT><BR>");
    PicLogger.get().info(picSession.userInfo()+" deleted '"+file+"' pid="+pid);
    return true;
  } // delete


  /** Rotates an image!  This will make a copy of the image so use with caution
   * @param pid the id of the picture to rotate
   * @param dir  This should be "right" or "left" as appropriate.  This will default to a "right" rotate if dir is anything except "left"
   * @param messages Messages formatted for HTML, indicating success or failure
   * @return true if the rotate succeeded and false otherwise
   */
  public static boolean rotate(String pid, String dir, StringBuffer messages, PicSession picSession) throws MyException {
    if ((pid == null) || (pid.length() == 0)) {
      messages.append("<FONT COLOR=RED>no pid specified to rotate!</FONT><BR>");
      return false;
    }

    HashMap fileData = PicDb.getRow("select * from pic where pid="+pid);
    String file = (String)fileData.get("file"); // relative
    String vertical = (String) fileData.get("vertical");
    String name = (String) fileData.get("name");

    // Calculate the label to use in the new file name
    String degrees = "90"; // default to rotate-right
    String rotate_label = "R";
    if ("left".equals(dir)) {
      degrees = "270"; // rotate left (-90) is really 270 degrees.
      rotate_label = "L";
    }
    String newVertical = "true";
    if ((vertical != null) && vertical.equals("true")) {
      newVertical = "false";
    }

    // Calculate the new File name
    int dotIndex = file.lastIndexOf(".");
    String newFile = file.substring(0, dotIndex) + rotate_label + file.substring(dotIndex); // relative
    String fullFile = PicConst.DB_DIR + File.separator + PicConst.FULL + File.separator + file; // absolute
    String fullNewFile = PicConst.DB_DIR + File.separator + PicConst.FULL + File.separator + newFile; // absolute

    try {
      // Call out to ImageMagick to do the rotation.
      String command = "convert -quality "+PicConst.JPG_QUALITY_ROTATE+" -rotate "+degrees+" "+fullFile+" "+fullNewFile;
      if (DEBUG) System.out.println("PicUpdate:rotate : Executing Command=" + command);
      Process proc = Runtime.getRuntime().exec(command);
      int val = proc.waitFor();
      if (val == 0) {
        PicDb.update("update pic set " +
            "  file = '"+newFile+"' " +
            " ,vertical = "+newVertical+
            " where pid = "+pid);
        messages.append("<FONT COLOR=GREEN>Rotate Image "+name+" to the "+dir+" OK.<FONT><BR>");

        // Create new thumbnails for the new image.
        PicUpdate.makeThumbnail(newFile, PicConst.LARGE, messages);
        PicUpdate.makeThumbnail(newFile, PicConst.SMALL, messages);
        PicLogger.get().info(picSession.userInfo()+" rotated '"+name+"' pid="+pid+" to the "+dir);

        return true;
      } else {
        messages.append("<FONT COLOR=RED>There was a problem rotating the image. Error code : "+val+"</FONT><BR>");
        return false;
      }
    } catch (Exception e) {
      messages.append("<FONT COLOR=RED> Error running rotate script for fullFile "+fullFile+"</FONT><BR>"+e.getMessage()+"<BR>");
      return false;
    }

  } // rotate

  /** Makes thumbnails
   * @param file the relative filename of the image (as stored in the db)
   * @param size Should be PicConst.LARGE or PicConst.SMALL, the relative path
   * @param messages the StringBuffer to use to store any messages to go back to the user. */
  public static boolean makeThumbnail(String file, String size, StringBuffer messages) {
    // Get the original and the destination
    String fullFile = PicConst.DB_DIR + File.separator + PicConst.FULL + File.separator + file;
    File newFile = new File(PicConst.DB_DIR + File.separator + size, file);
    File newDir = newFile.getParentFile();

    if (!newDir.exists()) {
      if (!newDir.mkdirs()) { // make any directories up to this file that might be missing.
        messages.append("<FONT COLOR=RED>Unable to make directory for thumbnail : "+newDir.getAbsolutePath()+"</FONT><BR>");
        return false; // cannot continue if we failed to make the directory
      }
    }

    String sizeStr = null;
    String qualityStr = null;
    if (size.equals(PicConst.SMALL)) {
      sizeStr = PicConst.SMALL_SIZE + "x" + PicConst.SMALL_SIZE;
      qualityStr = PicConst.JPG_QUALITY_SMALL;
    } else if (size.equals(PicConst.LARGE)) {
      sizeStr = PicConst.LARGE_SIZE + "x" + PicConst.LARGE_SIZE;
      qualityStr = PicConst.JPG_QUALITY_LARGE;
    } else {
      messages.append("<FONT COLOR=RED>Unknown size when making thumbnail size="+size+"</FONT><BR>");
      return false;
    }

    String command = "convert +profile \"*\" -size "+sizeStr+" -resize "+sizeStr+" -quality "+qualityStr+
        " "+fullFile+" "+newFile.getAbsolutePath();
    if (DEBUG) System.out.println("PicUpdate.makeThumbnail : Executing Command=" + command);
    try {
      Process proc = Runtime.getRuntime().exec(command);
      int val = proc.waitFor();
      if (val != 0) {
        messages.append("<FONT COLOR=RED>Error creating thumbnail with command : "+command+"</FONT><BR>");
        return false;
      }
      return true;
    } catch (Exception e) {
      messages.append("<FONT COLOR=RED>Error creating thumbnail with command : "+command+" Exception : "+e.getMessage()+"</FONT><BR>");
      return false;
    }
  } // makeThumbnail


  public static boolean update(String pid, String name, String description, String keywords, boolean hidden,
                               boolean vertical, StringBuffer messages) throws MyException {
    if ((pid == null) || (pid.length() == 0)) {
      messages.append("<FONT COLOR=RED>no pid specified to update!</FONT><BR>");
      return false;
    }

    if (name == null) name = "";
    if (description == null) description = "";
    if (keywords == null) keywords = "";
    PicDb.update("update pic set name='"+name+"', description='"+description+"', keywords='"+keywords+"', " +
        " hidden="+hidden+", vertical="+vertical+" where pid="+pid);
    messages.append("<FONT COLOR=GREEN>Updated picture "+name+" OK.</FONT><BR>");
    return true;
  } // update

  /** Adds a comment */
  public static boolean addComment(String pid, String comment, PicSession picSession) throws MyException {
    PicDb.update("insert into pic_comment (pid, uid, comment) values " +
        " ("+pid+", "+picSession.getUid()+", '"+comment+"' ) ");
    String picName = PicDb.getSingleValue("select name from pic where pid="+Integer.parseInt(pid));
    PicLogger.get().info(picSession.userInfo()+" added comment to '"+picName+"' pid="+pid+" comment="+comment);
    return true;
  }

}

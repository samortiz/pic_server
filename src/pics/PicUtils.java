package pics;

import util.MyException;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.Iterator;

/**
 *  MyUtils for working with pictures.
 */
public class PicUtils {

  /** @return Reads an image from a file and returns it as a BufferedImage
   *  The file can be : jpg, gif, png (and possibly others) */
  public static BufferedImage readImage(String fileName) throws IOException {
    return ImageIO.read(new File(fileName));
  } // readImage


  /** Writes an image to the OutputStream as the given type
   *  @param imageTypeExtension should be "jpg" or "png"  (gif is not supported)
   *  Java 1.4.1 only supports jpg and png for writing. */
  public static void writeImage(BufferedImage img, OutputStream outStr, String imageTypeExtension) throws MyException, IOException {
    // Setup to output the image
    Iterator writers = ImageIO.getImageWritersByFormatName(imageTypeExtension);
    if (writers.hasNext()) {
      ImageWriter writer = (ImageWriter) writers.next();
      ImageOutputStream ios = ImageIO.createImageOutputStream(outStr);
      writer.setOutput(ios);
      // Write out the image to the stream
      writer.write(img);
    } else {
      throw new MyException("Cannot find "+imageTypeExtension+" writer");
    }

  } // writeImage

  public static BufferedImage rotate(BufferedImage bi) {
    /*
    BufferedImage new_bi =
        new BufferedImage(
        bi.getHeight(),
        bi.getWidth(),
        bi.getType());
    Graphics2D g2d = new_bi.createGraphics();
    AffineTransform at = g2d.getTransform();
    AffineTransform rotation = new AffineTransform();
    rotation.rotate(Math.PI / 2, bi.getWidth()/2, bi.getHeight()/2);
    g2d.transform(rotation);
    g2d.drawImage(
        bi,
        null,
        bi.getWidth() / 2 - new_bi.getWidth() / 2,
        (bi.getHeight() / 2 - new_bi.getHeight() / 2) * -1);
    g2d.setTransform(at);
    return new_bi;
      */

    AffineTransform tx = new AffineTransform();
    tx.rotate(Math.PI / 2, bi.getWidth()/2, bi.getHeight()/2);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return op.filter(bi, null);

  }


  /** Sends the image using the response OutputStream
   *  @param imageTypeExtension should be "png" or "jpg"
   */
  public static void sendImageResponse(BufferedImage img, HttpServletResponse response, String imageTypeExtension) throws MyException, IOException {
    response.setContentType("image/"+imageTypeExtension);
    response.setHeader("Content-Disposition","filename=temp."+imageTypeExtension);
    PicUtils.writeImage(img, response.getOutputStream(), imageTypeExtension);
  } // sendImageResponse


  /** Makes all the directories leading up to the file */
  public static boolean mkDirs(File file) throws Exception {
    if (file == null) return false;
    File parentDir = null;

    if (file.isDirectory()) {
      parentDir = file;
    } else { // must be a file (but isFile will return false if it doesn't exist)
      parentDir = file.getParentFile();
    }
    if (parentDir.exists()) return true; // if it already exists, all is OK
    if (parentDir == null) return false; // no such directory (or no parents)

    return parentDir.mkdirs();
  } // mkDirs

  /**
   *  Calls out to the command line and executes some command
   * @return true if it ran OK, false if there was an error
   */
  public static boolean execute(String command) {
    try {
      // Call out to ImageMagick to do the concatenation
      Process proc = Runtime.getRuntime().exec(command);
      return proc.waitFor() == 0;
    } catch (Exception e) {
      return false;
    }
  }
  
}

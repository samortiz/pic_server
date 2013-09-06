/*
 * MyException.java
 *
 * Created on October 15, 2003, 1:08 PM
 */

package util;

/**
 * Generic Exception for this web site.  
 * @author  sortiz
 */
public class MyException extends java.lang.Exception {
  
  /**
   * Creates a new instance of MyException without detail message.
   */
  public MyException() {
  }
  
  
  /**
   * Constructs an instance of MyException with the specified detail message.
   * @param msg the detail message.
   */
  public MyException(String msg) {
    super(msg);
  }
}

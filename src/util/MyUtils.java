package util;

import java.util.*;
import java.util.List;
import java.io.*;
import java.text.NumberFormat;
import java.awt.*;

/**
 * Static Methods for converting Data Types
 * @author  sortiz
 *
 * NOTE :  Most of these methods do not return null, they
 * will return "" or an empty ArrayList etc... This is so that
 * you don't have to constantly check for null to avoid NullPointerExceptions
 *  If this behaviour causes problems for you, you can write a copy of the routine that
 * returns null and name it something similiar eg. toCSV() -> toCSV_null(), please don't
 * change the behaviour of the original methods or code that relies on them may break.
 */
public class MyUtils {

     /** Do not create instances of this class, use the static methods instead */
     private MyUtils() {}


   // ----------------------------- CSV Functions (Comma Separated Values) ------------------

    /**
    * Converts an array of longs into a comma seperated string
    */
    public static String toCSV(long[] vals) {
      if (vals == null) return "";
      StringBuffer retVal = new StringBuffer();
      for (int i=0; i<vals.length; i++) {
        if (i>0) retVal.append(",");
        retVal.append(vals[i]);
      }//for
      return retVal.toString();
    }//toCSV

    /** Converts an array of ints to a comma seperated string */
    public static String toCSV(int[] vals) {
      if (vals == null) return "";
      StringBuffer retVal = new StringBuffer();
      for (int i=0; i<vals.length; i++) {
        if (i>0) retVal.append(",");
        retVal.append(vals[i]);
      }//for
      return retVal.toString();
    }//toCSV


    /** Converts and array of Strings into a comma seperated string */
    public static String toCSV(String[] vals) {
      if (vals == null) return "";
      StringBuffer retVal = new StringBuffer();
      for (int i=0; i<vals.length; i++) {
        if (i>0) retVal.append(",");
        retVal.append(vals[i]);
      }//for
      return retVal.toString();
    }


    /**
     * Converts an ArrayList into a comma seperated string
     * @param vals an arraylist of objects that can be converted into strings using .toString()
     */
    public static String toCSV(ArrayList vals) {
      return toCSV((List)vals);
    } //toCSV


    /** Converts a List into a comma seperated String
     * @param vals a list of objects that can be converted into strings using .toString()
     */
    public static String toCSV(List vals) {
      if (vals == null) return "";
      StringBuffer retVal = new StringBuffer();
      boolean firstItem = true;

      for (int i=0; i<vals.size(); i++) {
        if (vals.get(i) != null) {
          if (firstItem) firstItem = false; // don't put a comma the first time
          else retVal.append(",");
          retVal.append(vals.get(i).toString());
        }
      }//for

      return retVal.toString();
    }




    /** Converts an ArrayList to a String seperated by delimiter (if it's a "," then you can use toCSV)
     *  The arraylist can contain any object, the display is defined by the toString() method. */
    public static String toAnySV(ArrayList vals, String delimiter) {
      if ((vals==null) || (delimiter==null)) return "";
      StringBuffer retVal = new StringBuffer();
      boolean firstItem = true;

      for (int i=0; i<vals.size(); i++) {
        if (vals.get(i) != null) {
          if (firstItem) firstItem = false; // don't put a comma the first time
          else retVal.append(delimiter);
          retVal.append(vals.get(i).toString());
        }
      } //for

      return retVal.toString();
    }


    /** Converts a String[] to a String seperated by delimiter (if it's a "," then you can use toCSV) */
    public static String toAnySV(String[] vals, String delimiter) {
      if ((vals==null) || (delimiter==null)) return "";
      StringBuffer retVal = new StringBuffer();
      boolean firstItem = true;

      for (int i=0; i<vals.length; i++) {
        if (vals[i] != null) {
          if (firstItem) firstItem = false; // don't put the delimiter the first time
          else retVal.append(delimiter);
          retVal.append(vals[i]);
        }
      } //for

      return retVal.toString();
    }


    /** Converts a Object[] to a String seperated by delimiter (if it's a "," then you can use toCSV)
     *   This uses the object's toString() method to get a string representation of the object */
    public static String toAnySV(Object[] vals, String delimiter) {
      if ((vals==null) || (delimiter==null)) return "";
      StringBuffer retVal = new StringBuffer();
      boolean firstItem = true;

      for (int i=0; i<vals.length; i++) {
        if (vals[i] != null) {
          if (firstItem) firstItem = false; // don't put the delimiter the first time
          else retVal.append(delimiter);
          retVal.append(vals[i].toString());
        }
      } //for

      return retVal.toString();
    }

    /** Converts a CSV string into an ArrayList containing Strings
     * @param source the comma seperated string (no leading or trailing commas)
     * NOTE : This will String.trim() all the entries (removing spaces from the start and end)
     */
    public static ArrayList parseCSV(String source) {
      ArrayList dest = new ArrayList();
      if ((source == null) || (source.length() == 0)) {
        return dest;
      }

      int loc = 0; // current location in the string
      int commaIndex = source.indexOf(",");
      while (commaIndex >= 0) {
        dest.add(source.substring(loc,commaIndex).trim());
        loc = commaIndex+1;
        commaIndex = source.indexOf(",",loc);
      } // while
      dest.add(source.substring(loc).trim()); // remainder of string (no trailing comma)

      return dest;
    } // parseCSV

    /** Converts a CSV String into an array of Strings.
     * @param source the comma seperated string (no leading or trailing commas)
     * NOTE : This will String.trim() all the entries (removing spaces from the start and end)
     * NOTE : This is not particularly efficient, it generates the arraylist first and then converts it to a String[]
     */
    public static String[] parseCVS_array(String source) {
      return parseCSV_array(source);
    }
    public static String[] parseCSV_array(String source) {
      if (source == null) return new String[0]; // return empty array
      ArrayList results = parseCSV(source);
      String[] retVal = new String[results.size()];
      for (int i=0; i<results.size(); i++) {
        retVal[i] = (String) results.get(i);
      }
      return retVal;
    }


    /** Subtracts all elements of list2 from list1.
     *  NOTE : To use this with Arraylists it is simpler to just use ArrayList.subtract
     *         So I do not have an ArrayList overload of this function.
     * @param list1 A comma seperated String of elements as per toCSV
     * @param list2 A comma seperated String of elements as per toCSV
     * @return a String of comma seperated values
     */
    public static String subtractCSV(String list1, String list2) {
     ArrayList one = parseCSV(list1);
     ArrayList two = parseCSV(list2);
     one.removeAll(two);
     return toCSV(one);
    } // listSubtract


    /** Gets the intersection of two lists. Intersect is the "common" values between the two lists
     *  NOTE : duplicate items within a list will be duplicated in the result only if they are duplicated in list1
     *         (list2 duplicates are not duplicated in the result)
     * @param list1 A comma seperated String of elements as per toCSV
     * @param list2 A comma seperated String of elements as per toCSV
     * @return a String of comma seperated values
     */
    public static String intersectCSV(String list1, String list2) {
     return toCSV(intersect(parseCSV(list1), parseCSV(list2)));
    }

    /** Gets the intersection of two lists. Intersect is the "common" values between the two lists
     *  NOTE : duplicate items within a list will be duplicated in the result only if they are duplicated in list1
     *         (list2 duplicates are not duplicated in the result)
     * @param list1 A comma seperated String of elements as per toCSV
     * @param list2 A comma seperated String of elements as per toCSV
     * @return a String of comma seperated values
     */
    public static ArrayList intersect(ArrayList list1, ArrayList list2) {
      ArrayList intersect = new ArrayList();
      for (int i=0; i<list1.size(); i++) {
        if (list2.contains(list1.get(i))) {
          intersect.add(list1.get(i));
        }
      } //
     return intersect;
    }

    /** Returns the union of the two lists.  Union is the "merging" of two lists so any value in
     *  either list will be returned, but not duplicates.
     *  NOTE:  this will remove duplicated items, even within list1 or list2!
     * @param list1 A comma seperated String of elements as per toCSV
     * @param list2 A comma seperated String of elements as per toCSV
     * @return a String of comma seperated values
     */
    public static String unionCSV(String list1, String list2) {
     return toCSV(union(parseCSV(list1), parseCSV(list2)));
    }

    /** Returns the union of the two lists. Union is the merging of two lists so that
     *  any value in either list will be returned but not duplicates.
     *  NOTE : this will remove duplicated items, even within list1 or list2!
     *  NOTE : Equality is determined by Collections.contains :
     *         Returns true if this collection contains the specified element.
     *         More formally, returns true if and only if this collection contains at least one element e such that
     *          (o==null ? e==null : o.equals(e))
     *
     * @param list1 list to be unioned
     * @param list2 list to be unioned
     * @return  The union of the lists
     */
    public static ArrayList union(ArrayList list1, ArrayList list2) {
      ArrayList union = new ArrayList();
      for (int i=0; i<list1.size(); i++) {
        if (!union.contains(list1.get(i))) {
          union.add(list1.get(i));
        }
      }
      for (int i=0; i<list2.size(); i++) {
        if (!union.contains(list2.get(i))) {
          union.add(list2.get(i));
        }
      }
      return union;
    } // union



   /**  Turns a Hashmap into a String through Properties.store
    * @param h the HashMap to convert
    * @return the String containing all the keys and values
    */
    public static String hashMapToString(HashMap h) {
      Properties p = new Properties();
      p.putAll(h);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
        p.store(out,null);
      } catch (IOException e) {
        System.out.println("IO Error writing to a String! This shouldn't happen!");
      }//try
      return out.toString();
    }//HashMaptoPropertiesString


    // ------------------------- General Conversions -----------------------------------

    /** Returns a String with a formatted number.
     *  This uses the default format, though this method could be overridden to specify a format
     *  This function is more desirable (in some cases) than String.valueOf(float), because the default formatting is nicer.
     *  @return the String representation of the number
     */
    public static String floatToString(float inFloat) {
     NumberFormat nf = NumberFormat.getInstance();
     return nf.format(inFloat);
    }


    /** returns a color given a string containg RGB as hex values
     * @param rgbHex a String in the format "RRGGBB" (web-style)
     *        R = Red (hex)
     *        G = Green (hex)
     *        B = Blue (hex)
     *  This will return null if there is an error  */
    public static Color getColor(String rgbHex) {
      if (rgbHex.length() != 6) return null;
      String red   = rgbHex.substring(0,2);
      String green = rgbHex.substring(2,4);
      String blue  = rgbHex.substring(4,6);
      try {
        return new Color(Integer.parseInt(red,16), Integer.parseInt(green,16), Integer.parseInt(blue,16));
      } catch (Exception e) {
        // Error creating color, return nothing
        return null;
      }
    } // getColor


    /** Counts the number of times the toFind string is found in the source
     * @return the number of non-overlapping matches, or 0 if none were found or something is a null. */
    public static int countSubstring(String source, String toFind) {
      if ((source == null) || (toFind == null)) {
        return 0;
      }
      int count = 0;
      int pos = source.indexOf(toFind);
      while (pos >= 0 ) {
        count += 1;
        pos += toFind.length(); // to move to the next occurence
        pos = source.indexOf(toFind, pos);
      }
      return count;
    }

    // Strings that count as WhiteSpace in the trimAll function
    public static final String[] TRIM = {" ", "\n", "\r", "\t", "&nbsp;", "<BR>", "<BR/>", "<br>", "<br/>"};
    /** Trims all Whitespace characters, and HTML whitespace from the start and end of the string
     * See ConversionUtils.TRIM variable for which strings are considered whitespace */
    public static String trimAll(String in) {
      return trimAll(in, TRIM);
    }

    /** Removes all sorts of Strings from the start and end of the string.
     *  This will remove all the Strings in the TRIM variable from the start and end.
     * @param in the String to trim
     * @return  The trimmed string, or null if null was passed in
     */
    public static String trimAll(String in, String[] trim) {
      if (in == null) return null;

      // Find the first non-whitespace String
      int start = 0;
      while (start <= in.length()) {
        boolean found = false;
        for (int i=0; i<trim.length; i++) {
          if (in.startsWith(trim[i], start)) {
            found = true;
            start += trim[i].length();
            break; // this item matches, don't keep searching
          }
        } // for i
        // Quit here if the String is not found
        if (!found) break;
      } // for start

      // entire string is trimmable
      if (start == in.length()) return "";

      // Find the last non-whitespace String
      int end = in.length();
      while (end >= 0) {
        boolean found = false;
        for (int i=0; i<trim.length; i++) {
          if (in.substring(0, end).endsWith(trim[i])) {
            found = true;
            end -= trim[i].length();
            break; // this item matches, don't keep searching
          }
        } // for i
        // Quit here if the String is not found
        if (!found) break;
      } // for end

      return in.substring(start, end);
    }

    /** Runs trimAll on the array of passed in strings, and removes any entries that trim to an empty string,
     * or that are null
     * @param in  The String[] to trim
     * @param whitespace The strings that are considered whitespace
     * @return a String[] of strings that are
     */
    public static String[] trimAll(String[] in, String[] whitespace) {
      if (in == null) return null;
      ArrayList results = new ArrayList();
      for (int i=0; i<in.length; i++) {
        String trimmed = trimAll(in[i], whitespace);
        if ((trimmed != null) && (trimmed.length() > 0)) {
          results.add(trimmed);
        }
      }
      return (String[]) results.toArray(new String[results.size()]);
    }

    /** Runs trimAll on the array of passed in strings, and removes any entries that trim to an empty string,
     * or that are null.  See the TRIM variable to see what counts as whitespace
     * @param in  The String[] to trim
     * @return a String[] of strings that are
     */
    public static String[] trimAll(String[] in) {
      return trimAll(in, TRIM);
    }


     // --------------------- Array Conversions ------------------------------


    /** Converts a String[] into an int[].   If any of the strings fail the cast to an int
     * A NumberFormatException will be thrown */
    public static int[] StringToIntArray(String[] source) {
      int[] retVal = new int[source.length];
      for (int i=0; i<source.length; i++) {
          retVal[i] = Integer.parseInt(source[i]);
      } // for i
      return retVal;
    }

    /** Converts a String[] to an ArrayList of Strings */
    public static ArrayList toArrayList(String[] source) {
      if (source == null) return new ArrayList(); // empty arraylist
      ArrayList retVal = new ArrayList(source.length);
      for (int i = 0; i < source.length; i++) {
        retVal.add(source[i]);
      } // for
      return retVal;
    }

    /** Converts an array of Objects to an arraylist containing the objects.
     *  Maybe Java has this, but I couldn't find it in my few minutes of searching.
     *  This is a more generic version of toArrayList(String[]) */
    public static ArrayList toArrayList(Object[] source) {
      ArrayList retVal = new ArrayList(source.length);
      for (int i = 0; i < source.length; i++) {
        retVal.add(source[i]);
      }
      return retVal;
    } // toArrayList

    /** Searches the array for the specified value,
     * @return the index if it is found, -1 otherwise
     */
    public static int indexOf(int[] array, int target) {
      if (array == null) return -1;
      for (int i=0; i<array.length; i++) {
        if (array[i] == target) return i;
      }
      return -1;
    }

    /** Searches the array for the specified value (Using Object.equals)
     * @return the index in the array, if it is found, -1 otherwise
     */
    public static int indexOf(Object[] array, Object target) {
      if (array == null) return -1;
      for (int i=0; i<array.length; i++) {
        if (array[i].equals(target)) return i;
      }
      return -1;
    }


    /** Converts and ArrayList containing objects to an ArrayList containing Strings
      * @param array  The arraylist of Objects
     * @param preserveNull  If true nulls will return null,  if false nulls will return ""
     * @return an ArrayList of Strings.  Returns null if array is null
     */
    public static ArrayList toStrings(ArrayList array, boolean preserveNull) {
      if (array == null) return null;
      ArrayList retVal = new ArrayList(array.size());
      for (int i = 0; i < array.size(); i++) {
        Object obj = array.get(i);
        if (obj != null) {
          retVal.add(obj.toString());
        // Object is null
        } else if (preserveNull) {
          retVal.add(null);
        } else {
          retVal.add("");
        }
      } // for
      return retVal;
    } // toStrings


    /** Converts binary buf to encoded hex string equivalent
     * @return buf as a hex string
     */
    public static String bytesToHex(byte buf[]) {
      StringBuffer strbuf = new StringBuffer(buf.length * 2);
      int i;

      for (i = 0; i < buf.length; i++) {
        if (((int) buf[i] & 0xff) < 0x10)
          strbuf.append("0");
        strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
      }
      return strbuf.toString();
    }

    public static byte[] toByteArray(short foo) {
      return toByteArray(foo, new byte[2]);
    }

    public static byte[] toByteArray(int foo) {
      return toByteArray(foo, new byte[4]);
    }

    public static byte[] toByteArray(long foo) {
      return toByteArray(foo, new byte[8]);
    }

    private static byte[] toByteArray(long foo, byte[] array) {
      for (int iInd = 0; iInd < array.length; ++iInd) {
        array[iInd] = (byte) ((foo >> (iInd * 8)) % 0xFF);
      }
      return array;
    }

    /** Searches the ArrayList for the specified value (Using Object.equals)
     * @return the index in the ArrayList, if it is found, -1 otherwise
     *  NOTE: this can be done with arraylist.indexOf(target) easier. -Sam
     */
    public static int indexOf(ArrayList array, Object target) {
      if (array == null) return -1;
      for (int i=0; i<array.size(); i++) {
        if (array.get(i).equals(target)) return i;
      }
      return -1;
    }

    /** Adds all the elements from an array into a List.
     * There might be a method of doing this.. but I couldn't find it! */
    public static void addElements(List list, Object[] toAdd) {
      if ((list == null) || (toAdd == null)) return;
      for (int i = 0; i < toAdd.length; i++) {
        list.add(toAdd[i]);
      }// for
    }


    /** Removes the target String from the array (as many times as it occurs) */
    public static String[] removeFromArray(String[] source, String target) {
      if (target == null) {
        return source;
      }
      ArrayList array = new ArrayList();
      for (int i=0; i<source.length; i++) {
        if (!target.equals(source[i])) {
          array.add(source[i]);
          System.out.println("keeping : "+source[i]);
        }
      } // for i

      String[] retVal = new String[array.size()];
      return (String[]) array.toArray(retVal);
    }

    /** Takes an object and returns a string representation of it, or "" if the object is null
     * @param o  The object
     * @return A String representation of the object using toString or "" if the object is null
     */
    public static String noNull(Object o) {
      if (o == null) return "";
      return o.toString();
    }

    /** Removes the target Object from the ArrayLIst (as many times as it occurs)
     *  This is different from ArrayList.remove becuase it will remove all the occurences.
     *  This willl use the .equals method on the object to compare for equality */
    /* // Warning - not used / tested!
    public static ArrayList removeFromArrayList(ArrayList source, Object target) {
      for (int i=source.size()-1; i >= 0; i--) {
        if (target.equals(source.get(i))) {
          source.remove(i);
        }
      } // for i
      return source;
    }
    */


  /** Copies binary data from the input to the output. */
  public static final void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[32768];
    int len;

    while((len = in.read(buffer)) >= 0) {
      out.write(buffer, 0, len);
    }
  } // copyStream


  /** Takes a 'parameter' list and returns a hashmap of "key" value pairs.
   * NOTE : This will NOT handle duplicate parameters!  Duplicates will only have the last one added
   * @param params the params in the form param1=val1&param2=val2
   * @return a hashmap with key/vals from the params
   */
  public static final HashMap loadParams(String params) {
    HashMap retVal = new HashMap();
    if (params == null) return retVal;
    
    String[] pairs = params.split("&");
    for (int i = 0; i < pairs.length; i++) {
      String[] vals = pairs[i].split("=");
      if (vals.length == 2) {
        retVal.put(vals[0], vals[1]);
      }
    } // for i
    return retVal;
  } // loadParams


}




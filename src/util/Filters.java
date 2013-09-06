package util;

import java.util.*;
import java.util.List;
import java.io.*;
import java.text.NumberFormat;
import java.awt.*;

public class Filters {
  
  /** Escapes a String going into the database. The string is expected to be put inside 
   * single quotes when going into the database.  If it is not, then use escapeDbNum instead
   * This will translate any dangerous characters going into the database.
   */
  public static final String escapeDbStr(String inStr) {
    if (inStr == null) return "";
    String retVal = inStr;
    retVal = retVal.replaceAll("\\\\","\\\\\\\\"); // replace \ with \\
    retVal = retVal.replaceAll("'","\\\\'"); // replace ' with \'
    return retVal;
  }
  
  /** Escapes a number going into the database.  This will ensure that the inStr is a valid
   * number and will return null if it is not. 
   */
  public static final String escapeDbNum(String inStr) {
    if (inStr == null) return null;
    try {
       Double.parseDouble(inStr);
    } catch (NumberFormatException e) {
      return null;
    }
    return inStr;
  }
  
  /** Converts a byte[] to a hext string, using all lowercase letters 
   * The seperator is the string used to seperate each byte (each byte turns into two chars)
   */
  public static String toHexString( byte[] b, String separator ) {
    int inputLength = b.length;
    if ( inputLength > 0 ) {
      int sepLength = separator.length();
      int spaceNeeded = inputLength * (2 + sepLength) - sepLength;
      StringBuffer sb = new StringBuffer(spaceNeeded);

      for (int i=0 ; i<inputLength ; i++) {
        if ( i > 0 ) {
          sb.append( separator );
        }
        // This is to add the leading zero
        if( ( b[i] & 0xF0 ) == 0 ) {
          sb.append( '0' );
        }
        sb.append( Integer.toHexString( b[i] & 0xFF ) );
      }
      return sb.toString();
    }
    // Input Length = 0, return an empty string
    return "";
  }


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


  /**
   * Replaces strings
   * @param examine The string that is to be searched.
   * @param toFind The string to find.
   * @param replaceWith The string to replace 'toFind' with.
   * @param ignoreCase if true, match and replace ignoring case.
   */
  public static final String replace(String examine, String toFind, String replaceWith, boolean ignoreCase){
    StringBuffer sBuff = new StringBuffer(examine);
    int loc = -1;
    if (ignoreCase)
      loc=examine.toLowerCase().indexOf(toFind.toLowerCase());
    else
      loc=examine.indexOf(toFind);

    while (loc!=-1) {
      sBuff.replace(loc,loc+toFind.length(),replaceWith);
      // Don't research previously examined parts of the string.
      loc+=replaceWith.length();
      examine = sBuff.toString();

      // Check for more occurences
      if (ignoreCase)
        loc=examine.toLowerCase().indexOf(toFind.toLowerCase(),loc);
      else
        loc=examine.indexOf(toFind,loc);
    }
    return examine;
  }


// ------------------ Escape / Translation Functions  -------------------------

// Capitalizes the first character in each word in the string
// NOTE: This will put one space between each token! (even if the original string had more than one space or tabs between tokens)
//       This also trims the string of any leading or trailing whitespace
//       This will replace _ with spaces before applying the Capitalization
// Originally written by Fox and posted to comp.lang.java.programmer on 06/19/2000 (modified by Sam Ortiz)
   public static String formatForDisplay(String inStr) {
     StringBuffer retval = new StringBuffer();
     String sourceStr = inStr;
     sourceStr = Filters.replace(inStr,"_"," ",false);
     StringTokenizer st = new StringTokenizer(sourceStr.trim());
     try {
       while(st.hasMoreElements()) {
        StringBuffer temp = new StringBuffer(st.nextToken());
        retval.append(Character.toUpperCase(temp.charAt(0)) + temp.toString().substring(1) + (st.hasMoreTokens() ? " " : ""));
       } //while
     } catch(NoSuchElementException e) {

     } //try
     return retval.toString();
   } // initCap


// This will escape quotes for SQL going into the database
// NOTE: This is not meant to be protection against SQL injection attacks (but it should provide some protection)
//       StaticUtil.filterInput should be better protection for SQL Injection attacks, but at the moment it is
//       removing ' which is not acceptable for some uses. (where the grammar is important) (its != it's)
// NOTE : This will return "" and never null!
   public static String escape(String inStr) {
     if (inStr == null) return "";
     String retVal = inStr;
     retVal = Filters.replace(retVal,"\\","\\\\",false); // replace \ with \\
     retVal = Filters.replace(retVal,"'","\\'",false); // replace ' with \'
     return retVal;
   }//escape

   /** This behaves like escape, escaping strings coming from the user and needing to go into the database,
    *  NOTE: unlike escape this will return null if the inStr is null! (sometimes useful)
    */
   public static String escapeSQL(String inStr) {
     if (inStr == null) return null;
     return escape(inStr);
   }

   /** Calls escape (for SQL) on each of the Strings  */
   public static String[] escape(String[] inStrs) {
     if (inStrs == null) return new String[0];
     String[] retVal = new String[inStrs.length];
     for (int i=0; i<inStrs.length; i++) {
        retVal[i] = escape(inStrs[i]);
      }//for
     return retVal;
   }//escape[]

   /** This will escape the string so it is suitable for display as HTML.
    *  This will Preserve the "BCI" tags, and escape everything else.  The BCI tags
    * are the limited subset of HTML that forms supports <BR> and <B>
    * @param inStr String to escape (data from question text or help text)
    * @return escaped String, suitable for displaying as HTML
    */
   public static String escapeMyHTML(String inStr) {
     String retVal = inStr.replaceAll("<BR>", "~BR~").replaceAll("<B>", "~B~");
     retVal = Filters.replace(retVal, ">", "&gt;", false);
     retVal = Filters.replace(retVal, "<", "&lt;", false);
     retVal = retVal.replaceAll("~B~", "<B>").replaceAll("~BR~", "<BR>");
     return retVal;
   }

   /** Replaces HTML special characters with their HTML equivalent
    * " gets replaced with &quot;  etc */
   public static String escapeHTML(String inStr) {
     if (inStr == null) return "";
     String retVal = inStr;
     retVal = Filters.replace(retVal, "&", "&amp;", false); // this must be first! (or else the escape chars would get escaped)
     retVal = Filters.replace(retVal, "#", "&#35;", false); // this must be second, or else any &#XXX; would get mangled
     retVal = Filters.replace(retVal, "\"", "&quot;", false);
     retVal = Filters.replace(retVal, "'", "&#39;", false);
     retVal = Filters.replace(retVal, ">", "&gt;", false);
     retVal = Filters.replace(retVal, "<", "&lt;", false);
     retVal = Filters.replace(retVal, "%", "&#37;", false);
     retVal = Filters.replace(retVal, "?", "&#63;", false);
     return retVal;
   }//escapeHTML

   /** Encodes the string suitable for passing in a URL
    *  This replaces URL sensitive characters with their % escape sequence */
   public static String escapeURL(String inStr) {
     if (inStr == null) return "";
     String retVal = inStr;
     retVal = Filters.replace(retVal, "%", "%25", false); // this must be first! (or else the escape chars would get escaped)
     retVal = Filters.replace(retVal, ":", "%3A", false);
     retVal = Filters.replace(retVal, "?", "%3F", false);
     retVal = Filters.replace(retVal, "&", "%26", false);
     retVal = Filters.replace(retVal, "=", "%3D", false);
     retVal = Filters.replace(retVal, "#", "%23", false);
     retVal = Filters.replace(retVal, "$", "%24", false);
     retVal = Filters.replace(retVal, " ", "%20", false);
     return retVal;
   }

   /** Encodes a string for storage in XML
    *  this replaces XML sensitive characters with XML escape codes (similar to HTML) */
   public static String escapeXML(String inStr) {
     if (inStr == null) return "";
     String xml = inStr;
     xml = xml.replaceAll("&", "&amp;");
     xml = xml.replaceAll("<", "&lt;");
     xml = xml.replaceAll(">", "&gt;");
     xml = xml.replaceAll("'", "&apos;");
     xml = xml.replaceAll("\"","&quot;");
     return xml;
   }


   /** This will escape characters that may cause problems in javascript.
    *  This is different from HTML encoding, which will protect the data getting
    *  parsed in HTML, but things like &#39; (single quote : ') will be translated
    *  To a ' in the HTML page, and can still cause javascript problems.
    *  Use this function when you know the string is going to end up in a Javascript
    *  String constant (inside " and/or ' usually).
    *  JavaScript escaping is different from HTML escaping, in the way it handles
    *  single quotes :
    *  '  turns into \'  (different from HTML escape of &#39; )
    *  "  turns into &quot; (same as HTML escape)
    *  \n turns into <BR> (same as HTML escape)
    *
    * This function will turn &#39; into \&#39; so that if the text is already HTML escaped
    * it can be JavaScript escaped as well...
    *  NOTE : Once a string is JavaScript escaped, it will not display properly in HTML.
    *  ' will turn into \' in the display.  It must be put into a JavaScript string to display
    *  correctly.
    */
   public static String escapeJavaScript(String inStr) {
     if (inStr == null) return null;
     String retVal = inStr;
     retVal = Filters.replace(retVal, "'", "\\'", false);
     retVal = Filters.replace(retVal, "&#39;", "\\&#39;", false);
     retVal = Filters.replace(retVal, "\"", "&quot;", false);
     retVal = Filters.replace(retVal, "\n", "<BR>", false);
     retVal = Filters.replace(retVal, "\r", "", false);
     return retVal;
   } // escapeJavaScript


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
       if (array[i] == null) {
         if (target == null) return i;
      } else {
         if (array[i].equals(target)) return i;
       }
     } // for
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



}
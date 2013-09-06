package util;

import javax.servlet.http.*;
import java.util.*;
import java.text.*;
import java.sql.Date;
import java.sql.*;

/**
 * Date Utilities.
 * These can be used for getting / submitting dates in HTML
 * See the comment at the end of this package for a usage example
 * @author Sam Ortiz
 */

public final class DateUtils {

  public static final String[] MONTHS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
  public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd"; // for Java Date Casting
  public static final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT+" HH:mm";
  public static final int MILLIS_IN_DAY=24*60*60*1000;
  public static final int MILLIS_IN_HOUR=60*60*1000;
  public static final int MILLIS_IN_MINUTE=60*1000;
  public static final int DAYS = 0;
  public static final int HOURS = 1;
  public static final int MINUTES = 2;


  // Do not instantiate this class, use the static methods
  private DateUtils() { }



  // ----------------------------------------- Generating HTML (html date chooser) ---------------------------------------

  /** @return HTML for displaying a date, the form
   * elements will be named *_month *_day *_year where *=parameter prefix
   *  NOTE:  The parameter_prefix must be unique across the entire form.
   *         (meaning you cannot use the same parameter prefix twice, or
   *          only the first one will be returned)
   */
  public static String getHTML(Calendar defaultDate, String parameter_prefix) {
    StringBuffer html = new StringBuffer();

    // Display the month
    html.append("<SELECT NAME=\""+parameter_prefix+"_month\">");
    for (int i=0; i<MONTHS.length; i++) {
      html.append("<OPTION VALUE=\""+i+"\"");
      if (i==defaultDate.get(Calendar.MONTH)) html.append(" SELECTED ");
      html.append(">"+MONTHS[i]+"</OPTION>");
    }
    html.append("</SELECT>\n");

    // Display the Day
    html.append("<SELECT NAME=\""+parameter_prefix+"_day\">");
    for (int i=1; i<=31; i++) {
      html.append("<OPTION VALUE=\""+i+"\"");
      if (i==defaultDate.get(Calendar.DAY_OF_MONTH)) html.append(" SELECTED ");
      html.append(">"+i+"</OPTION>");
    }
    html.append("</SELECT>\n");

    // Display the Year
    int thisYear = defaultDate.get(Calendar.YEAR);
    html.append("<SELECT NAME=\""+parameter_prefix+"_year\">");
    for (int i=(thisYear-10); i<=(thisYear+10); i++) {
      html.append("<OPTION VALUE=\""+i+"\"");
      if (i==thisYear) html.append(" SELECTED ");
      html.append(">"+i+"</OPTION>");
    }
    html.append("</SELECT>\n");

    return html.toString();
  }//getHTML


  /** @return HTML for entering a date, defaulted to today's date*/
  public static String getHTML(String parameter_prefix) {
    return getHTML(Calendar.getInstance(), parameter_prefix);
  }//getHTML


  /** @return HTML for entering a date, defaulting to a date specified by a string
    * @throws  MyException if the date cast fails */
  public static String getHTML(String defaultDateStr, String parameter_prefix) throws MyException {
      java.util.Date defaultDate = stringToDate(defaultDateStr);
      Calendar c = Calendar.getInstance();
      c.setTime(defaultDate);
      return getHTML(c, parameter_prefix);
  }//getHTML


  /** @return HTML for entering a date, defaulting to an SQL Date
    * This may be useful, as we pull dates out of the database in java.sql.Date format */
  public static String getHTML(java.sql.Date defaultDateSQL, String parameter_prefix) {
    Calendar c = Calendar.getInstance();
    c.setTime(defaultDateSQL);
    return getHTML(c, parameter_prefix);
  }//getHTML







// ----------------------------- Generating Javascript (popup date chooser) ------------------------------------

 /** returns a string to load the javascript header
  *  this MUST be called before using the getJavaScript functions
  */
  public static String getJavaScriptHeader() {
    return "<script language=\"JavaScript\" src=\"/js/date-picker.js\"></script>";
  }//getJavaScriptHeader


  /** Returns HTML to choose a date, using JavaScript
   *  NOTES:
   *   - You must call getJavaScriptHeader() earilier on the page than this function!
   *   - For the JavaScript to work, this should be called within the first form on the page
   * @return HTML/JavaScript
   * @param defaultDate Calendar to specify the default date
   * @param parameter_prefix String used to identify this field/fields for later retrieval (after submitting)
   */
  public static String getJavaScript(Calendar defaultDate, String parameter_prefix) {
    StringBuffer javaScript = new StringBuffer();
    String defaultDateStr = "";
    String fieldName = parameter_prefix+"_field";
    
    if (defaultDate != null) defaultDateStr = calendarToString(defaultDate, DEFAULT_DATE_FORMAT);

    javaScript.append("<input type=text name=\""+fieldName+"\" value=\""+defaultDateStr+"\" size=15>");
    javaScript.append("<a href=\"javascript:show_calendar('forms[0]."+fieldName+"');\" ");
    javaScript.append("onmouseover=\"window.status='Date Picker';return true;\" ");
    javaScript.append("onmouseout=\"window.status='';return true;\">");
    javaScript.append("<img src=\"/images/calendar_icon_16x16.gif\" width=16 height=16 border=0></a>");
    return javaScript.toString();
  }//getJavaScript

  // Defaults the defaultDate to today
  public static String getJavaScript(String parameter_prefix) {
    return getJavaScript(Calendar.getInstance(), parameter_prefix);
  }


  // Converts the date to a Calendar then gets the JavaScript
  public static String getJavaScript(java.sql.Date defaultDateSQL, String parameter_prefix) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(defaultDateSQL);
    return getJavaScript(cal, parameter_prefix);
  }


  // Parses a date from the string and then returns the JavaScript
  public static String getJavaScript(String defaultDateStr, String parameter_prefix) throws MyException {
    Calendar cal = null;
    java.util.Date newDate = stringToDate(defaultDateStr);
    if (newDate != null) {
      cal = Calendar.getInstance();
      cal.setTime(newDate);
    } 
    return getJavaScript(cal, parameter_prefix);
  }




// -------------------------- Parsing Parameters ---------------------------------------------



  /** @return a Calendar set to the correct date from the parameters
    * @throws NumberFormatException if any of the parameters are invalid
    * @param year The year number
    * @param month the Month number, should be between 0 and 11
    * @param day  the day of the month, should be between 1 and 31 (less for some months) */
  public static Calendar getDateAsCalendar(String year, String month, String day) throws NumberFormatException{
    Calendar c = Calendar.getInstance();
    c.setLenient(false);
    c.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    return c;
  }// getDateCalendar

   /**  @return a Calendar set to the correct date from the parameters or null if
    *           the calendar is invalid
     *  This can be used with either the getHTML or getJavaScript inputs  */
  public static Calendar getDateAsCalendar(HttpServletRequest request, String parameter_prefix) throws NumberFormatException, MyException {
    try {
      Calendar cal = null;
      String jsField = request.getParameter(parameter_prefix+"_field");
      if (jsField != null) {
        cal=getFieldDateAsCalendar(jsField);
      } else {
        String year = request.getParameter(parameter_prefix+"_year");
        String month = request.getParameter(parameter_prefix+"_month");
        String day = request.getParameter(parameter_prefix+"_day");
        cal=getDateAsCalendar(year, month, day);
      }
      if (cal==null) return null;
      // Make the calendar accept only valid dates without trying to interpret
      // strange values (ie. month 13)
      cal.setLenient(false);
      // Forces a validity check on the calendary object
      cal.getTime();
      return cal;
    }
    catch (Exception e){
      // Any exceptions indicate the date is invalid.
      return null;
    }
  } //getDateAsCalendar

  /**
   * Check whether the user's supplied date is valid. If the target date is
   * destined for further manipulation/storage, it is better to use getDateAsCalender()
   * as this will prevent duplicate execution of the code (once to check the date, once to
   * get the date).
   * @return true - its valid, false - the date is invalid
   */
  public static boolean isValidDate(HttpServletRequest request, String parameter_prefix) throws NumberFormatException, MyException {
    return (getDateAsCalendar(request,parameter_prefix)==null?false:true);
  }

  /** @return gets the date as a String formatted with the supplied format */
  public static String getDateAsString(String year, String month, String day, String format) throws NumberFormatException {
    Calendar c = getDateAsCalendar(year, month, day);
    return calendarToString(c, format);
  }

  /** @return the date from the parameters as a string, formatted with a default date format */
  public static String getDateAsString(String year, String month, String day) throws NumberFormatException {
    return getDateAsString(year, month, day, DEFAULT_DATE_FORMAT);
  }

  /** @return the date from the parameters as a string formatted with a default date format
    *  This can be used with either the getHTML or getJavaScript inputs */
  public static String getDateAsString(HttpServletRequest request, String parameter_prefix) throws NumberFormatException, MyException {
    String jsField = request.getParameter(parameter_prefix+"_field");
    if (jsField != null) return getFieldDateAsString(jsField);
    String year = request.getParameter(parameter_prefix+"_year");
    String month = request.getParameter(parameter_prefix+"_month");
    String day = request.getParameter(parameter_prefix+"_day");
    if ((year==null) || (month==null) || (day==null)) return null;
    return getDateAsString(year, month, day);
  }

  /** @return the date from the parameters as a string formatted with the specified format string
   *  This can be used with either the getHTML or getJavaScript inputs
   * Format strings should follow the conventions specified in documentation for java.text.SimpleDateFormat
   * yyyy = year (eg. 2002)
   * MM = month (eg. 02)
   * MMM = month (eg. Jan)
   * dd = day (eg 23)   */
  public static String getDateAsString(HttpServletRequest request, String parameter_prefix, String format) throws NumberFormatException, MyException {
    String jsField = request.getParameter(parameter_prefix+"_field");
    if (jsField != null) return getFieldDateAsString(jsField, format);
    String year = request.getParameter(parameter_prefix+"_year");
    String month = request.getParameter(parameter_prefix+"_month");
    String day = request.getParameter(parameter_prefix+"_day");
    if ((year==null) || (month==null) || (day==null)) return null;
    return getDateAsString(year, month, day, format);
  }

  // For parsing dates from single fields (used in javascript)
  public static Calendar getFieldDateAsCalendar(String field) throws MyException {
    if (field == null) return null;
    Calendar c = Calendar.getInstance();
    java.util.Date genDate = stringToDate(field);
    if (genDate==null)
      return null;
    c.setTime(stringToDate(field));
    return c;
  }//getFieldDateAsCalendar

  public static String getFieldDateAsString(String field) throws MyException {
    if (field == null) return "";
    java.util.Date date = stringToDate(field);
    if (date == null) return null;
    return dateToString(date);
  }//getFieldDateAsString

  public static String getFieldDateAsString(String field, String format) throws MyException {
    if (field == null) return "";
    java.util.Date date = stringToDate(field);
    if (date == null) return null;
    return dateToString(date, format);
  }//getFieldDateAsString


  // ------------------------------- MISC Date Casting Functions -------------------------------------------


  /**
   * Takes a string representation of a date in the form yyyy/mm/dd and converts
   * it into a date. If there is a problem, null is returned.
   * @return the date if successful, otherwise null
   */
  public static final java.util.Date stringToDate(String dateString) {
    if (dateString == null) return null;
    SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    df.setLenient(false);
    try {
      return df.parse(dateString);
    } catch (Exception e) {
      System.out.println("Error parsing date :"+dateString+" with format :"+DEFAULT_DATE_FORMAT+". "+e.getMessage());
      return null;
    }
  }


  /** @return a String with the date formatted according to format */
  public static String dateToString(java.util.Date inDate, String format) {
    if (inDate == null) return null;
    SimpleDateFormat df = new SimpleDateFormat(format);
    return df.format(inDate);
  }

  /** @return a String with a formatted date
   * this is used to provide a uniform default date formatting */
  public static String dateToString(java.util.Date inDate) {
    if (inDate == null) return null;
    return dateToString(inDate, DEFAULT_DATE_FORMAT);
  }

  /**
   * @return a String formatted according to the default timestamp pattern
   */
  public static String timestampToString(Timestamp ts) {
    if (ts == null) return null;
    return dateToString(ts, DEFAULT_DATE_TIME_FORMAT);
  }

  /** @return a String with the date formatted according to format*/
  public static String calendarToString(Calendar c, String format) throws NumberFormatException {
    if (c == null) return null;
    return dateToString(c.getTime(), format);
  }

  /** @return a String with the date formatted according to the default format*/
  public static String calendarToString(Calendar c) throws NumberFormatException {
    if (c == null) return null;
    return calendarToString(c, DEFAULT_DATE_FORMAT);
  }

  /**
   * Return the number of days between now and the passed date
   */
  public static final int getDaysToDate(java.util.Date toCheck){
    long now = System.currentTimeMillis();
    return (int)((toCheck.getTime()-now)/MILLIS_IN_DAY);
  }

  /**
   * Get the number of days, hours and minutes between the passed date and the current time
   * @return [0] The days, [1] the hours, [2] the minutes
   */
  public static final int[] getCountdownInfo(java.util.Date toCheck){
    if (toCheck==null)
      return null;

    long now = System.currentTimeMillis();
    //long checkTime = toCheck.getTime();

    int[] diff = new int[3];
    diff[DAYS]=(int)((toCheck.getTime()-now)/MILLIS_IN_DAY);
    diff[HOURS]=(int)(((toCheck.getTime()-now)%MILLIS_IN_DAY)/MILLIS_IN_HOUR);
    diff[MINUTES]=(int)(((toCheck.getTime()-now)%MILLIS_IN_DAY)%MILLIS_IN_HOUR)%MILLIS_IN_MINUTE;
    return diff;
  }

} // class


/* // Example of a JSP that uses the DateUtils Package
<%@ page import=" com.bci.car.ui.util.* java.util.* java.text.* "%>

<FORM METHOD=post action="controller.jsp?action=util.Menu&mainpanel=bciadmin/mainpanel.jsp&flush=false">

<!-- How to draw a date in a form (HTML only - No JavaScript) -->
 Start : <%=DateUtils.getHTML("start")%> <!-- default to todays date --> <BR>
 End : <%=DateUtils.getHTML("Jan 12, 2003", "end")%>  <!-- default date as a String --> <BR>
 SQL : <%=DateUtils.getHTML(new java.sql.Date(System.currentTimeMillis()), "sql")%>  <!-- default date as an sql.Date --> <BR>

 <!-- using Javascript popup -->
 <%=DateUtils.getJavaScriptHeader()%>
 start (js) : <%=DateUtils.getJavaScript("start_js")%> <!-- default to today's date using javascript pop-up -->
 end (js)   : <%=DateUtils.getJavaScript("2002/12/29", "end_js")%> <!-- with javascript pop-up and specified default date -->

 <INPUT TYPE=submit NAME=submit VALUE=submit>
</FORM>

<% // This part gets the date from the parameters
   // This could be done in a requesthandler or a jsp.

   if (request.getParameter("submit") != null) {

     // Get date from parameters as Calendar
     Calendar c = DateUtils.getDateAsCalendar(request, "start");
     out.write("Start="+DateFormat.getDateInstance().format(c.getTime())+"<BR>");

     // Get date from parameters as String
     // NOTE: getting javascript fields is exactly the same as getting html fields
     out.write("End="+DateUtils.getDateAsString(request,"end")+"<BR>");
     out.write("End(js)="+DateUtils.getDateAsString(request,"end_js")+"<BR>");

     // Get the date from the parameters as a formatted String
     out.write("SQL="+DateUtils.getDateAsString(request,"sql","yyyy MM dd")+"<BR>");

   }
%>
 */

package util;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import java.text.*;

/**
 *  Database Utilities.  
 *  These wrap calls to the database
 * @author  sortiz
 */
public class DbUtils {
  
  private static Connection getConnection() {
    try {
      // Retrieve the database info from the the application server's .xml initialization file
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      DataSource source = (DataSource) env.lookup("jdbc/church");
      return source.getConnection();
    } catch (Exception e){
      System.out.println("Unable to get connection to the database.");
      e.printStackTrace();
    }
    return null;
  }


 // Gets a statement from a connection
 private static Statement getStatement(Connection conn) throws MyException {
   try {
    return conn.createStatement();
   } catch (SQLException e) {
     e.printStackTrace();
     throw new MyException(e.getMessage());
   }
 } // getStatement

 // Closes a statement
 private static void closeStatement(Statement stat) {
   try {
    if (stat!=null) stat.close();
   } catch (SQLException e) {}
 } // closeStatement

 // Closes a Connection
 private static void closeConnection(Connection conn) {
  try {
    if (conn!=null) conn.close();
  } catch (SQLException e) {}
 } // closeConnection

  
 // Runs an update for some generic SQLText.  
 // returns the number of rows processed
 public static int update(String SQLText) throws MyException {
  boolean success = true;
  // Get a connection to the database
  Connection conn = getConnection();
  Statement stat = getStatement(conn);
  int rows_processed = -1;
  try {
    rows_processed = stat.executeUpdate(SQLText);
  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeStatement(stat);
    closeConnection(conn);
  }
  return rows_processed;
 } // update
 
 
 
 // returns a String from a ResultSet as a String
 // This is used becuase I want to do proper NULL checking.
 private static String getStringStr(ResultSet rs, String colName) throws SQLException {
  String theVal = rs.getString(colName);
  if (theVal != null) {
    return theVal;
  } else {
    return "";
  }
 }

 // Returns a Timestamp from a resultset as a String
 private static String getTimestampStr(ResultSet rs, String colName) throws SQLException {
  java.sql.Timestamp theVal = rs.getTimestamp(colName);
  if (theVal != null) {
    SimpleDateFormat df = new SimpleDateFormat(DateUtils.DEFAULT_DATE_TIME_FORMAT);
    return df.format(new java.util.Date(theVal.getTime()));
  } else {
    return "";
  } // if
 } // getTimestampStr

 // Returns a Date from a resultset as a String
 private static String getDateStr(ResultSet rs, String colName) throws SQLException {
  java.util.Date theVal = rs.getDate(colName);
  if (theVal != null) {
    SimpleDateFormat df = new SimpleDateFormat(DateUtils.DEFAULT_DATE_FORMAT);
    return df.format(theVal);
  } else {
    return "";
  } // if
 } // getDateStr

 // Returns a boolean (aka BIT) from a resultset as a String
 // this returns the string "true" or "false"
 // this return String can later be parsed into a boolean with the function getBooleanFromMap
 private static String getBooleanStr(ResultSet rs, String colName) throws SQLException {
  boolean theVal = rs.getBoolean(colName);
  if (theVal) {
    return "true";
  } else {
    return "false";
  } // if
 } // getBooleanStr

 // returns an int from a ResultSet as a String
 // the only purpose of this function is to provide a standard interface with other types
 private static String getIntStr(ResultSet rs, String colName) throws SQLException {
  return new Integer(rs.getInt(colName)).toString();
 }

 // returns a big number from a ResultSet as a String
 // the only purpose of this function is to provide a standard interface with other types
 private static String getBigDecimalStr(ResultSet rs, String colName) throws SQLException {
  return rs.getBigDecimal(colName).toString();
 }
 
 // returns an double from a ResultSet as a String
 // This is used to provide a standard interface with the other types
 private static String getDoubleStr(ResultSet rs, String colName) throws SQLException {
  return new Double(rs.getDouble(colName)).toString();
 }

 // returns any type of data from a resultset as a String.  (given the column name and type)
  private static String getAnyStr(ResultSet rs, String name, int type) throws SQLException {
  if ((type == Types.INTEGER) || (type == Types.SMALLINT)) {
    return getIntStr(rs,name);
  } else if (type == Types.BIGINT) {
    return getBigDecimalStr(rs,name);
  } else if ((type == Types.VARCHAR) || (type==Types.CHAR)){
    return getStringStr(rs,name);
  } else if (type == Types.TIMESTAMP) {
    return getTimestampStr(rs,name);
  } else if (type == Types.DATE) {
    return getDateStr(rs,name);
  } else if (type == Types.BIT || type==Types.BOOLEAN) { 
    return getBooleanStr(rs,name);
  } else if (type == Types.REAL || type == Types.NUMERIC || type == Types.DOUBLE || type == Types.FLOAT) {
    return getDoubleStr(rs,name);
  } else {
    throw new SQLException("UNKNOWN Column Type in DbUtils.getAnyStr: name="+name+" Type="+type);
  } // if else...
 }

 // Returns an HTML String with all the java.sql.Types and their code values
 public static String getSQLTypesStr() {
  return
   " ARRAY="+Types.ARRAY+"<BR>"+
   " BIGINT="+Types.BIGINT+"<BR>"+
   " BINARY="+Types.BINARY+"<BR>"+
   " BIT= "+Types.BIT+"<BR>"+
   " BLOB="+Types.BLOB+"<BR>"+
   " BOOLEAN="+Types.BOOLEAN+"<BR>"+
   " CHAR="+Types.CHAR+"<BR>"+
   " CLOB="+Types.CLOB+"<BR>"+
   " DATALINK="+Types.DATALINK+"<BR>"+
   " DATE="+Types.DATE+"<BR>"+
   " DECIMAL="+Types.DECIMAL+"<BR>"+
   " DISTINCT="+Types.DISTINCT+"<BR>"+
   " DOUBLE="+Types.DOUBLE+"<BR>"+
   " FLOAT="+Types.FLOAT+"<BR>"+
   " INTEGER="+Types.INTEGER+"<BR>"+
   " JAVA_OBJECT="+Types.JAVA_OBJECT+"<BR>"+
   " LONGVARBINARY="+Types.LONGVARBINARY+"<BR>"+
   " LONGVARCHAR="+Types.LONGVARCHAR+"<BR>"+
   " NULL="+Types.NULL+"<BR>"+
   " NUMERIC="+Types.NUMERIC+"<BR>"+
   " OTHER="+Types.OTHER+"<BR>"+
   " REAL="+Types.REAL+"<BR>"+
   " REF="+Types.REF+"<BR>"+
   " SMALLINT="+Types.SMALLINT+"<BR>"+
   " STRUCT="+Types.STRUCT+"<BR>"+
   " TIME="+Types.TIME+"<BR>"+
   " TIMESTAMP="+Types.TIMESTAMP+"<BR>"+
   " TINYINT="+Types.TINYINT+"<BR>"+
   " VARBINARY="+Types.VARBINARY+"<BR>"+
   " VARCHAR="+Types.VARCHAR+"<BR>";
 }//getSQLTypesStr



// Returns a hashmap of strings with the data from the select
// Will return empty strings and not null.  The keys in the hashmap
// will match the column names in the select. (Make sure you name your columns!)
// NOTE!  Do not return more than one row from the select, or else only
//        the last row will be in the hashmap!
 public static HashMap getRow(String SQLText) throws MyException {
  HashMap retVal = new HashMap();

  Connection conn = getConnection();
  Statement stat = DbUtils.getStatement(conn);

  try {
    ResultSet rs = stat.executeQuery(SQLText);
    while (rs.next()){
      ResultSetMetaData rsMD = rs.getMetaData();
      for (int i=1; i<=rsMD.getColumnCount(); i++) {
        String name = rsMD.getColumnName(i);
        int type = rsMD.getColumnType(i);
        retVal.put(name, getAnyStr(rs,name,type));
      } // for
    }// while

  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeConnection(conn);
  }
  
  return retVal;
 } // getRow



  // This will return an ArrayList of HashMaps.
  // Each HashMap will contain the results of the SQL select for one row
  // The HashMap values are all Strings and will return "" and not null
  public static ArrayList getManyRows(String SQLText) throws MyException {
  ArrayList retVal = new ArrayList();
  Connection conn = getConnection();
  Statement stat = DbUtils.getStatement(conn);

  try {
    ResultSet rs = stat.executeQuery(SQLText);
    while (rs.next()){
      HashMap thisVal = new HashMap();
      ResultSetMetaData rsMD = rs.getMetaData();
      for (int i=1; i<=rsMD.getColumnCount(); i++) {
        String name = rsMD.getColumnName(i);
        int type = rsMD.getColumnType(i);
        thisVal.put(name, getAnyStr(rs, name,type));
      } // for
      // add the HashMap to the vector
      retVal.add(thisVal);
    }// while
  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeConnection(conn);
  }

  return retVal;
 } // getManyRows


  // This returns an ArrayList of Strings.
  // Each String contains the results of the first column of the SQL select for one row
  // this will return "" and not null
  public static ArrayList getRows_SingleCol(String SQLText) throws MyException {
  ArrayList retVal = new ArrayList();
  Connection conn = getConnection();
  Statement stat = DbUtils.getStatement(conn);

  try {
    ResultSet rs = stat.executeQuery(SQLText);
    while (rs.next()){
      ResultSetMetaData rsMD = rs.getMetaData();
      String name = rsMD.getColumnName(1);
      int type = rsMD.getColumnType(1);
      retVal.add(getAnyStr(rs, name, type));
    }// while
  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeConnection(conn);
  }

  return retVal;
 } // getRows_SingleCol
  


 // Returns a HashMap of the first two columns in the select with an
 // entry in the hashmap for each row returned.
 // the first column is the key and the second column is the value
 // the key and value are both converted to Strings
 // NOTE: The select must have two columns.  If there is only one an exception will be thrown
 // NOTE: if the select returns multiple columns with the same key (first column)
 //       only the last row with that key will be in the hashmap.
 public static HashMap getRows(String SQLText) throws MyException {
  HashMap retVal = new HashMap();

  Connection conn = getConnection();
  Statement stat = DbUtils.getStatement(conn);

  try {
    ResultSet rs = stat.executeQuery(SQLText);
    ResultSetMetaData rsMD = rs.getMetaData();
    // error checking
    if (rsMD.getColumnCount() <= 1) throw new MyException("Too few columns!");

    while (rs.next()){
      String keyName = rsMD.getColumnName(1);
      int keyType = rsMD.getColumnType(1);
      String valName = rsMD.getColumnName(2);
      int valType = rsMD.getColumnType(2);
      // Convert the column Types into suitable Strings and put them into the hashTable.
      retVal.put(getAnyStr(rs,keyName,keyType), getAnyStr(rs,valName,valType));
    }// while

  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeConnection(conn);
  }
  
  return retVal;
 } // getDataRows



 // Gets the first column of the last row (there should be only one row and one column) from the database
 // Returns the value as a String returns "" if there is no value or a null is returned
 public static String getSingleValue(String SQLText) throws MyException {
  String retVal = "";

  Connection conn = getConnection();
  Statement stat = DbUtils.getStatement(conn);
  try {
    ResultSet rs = stat.executeQuery(SQLText);
    ResultSetMetaData rsMD = rs.getMetaData();
    while (rs.next()){
      String name = rsMD.getColumnName(1);
      int type = rsMD.getColumnType(1);
      retVal = getAnyStr(rs,name,type);
    }// while
  } catch (SQLException e) {
    e.printStackTrace();
    throw new MyException(e.getMessage());
  } finally {
    closeConnection(conn);
  }
  return retVal;
 }
 
  
  
} // DbUtils

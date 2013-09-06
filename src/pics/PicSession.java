package pics;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.postgresql.util.*;
import util.MyException;
import util.Filters;


/**
* This class maintains state information for a client. The default session
* was not used to allow for future storage of methods in the session context.
* It also complicates any attempt to forge authentication data into the session.
* <BR/>
* Implements HTTPSessionBindingListener to ensure that resources are freed
* when the session terminates.
* Any RequestHandler object can interact with JSP view via this class.
*/
public final class PicSession implements Serializable, HttpSessionBindingListener {
  
  // Name of the key stored in the pageContext that holds the session
  public static final String SESSION_NAME = "pic_session";
  private static final boolean DEBUG = false;

  // Session/System Info
  private static Vector allSessions = new Vector(); // Stores a list of all the sessions
  private HashMap data = new HashMap(); //Stores information for display in a JSP view
  private ArrayList messages = new ArrayList(); //Stores a list of messages for display in a JSP view
  
  // Login Info
  private boolean isLoggedIn = false;
  private int uid = -1;
  private String username = "";
  private String name = "";

  /** Constructor */
  public PicSession() {
  }

  /**
   * Gets a HashMap which will have been populated by a handler in order
   * to provide data to the JSP view.
   */
  public final HashMap getHashMap() {
    return data;
  }

  /** Gets an object from the session HashMap */
  public final Object getFromHashMap(String key) {
    return (data.get(key));
  }

  /**
   * Stores an Object in the session HashMap. This is available to
   * JSP pages, servlets and other classes.
   */
  public final void putHashMap(String objectKey, Object toStore) {
    data.put(objectKey, toStore);
  }

  /** Returns a list of messages */
  public final ArrayList getMessages() {
    return messages;
  }

  /** Remove any messages from the list */
  public final void clearMessages() {
    messages.clear();
  }

  /**
   * Puts a message into the message list.
   * @param message The message to add (can contain HTML)
   */
  public final void putMessage(String message) {
    messages.add(message);
  }

  /** Get the number of messages that are available for display  */
  public final int getMessageCount() {
    return messages.size();
  }

  /** Displays all the messages as HTML, and removes them */
  public String displayMessagesHTML() {
    StringBuffer html = new StringBuffer();
    for (int i=0; i<messages.size(); i++) {
      html.append(messages.get(i) + "<BR>");
    } // for
    
    clearMessages();
    return html.toString();
  } // displayMessagesHTML




  // ---------------------------- Login Routines -------------------------------
  
  public boolean isLoggedIn() {
    return this.isLoggedIn;
  }

  /** Returns true if the logged in user has the specified permissions
   * To check for permissions to ~Admin~ in the db use the string "Admin" for perm */
  public boolean hasPermission(String perm) {
    if ((perm == null) || perm.equals("")) return true; // no permission
    if (isLoggedIn) {
      try {
        String dbPerm = PicDb.getSingleValue("select permissions from users where username='"+username+"'");
        if (dbPerm.indexOf("~"+perm+"~") >=0) {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    }
    // Not logged in people have no permissions
    return false;
  }


  public static boolean authenticate(String inUsername, String inPassword) throws MyException {
    String username = Filters.escapeDbStr(inUsername);
    String password = Filters.escapeDbStr(inPassword);
    //System.out.println(username+":"+UnixCrypt.crypt(password)); // (manually enter users in database)
    String dbPass = PicDb.getSingleValue("select password from users where lower(username)=lower('"+username+"') limit 1");
    
    // Match the db password against the user supplied one.
    if ( dbPass.length() > 0 && // User found (with a password) in the database
         UnixCrypt.matches(dbPass, password)) { // passwords match
      return true; // passwords match
    } else {
      return false; // user/password not found or they don't match
    }
  } // authenticate
  
  /** Call this to login the user
   *  @return true if the login succeeded false if it failed
   *  This will set the isLoggedIn variable and the user-info
   */
  public boolean login(String username, String password) throws MyException {
    if ((username != null) && (password != null)) {
      if (authenticate(username, password)) {
        // Passed authentication
        this.isLoggedIn = true;
        HashMap userInfo = PicDb.getRow("select uid, username, name from users where lower(username)=lower('"+Filters.escapeDbStr(username)+"')");
        this.uid = Integer.parseInt((String)userInfo.get("uid"));
        this.username = (String)userInfo.get("username");
        this.name = (String)userInfo.get("name");
        PicLogger.get().info(userInfo()+" logged in");
        return true;
      } else { // Passwords did not match! (failed authentication)
        logout(); // failed password logs the user out
        return false;
      }
    } else { // No username / password specified (Do nothing!)
      logout(); // no username/password logs the user out
      return false;
    }
  } // login
  
  /** Logs the user out. */
  public void logout() {
    if (isLoggedIn) {
      PicLogger.get().info(this.userInfo()+" logged out");
    }
    this.isLoggedIn = false;
    this.uid = -1;
    this.username = "";
    this.name = "";
  } // logout
   
  // Get the user info
  public int getUid() {
    return this.uid;
  }
  public String getUsername() {
    return this.username;
  }
  public String getName() {
   return this.name;
  }

  /** Returns a string with the user's info (name id etc..) */
  public String userInfo() {
    return "#"+this.uid+" "+this.name;
  }

  /** Gets a particular username specified with a uid */
  public String getUserName(int uid) throws MyException {
    return PicDb.getSingleValue("select name from users where uid="+uid);
  }


  // ------------------ HttpSessionBindingListener Interface methods ---------------------
  public final void valueBound(HttpSessionBindingEvent event) {
    if (DEBUG) System.out.println("Pic Session Started");
    allSessions.add(this);
  }

  /**
  *   Use this callback from the JSP engine (issued when this object
  *   is being removed from the session when the session ends)
  *   to ensure that the data source cleans up after itself
  */
  public final void valueUnbound(HttpSessionBindingEvent event) {
    if (DEBUG) System.out.println("Pic Session Ended");
    allSessions.remove(this);
  }

  /**
   * Gets an Vector containing all the active sessions
   */
  public static final Vector getAllSessions() {
    return allSessions;
  }
  
  
  
  // ---------------------------- Session Utilities -------------------------------
  
  public static PicSession getSession(PageContext pageContext) {
    PicSession shrewSession = (PicSession) pageContext.getAttribute(SESSION_NAME,  PageContext.SESSION_SCOPE);
    if (shrewSession == null) {
      shrewSession = new PicSession();
      pageContext.setAttribute(SESSION_NAME, shrewSession, PageContext.SESSION_SCOPE);
    } 
    return shrewSession;
  } // getSession
  
}
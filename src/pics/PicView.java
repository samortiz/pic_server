package pics;

import util.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: sortiz Date: Aug 21, 2004
 */
public class PicView {

  private static boolean DEBUG = false;

  private String start = null;
  private String end = null;
  private String keyword = null;
  private String filter = null;
  private String pids = null;
  private String filterPids = null;

  private int currentIndex = 0;
  private ArrayList data = new ArrayList();
  private String SQL; // set in the constructor
  private PicSession picSession = null;

  /** Use this to get the current view from the session, or create a new one from the parameters
   * Remember : to create a new view you need to pass the parameter "newview" */
  public static PicView getView(PicSession picSession, HttpServletRequest request) throws MyException {
    PicView picView = (PicView) picSession.getFromHashMap("viewpics_view");
    if ((picView == null) || (request.getParameter("newview") != null)) {
      picView = new PicView(request, picSession);
      picSession.putHashMap("viewpics_view", picView);
    }
    return picView;
  }



  /** Constructs a pic view from the parameters found in the request */
  public PicView (HttpServletRequest request, PicSession picSession) throws MyException {
    this.picSession = picSession;

    try {
      start = DateUtils.getDateAsString(request, "start");
      end = DateUtils.getDateAsString(request, "end");
    } catch (Exception e) {
      start = null;
      end = null;
    }

    boolean noDate = (request.getParameter("nodate") != null) && (request.getParameter("nodate").equals("true"));
    if (noDate) {
      start = null;
      end = null;
    }

    keyword = Filters.escapeDbStr(request.getParameter("keyword"));
    if ((keyword != null) && (keyword.length() == 0)) {
      keyword = null;
    }

    filter = Filters.escapeDbStr(request.getParameter("filter"));
    if ((filter != null) && (filter.length() == 0)) {
      filter = null;
    }

    pids = Filters.escapeDbStr(request.getParameter("pids"));
    if ((pids != null) && (pids.length() == 0)) {
      pids = null;
    }

    filterPids = Filters.escapeDbStr(request.getParameter("filterpids"));
    if ((filterPids != null) && (filterPids.length() == 0)) {
      filterPids = null;
    }


    boolean unnamed = "true".equals(request.getParameter("unnamed"));
    boolean movie = "true".equals(request.getParameter("movie"));
    boolean hidden = "true".equals(request.getParameter("hidden"));
    boolean no_vertical = "true".equals(request.getParameter("no_vertical"));
    boolean no_horizontal = "true".equals(request.getParameter("no_horizontal"));

    // Only admins can view hidden pictures
    if (!picSession.hasPermission("Admin")) hidden = false;


    // Get the data
    SQL =
        " select pid from pic " +
        " where true "+ // required since the rest is dynamic and might all be missing...
        ((start != null) ? " and created >= '"+start+"'::timestamp " : "") +
        ((end != null) ? " and created <= '"+end+" 23:59'::timestamp " : "") +
        ((keyword != null) ? " and (name ilike '%"+keyword+"%' or description ilike '%"+keyword+"%' " +
                                   " or keywords ilike '%"+keyword+"%' )" : "") +
        ((filter != null) ? " and not (name ilike '%"+filter+"%' or description ilike '%"+filter+"%' " +
                                   " or keywords ilike '%"+filter+"%' )" : "") +
        ((pids != null) ? " and pid in ("+pids+") " : "") +
        ((filterPids != null) ? " and pid not in ("+filterPids+") " : "") +
        (unnamed ? " and name = '' " : "" ) +
        (movie ? "" : " and movie = false ") + // show all or show just non-movies (pictures)
        (hidden ? "" : " and hidden = false ") + // show all or show just non-hidden
        (no_vertical ? " and vertical=false " : "") +// hide vertical images
        (no_horizontal ? " and vertical=true " : "")+ // Hide horizontal images
        " order by created, pid "+
        " limit "+ (PicConst.PICS_PER_PAGE * 50); // or else it beats up the db pretty badly!
    if (DEBUG) System.out.println("SQL to generate View : "+SQL);
    refreshView();

    PicLogger.get().info(picSession.userInfo()+" Viewing "+(noDate? "No Date" : start+" to "+end)+
        (keyword != null ? " keyword="+keyword : "") + (filter != null ? " filter="+filter :  "") +
        (unnamed ? " UnNamed" : "") + (movie ? " Movie" : "") + (hidden ? " Hidden" : "") +
        " PicCount="+data.size());
  }

  /** Refreshes the view */
  public void refreshView() throws MyException {
    data = PicDb.getRows_SingleCol(SQL);
    if (currentIndex >= data.size()) {
      currentIndex = data.size() -1; // last picture (remain there if we deleted the last one)
    }
  }


  /** Returns all the data for the picture specified by the offset */
  public HashMap getPictureData(int index) throws MyException {
    if (!hasPictures() || (index >= data.size())) {
      return new HashMap();
    } else {
      return PicDb.getRow("select * from pic where pid ="+data.get(index));
    }
  }
  public HashMap getPictureData() throws MyException {
    return getPictureData(currentIndex);
  }
  /** Gets all the file names from the database for all the pictures in this view
   *  This will start at picture index specified by offset, and return at maximum -limit- number of pictures */
  public ArrayList getFileNames(int limit, int offset) throws MyException {
    if (data.size() == 0) return new ArrayList();
    return PicDb.getRows_SingleCol("select file from pic where pid in ("+MyUtils.toCSV(data)+") " +
        " order by created, pid limit "+limit+" offset "+offset+" ");
  }

  /** Calculates the number of pictures that match this criteria */
  public int getCount() throws MyException {
    return data.size();
  }

  public int getIndex() {
    return currentIndex;
  }

  public void setIndex(int newIndex) {
    if ((newIndex >= 0) && (newIndex < data.size())) {
      this.currentIndex = newIndex;
    }
  }

  /** returns true if there are any pictures found in the select */
  public boolean hasPictures() {
    return data.size() > 0;
  }

  /** Goes to the next picture */
  public boolean next() {
    if (currentIndex < (data.size() - 1)) {
      currentIndex += 1;
      return true;
    } else {
      return false;
    }
  } // next
  /** returns true if there is a next picture */
  public boolean hasNext() {
    return currentIndex < (data.size()-1);
  }


  /** Goes to the previous picture */
  public boolean prev() {
    if (currentIndex > 0) {
      currentIndex -= 1;
      return true;
    } else {
      return false;
    }
  } // prev
  /** Returns true if there is a previous picture */
  public boolean hasPrev() {
    return currentIndex > 0;
  }



}

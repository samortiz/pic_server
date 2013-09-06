package pics;
/**
 *  This will contain the template objects for the Picture screens.
 */
public class PicTemplate {

  public static int SEARCH = 0;
  public static int EVENT = 1;
  public static int DATE = 2;
  public static int ADMIN = 3;


  public static String header(PicSession session) {
    return
        "<HTML><BODY BGCOLOR='"+PicConst.BACKGROUND+"'>" +
        "      <head>\n" +
        "        <style type=\"text/css\"> \n" +
        "          .outer_border {\n" +
        "             border:1px;\n" +
        "             border-color:black;\n" +
        "             border-collapse:collapse;\n" +
        "             border-style:solid;\n" +
        "          }\n" +
        "          .inner_border {\n" +
        "             border:1px;\n" +
        "             border-color:black;\n" +
        "             border-style:solid;\n" +
        "          }\n" +
        "        </style>\n" +
        "      </head>" +
        "" +
        "<TABLE WIDTH='100%' BORDER='0' CELLSPACING='0' CELLPADDING='0'>" +
        //" <TR><TD align='center' valign='top'><FONT SIZE='+1'>Sam and Joyce's Pics</FONT></TD></TR>" +
        "  <TR><TD align='center' valign='top'>";
  }

  public static String footer(PicSession session) {
    StringBuffer footer = new StringBuffer();

    footer.append("</TD></TR>");

    if (session.isLoggedIn()) {
      footer.append(
          "<TR><TD valign='bottom' align='center'>"+
          " <FONT SIZE='+1'>Sam and Joyce's Pics</FONT> &nbsp; &nbsp; &nbsp; &nbsp; "+
          "<A HREF=\"/mainpage.jsp\">Main Page</A> &nbsp; "+
          "<A HREF=\"/logout.jsp\">Logout</A>" +
          "</TD></TR>");

    } else { // Not Logged in
      footer.append(
          "<TR><TD valign='bottom' align='center'>"+
          "<A HREF=\"/login.jsp\">Login</A>" +
          "</TD></TR>");
    }

    footer.append("</TABLE>");

    footer.append("</BODY></HTML>");

    return footer.toString();
  }

  /** Draws a navigation row in a table.  This is the grey bar with all the options you can select */
  public static String navRow(int selected, PicSession session) {

    return
    "<TR>"+
    (session.hasPermission("Admin") ? "<TD align=left NOWRAP BGCOLOR="+(selected == ADMIN ? PicConst.MED_LIGHT : PicConst.HIGHLIGHT)+
          " ><B><A HREF='/admin/admin.jsp'>&nbsp;Admin&nbsp;</A></B></TD>" : "") +

    "   <TD align=left NOWRAP BGCOLOR="+(selected == SEARCH ? PicConst.MED_LIGHT : PicConst.HIGHLIGHT)+
          " ><B><A HREF='/search.jsp'>&nbsp;Search for Pics&nbsp;</A></B></TD>"+
    "   <TD align='center' BGCOLOR="+(selected == EVENT ? PicConst.MED_LIGHT : PicConst.HIGHLIGHT)+
          " ><B><A HREF='/events.jsp'>&nbsp;View by Event&nbsp;</A></B></TD>"+
    "   <TD align=right BGCOLOR="+(selected == DATE ? PicConst.MED_LIGHT : PicConst.HIGHLIGHT)+
          " ><A HREF='/viewbymonth.jsp'><B>&nbsp;View by Date&nbsp;</B></A></TD>"+
    " </TR>";
  }


}

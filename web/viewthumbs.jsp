<%@ page import="java.util.*,
                 pics.*,
                 java.io.* "%>
<%@ include file="picauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  // Get the view of the pictures
  PicView picView = PicView.getView(picSession, request);

  int NUM_ROWS = PicConst.PICS_PER_PAGE / PicConst.NUM_COLS;

  int totalPages = (int)Math.ceil((double)picView.getCount() / (double)PicConst.PICS_PER_PAGE);

  // What page we are on
  int curPage = 0;
  try {
    curPage = Integer.parseInt(request.getParameter("page"));
  } catch (Exception e) {
    curPage = 0;
  }

  // Get the next picture
  ArrayList fileNames = picView.getFileNames(PicConst.PICS_PER_PAGE, (curPage*PicConst.PICS_PER_PAGE));
  String base = PicConst.WEB_DIR + File.separator + PicConst.SMALL + File.separator;

%>

<TABLE class='outer_border' CELLPADDING='2' CELLSPACING='0' BGCOLOR='<%=PicConst.LIGHT%>' >

 <!-- Navigation -->
 <TR><TD colspan='100%' valign=center>
 <%
  if (curPage > 0) {
    out.write("<A HREF='/viewthumbs.jsp?page="+(curPage - 1)+"'>Prev</A>");
  } else {
    out.write("<FONT COLOR=GREY>Prev</FONT>");
  }
  out.write(" &nbsp; ");
  if (curPage < (totalPages-1)) { // -1 to correct for the offset (index vs count)
    out.write("<A HREF='/viewthumbs.jsp?page="+(curPage + 1)+"'>Next</A>");
  } else {
    out.write("<FONT COLOR=GREY>Next</FONT>");
  }
 %>
 &nbsp; Page <%=(curPage+1)%> of <%=totalPages%>
 </TD></TR>



<%
  // If there are no pictures
  if (!picView.hasPictures()) {
    out.write("<TR><TD colspan='100%' align=center><FONT COLOR='RED'>No Pictures Found!</FONT></TD></TR>");
  } else { // else it has Pictures!

    for (int y=0; y<NUM_ROWS; y++) {
      out.write("<TR>");

      for (int x=0; x<PicConst.NUM_COLS; x++) {
        int index = (y * PicConst.NUM_COLS) + x;
        String imgPath = null;
        // Use the relative Index to get from the current list of pictures
        if (index < fileNames.size()) {
          imgPath = base+fileNames.get(index);
        }
        // Set the index to the absolute index in the view (to jump to the picture)
        index = (curPage * PicConst.PICS_PER_PAGE) + index;
        out.write("<TD valign=center align=center width='200px'>");
        if (imgPath != null) {
          out.write("<A HREF='/viewpics.jsp?goto="+index+"'><IMG SRC='"+imgPath+"' BORDER=0></A>");
        }
        out.write("</TD>");
      } // for x

      out.write("</TR>");
    } // for y
  } // else has pictures
%>



</TABLE>

<%=PicTemplate.footer(picSession)%>

<%@ page import="java.util.*,
                 pics.*,
                 util.*,
                 java.io.* "%>
<%@ include file="picauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  // get permissions
  boolean isAdmin = picSession.hasPermission("Admin");

  // View pictures
  StringBuffer messages = new StringBuffer();

  // Get or create the view
  PicView picView = PicView.getView(picSession, request);

  // This will do any updates that may be required (rotate,delete,update,comments, etc)
  PicUpdate.doUpdate(request, picView, messages, picSession);


  String go = request.getParameter("go");
  if ("Next".equals(go)) {
    if (picView.hasNext()) {
      picView.next();
    } else {
      messages.append("<FONT COLOR=RED>There are no further pictures!</FONT><BR>");
    }
  }
  if ("Prev".equals(go)) {
    if (picView.hasPrev()) {
      picView.prev();
    } else {
      messages.append("<FONT COLOR=RED>There are no previous pictures!</FONT><BR>");
    }
  }

  String goTo = request.getParameter("goto");
  if (goTo != null) {
    try {
      picView.setIndex(Integer.parseInt(goTo));
    } catch (Exception e) {
      messages.append("<FONT COLOR=RED>Malformed goto request! goto="+goTo+"</FONT><BR>");
    }
  }

  // Calculate what "page" of thumbnails we're on
  int curPage = picView.getIndex() /  PicConst.PICS_PER_PAGE;


  // Get the next picture
  HashMap data = picView.getPictureData();
  String filePath = (String)data.get("file");
  String largeFile = PicConst.WEB_DIR +File.separator + PicConst.LARGE + File.separator + filePath;
  String fullFile  = PicConst.WEB_DIR +File.separator + PicConst.FULL + File.separator + filePath;
  String pid = (String) data.get("pid");
  String name = (String) data.get("name");
  String description = (String) data.get("description");
  String created = (String) data.get("created");
  String keywords = (String) data.get("keywords");
  boolean hidden = "true".equals((String)data.get("hidden"));
  boolean vertical = "true".equals((String)data.get("vertical"));

  // Format the date (cut off the time, as it tends to be inaccurate)
  created = DateUtils.dateToString(DateUtils.stringToDate(created), DateUtils.DEFAULT_DATE_FORMAT);

  if ((pid == null) || (pid.length() == 0)) {
    pid = "-1";
  }

  ArrayList comments = PicDb.getManyRows(
       "select pcid, created, comment, (select name from users where uid=pic_comment.uid) as username " +
       " from pic_comment where pid="+pid+" order by created");

  // No pictures found that match the search criteria!
  if (!picView.hasPictures()) {
    messages.append("<BR><FONT COLOR=red>No pictures found!</FONT>");
  }

%>

<TABLE CELLSPACING='0' CELLPADDING='1' WIDTH='100%' class='outer_border' >


<% if (!picView.hasPictures()) { %>
 <TR><TD colspan='100%' valign='center'><%= messages.toString() %></TD></TR>
<% } else { %>

<TR>

 <FORM NAME="viewpic" METHOD="post" ACTION="/viewpics.jsp?pid=<%=pid%>&mode=update">
 <TD valign=top>
<div style='padding-left:4px'>

 <TABLE BORDER='0' CELLSPACING='0' CELLPADDING='1' WIDTH='100%'>
 <!-- Navigation -->
 <TR><TD align='left'>
 <%
  if (picView.hasPrev()) {
    out.write("<A HREF='/viewpics.jsp?go=Prev'><IMG SRC='/images/left.gif' border='0'></A>");
  } else {
    out.write("<IMG SRC='/images/blank.gif' border='0'>");
  }
  out.write(" &nbsp; ");
  if (picView.hasNext()) {
    out.write("<A HREF='/viewpics.jsp?go=Next'><IMG SRC='/images/right.gif' border='0'></A>");
  } else {
    out.write("<IMG SRC='/images/blank.gif' border='0'>");
  }
 %>
 &nbsp; &nbsp; <A HREF="viewthumbs.jsp?page=<%=curPage%>">Thumbnails</A> &nbsp;
 </TD></TR>
 </TABLE>

  <!-- Create Date -->
 <%=created%>  &nbsp; #<%=pid%><BR>

<% if (isAdmin) { %>
       <A HREF="/viewpics.jsp?mode=delete&pid=<%=pid%>"
         onClick="JavaScript:return confirm('Are you sure you want to permanently delete this picture?');"
        ><IMG SRC="/images/delete16.gif" BORDER=0></A>
      &nbsp; <A HREF="/viewpics.jsp?mode=rotate&dir=right&pid=<%=pid%>"><IMG SRC="/images/rotate_right.gif" BORDER=0></A>
      &nbsp; <A HREF="/viewpics.jsp?mode=rotate&dir=left&pid=<%=pid%>"><IMG SRC="/images/rotate_left.gif" BORDER=0></A>
      &nbsp; <INPUT TYPE="checkbox" NAME="hidden" value="true" <%=(hidden ? " CHECKED " : "")%>> Hidden
      &nbsp; <select name='vertical'>
        <option value='true' <%=(vertical?"SELECTED":"")%>>Vertical</option>
        <option value='false' <%=(!vertical?"SELECTED":"")%>>Horizontal</option>
      </select>
     <BR>
     <BR>
      Name <INPUT TYPE="text" name="name" SIZE='30' value="<%=name%>">
     <BR>
     <BR>
     Description<BR><TEXTAREA NAME="description" COLS='35' ROWS='4'><%=description%></TEXTAREA>
     <BR>
     <BR>
     Keywords <INPUT TYPE="text" SIZE='30' name="keywords" value="<%=keywords%>">
     <BR>
     <BR>

<% } else { // is not an Admin
    if (name.length() > 0) out.write("Name : "+name+"<BR>");
    if (description.length() > 0) out.write("Description : "+description+"<BR>");
   }
%>

   Comments : <BR>
<%
   for (int i=0; i<comments.size(); i++) {
     HashMap comment = (HashMap) comments.get(i);
     String pcid = (String) comment.get("pcid");
     String commentCreated = (String) comment.get("created");
     String commentText= (String) comment.get("comment");
     String commentUserName = (String) comment.get("username");
     out.write("<I>"+commentUserName+" "+commentCreated+"</I> : "+commentText + " ");
     if (isAdmin) {
      out.write(" <A HREF=\"/viewpics.jsp?mode=deletecomment&pcid="+pcid+"\""+
          "          onClick=\"JavaScript:return confirm('Are you sure you want to permanently delete this comment?');\" "+
          "       ><IMG SRC=\"/images/delete16.gif\" BORDER=0></A> ");
     }
     out.write("<BR>");
   } // for i
%>
   <TEXTAREA name="comment" COLS="35" ROWS="3"></TEXTAREA>
   <BR>

  <!-- Half a BR -->
  <TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0><TR><TD height='10px'></TD></TR></TABLE>
  &nbsp;
  <INPUT TYPE="Submit" NAME="go" VALUE="Prev" <%if(!picView.hasPrev()){out.write("DISABLED");}%> >
  <INPUT TYPE="Submit" NAME="update_button" VALUE="Update">
  <INPUT TYPE="Submit" NAME="go" VALUE="Next" <%if(!picView.hasNext()){out.write("DISABLED");}%>  >
  <BR>

  <!-- Messages -->
  <%= messages.toString() %><BR>

 </div>
 </TD>
 </FORM>
 <TD align=center valign=top><A HREF="<%=fullFile%>"><IMG SRC="<%=largeFile%>" BORDER=0></A></TD>
</TR>


<% } // hasPictures %>
</TABLE>
<SCRIPT LANGUAGE='JavaScript'>
 document.viewpic.name.focus();
</SCRIPT>
<%=PicTemplate.footer(picSession)%>

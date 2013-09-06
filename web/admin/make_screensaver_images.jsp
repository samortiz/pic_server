<%@ page import="pics.* "%>
<%@ page import="java.io.File"%>
<%@ page import="java.util.*"%>
<%@ include file="adminauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  long start = System.currentTimeMillis();
  picSession.putHashMap("last_page", "/admin/admin.jsp");
  StringBuffer html = new StringBuffer();

  html.append("Generating Images for Dual Monitor Screen Saver... <br>");

  File dbDir = new File(PicConst.DB_DIR);
  File fullDir = new File(dbDir, PicConst.FULL);
  File outDir = new File(dbDir, "double");
  File tempDir = new File(outDir, "temp");

  // Create the output directory if it doesn't already exist
  if (!outDir.exists()) {
    if (!outDir.mkdirs()) {
      html.append("<font color='red'> ERROR! Could not create output directory : "+outDir.getCanonicalPath()+"</font><br>");
    }
  }
  // Create the temp directory if it doesn't already exist
  if (!tempDir.exists()) {
    if (!tempDir.mkdirs()) {
      html.append("<font color='red'> ERROR! Could not create temp directory : "+tempDir.getCanonicalPath()+"</font><br>");
    }
  }


  for (int i=0; i<2; i++) {
    boolean vertical = i != 0;
    //How many files to concatenate together (2 for horizontal and 4 for vertical)
    int fileCount = (i==0 ? 2 : 4);

    ArrayList imgs = PicDb.getRows_SingleCol(
        "select file from pic where vertical="+vertical+" and movie=false and hidden=false order by created, pid");
    html.append(imgs.size()+" vertical="+vertical+" pics found! <br>");
    Collections.shuffle(imgs);

    for (int j=0; j<=imgs.size()-fileCount; j=j+fileCount) {
      StringBuffer command = new StringBuffer("convert ");
      // For each file
      for (int filePos=0; filePos < fileCount; filePos++) {
        String relativePath = (String) imgs.get(j+filePos);
        // Resize the image so that they're the same size for my monitors
        String oldFileName = new File(fullDir, relativePath).getAbsolutePath();
        String newFileName = new File(tempDir, "p"+filePos+".jpg").getAbsolutePath();
        String resizeCommand = "convert +profile \"*\" -resize 1280x1024 "+oldFileName+" "+newFileName;
        if (PicUtils.execute(resizeCommand)) {
          // Executed OK
          command.append(newFileName+" ");
        } else {
          html.append("<font color='red'>Error! Failed command : </font>"+resizeCommand+"<br>");
          command.append(oldFileName+" ");
        }
      }

      String outFileName = new File(outDir, (i==0?"h":"v")+j+".jpg").getAbsolutePath();
      command.append(" +append "+outFileName);
      html.append(command+"<br>");

      // Do the update
      if (PicUtils.execute(command.toString())) {
        html.append("Created Img "+outFileName+". Time:"+(System.currentTimeMillis()-start)+"ms<BR>");
      } else {
        html.append("<FONT COLOR=RED> Error creating img "+outFileName+"</FONT><BR>");
      }


    } // for i

  } // for i

  PicUtils.execute("scp "+outDir.getAbsolutePath()+"/*.jpg sortiz@wolf:~/pics");

  html.append("Finished! Time:"+(System.currentTimeMillis()-start)+"ms");

%>
<TABLE class='outer_border' CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>'>
 <%=PicTemplate.navRow(PicTemplate.ADMIN, picSession)%>
 <TR><TD colspan='100%' NOWRAP>
   <%=html.toString()%>
 </TD></TR>
</TABLE>
<%=PicTemplate.footer(picSession)%>

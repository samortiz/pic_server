<%@ page import=" util.*,
                  pics.*,
                  java.util.*,
                  java.io.*,
                  java.text.SimpleDateFormat"
 %><%@ include file="adminauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  StringBuffer messages = new StringBuffer();

  // NOTE : The import dir must be on the same disk partition as the database (or else the File.rename wil fail) 
  long MS_DAY = 1000 * 60 * 60 * 24; // one day in milliseconds (ms * sec * min * hr)
  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


  File source = new File(PicConst.IMPORT_DIR);
  File[] months = source.listFiles();
  // Go through each month
  for (int m=0; m<months.length; m++) {
    File month = months[m];
    if (!month.getName().matches("200[0-9]_[0-9]{2}")) continue; // Do not process root dirs not in the form 200Y_MM

    String yrStr = month.getName().substring(0,4);
    String monthStr = month.getName().substring(5,7);
    messages.append("<HR>Importing Month : "+month.getCanonicalPath()+" "+monthStr+" "+yrStr+"<BR>");
    System.out.println("\n\nImporting Month : "+month.getCanonicalPath()+" "+monthStr+" "+yrStr);

    // Go through each day in the month
    if (month.isDirectory()) {
      File[] days = month.listFiles();
      for (int d=0; d<days.length; d++) {
        File day = days[d];
        String dayName = day.getName();
        boolean hiddenDay = false; // if the entire day's worth of pictures are hidden
        // Validate the day directory name
        if (!dayName.matches("[A-Za-z]{3}_[0-9]{2}.*")) {
          // Check to see if it's a full month name
          dayName = dayName.replaceAll("April", "Apr");
          dayName = dayName.replaceAll("June", "Jun");
          dayName = dayName.replaceAll("July", "Jul");

          // Check to see if it's a "hidden" day
          if (dayName.matches("[_\\.]{1}[A-Za-z]{3}_[0-9]{2}.*")) {
            hiddenDay = true;
            dayName = dayName.substring(1); // cut off the hidden marker
            messages.append("<FONT COLOR='GREEN'> Found hidden day : '"+dayName+"'</FONT><BR>");
            System.out.println("Found hidden day : '"+dayName+"' ");
          }

          // Validate that it's a valid day
          if (!dayName.matches("[A-Za-z]{3}_[0-9]{2}.*")) {
            messages.append("<FONT COLOR='RED'> '"+dayName+"' is not a valid day!</FONT><BR>");
            System.out.println("'"+dayName+"' is not a valid day!\n");
            continue; // Isn't valid, skip this day
          }
        }
        String dayStr = dayName.substring(4,6);
        String monStr = dayName.substring(0,3);
        messages.append("<BR>Importing Day  : "+day.getCanonicalPath()+"<BR>");
        System.out.println("Importing Day  : "+day.getCanonicalPath()+"\n");

        // Get what the event is (if one is specified in the day name)
        String event = (dayName.length() > 6) ? dayName.substring(6) : "" ;
        event = event.replaceAll("_", " ").trim();

        File[] pics = day.listFiles();
        for (int p=0; p<pics.length; p++) {
          File pic = pics[p];
          String picName = pic.getName();
          boolean hidden = false;
          // Determine if this picture is hidden (name starts with _ or .)
          if (picName.matches("[_\\.].*")) {
            picName = picName.substring(1);
            hidden = true;
          }
          if (hiddenDay) hidden = true; // all pictures in a hidden day are hidden.

          // Get the orientation of the image (vertical)
          boolean vertical = false;
          try {
            Process proc = Runtime.getRuntime().exec("identify -format '%wx%h' "+pic.getCanonicalPath());
            InputStream instr = proc.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            MyUtils.copyStream(instr, bout);
            String size = bout.toString();
            if (size.length() > 0) {
              int width = Integer.parseInt(size.substring(1, size.indexOf("x")));
              int height = Integer.parseInt(size.substring(size.indexOf("x")+1, size.lastIndexOf("'")));
              vertical = (width > height);
            }
          } catch (Exception e) {
            messages.append("<FONT COLOR='RED'>Error : "+e.getMessage()+"</FONT><BR>");
            System.out.println("Error : "+e.getMessage()+"\n");
          }

          // Get the created Date (calculated)
          Date date = new Date(pic.lastModified()); // Get the date from the file modificationdate
          Date pathDate = df.parse(yrStr+"-"+monthStr+"-"+dayStr+" 00:00:00"); // Get the date from the path
          long diff = date.getTime() - pathDate.getTime();
          //messages.append(" date="+df.format(date)+" pathDate="+df.format(pathDate)+" ms_day="+MS_DAY+" diff="+diff);
          // check if the dates are really out of whack!
          if ((diff > MS_DAY) || (diff < 0)) {
            // The time distance is too long (the file timestamp is outside of the day it's in the pathed dir for
            date.setTime(pathDate.getTime()); // use the pathDate, not the last modified.
          }

          // Determine the relative file path
          String path = month.getName() + File.separator + monStr+"_"+dayStr + File.separator + picName;

          // Determine the name of the picture
          int dotIndex = picName.lastIndexOf(".");
          String name = (dotIndex >= 0 ? picName.substring(0, dotIndex) : picName.toString());
          name = name.replaceAll("_", " ");

          // Erase auto-generated names (so they show up as no-name)
          if (name.matches("P[0-9]*")) name = "";

          boolean isPic = picName.endsWith(".JPG") || picName.endsWith(".jpg");
          boolean isMovie = picName.endsWith(".MOV") || picName.endsWith(".mov");

          // Move the file to the new location
          File newPic = new File(PicConst.DB_DIR + File.separator + PicConst.FULL + File.separator + path);
          if (!PicUtils.mkDirs(newPic)) messages.append("<FONT COLOR='RED'>Failed to make directories for "+newPic.getCanonicalPath()+"<BR></FONT>");
          // Move the picture to the new full directory
          if (!pic.renameTo(newPic)) messages.append("<FONT COLOR='RED'>Failed to move file from "+pic.getCanonicalPath()+" to "+newPic.getCanonicalPath()+"<BR></FONT>");

          // Store the picture in the database
          PicDb.update("insert into pic (file, name, description, created, vertical, movie, hidden) values " +
                  " ('"+path+"', '"+name+"', '"+event+"' , '"+df.format(date)+"', '"+vertical+"', "+isMovie+", "+hidden+")");

          // Make the thumbnails (from images)
          if (isPic) {
            PicUpdate.makeThumbnail(path, PicConst.LARGE, messages);
            PicUpdate.makeThumbnail(path, PicConst.SMALL, messages);
          }

        } // for p

        // The day directory should be empty.  If it's not, then this delete will fail.
        if (!day.delete()) {
          messages.append("<FONT COLOR='RED>Could not delete day: "+day.getAbsolutePath()+"</FONT><BR>");
          System.out.println("Could not delete day: "+day.getAbsolutePath()+"\n");
        }
      }// for d
    } // if isDir

    // The month should be empty, if it's not then this delete will fail.
    if (!month.delete()) {
      messages.append("<FONT COLOR='RED>Could not delete month : "+month.getAbsolutePath()+"</FONT><BR>");
      System.out.println("Could not delete month : "+month.getAbsolutePath()+"\n");
    }
  } // for m


%>
<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0><TR><TD align='left'>
<%=messages.toString()%>
</TD></TR></TABLE>
<%=PicTemplate.footer(picSession)%>
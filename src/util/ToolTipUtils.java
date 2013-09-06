package util;

/**
 * ToolTip MyUtils.  Utilities for using Tool Tip Javascript
 * @author  sortiz
 */
public class ToolTipUtils {
  
  public static final int    DEFAULT_TIP_WIDTH = 300;
  public static final String DEFAULT_FONT_COLOR = "#000000";
  public static final String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";
  public static final int    DEFAULT_PADDING = 2;
  public static final String DEFAULT_BORDER_COLOR = "navy";
  
  
  public static final String getToolTipHeader(int tipWidth, String fontColor, String backgroundColor, int padding, String borderColor) {
    return 
     "<script type=\"text/javascript\">"
    +"<!-- \n"
    +"/* \n"
    +"This code is from Dynamic Web Coding\n"
    +"www.dyn-web.com\n"
    +"Copyright 2002 by Sharon Paine\n"
    +"Permission granted to use this code\n"
    +"as long as this entire notice is included.\n"
    +"*/\n"
    +"\n"
    +"// dw_tooltip.js contains onresize and onload handlers\n"
    +"// Put tooltip div and script tags for external js files at end of document\n"
    +"\n"
    +"// avoid error of passing event object from link in older browsers\n"
    +"if (!document.getElementById && !document.all && !document.layers)\n"
    +"        event = \"\";\n"
    +"\n"
    +"var tooltip;\n"
    +"function hideTip() {} // avoid errors until loaded\n"
    +"var tipFollowMouse = true;\n"
    +"var tipWidth = "+tipWidth+"; // width of tooltip in pixels\n"
    +"\n"
    +"// how far from mouse to show tooltip\n"
    +"var tipOffX     = 8;    // horizontal offset\n"
    +"var tipOffY     = 12; // vertical offset\n"
    +"\n"
    +"var tipFontFamily = \"Verdana, arial, helvetica, sans-serif\";\n"
    +"var tipFontSize = \"11px\";       // string with pixels or points (px or pt)\n"
    +"// tooltip content line-height\n"
    +"var tipLineHeight       = 1.2;  // number\n"
    +"var tipFontColor = \""+fontColor+"\";\n"
    +"var tipBgColor = \""+backgroundColor+"\";\n"
    +"// background image for tooltip (leave empty string if no bg image)\n"
    +"var tipBgImg = \"\";      // usually empty for this tooltip version\n"
    +"// \"breathing room\" around tooltip content\n"
    +"var tipPadding = "+padding+";     // integer (pixel value)\n"
    +"\n"
    +"// leave a color in here even if you don't want a border\n"
    +"var tipBorderColor = \""+borderColor+"\";\n"
    +"\n"
    +"// if you don't want a border, set tipBorderWidth to 0\n"
    +"var tipBorderWidth = 1; // integer (pixel value)\n"
    +"\n"
    +"// if you don't want a border, set tipBorderStyle to \"none\"\n"
    +"// options for border style: \"none\", \"solid\", \"ridge\", \"outset\",\n"
    +"// \"inset\", \"groove\", \"double\", \"dashed\"\n"
    +"// (they won't look the same in all browsers though!)\n"
    +"var tipBorderStyle = \"solid\";\n"
    +"\n"
    +"function doTooltip(evt,tip) {\n"
    +"  if (!tooltip) return;\n"
    +"  var cntnt = wrapTip(tip);\n"
    +"  goTooltip(evt,cntnt);\n"
    +"}\n"
    +"\n"
    +"// wrap tip content for formatting\n"
    +"function wrapTip(txt) {\n"
    +"        var cntnt = \"\";\n"
    +"        if (document.layers) {\n"
    +"                cntnt = '<table bgcolor=\"' + tipBorderColor + '\" width=\"' + tipWidth + '\" cellspacing=\"0\" cellpadding=\"' + tipBorderWidth + '\" border=\"0\"><tr><td><table bgcolor=\"' + tipBgColor + '\" width=\"100%\" cellspacing=\"0\" cellpadding=\"' + tipPadding + '\" border=\"0\"><tr><td><div style=\"font-family:' + tipFontFamily + '; font-size:' + tipFontSize + '; color:' + tipFontColor + ';\">' + txt  + '</div></td></tr></table></td></tr></table>';\n"
    +"        } else cntnt = txt;\n"
    +"        return cntnt;\n"
    +"}\n"
    +"\n"
    +"//-->\n"
    +"</script>";
  }
  // Override of getToolTipHeader to default all the settings 
  public static final String getToolTipHeader() {
    return getToolTipHeader(DEFAULT_TIP_WIDTH, DEFAULT_FONT_COLOR, DEFAULT_BACKGROUND_COLOR, DEFAULT_PADDING, DEFAULT_BORDER_COLOR);
  }
  
 /** Use this inside the definition of an <A> tag or wherever onmouseover / onmouseout will work 
  * eg. "<A "+addToolTip("tip")+">" 
  * NOTE : Watch out for " and ' and newlines in the tip. The tip is enclosed with ' and " and 
  * newlines can cause problems with javascript.  So escape or strip these in tipHelp before 
  * calling this function.
  */
  public static final String addToolTip(String tipHelp) {
    return "onmouseover=\"doTooltip(event,'"+tipHelp+"')\" onmouseout=\"hideTip()\"";
  }
  
  
  /** This is required at the bottom of the page using tooltips */
  public static final String getToolTipFooter() {
    return 
      " <!-- JavaScript includes --> \n"
     +" <div id=\"tipDiv\" style=\"position:absolute; visibility:hidden;  z-index:1000\"></div>\n"
     +" <script src=\"/car/js/tooltip/dw_core.js\" type=\"text/javascript\"></script>\n"
     +" <script src=\"/car/js/tooltip/dw_util.js\" type=\"text/javascript\"></script>\n"
     +" <script src=\"/car/js/tooltip/dw_tooltip.js\" type=\"text/javascript\"></script>\n";
  }
  
}

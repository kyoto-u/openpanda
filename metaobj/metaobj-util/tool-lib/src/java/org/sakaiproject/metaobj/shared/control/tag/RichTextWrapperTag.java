/*
 * *********************************************************************************
 *  $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/tag/RichTextWrapperTag.java $
 *  $Id: RichTextWrapperTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 * **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * *********************************************************************************
 *
 */

package org.sakaiproject.metaobj.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.EditorConfiguration;
import org.sakaiproject.util.Web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class RichTextWrapperTag extends BodyTagSupport {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private static PortalService portalService = (PortalService) ComponentManager.get(PortalService.class);
   
   //private static final String SCRIPT_PATH;
   //private static final String HTMLAREA_SCRIPT_PATH;
   //private static final String RESOURCE_PATH;
   
   private String textAreaId;
   /*
// we have static resources for our script path and built-in toolbars etc.
   static {
     ConfigurationResource cr = new ConfigurationResource();
     SCRIPT_PATH = cr.get("inputRichTextScript");
     HTMLAREA_SCRIPT_PATH = cr.get("inputRichTextHTMLArea");
     RESOURCE_PATH = cr.get("resources");
   }
   */
   
   public int doStartTag() throws JspException {
      JspWriter writer = pageContext.getOut();

      String textAreaId = (String) ExpressionUtil.evalNotNull("richTextWrapper", "textAreaId", getTextAreaId(),
         String.class, this, pageContext);
      
      try {
    	  
         writer.write("<script type=\"text/javascript\" defer=\"1\">sakai.editor.launch('" + textAreaId + "');</script>");
    	  
    	  
    	  
    	  /*
         String editor = ServerConfigurationService.getString("wysiwyg.editor");
         if(editor != null && !editor.equalsIgnoreCase("FCKeditor"))
         {
//          Render JavaScripts.
            //writeExternalScripts(locale, writer);
            writer.write("<script type=\"text/javascript\" src=\"/library/editor/HTMLArea/sakai_editor.js\"></script>\n");
            writer.write("<script type=\"text/javascript\" defer=\"1\">chef_setupformattedtextarea('"+textAreaId+"');</script>");
            
         } else {
   
            String collectionId = getContentHostingService().getSiteCollection(ToolManager.getCurrentPlacement().getContext());
            String tagFocus = ServerConfigurationService.getString("tags.focus");
            boolean resourceSearch = EditorConfiguration.enableResourceSearch();
            
            writer.write("<script type=\"text/javascript\" src=\"/library/editor/FCKeditor/sakai_fckconfig.js\"></script>\n");
            writer.write("<script type=\"text/javascript\" defer=\"1\">\n");
            if(resourceSearch)
            {
            	// need to set document.__pid to placementId
            	String placementId = ToolManager.getCurrentPlacement().getId();
            	writer.write("\t\tdocument.__pid=\"" + placementId + "\";\n");
            	
            	// need to set document.__baseUrl to baseUrl
            	String baseUrl = ServerConfigurationService.getToolUrl() + "/" + Web.escapeUrl(placementId);
            	writer.write("\t\tdocument.__baseUrl=\"" + baseUrl + "\";\n");
            }
            writer.write("\t\tvar inputArea = document.getElementById('"+textAreaId+"');\n");
            writer.write("\t\tvar cols = document.getElementById('"+textAreaId+"').cols;\n");
            writer.write("\t\tvar rows = document.getElementById('"+textAreaId+"').rows;\n");
            writer.write("\t\tvar width = 450 * cols / 80;\n");
            writer.write("\t\tvar height = 50 * rows / 4;\n");
            
            
            writer.write("\t\tchef_setupfcktextarea('"+
                  textAreaId+"', width, height, '" + collectionId + "', '" + tagFocus + "', '" + Boolean.toString(resourceSearch) + "');\n");
            writer.write("\t\tvar f = document.getElementById('"+textAreaId+"').form;\n" +
               "\t\tif (typeof f.onsubmit != \"function\") f.onsubmit = function() {};\n");
            writer.write("</script>");
         }
   */      
   
      } catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }
      return EVAL_BODY_INCLUDE;
   }
   
   
   /**
    * @todo do these as a document.write after testing if done
    * @param contextPath
    * @param writer
    * @throws IOException
    */
   /*
   protected void writeExternalScripts(Locale locale, ResponseWriter writer)
       throws IOException {
     writer.write("<script type=\"text/javascript\">var _editor_url = \"" +
                  "/" + RESOURCE_PATH + "/" + HTMLAREA_SCRIPT_PATH + "/" +
                  "\";</script>\n");
     writer.write("<script type=\"text/javascript\" src=\"" + "/" +
                  RESOURCE_PATH + "/" + HTMLAREA_SCRIPT_PATH + "/" +
                  "htmlarea.js\"></script>\n");
     writer.write("<script type=\"text/javascript\" src=\"" + "/" +
                  RESOURCE_PATH + "/" + HTMLAREA_SCRIPT_PATH + "/" +
                  "dialog.js\"></script>\n");
     writer.write("<script type=\"text/javascript\" src=\"" + "/" +
                  RESOURCE_PATH + "/" + HTMLAREA_SCRIPT_PATH + "/" +
                  "popupwin.js\"></script>\n");
     writer.write("<script type=\"text/javascript\" src=\"" + "/" +
                  RESOURCE_PATH + "/" + HTMLAREA_SCRIPT_PATH + "/" +
                  "lang/en.js\"></script>\n");

     String language = locale.getLanguage();
     if (!Locale.ENGLISH.equals(language))
     {
       writer.write("<script type=\"text/javascript\" src=\"" + "/" +
         RESOURCE_PATH + "/"     + HTMLAREA_SCRIPT_PATH + "/" +
         "lang/" + language + ".js\"></script>\n");
     }
     writer.write("<script type=\"text/javascript\" src=\"" + "/" +
       RESOURCE_PATH + "/" + SCRIPT_PATH + "\"></script>\n");
   }
   
   
   protected void doFckStuff(JspWriter writer, String clientId) {
//    set up dimensions
      int widthPx = 450;
      int heightPx = 50;
      int textareaColumns = 80;
      int textareaRows = 4;

      widthPx = (DEFAULT_WIDTH_PX*textareaColumns)/DEFAULT_COLUMNS;
      heightPx = (DEFAULT_HEIGHT_PX*textareaRows)/DEFAULT_ROWS;
      
   
//    not as slick as the way htmlarea is rendered, but the difference in functionality doesn't all
      //make sense for FCK at this time since it's already got the ability to insert files and such.
      String collectionId = ContentHostingService.getSiteCollection(ToolManager.getCurrentPlacement().getContext());

      //is there a slicker way to get this? 
      String connector = "/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector";

      //writer.write("<table border=\"0\"><tr><td>");

      writer.write("<script type=\"text/javascript\" src=\"/library/editor/FCKeditor/fckeditor.js\"></script>\n");
      writer.write("<script type=\"text/javascript\" language=\"JavaScript\">\n");
      writer.write("function chef_setupformattedtextarea(textarea_id){\n");
      writer.write("var oFCKeditor = new FCKeditor(textarea_id);\n");
      writer.write("oFCKeditor.BasePath = \"/library/editor/FCKeditor/\";\n");

      if (widthPx < 0)
        widthPx = 600;
      if (heightPx < 0)
        heightPx = 400;
      //FCK's toolset is larger then htmlarea and this prevents tools from ending up with all toolbar
      //and no actual editing area.
      if (heightPx < 200)
        heightPx = 200;

      writer.write("oFCKeditor.Width  = \"" + widthPx + "\" ;\n");
      writer.write("oFCKeditor.Height = \"" + heightPx + "\" ;\n");

      if ("archival".equals(ServerConfigurationService.getString("tags.focus")))
         writer.write("\n\toFCKeditor.Config['CustomConfigurationsPath'] = \"/library/editor/FCKeditor/archival_config.js\";\n");
      else {

        writer.write("\n\t\tvar courseId = \"" + collectionId  + "\";");
        writer.write("\n\toFCKeditor.Config['ImageBrowserURL'] = oFCKeditor.BasePath + " +
              "\"editor/filemanager/browser/default/browser.html?Connector=" + connector + "&Type=Image&CurrentFolder=\" + courseId;");
        writer.write("\n\toFCKeditor.Config['LinkBrowserURL'] = oFCKeditor.BasePath + " +
              "\"editor/filemanager/browser/default/browser.html?Connector=" + connector + "&Type=Link&CurrentFolder=\" + courseId;");
        writer.write("\n\toFCKeditor.Config['FlashBrowserURL'] = oFCKeditor.BasePath + " +
              "\"editor/filemanager/browser/default/browser.html?Connector=" + connector + "&Type=Flash&CurrentFolder=\" + courseId;");
        writer.write("\n\toFCKeditor.Config['ImageUploadURL'] = oFCKeditor.BasePath + " +
              "\"" + connector + "?Type=Image&Command=QuickUpload&Type=Image&CurrentFolder=\" + courseId;");
        writer.write("\n\toFCKeditor.Config['FlashUploadURL'] = oFCKeditor.BasePath + " +
              "\"" + connector + "?Type=Flash&Command=QuickUpload&Type=Flash&CurrentFolder=\" + courseId;");
        writer.write("\n\toFCKeditor.Config['LinkUploadURL'] = oFCKeditor.BasePath + " +
              "\"" + connector + "?Type=File&Command=QuickUpload&Type=Link&CurrentFolder=\" + courseId;");

        writer.write("\n\n\toFCKeditor.Config['CurrentFolder'] = courseId;");

        writer.write("\n\toFCKeditor.Config['CustomConfigurationsPath'] = \"/library/editor/FCKeditor/config.js\";\n");
      }
   }
*/
   
   public String getTextAreaId() {
      return textAreaId;
   }

   public void setTextAreaId(String textAreaId) {
      this.textAreaId = textAreaId;
   }

   protected ContentHostingService getContentHostingService() {
      return (ContentHostingService) ComponentManager.get("org.sakaiproject.content.api.ContentHostingService");
   }
   
}

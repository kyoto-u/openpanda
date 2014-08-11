/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-2.9.2/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/xml/XsltFunctions.java $
 * $Id: XsltFunctions.java 103302 2012-01-19 18:57:04Z botimer@umich.edu $
 * **********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.sakaiproject.metaobj.utils.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentTypeImageService;
import org.sakaiproject.metaobj.utils.DateWidgetFormat;
import org.sakaiproject.portal.api.Editor;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.EditorConfiguration;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.cover.EntityManager;

import java.util.*;
import java.text.*;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Nov 20, 2006
 * Time: 8:27:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class XsltFunctions {

   private static final String DATE_FORMAT = "yyyy-MM-dd";
   private static final String TIME_FORMAT = "HH:mm:ss.SSSZ";
   private static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" +
         TIME_FORMAT;

   private static final Format dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
   private static final Format dateFormat = new SimpleDateFormat(DATE_FORMAT);
   private static final Format timeFormat = new SimpleDateFormat(TIME_FORMAT);

   private static Map<String, ResourceLoader> resourceLoaders = new Hashtable<String, ResourceLoader>();
   
   private static PortalService portalService = (PortalService) ComponentManager.get(PortalService.class);

   //NOTE: For 2.8.1 and newer, it is encouraged to use getRichTextHead and either
   //      getRichTextLaunch or to simply call sakai.editor.launch via JavaScript,
   //      rather than getRichTextScript. This is to allow for installations to
   //      progressively update their custom creation renderers.
   //
   //      This is related to SAK-21126. See formCreate.xslt and
   //      formFieldTemplate.xslt for an example of how to use the updated binding.
   public static String getRichTextScript(String textBoxId, Node schemaElement) {
      String script = "";

      String editor = ServerConfigurationService.getString("wysiwyg.editor");
      String twinpeaks = ServerConfigurationService.getString("wysiwyg.twinpeaks");
      String collectionId = "/";
      if (ToolManager.getCurrentPlacement() != null) {
         collectionId = getContentHostingService().getSiteCollection(ToolManager.getCurrentPlacement().getContext());
      }

      if (editor.equalsIgnoreCase("FCKeditor")) {
         script += "\t<script type=\"text/javascript\" language=\"JavaScript\">\n" +
            "\n" +
            "\tfunction chef_setupformattedtextarea(textarea_id)\n" +
            "\t{\n" +
            "        \tvar oFCKeditor = new FCKeditor(textarea_id);\n" +
            "\t\toFCKeditor.BasePath = \"/library/editor/FCKeditor/\";\n" +
            "\n" +
            "                var courseId = \"" + collectionId + "\";\n" +
            "\n" +
            "                oFCKeditor.Config['ImageBrowserURL'] = oFCKeditor.BasePath + \"editor/filemanager/browser/default/browser.html?Connector=/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector&Type=Image&CurrentFolder=\" + courseId;\n" +
            "                oFCKeditor.Config['LinkBrowserURL'] = oFCKeditor.BasePath + \"editor/filemanager/browser/default/browser.html?Connector=/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector&Type=Link&CurrentFolder=\" + courseId;\n" +
            "                oFCKeditor.Config['FlashBrowserURL'] = oFCKeditor.BasePath + \"editor/filemanager/browser/default/browser.html?Connector=/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector&Type=Flash&CurrentFolder=\" + courseId;\n" +
            "                oFCKeditor.Config['ImageUploadURL'] = oFCKeditor.BasePath + \"/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector?Type=Image&Command=QuickUpload&Type=Image&CurrentFolder=\" + courseId;\n" +
            "                oFCKeditor.Config['FlashUploadURL'] = oFCKeditor.BasePath + \"/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector?Type=Flash&Command=QuickUpload&Type=Flash&CurrentFolder=\" + courseId;\n" +
            "                oFCKeditor.Config['LinkUploadURL'] = oFCKeditor.BasePath + \"/sakai-fck-connector/web/editor/filemanager/browser/default/connectors/jsp/connector?Type=File&Command=QuickUpload&Type=Link&CurrentFolder=\" + courseId;\n" +
            "\t\toFCKeditor.Width  = \"600\" ;\n" +
            "\t\toFCKeditor.Height = \"400\" ;\n" +
            "                oFCKeditor.Config['CustomConfigurationsPath'] = \"/library/editor/FCKeditor/config.js\";\n" +
            "    \t\toFCKeditor.ReplaceTextarea() ;\n" +
            "\t}\n" +
            "\t\n" +
            "</script>\n" +
            "\n" +
            "\n" +
            "\n";
      }
      else {
         script += "\t<script type=\"text/javascript\" language=\"JavaScript\">\n" +
            "\tvar _editor_url = \"/library/editor/"+editor+"\"\n" +
            "\tvar _editor_lang = \"en\";\n" +
            "</script>\n" +
            "\t<script type=\"text/javascript\" language=\"JavaScript\" src=\"/library/editor/"+editor+"/htmlarea.js\">\n" +
            "\t</script>\n" +
            "\t<script type=\"text/javascript\" language=\"JavaScript\" src=\"/library/editor/"+editor+"/sakai_editor";
         if (twinpeaks != null && twinpeaks.equalsIgnoreCase("true")) {
            script += "_twinpeaks";
         }

         script += ".js\">\n" +
            "\t</script>\n" +
            "";
      }

      script +=
         "\t<script type=\"text/javascript\" defer=\"1\">chef_setupformattedtextarea('"+textBoxId+"');</script>";

      return script;
   }
   
   public static String getRichTextHead() {
      Placement placement = ToolManager.getCurrentPlacement();
      Editor editor = portalService.getActiveEditor(placement);
      String preloadScript = editor.getPreloadScript() == null ? ""
         : "<script type=\"text/javascript\" language=\"JavaScript\">" + editor.getPreloadScript() + "</script>\n";
      String editorScript = editor.getEditorUrl() == null ? ""
         : "<script type=\"text/javascript\" language=\"JavaScript\" src=\"" + editor.getEditorUrl() + "\"></script>\n";
      String launchScript = editor.getLaunchUrl() == null ? ""
         : "<script type=\"text/javascript\" language=\"JavaScript\" src=\"" + editor.getLaunchUrl() + "\"></script>\n";
	      
      StringBuilder headJs = new StringBuilder();
      headJs.append("<script type=\"text/javascript\" language=\"JavaScript\" src=\"/library/js/headscripts.js\"></script>\n");
      headJs.append("<script type=\"text/javascript\" language=\"JavaScript\">var sakai = sakai || {}; sakai.editor = sakai.editor || {}; \n");
      headJs.append("sakai.editor.collectionId = '" + portalService.getBrowserCollectionId(placement) + "';\n");
      headJs.append("sakai.editor.enableResourceSearch = " + EditorConfiguration.enableResourceSearch() + ";</script>\n");
      headJs.append(preloadScript);
      headJs.append(editorScript);
      headJs.append(launchScript);
	      
      return headJs.toString();
   }

   public static String getRichTextLaunch(String textBoxId, Node schemaElement) {
      return getRichTextLaunch(textBoxId, "{}", schemaElement);
   }

   //Note that config should be a well-formed JavaScript object as a string (JSON). It will be used
   //verbatim in the editor launch call.
   public static String getRichTextLaunch(String textBoxId, String config, Node schemaElement) {
      return "<script type=\"text/javascript\" defer=\"1\">sakai.editor.launch('" + textBoxId + "', " + config + ");</script>";
   }

   public static String formatDate(String date, String format) {
      return formatDate(date, new SimpleDateFormat(format));
   }

   protected static String formatDate(String date, DateFormat format) {
      Date dateObject = null;
      if (date == null || date.equals("")) {
         return "";
      }
      else {
         try {
            dateObject = (Date) dateFormat.parseObject(date);
            return format.format(dateObject);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static long dateField(String date, int field, String type) {
      Format useFormat = dateTimeFormat;
      if (type.equalsIgnoreCase("date")) {
         useFormat = dateFormat;
      }
      else if (type.equals("time")) {
         useFormat = timeFormat;
      }

      Date dateObject = null;
      if (date == null || date.equals("")) {
         return -1;
      }
      else {
         try {
            dateObject = (Date) useFormat.parseObject(date);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateObject);
      return cal.get(field);
   }

   public static String getDateWidget(String fieldId, String name) {
      String calType = "2"; // default to US
      DateWidgetFormat format = new DateWidgetFormat();

      if (format.getLocaleDateFormat().equals(DateWidgetFormat.DD_MM_YYYY())) {
         calType = "1"; // European type
      }

      String javascript = "javascript:var cal"+fieldId+" = new calendar"+calType+
         "(document.getElementById('"+name+"'));cal"+fieldId+".year_scroll = true;cal"+fieldId+
         ".time_comp = false;cal"+fieldId+".popup('','/jsf-resource/inputDate/')";

      return javascript;
   }

   public static String formatDateWidget(String date) {
      DateWidgetFormat format = new DateWidgetFormat();
      return formatDate(date, format.getLocaleDateFormat());
   }

   public static NodeList loopCounter(int start, int end) {
      Document doc = Xml.createDocument();

      Element parent = doc.createElement("parent");

      for (int i=start;i<=end;i++) {
         Element data = doc.createElement("data");
         data.appendChild(doc.createTextNode(i + ""));
         parent.appendChild(data);
      }

      return parent.getElementsByTagName("data");
   }

   public static String currentDate() {
      return dateFormat.format(new Date());
   }

   public static String getMessage(String loaderKey, String key) {
      ResourceLoader loader = getLoader(loaderKey);

      return (String) loader.get(key);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1) {
      return formatMessageInternal(loaderKey, key, arg1);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2) {
      return formatMessageInternal(loaderKey, key, arg1, arg2);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2, Object arg3) {
      return formatMessageInternal(loaderKey, key, arg1, arg2, arg3);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2, Object arg3,
                                      Object arg4) {
      return formatMessageInternal(loaderKey, key, arg1, arg2, arg3, arg4);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2, Object arg3,
                                      Object arg4, Object arg5) {
      return formatMessageInternal(loaderKey, key, arg1, arg2, arg3, arg4, arg5);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2, Object arg3,
                                      Object arg4, Object arg5, Object arg6) {
      return formatMessageInternal(loaderKey, key, arg1, arg2, arg3, arg4, arg5, arg6);
   }

   public static String formatMessage(String loaderKey, String key, Object arg1, Object arg2, Object arg3,
                                      Object arg4, Object arg5, Object arg6, Object arg7) {
      return formatMessageInternal(loaderKey, key, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
   }

   public static String formatMessageInternal(String loaderKey, String key, Object... arguments) {
      ResourceLoader loader = getLoader(loaderKey);

      String format =  (String) loader.get(key);
      return MessageFormat.format(format, arguments);
   }

   public static ResourceLoader getLoader(String loaderKey) {
      ResourceLoader loader = resourceLoaders.get(loaderKey);

      if (loader == null) {
         loader = new ResourceLoader(loaderKey);
         registerLoader(loaderKey, loader);
      }
      return loader;
   }

   public static void registerLoader(String key, ResourceLoader loader) {
      resourceLoaders.put(key, loader);
   }

   public static String getReferenceName(String idString) {
	   return getReferenceName(idString, "");
   }
   
   public static String getReferenceName(String idString, String decoration) {
      String refString = getContentHostingService().getReference(idString);
      String contentRef = refString;
      if (decoration != null && !decoration.equals("")) {
    	  refString = decoration + refString;
      }
      Reference ref = EntityManager.newReference(refString);

      getSecurityService().pushAdvisor(
 	         new LocalSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
 	        		 contentRef));
      
      if (ref == null || ref.getEntity() == null) {
         return "";
      }
      
      ResourceProperties props = ref.getEntity().getProperties();
      String prop = props.getNamePropDisplayName();
      return props.getProperty(prop);
   }
   
   public static String getReferenceUrl(String idString) {
	   return getReferenceUrl(idString, "");
   }

   public static String getReferenceUrl(String idString, String decoration) {
      String refString = getContentHostingService().getReference(idString);
      String contentRef = refString;
      if (decoration != null && !decoration.equals("")) {
    	  refString = decoration + refString;
      }
      
      getSecurityService().pushAdvisor(
 	         new LocalSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
 	        		 contentRef));
      
      Reference ref = EntityManager.newReference(refString);
      if (ref == null || ref.getEntity() == null) {
         return "";
      }

      return ref.getUrl();
   }

   public static String getImageUrl(String idString) {
      String refString = getContentHostingService().getReference(idString);
      Reference ref = EntityManager.newReference(refString);

      if (ref == null) {
         return "/library/image/sakai/unknown.gif";
      }

      return "/library/image" + getContentTypeImageService().getContentTypeImage(ref.getType());
   }

   protected static ContentHostingService getContentHostingService() {
      return (ContentHostingService) ComponentManager.get(
         "org.sakaiproject.content.api.ContentHostingService");
   }
   
   protected static ContentTypeImageService getContentTypeImageService() {
      return (ContentTypeImageService)ComponentManager.get(
         "org.sakaiproject.content.api.ContentTypeImageService");   
   }
   
   protected static SecurityService getSecurityService() {
	      return (SecurityService) ComponentManager.get(
	         "org.sakaiproject.authz.api.SecurityService");
	   }
   
   public static class LocalSecurityAdvisor implements SecurityAdvisor {

	   private String function;
	   private String reference;

	   public LocalSecurityAdvisor() {
		   ;
	   }

	   public LocalSecurityAdvisor(String function, String reference) {
		   this.function = function;
		   this.reference = reference;
	   }

	   public SecurityAdvice isAllowed(String userId, String function, String reference) {
		   if (this.function.equals(function) && this.reference.equals(reference)) {
			   return SecurityAdvice.ALLOWED;
		   }
		   return SecurityAdvice.PASS;
	   }
   }
   
}

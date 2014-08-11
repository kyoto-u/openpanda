
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/jsf/widgets/src/java/org/theospi/jsf/renderer/XHeaderTitleRenderer.java $
* $Id: XHeaderTitleRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
**********************************************************************************/
package org.theospi.jsf.renderer;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.XHeaderComponent;
import org.theospi.jsf.component.XHeaderDrawerComponent;
import org.theospi.jsf.component.XHeaderTitleComponent;
import org.theospi.jsf.util.ConfigurationResource;
import org.theospi.jsf.util.OspxTagHelper;


public class XHeaderTitleRenderer extends Renderer
{
   private static final String RESOURCE_PATH;
   private static final String BARIMG_RIGHT;
   private static final String BARIMG_DOWN;
   private static final String CURSOR;
   private static final String JS_LOC;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     BARIMG_RIGHT = RESOURCE_PATH + "/" +cr.get("xheaderRight");
     BARIMG_DOWN = RESOURCE_PATH + "/" +cr.get("xheaderDown");
     CURSOR = cr.get("picker_style");
     JS_LOC = RESOURCE_PATH + "/" + cr.get("xheaderScript");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
   }
   
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}
   

	public void encodeBegin(FacesContext context, UIComponent inComponent) throws IOException
	{
		if(!(inComponent instanceof XHeaderTitleComponent))
			throw new IOException("the xheadertitle was expecting an xheadertitlecomponent");
		
		ResponseWriter writer = context.getResponseWriter();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
      
      RendererUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
      RendererUtil.writeExternalJSDependencies(context, writer, "osp.jsf.xheader.js", JS_LOC);
      
		XHeaderTitleComponent component = (XHeaderTitleComponent)inComponent;
		
		String id = (String) RendererUtil.getAttribute(context, component, "id");
      String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
      String value = (String) RendererUtil.getAttribute(context, component, "value");

		if(cssclass == null)
			cssclass = "xheader";
		
      writer.write("<div");
		
      RendererUtil.writeAttr(writer, "class", cssclass);
      RendererUtil.writeAttr(writer, "id", id);

		writer.write(">");
      
      XHeaderComponent parent = (XHeaderComponent)component.getParent();
		//if(component.getDrawerComponent() != null) {
      XHeaderDrawerComponent drawer = parent.getDrawerComponent(); 
      if (drawer != null) {
         String divId = "div" + drawer.getClientId(context);
         drawer.setDivId(divId);
			writer.write("<span onclick=\"showHideDiv('" + divId + "', '" + RESOURCE_PATH + "');" +
		              request.getAttribute("sakai.html.body.onload") +
               " refreshChildren" + Math.abs(drawer.getDivId().hashCode()) + "(); "
               +"\">");
				
         writer.startElement("img", component);
         writer.writeAttribute("style", "position:relative; float:left; margin-right:10px; left:3px; top:2px;", "style");
         writer.writeAttribute("id", "img" + divId, "id");
         String initiallyexpandedStr = (String) RendererUtil.getAttribute(context, drawer, "initiallyexpanded");
         if (initiallyexpandedStr == null) initiallyexpandedStr = "false";
         if (OspxTagHelper.parseBoolean(initiallyexpandedStr))
            writer.writeAttribute("src", BARIMG_RIGHT, "src");
         else         
            writer.writeAttribute("src", BARIMG_DOWN, "src");
         writer.endElement("img");

         if (value != null) {
            writer.write(value);
         }

         writer.write("</span>");
		}
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{		
		ResponseWriter writer = context.getResponseWriter();
		writer.write("</div>");
	}
   
     /**
      * This component renders its children
      * @return true
      */
     public boolean getRendersChildren()
     {
       return false;
     }
}




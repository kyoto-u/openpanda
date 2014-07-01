
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/jsf/widgets/src/java/org/theospi/jsf/renderer/TabAreaRenderer.java $
* $Id: TabAreaRenderer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
**********************************************************************************/
package org.theospi.jsf.renderer;


import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.TabComponent;
import org.theospi.jsf.util.ConfigurationResource;
import org.theospi.jsf.util.OspxTagHelper;

/**
 * This creates a split content area.  It divides a space in two.
 * This creates a table of size width x height.  It the uses
 * the divider position to give the first cell a height or width
 * based on if the direction is vertical or horizontal, respectively.
 * <br><br>
 * This class depends on the splitdivider tag to create the actual divide
 * The second class makes the second cell.
 * 
 * @author andersjb
 *
 */

public class TabAreaRenderer extends Renderer
{
   
   private static final String RESOURCE_PATH;
   private static final String CURSOR;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     CURSOR = cr.get("picker_style");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
   }
   
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	/**
	 * This renders html for the beginning of the tag.
	 * 
	 * @param context
	 * @param component
	 * @throws IOException
	 */
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		String height = (String) RendererUtil.getAttribute(context, component, "height");
		String width = (String) RendererUtil.getAttribute(context, component, "width");
		
		//checks for vertical, its abbr., and the y axis
		
      RendererUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
		writer.write("<table border=\"0\" ");
      RendererUtil.writeAttr(writer, "height", height);
      RendererUtil.writeAttr(writer, "width", width);
		
		//the tab cell needs to be small, it will be expanded
		writer.write("><tr><td width=\"1%\">");
		
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		String directionStr = (String) RendererUtil.getAttribute(context, component, "direction");
		ResponseWriter writer = context.getResponseWriter();

		writer.write("</td>");
      if(OspxTagHelper.isVertical(directionStr)) {
		 writer.write("<td width=\"*\">");
         encodeTabContent(context, component);
         writer.write("</td>");
      }
      else {
         writer.write("</tr><tr><td>");
         encodeTabContent(context, component);
         writer.write("</td>");
      }
      
		writer.write("</tr></table>");
      
	}
   
   protected void encodeTabContent(FacesContext context, UIComponent component) throws IOException {
      Iterator iter = component.getChildren().iterator();
      
      while (iter.hasNext())
      {
        UIComponent step = (UIComponent) iter.next();
        if (!(step instanceof TabComponent) || !step.isRendered())
        {
          continue;
        }
        TabComponent tab = (TabComponent) step;
        if (tab.getSelected().equalsIgnoreCase("true")) {
           for (Iterator i = tab.getChildren().iterator(); i.hasNext();) {
              UIComponent tab_content = (UIComponent) i.next();
              RendererUtil.encodeRecursive(context, tab_content);
           }
        }
      }  
   }

   public boolean getRendersChildren() {
      return false;
   }  
   
   
}




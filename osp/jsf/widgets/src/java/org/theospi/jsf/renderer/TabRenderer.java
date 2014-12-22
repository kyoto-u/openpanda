
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/jsf/widgets/src/java/org/theospi/jsf/renderer/TabRenderer.java $
* $Id: TabRenderer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.TabComponent;
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

public class TabRenderer extends Renderer
{
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
		
		String title = (String) RendererUtil.getAttribute(context, component, "title");
      String directionStr = (String) RendererUtil.getAttribute(context, component.getParent(), "direction");
      String selected = (String) RendererUtil.getAttribute(context, component, "selected");
      String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
      TabComponent tab = (TabComponent) component;
      boolean isSelected = false;
      
      if (selected.equalsIgnoreCase("true")) {
         tab.setSelected("true");
         isSelected = true;
      }
      if(cssclass == null)
    	  cssclass = "";
		
	  //checks for vertical, its abbr., and the y axis
	  if(OspxTagHelper.isVertical(directionStr))
          writer.write("<div style=\"padding:2px;\">");

      
      writer.write("<input type=\"submit\" class=\"osp_tab" + (isSelected ? "_selected":"") + " " +
    		  cssclass	+ 
    		  "\" value=\"" +
    		title +
      		"\" " + (isSelected ? "disabled=\"disabled\"":"") + " />");
	  //checks for vertical, its abbr., and the y axis
	  if(OspxTagHelper.isVertical(directionStr))
          writer.write("</div>");
      
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
	}

   public void encodeChildren(FacesContext context, UIComponent component) throws IOException
   {
      //Do nothing
   }

   public boolean getRendersChildren() {
      //Set to false so the content can be rendered in the correct place by the TabArea
      return true;
   }
   
   
}




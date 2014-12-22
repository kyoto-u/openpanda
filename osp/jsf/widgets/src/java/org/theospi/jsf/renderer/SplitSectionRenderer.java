
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/jsf/widgets/src/java/org/theospi/jsf/renderer/SplitSectionRenderer.java $
* $Id: SplitSectionRenderer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import org.theospi.jsf.component.SplitAreaComponent;
import org.theospi.jsf.util.OspxTagHelper;


public class SplitSectionRenderer extends Renderer
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
	    if (!component.isRendered())
	    {
	      return;
	    }
	    
		ResponseWriter writer = context.getResponseWriter();
		
		String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
		String valign = (String) RendererUtil.getAttribute(context, component, "valign");
		String align = (String) RendererUtil.getAttribute(context, component, "align");
		String size = (String) RendererUtil.getAttribute(context, component, "size");
		String id = (String) RendererUtil.getAttribute(context, component, "id");

       	UIComponent parent = component.getParent();
       	
        if (parent == null || !(parent instanceof SplitAreaComponent)) {
        	return;
        }

		String directionStr = (String) RendererUtil.getAttribute(context, parent, "direction");
		
		boolean vertical = OspxTagHelper.isVertical(directionStr);
        
		if(vertical)
			writer.write("<tr>");
		writer.write("<td");
		
      RendererUtil.writeAttr(writer, "class", cssclass);
      RendererUtil.writeAttr(writer, "id", id);
      RendererUtil.writeAttr(writer, "align", align);
      RendererUtil.writeAttr(writer, "valign", valign);
		
		if(vertical)
         RendererUtil.writeAttr(writer, "height", size);
		else
         RendererUtil.writeAttr(writer, "width", size);
			
		writer.write(">");

		
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
	    if (!component.isRendered())
	    {
	      return;
	    }
	    
		ResponseWriter writer = context.getResponseWriter();

		String directionStr = (String) RendererUtil.getAttribute(
										context, component.getParent(), "direction");
		
		
		writer.write("</td>");
		if(OspxTagHelper.isVertical(directionStr))
			writer.write("</tr>");
	}
}




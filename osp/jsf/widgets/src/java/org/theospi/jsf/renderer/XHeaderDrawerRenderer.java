
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/jsf/widgets/src/java/org/theospi/jsf/renderer/XHeaderDrawerRenderer.java $
* $Id: XHeaderDrawerRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.XHeaderDrawerComponent;
import org.theospi.jsf.util.OspxTagHelper;


public class XHeaderDrawerRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	public void encodeBegin(FacesContext context, UIComponent inComponent) throws IOException
	{
		if(!(inComponent instanceof XHeaderDrawerComponent))
			throw new IOException("the xheaderdrawer was expecting an xheaderdrawercomponent");
		
		ResponseWriter writer = context.getResponseWriter();
		XHeaderDrawerComponent component = (XHeaderDrawerComponent)inComponent;
		
		String initiallyexpandedStr = (String) RendererUtil.getAttribute(context, component, "initiallyexpanded");
		String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
        
		if(initiallyexpandedStr == null)
			initiallyexpandedStr = "false";
		
		boolean initiallyexpanded = OspxTagHelper.parseBoolean(initiallyexpandedStr);
		
		writer.write("<div");
      RendererUtil.writeAttr(writer, "class", cssclass);
      RendererUtil.writeAttr(writer, "id", component.getDivId());
		if(!initiallyexpanded)
//removed padding - let the css deal with that
			RendererUtil.writeAttr(writer, "style", "display:none;");
		else
			RendererUtil.writeAttr(writer, "style", "display:block;");
		writer.write(">");
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param inComponent UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent inComponent) throws IOException
	{
      XHeaderDrawerComponent component = (XHeaderDrawerComponent)inComponent;
		ResponseWriter writer = context.getResponseWriter();

      writer.write("</div>");
      writer.write("<script type=\"text/javascript\">\n");
      writer.write("function refreshChildren" + Math.abs(component.getDivId().hashCode()) + "() {");

      for (Iterator i=component.getInitScripts().iterator();i.hasNext();) {
         writer.write(i.next().toString());
      }

      writer.write("}");
      writer.write("</script>\n");
	}
}




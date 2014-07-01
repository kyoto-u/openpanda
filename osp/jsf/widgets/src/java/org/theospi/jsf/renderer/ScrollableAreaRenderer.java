
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/jsf/widgets/src/java/org/theospi/jsf/renderer/ScrollableAreaRenderer.java $
* $Id: ScrollableAreaRenderer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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


/**
 * This class renders a scrollable area.  It does this by
 * making a div and then making it "auto" overflow which places
 * scroll bars at the right and/or bottom.  If the content is 
 * small enough to fit into the div then no scroll bars are rendered.
 * <br><br>
 * There must be a height defined in this tag or by a surrounding tag.
 * If there is not then there must be a parent tag that has a height defined.
 * If there is not either, then the div will resize the height to 
 * the total size of the content thus making this tag moot.
 * 
 * @author andersjb
 * 
 */
public class ScrollableAreaRenderer extends Renderer
{
   public static final String SCROLL_VISIBLE = "visible";
   public static final String SCROLL_SCROLL = "scroll";
   public static final String SCROLL_HIDDEN = "hidden";
   public static final String SCROLL_AUTO = "auto";
   
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
		
		

		String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
		String id = (String) RendererUtil.getAttribute(context, component, "id");
      String width = (String) RendererUtil.getAttribute(context, component, "width");
      String height = (String) RendererUtil.getAttribute(context, component, "height");
      String scrollXStyle = (String) RendererUtil.getAttribute(context, component, "scrollXStyle");
      String scrollYStyle = (String) RendererUtil.getAttribute(context, component, "scrollYStyle");

      
      writer.startElement("div", component);

      if(cssclass != null)
         writer.writeAttribute("class", cssclass, "class");
      
      if(id != null)
         writer.writeAttribute("id", id, "id");
      
      String style = "";
      
      if(scrollXStyle == null && scrollYStyle == null) {
		   //	set the div tag to have scroll bars when the innerHTML is larger than the div size
         style += "overflow:auto;";
      } else if(scrollYStyle == null) {
         if(scrollXStyle.equals(SCROLL_SCROLL) || scrollXStyle.equals(SCROLL_AUTO))
            scrollYStyle = SCROLL_HIDDEN;
      } else if(scrollXStyle == null) {
         if(scrollYStyle.equals(SCROLL_SCROLL) || scrollYStyle.equals(SCROLL_AUTO))
            scrollXStyle = SCROLL_HIDDEN;
      }
      if(scrollXStyle != null)
         style += "overflow-x:" + scrollXStyle + ";";

      if(scrollYStyle != null)
         style += "overflow-y:" + scrollYStyle + ";";
      
		if(width != null) {
         style += "width:" + width + ";";
		}
		if(height != null) {
         style += "height:" + height + ";";
		}
      
      if(style.length() > 0)
         writer.writeAttribute("style", style, "style");
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

      writer.endElement("div");
	}
}




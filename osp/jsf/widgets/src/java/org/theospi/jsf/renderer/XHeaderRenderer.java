
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/jsf/widgets/src/java/org/theospi/jsf/renderer/XHeaderRenderer.java $
* $Id: XHeaderRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;


public class XHeaderRenderer extends Renderer
{
	static final protected String	kHasRenderedJSAttribute = "printed_XH_JS";
	
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	/**
	 * This class renders its own children and this is the function to do just that
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 */
   public void encodeChildren(FacesContext context, UIComponent inComponent) throws IOException
   {
      if (inComponent.isRendered()) {
         Iterator iter = inComponent.getChildren().iterator();
         while (iter.hasNext())
         {
           UIComponent step = (UIComponent) iter.next();
           RendererUtil.encodeRecursive(context, step);
         }
      }
   }
   
	/**
	 * This class renders its own children
	 * 
	 * @param context
	 * @param component
	 * @throws IOException
	 */
   public boolean getRendersChildren()
   {
   	return true;
   }
}




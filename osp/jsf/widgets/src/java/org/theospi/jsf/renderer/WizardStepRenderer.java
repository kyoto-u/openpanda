
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/jsf/widgets/src/java/org/theospi/jsf/renderer/WizardStepRenderer.java $
* $Id: WizardStepRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import org.sakaiproject.jsf.util.RendererUtil;


public class WizardStepRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}
   
   protected String getStepClass(FacesContext context, UIComponent component) {
      //Note: currentStep is zero-based and step is 1 one-based
      String stepNumber = (String) RendererUtil.getAttribute(context, component, "stepNumber");
      String currentStep = (String) RendererUtil.getAttribute(context, component.getParent(), "currentStep");
      int curStep = Integer.parseInt(currentStep);
      int loopStep = Integer.parseInt(stepNumber)-1;
      
      String retVal = "";
      if (RendererUtil.isDisabledOrReadonly(context, component))
         retVal = "disabled_state";
      else if (loopStep < curStep)
         retVal = "previous_state";
      else if (loopStep == curStep)
         retVal = "current_state";
      else
         retVal = "next_state";
      
      return retVal;
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
      String state = getStepClass(context, component);
      writer.write("<td class=\"" + state + "\">");
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
      
      String stepNumber = (String) RendererUtil.getAttribute(context, component, "stepNumber");
      String label = (String) RendererUtil.getAttribute(context, component, "label");
      String state = getStepClass(context, component);
      
      
      if (stepNumber != null)
      {
         writer.write("<div class=\"" + state + "\">");
         writer.write(stepNumber);
         writer.write("</div>");
      }
      
      if (label != null)
      {
  		 writer.write("&nbsp;");
         writer.write(label);
 		 writer.write("&nbsp;");
      }
		writer.write("</td>");
	}   
}




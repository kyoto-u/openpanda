
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/jsf/widgets/src/java/org/theospi/jsf/renderer/WizardStepsRenderer.java $
* $Id: WizardStepsRenderer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import java.text.MessageFormat;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.WizardStepComponent;
import org.theospi.jsf.util.ConfigurationResource;


public class WizardStepsRenderer extends Renderer
{
   private static final String RESOURCE_PATH;
   private static final String CSS_LOC;
   private static final String STEP_MSG;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
     STEP_MSG = cr.get("wizard_step_message");
     
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
      RendererUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
            
		writer.write("<div class=\"xheader\">");
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
	 * This class renders its own children and this is the function to do just that
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 */
   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      Iterator iter = component.getChildren().iterator();
      String currentStep = (String) RendererUtil.getAttribute(context, component, "currentStep");
      int totalSteps = countSteps(component);
      int i=0;
      while (iter.hasNext())
      {
        UIComponent step = (UIComponent) iter.next();
        if (!(step instanceof WizardStepComponent) || !step.isRendered() || !currentStep.equals(String.valueOf(i)))
        {
           i++;
          continue;
        }

        String label = (String) RendererUtil.getAttribute(context, step, "label");
        ResponseWriter writer = context.getResponseWriter();

        String stepText = MessageFormat.format(STEP_MSG, 
              new Object[]{String.valueOf(Integer.parseInt(currentStep)+1), 
                 String.valueOf(totalSteps), label});
        
        writer.write(stepText);
        break;
      }      
   }
   
   protected int countSteps(UIComponent component) {
      Iterator iter = component.getChildren().iterator();
      int i=0;
      while (iter.hasNext())
      {
        UIComponent step = (UIComponent) iter.next();
        if (!(step instanceof WizardStepComponent) || !step.isRendered())
        {
          continue;
        }
        
        i++;
      } 
      return i;
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




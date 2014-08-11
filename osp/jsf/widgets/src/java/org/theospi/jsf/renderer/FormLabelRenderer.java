/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/jsf/widgets/src/java/org/theospi/jsf/renderer/FormLabelRenderer.java $
* $Id: FormLabelRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
import org.theospi.jsf.util.ConfigurationResource;

public class FormLabelRenderer extends Renderer {
   
   private static final String RESOURCE_PATH;
   private static final String REQ_CHAR;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     REQ_CHAR = cr.get("req_field_char");
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
      String valueRequired = (String) RendererUtil.getAttribute(context, component, "valueRequired");
      String displayCharOnRight = (String) RendererUtil.getAttribute(context, component, "displayCharOnRight");
      if (valueRequired.equalsIgnoreCase("true") && 
            !displayCharOnRight.equalsIgnoreCase("true")) {
         writeReqChar(context, writer);
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
      String valueRequired = (String) RendererUtil.getAttribute(context, component, "valueRequired");
      String displayCharOnRight = (String) RendererUtil.getAttribute(context, component, "displayCharOnRight");
      
      if (valueRequired.equalsIgnoreCase("true") &&
            displayCharOnRight.equalsIgnoreCase("true")) {
         writeReqChar(context, writer);
      }
   }
   
   protected void writeReqChar(FacesContext context, ResponseWriter writer) throws IOException {
      RendererUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
      writer.write("<span class=\"osp_required_field\">");
      writer.write(REQ_CHAR);
      writer.write("</span>");
   }

}

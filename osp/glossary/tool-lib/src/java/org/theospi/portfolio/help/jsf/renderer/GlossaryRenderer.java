
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/renderer/ScrollableAreaRenderer.java $
* $Id: ScrollableAreaRenderer.java 10835 2006-06-17 03:25:03Z lance@indiana.edu $
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
package org.theospi.portfolio.help.jsf.renderer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Set;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.portfolio.help.helper.HelpTagHelper;
import org.theospi.portfolio.help.model.GlossaryEntry;


/**
 * 
 * @author andersjb
 * 
 */
public class GlossaryRenderer extends Renderer
{
   ResponseWriter originalWriter = null;
   StringWriter   tempWriter = null;
   private static final String TERM_TAG = "org.theospi.portfolio.help.jsf.renderer.GlossaryRenderer.terms";
   
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
		
      writer.write("<script type=\"text/javascript\" src=\"/osp-common-tool/js/eport.js\"></script>");
      originalWriter = writer;
      
      tempWriter = new StringWriter();
      ResponseWriter newResponseWriter = writer.cloneWithWriter(tempWriter);
      context.setResponseWriter(newResponseWriter);
	}

	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
      String hoverStr = (String) RendererUtil.getAttribute(context, component, "hover");
      String linkStr = (String) RendererUtil.getAttribute(context, component, "link");
      String firstOnlyStr = (String) RendererUtil.getAttribute(context, component, "firstOnly");

      if(hoverStr == null)
         hoverStr = "";
      if(linkStr == null)
         linkStr = "";
      if(firstOnlyStr == null)
         firstOnlyStr = "";
      
      //Get the temp writer
		ResponseWriter writer = context.getResponseWriter();
      
      //place the original writer back
      context.setResponseWriter(originalWriter);
      
      //ensure everything is passed out to the string writer
      writer.flush();
      
      //read the result
      String content = tempWriter.toString();
      StringReader strReader = new StringReader(content);

      Set termSet = getTerms(context.getExternalContext().getRequestMap());
      GlossaryEntry[] terms = new GlossaryEntry[termSet.size()];
      terms = (GlossaryEntry[]) termSet.toArray(terms);
      
      HelpTagHelper.renderHelp(strReader, content.length(), originalWriter, terms, 
            firstOnlyStr.equals("true"), hoverStr.equals("true"), linkStr.equals("true"));
      
      //originalWriter.writeText("--" + tempWriter.toString()+"--", null);
	}

   protected Set getTerms(Map requestMap) {
      if (requestMap.containsKey(TERM_TAG)) {
         return (Set)requestMap.get(TERM_TAG);
      }
      Set returned = HelpTagHelper.getHelpManager().getSortedWorksiteTerms();
      requestMap.put(TERM_TAG, returned);
      return returned;
   }
}




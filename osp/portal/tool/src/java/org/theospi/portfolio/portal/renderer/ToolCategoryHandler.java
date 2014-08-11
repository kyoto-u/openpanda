/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/renderer/ToolCategoryHandler.java $
* $Id:ToolCategoryHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.portal.renderer;

import java.io.IOException;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.portal.component.ToolCategoryComponent;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 4:52:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategoryHandler extends DefaultXmlTagHandler {

   private ToolCategoryRenderer toolCategoryRenderer;

   public ToolCategoryHandler(XmlTagFactory factory) {
      super(factory);
      this.toolCategoryRenderer = (ToolCategoryRenderer) factory;
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      ToolCategoryComponent container = (ToolCategoryComponent) context.getApplication().createComponent(ToolCategoryComponent.COMPONENT_TYPE);
      container.setId(root.createUniqueId());
      parent.getComponent().getChildren().add(container);

      ValueBinding contextVb = context.getApplication().createValueBinding("#{toolCategory.context}");
      ValueBinding siteTypeVb = context.getApplication().createValueBinding("#{toolCategory.siteTypeKey}");
      ValueBinding siteIdVb = context.getApplication().createValueBinding("#{toolCategory.siteId}");
      ValueBinding toolCategoryVb = context.getApplication().createValueBinding("#{toolCategory.toolCategoryKey}");
      String siteTypeKey = (String) siteTypeVb.getValue(context);
      String siteId = (String) siteIdVb.getValue(context);
      String toolCategoryKey = (String) toolCategoryVb.getValue(context);
      container.setSiteId(siteId);
      container.setContext((String) contextVb.getValue(context));
      container.setSiteType(getToolCategoryRenderer().getPortalManager().getSiteType(siteTypeKey));
      container.setToolCategory(getToolCategoryRenderer().getPortalManager().getToolCategory(siteTypeKey, toolCategoryKey));
      
      return new ComponentWrapper(parent, container, this);
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri,
                          String localName, String qName) throws IOException {
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch,
                          int start, int length) throws IOException {
      writeCharsToVerbatim(context, current, ch, start, length);
   }

   public ToolCategoryRenderer getToolCategoryRenderer() {
      return toolCategoryRenderer;
   }

   public void setToolCategoryRenderer(ToolCategoryRenderer toolCategoryRenderer) {
      this.toolCategoryRenderer = toolCategoryRenderer;
   }
}

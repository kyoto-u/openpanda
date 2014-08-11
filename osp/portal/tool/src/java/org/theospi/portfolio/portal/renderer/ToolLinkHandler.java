/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/renderer/ToolLinkHandler.java $
* $Id:ToolLinkHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.portal.component.ToolCategoryComponent;
import org.theospi.portfolio.portal.component.ToolComponent;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 10:28:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolLinkHandler extends DefaultXmlTagHandler {

   public ToolLinkHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      HtmlOutputLink link = (HtmlOutputLink) context.getApplication().createComponent(HtmlOutputLink.COMPONENT_TYPE);
      link.setId(root.createUniqueId());
      parent.getComponent().getChildren().add(link);

      link.setTarget("_parent");
      ToolCategoryComponent parentCategoryComponent = findParentCategory(parent);
      ToolComponent parentComponent = findParentTool(parent);

      if (parentComponent.getPage() != null) {
         // Resolve the site_type of the form /portal/category/<siteId>/<categoryKey>/<optionalToolId>
         String url = parentCategoryComponent.getContext() + "/category/" + parentCategoryComponent.getSiteId() + "/" +
               parentCategoryComponent.getToolCategory().getKey() + "/" + parentComponent.getPage().getId();
         link.setValue(url);
      }
      return new ComponentWrapper(parent, link, this);
   }

   protected ToolCategoryComponent findParentCategory(ComponentWrapper parent) {
      UIComponent parentComponent = parent.getComponent();
      while (!(parentComponent instanceof ToolCategoryComponent) && parentComponent != null) {
         parentComponent = parentComponent.getParent();
      }
      return (ToolCategoryComponent) parentComponent;
   }

   protected ToolComponent findParentTool(ComponentWrapper parent) {
      UIComponent parentComponent = parent.getComponent();
      while (!(parentComponent instanceof ToolComponent) && parentComponent != null) {
         parentComponent = parentComponent.getParent();
      }
      return (ToolComponent) parentComponent;
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
      writeCharsToVerbatim(context, current, ch, start, length);
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {

   }

}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/portal/tool/src/java/org/theospi/portfolio/portal/renderer/ToolCategoryRenderer.java $
* $Id: ToolCategoryRenderer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import org.sakaiproject.component.cover.ComponentManager;
import org.theospi.jsf.impl.DefaultXmlTagFactory;
import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagHandler;
import org.theospi.portfolio.portal.intf.PortalManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 4:52:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategoryRenderer extends DefaultXmlTagFactory {
   private final static String OSP_NS_URI = "http://www.osportfolio.org/OspML";

   private PortalManager portalManager;
   private XmlTagHandler toolCategoryHandler;
   private XmlTagHandler roleHandler;
   private XmlTagHandler toolLinkHandler;
   private XmlTagHandler toolHandler;

   public XmlTagHandler getHandler(String uri, String localName, String qName) {
      if (OSP_NS_URI.equals(uri)) {
         if ("tool".equals(localName)) {
            return toolHandler;
         }
         else if ("toolCategory".equals(localName)) {
            return toolCategoryHandler;
         }
         else if ("toolLink".equals(localName)) {
            return toolLinkHandler;
         }
         else if ("site_role".equals(localName)) {
            return roleHandler;
         }
      }
      return super.getHandler(uri, localName, qName);
   }

   public XmlTagHandler getToolCategoryHandler() {
      return toolCategoryHandler;
   }

   public void setToolCategoryHandler(XmlTagHandler toolCategoryHandler) {
      this.toolCategoryHandler = toolCategoryHandler;
   }

   public XmlTagHandler getRoleHandler() {
      return roleHandler;
   }

   public void setRoleHandler(XmlTagHandler roleHandler) {
      this.roleHandler = roleHandler;
   }

   public void init() {
      ComponentManager.loadComponent("org.theospi.jsf.intf.XmlTagFactory.toolCategory", this);
      setDefaultHandler(new DefaultXmlTagHandler(this));
      setToolCategoryHandler(new ToolCategoryHandler(this));
      setToolLinkHandler(new ToolLinkHandler(this));
      setRoleHandler(new RoleHandler(this));
      setToolHandler(new ToolHandler(this));
   }

   public PortalManager getPortalManager() {
      return portalManager;
   }

   public void setPortalManager(PortalManager portalManager) {
      this.portalManager = portalManager;
   }

   public XmlTagHandler getToolLinkHandler() {
      return toolLinkHandler;
   }

   public void setToolLinkHandler(XmlTagHandler toolLinkHandler) {
      this.toolLinkHandler = toolLinkHandler;
   }

   public XmlTagHandler getToolHandler() {
      return toolHandler;
   }

   public void setToolHandler(XmlTagHandler toolHandler) {
      this.toolHandler = toolHandler;
   }
}

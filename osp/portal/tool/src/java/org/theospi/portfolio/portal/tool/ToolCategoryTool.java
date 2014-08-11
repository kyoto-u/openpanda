/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/tool/ToolCategoryTool.java $
* $Id:ToolCategoryTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.portal.tool;

import java.io.InputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.theospi.portfolio.shared.tool.HelperToolBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 4:48:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategoryTool extends HelperToolBase {

   private XmlTagFactory factory;
   private PortalManager portalManager;
   private String siteTypeKey;
   private String toolCategoryKey;
   private String siteId;
   private String context;

   public InputStream getXmlFile() {
      String context = (String) getAttribute(PortalManager.CONTEXT);
      String siteType = (String) getAttribute(PortalManager.SITE_TYPE);
      String siteId = (String) getAttribute(PortalManager.SITE_ID);
      String toolCategoryKey = (String) getAttribute(PortalManager.TOOL_CATEGORY);
      setSiteTypeKey(siteType);
      setSiteId(siteId);
      setToolCategoryKey(toolCategoryKey);
      setContext(context);
      ToolCategory toolCategory = getPortalManager().getToolCategory(siteType, toolCategoryKey);

      ExternalContext exContext = FacesContext.getCurrentInstance().getExternalContext();
      return exContext.getResourceAsStream(toolCategory.getHomePagePath());
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public PortalManager getPortalManager() {
      return portalManager;
   }

   public void setPortalManager(PortalManager portalManager) {
      this.portalManager = portalManager;
   }

   public String getSiteTypeKey() {
      return siteTypeKey;
   }

   public void setSiteTypeKey(String siteTypeKey) {
      this.siteTypeKey = siteTypeKey;
   }

   public String getToolCategoryKey() {
      return toolCategoryKey;
   }

   public void setToolCategoryKey(String toolCategoryKey) {
      this.toolCategoryKey = toolCategoryKey;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }
}

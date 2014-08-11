/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/component/ToolCategoryComponent.java $
* $Id:ToolCategoryComponent.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.portal.component;

import javax.faces.component.UINamingContainer;

import org.sakaiproject.authz.api.Role;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 9:33:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategoryComponent extends UINamingContainer {

   public static final String COMPONENT_TYPE = "org.theospi.portfolio.portal.component.ToolCategoryComponent";
   private String context;
   private ToolCategory toolCategory;
   private SiteType siteType;
   private String siteId;
   private Role currentRole;

   public ToolCategory getToolCategory() {
      return toolCategory;
   }

   public void setToolCategory(ToolCategory toolCategory) {
      this.toolCategory = toolCategory;
   }

   public SiteType getSiteType() {
      return siteType;
   }

   public void setSiteType(SiteType siteType) {
      this.siteType = siteType;
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public Role getCurrentRole() {
      return currentRole;
   }

   public void setCurrentRole(Role currentRole) {
      this.currentRole = currentRole;
   }
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.2/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/worksite/mgt/impl/WorksiteManagerImpl.java $
 * $Id: WorksiteManagerImpl.java 128044 2013-08-01 03:14:24Z botimer@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.worksite.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;

import java.util.*;

public class WorksiteManagerImpl implements WorksiteManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private IdManager idManager = null;

   public List getUserSites() {
      return getUserSites(null);
   }

   public List getUserSites(Map properties) {
      // process all the sites
      return getUserSites(properties, null);
   }
   
   public List getUserSites(Map properties, List siteTypes) {
      List mySites;
      if ((properties == null || properties.isEmpty()) && (siteTypes == null || siteTypes.isEmpty())) {
         mySites = SiteService.getUserSites();
      }
      else {
         mySites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
            siteTypes, null, properties, org.sakaiproject.site.api.SiteService.SortType.NONE, null);
      }

      if (mySites.size() > 0) {
         Collections.sort(mySites);
      }

      return mySites;
   }

   public Id getCurrentWorksiteId() {
      Placement currentPlacement = ToolManager.getCurrentPlacement();

      if (currentPlacement == null) {
         currentPlacement = getToolSessionPlacement();

         if (currentPlacement == null) {
            return null;
         }
      }

      String id = currentPlacement.getContext();

      if (id != null) {
         return getIdManager().getId(id);
      }
      return null;
   }

   protected Placement getToolSessionPlacement() {
      ToolSession session = SessionManager.getCurrentToolSession();

      if (session == null) {
         return null;
      }

      String placementId = session.getPlacementId();
      return getTool(placementId);
   }

   public List getSiteTools(String toolId, Site site) {
      List tools = new ArrayList();

      List pages = site.getPages();

      for (Iterator i = pages.iterator(); i.hasNext();) {
         SitePage page = (SitePage) i.next();

         for (Iterator j = page.getTools().iterator(); j.hasNext();) {
            ToolConfiguration tool = (ToolConfiguration) j.next();
            if (toolId == null) {
               tools.add(tool);
            }
            else if (toolId.equals(tool.getToolId())) {
               tools.add(tool);
            }
         }
      }

      return tools;
   }

   public Site getSite(String siteId) {
      try {
         return SiteService.getSite(siteId);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public AuthzGroup getSiteRealm(String siteId) {
      AuthzGroup siteRealm = null;
      try {
         siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               siteId);
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return siteRealm;
   }

   public ToolConfiguration getTool(String id) {
      return SiteService.findTool(id);
   }

   public boolean isUserInSite(String siteId) {
      return SiteService.allowAccessSite(siteId);
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

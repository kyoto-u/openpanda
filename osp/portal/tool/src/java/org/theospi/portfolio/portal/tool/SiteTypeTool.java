/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/tool/SiteTypeTool.java $
* $Id:SiteTypeTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.sakaiproject.site.api.SiteService;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.shared.tool.PagingList;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 10:01:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteTypeTool extends HelperToolBase {

   private PortalManager portalManager;

   private PagingList sites = null;

   public PagingList getSites() {
      if (getAttribute(PortalManager.RELOAD_SITES) != null) {
         List sitesBase = getPortalManager().getSitesForType(getSiteType(), SiteService.SortType.TITLE_ASC, null);
         if (sites != null) {
            sites.setWholeList(sitesBase);
         }
         else {
            setSites(new PagingList(sitesBase));
         }
         removeAttribute(PortalManager.RELOAD_SITES);
      }

      return sites;
   }

   public String getSiteType() {
      return (String) getAttribute(PortalManager.SITE_TYPE);
   }

   public String getSiteTypeClass() {
      return getSiteType().replace('.', '_');
   }

   public void setSites(PagingList sites) {
      this.sites = sites;
   }

   public PortalManager getPortalManager() {
      return portalManager;
   }

   public void setPortalManager(PortalManager portalManager) {
      this.portalManager = portalManager;
   }

}

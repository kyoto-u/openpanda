/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/api/src/java/org/theospi/portfolio/portal/intf/PortalManager.java $
* $Id:PortalManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.portal.intf;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:05:11 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PortalManager {

   public static final String CONTEXT = "org.theospi.portfolio.portal.context";
   public static final String SITE_TYPE = "org.theospi.portfolio.portal.siteType";
   public static final String RELOAD_SITES = "org.theospi.portfolio.portal.reloadSites";
   public static final String SITE_ID = "org.theospi.portfolio.portal.siteId";
   public static final String TOOL_CATEGORY = "org.theospi.portfolio.portal.toolCategory";

   public User getCurrentUser();

   public Map getSitesByType();

   public Map getSitesByType(String siteId);

   public List getSitesForType(String type, SiteService.SortType sort, PagingPosition page);

   public Map getPagesByCategory(String siteId);

   public List getToolsForPage(String pageId);

   public Site getSite(String siteId);

   public SitePage getSitePage(String pageId);

   public String getPageCategory(String siteId, String pageId);

   public String decorateSiteType(Site site);

   public SiteType getSiteType(String siteTypeKey);

   public ToolCategory getToolCategory(String siteType, String toolCategoryKey);

   public boolean isAvailable(String toolId, String siteId);

   public SitePage getPage(String toolId, String siteId);

   public boolean isUserInRole(String roleId, String siteId);

   public List getRoles(String siteId);

   public boolean isDisplaySiteTypes();

   public Collection getCategoriesInNeedOfFiles();

   public void saveToolCategories(Collection toolCategories);

   public boolean isUseDb();

   public byte[] getCategoryPage(String href);

   public Map<String, SiteType> storeComponentsSiteTypes(Map siteTypes);
}

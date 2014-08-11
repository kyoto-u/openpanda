/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/api-impl/src/java/org/theospi/portfolio/portal/impl/PortalManagerImpl.java $
* $Id:PortalManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.portal.impl;

import org.sakaiproject.authz.api.Role;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.SitePageWrapper;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.theospi.portfolio.portal.model.ToolType;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:38:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PortalManagerImpl extends HibernateDaoSupport implements PortalManager {

   private UserDirectoryService userDirectoryService;
   private SiteService siteService;
   private IdManager idManager;
   private org.sakaiproject.metaobj.security.AuthorizationFacade sakaiAuthzManager;
   private org.theospi.portfolio.security.AuthorizationFacade ospAuthzManager;
   private boolean displayToolCategories = true;
   private boolean displaySiteTypes = true;
   private boolean useDb = false;
   private boolean reloadDb = false;
   private boolean cleanedDb = false;
   private boolean autoDdl = true;

   private Map siteTypes;
   private static final String TYPE_PREFIX = "org.theospi.portfolio.portal.";
   private ResourceLoader rl = new ResourceLoader();

   public void init() {

      if (isUseDb() && isAutoDdl()) {
         Map<String, SiteType> siteTypesTempMap = loadDbSiteTypes();
         if (siteTypesTempMap.size() == 0 || isReloadDb()) {
            siteTypesTempMap = storeComponentsSiteTypes(siteTypes);
            cleanedDb = true;
         }
         else {
            setSiteTypes(new HashMap(siteTypesTempMap));
         }
      }
   }

   public Map<String, SiteType> storeComponentsSiteTypes(Map siteTypes) {
      Map<String, SiteType> siteTypesTempMap = loadDbSiteTypes();
      getHibernateTemplate().deleteAll(siteTypesTempMap.values());
      getHibernateTemplate().flush();
      Map<String, SiteType> returned = new Hashtable<String, SiteType>();
      Collection<SiteType> siteTypesCol = siteTypes.values();
      for (Iterator<SiteType> i=siteTypesCol.iterator();i.hasNext();) {
         SiteType type = i.next();
         storeComponentsSiteType(type);
         returned.put(type.getName(), type);
      }
      setSiteTypes(new HashMap(siteTypesTempMap));
      return returned;
   }

   protected void storeComponentsSiteType(SiteType type) {
      List<ToolCategory> toolCategories = type.getToolCategories();
      for (Iterator<ToolCategory> i=toolCategories.iterator();i.hasNext();) {
         fixComponentsToolCategory(i.next());
      }
      getHibernateTemplate().save(type);
   }

   protected void fixComponentsToolCategory(ToolCategory toolCategory) {
      for (Iterator<Map.Entry> i=toolCategory.getTools().entrySet().iterator();i.hasNext();) {
         Map.Entry entry = i.next();
         if (entry.getValue() instanceof String) {
            entry.setValue(new ToolType());
         }
      }

      //todo load i18n pages
   }

   protected Map<String, SiteType> loadDbSiteTypes() {
      Map<String, SiteType> returned = new Hashtable<String, SiteType>();
      List<SiteType> siteTypesList = getHibernateTemplate().loadAll(SiteType.class);
      for (Iterator<SiteType> i=siteTypesList.iterator();i.hasNext();) {
         SiteType type = i.next();
         returned.put(type.getName(), type);
      }
      return returned;
   }

   public User getCurrentUser() {
      return getUserDirectoryService().getCurrentUser();
   }

   public Map getSitesByType() {
      return getSitesByType(null);
   }

   public Map getSitesByType(String siteId) {
      Map typeMap = new Hashtable();
      boolean addSite = (siteId != null);

      if (siteId != null) {
         getOspAuthzManager().pushAuthzGroups(siteId);
      }

      User currentUser = getCurrentUser();
      if (currentUser != null && currentUser.getId().length() > 0) {
         String mySiteId = addMyWorkspace(typeMap);
         if (addSite) {
            addSite = !(siteId.equals(mySiteId));
         }
      }
      else {
         return createGatewayMap(typeMap, siteId);
      }

      List types = getSiteService().getSiteTypes();

      List allUserSites = getSiteService().getSites(SiteService.SelectionType.ACCESS,
         null, null, null, SiteService.SortType.TITLE_ASC, null);

      for (Iterator i=types.iterator();i.hasNext();) {
         String type = (String) i.next();
         SiteType siteType = (SiteType) getSiteTypes().get(type);

         if (siteType == null) {
            siteType = SiteType.OTHER;
         }
         
         if (!siteType.isHidden()) {
            List sites = getSiteService().getSites(SiteService.SelectionType.ACCESS, type, null,
                  null, SiteService.SortType.TITLE_ASC, null);
   
            addSpecialSites(siteType.getSpecialSites(), sites, allUserSites);
   
            if (sites.size() > 0 && siteType.isDisplayTab()) {
               if (addSite) {
                  addSite = !checkSites(siteId, sites);
               }
   
               typeMap.put(siteType, sites);
            }
         }
      }

      if (addSite) {
         // didn't already get the site... let's add it here
         Site siteToAdd = getSite(siteId);
         SiteType typeToAdd = (SiteType) getSiteTypes().get(siteToAdd.getType());
         if (typeToAdd == null) {
            typeToAdd = SiteType.OTHER;
         }
         List sitesToAdd = (List) typeMap.get(typeToAdd);
         if (sitesToAdd == null) {
            sitesToAdd = new ArrayList();
            typeMap.put(typeToAdd, sitesToAdd);
         }
         sitesToAdd.add(siteToAdd);
      }

      return typeMap;
   }

   protected boolean checkSites(String siteId, List sites) {
      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site) i.next();
         if (site.getId().equals(siteId)) {
            return true;
         }
      }
      return false;
   }

   protected void addSpecialSites(List specialSites, List sites, List allUserSites) {

      if (specialSites == null) {
         return;
      }

      for (Iterator i=allUserSites.iterator();i.hasNext();) {
         Site site = (Site) i.next();
         if (specialSites.contains(site.getId())) {
            sites.add(site);
         }
      }
      Collections.sort(sites, new SiteTitleComparator());
   }

   protected Map createGatewayMap(Map typeMap, String siteId) {
      String gatewayId = ServerConfigurationService.getGatewaySiteId();
      
      //If I'm passing a site, I want to override the configured gateway
      if (siteId != null) {
         gatewayId = siteId;
      }
      
      try {
         Site gateway = getSiteService().getSite(gatewayId);
         List sites = new ArrayList();
         sites.add(gateway);
         typeMap.put(SiteType.GATEWAY, sites);
         return typeMap;
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   protected String addMyWorkspace(Map typeMap) {
      String myWorkspaceId = getSiteService().getUserSiteId(getCurrentUser().getId());
      try {
         Site myWorkspace = getSiteService().getSite(myWorkspaceId);
         List sites = new ArrayList();
         sites.add(myWorkspace);
         typeMap.put(SiteType.MY_WORKSPACE, sites);
         return myWorkspace.getId();
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   public List getSitesForType(String type, SiteService.SortType sort, PagingPosition page) {
      String baseType = extractType(type);
      List sites = getSiteService().getSites(SiteService.SelectionType.ACCESS, baseType, null,
				null, sort, page);

      List allUserSites = getSiteService().getSites(SiteService.SelectionType.ACCESS,
         null, null, null, SiteService.SortType.TITLE_ASC, null);

      SiteType siteType = (SiteType) getSiteTypes().get(baseType);
      addSpecialSites(siteType.getSpecialSites(), sites, allUserSites);

      return sites;
   }

   protected String extractType(String type) {
      return type.substring(TYPE_PREFIX.length());
   }

   public Map getPagesByCategory(String siteId) {
      try {
         Site site = getSiteService().getSite(siteId);

         List pages = site.getPages();
         List toolOrder = new ArrayList(ServerConfigurationService.getToolOrder(site.getType()));

         SiteType siteType = (SiteType) getSiteTypes().get(site.getType());

         if (siteType != null) {
            while (siteType.getFirstCategory() > toolOrder.size()) {
               toolOrder.add(Integer.valueOf(0));
            }

            toolOrder.addAll(siteType.getFirstCategory(), siteType.getToolCategories());
         }

         getOspAuthzManager().pushAuthzGroups(siteId);
         getSakaiAuthzManager().pushAuthzGroups(siteId);

         Map categories = categorizePages(pages, siteType, toolOrder);

         List categoryList = new ArrayList(categories.keySet());

         Collections.sort(categoryList);

         Map newCategories = new HashMap();

         int index = 0;
         for (Iterator i=categoryList.iterator();i.hasNext();) {
            ToolCategory category = (ToolCategory) i.next();
            Object oldValue = categories.get(category);
            category.setOrder(index);
            index++;
            newCategories.put(category, oldValue);
         }

         return newCategories;
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   protected Map categorizePages(List pages, SiteType type, List toolOrder) {
      Map pageMap = new Hashtable();

      int index = 0;
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage) i.next();
         categorizePage(page, pageMap, type, findIndex(page, toolOrder, index));
         index++;
      }

      return pageMap;
   }

   protected int findIndex(SitePage page, List toolOrder, int defaultValue) {
      List tools = page.getTools();

      if (tools.size() == 1) {
         ToolConfiguration toolConfig = (ToolConfiguration) tools.get(0);
         if (toolConfig.getTool() != null) {
            String toolId = toolConfig.getTool().getId();
            if (toolOrder.contains(toolId)) {
               return toolOrder.indexOf(toolId);
            }
         }
      }

      return defaultValue;
   }

   protected void categorizePage(SitePage page, Map pageMap, SiteType siteType, int index) {
      ToolCategory[] categories = findCategories(page, siteType, index);

      for (int i=0;i<categories.length;i++) {
         List pages = (List) pageMap.get(categories[i]);
         if (pages == null) {
            pages = new ArrayList();
            pageMap.put(categories[i], pages);
         }
         pages.add(new SitePageWrapper(page, index));
      }
   }

   protected ToolCategory[] findCategories(SitePage page, SiteType siteType, int index) {
      List tools = page.getTools();

      if (tools.size() == 0 || !isDisplayToolCategories()) {
         return createUncategorized(index);
      }

      Placement tool = (Placement) tools.get(0);

      boolean foundCategory = false;
      List toolCategories = new ArrayList();
      
      if (tool.getTool() != null) {
         String toolId = tool.getTool().getId();
         if (siteType != null && siteType.getToolCategories() != null) {
            for (Iterator i=siteType.getToolCategories().iterator();i.hasNext();){
               ToolCategory category = (ToolCategory) i.next();
               if (category.getTools().containsKey(toolId)) {
                  foundCategory = true;
                  Object key = category.getTools().get(toolId);
                  if (key instanceof String) {
                     // no functions or authz to check here...
                     toolCategories.add(new ToolCategory(category));
                  }
                  else if (hasAccess(page, tool, (ToolType)category.getTools().get(toolId))) {
                     toolCategories.add(new ToolCategory(category));
                  }
               }
            }
         }
      }

      if (toolCategories.size() == 0 && !foundCategory) {
         return createUncategorized(index);
      }

      return (ToolCategory[]) toolCategories.toArray(new ToolCategory[toolCategories.size()]);
   }

   protected boolean hasAccess(SitePage page, Placement tool, ToolType toolType) {

      if (toolType.getFunctions() == null || toolType.getFunctions().size() == 0) {
         // nothin to check
         return true;
      }

      for (Iterator i=toolType.getFunctions().iterator();i.hasNext();) {
         if (toolType.getQualifierType().equals(ToolType.SAKAI_QUALIFIER)) {
            if (getSakaiAuthzManager().isAuthorized((String)i.next(), null)) {
               return true;
            }
         }
         else {
            String qualifier = null;
            if (toolType.getQualifierType().equals(ToolType.PLACEMENT_QUALIFIER)) {
               qualifier = tool.getId();
            }
            else if (toolType.getQualifierType().equals(ToolType.SITE_QUALIFIER)) {
               qualifier = page.getSiteId();
            }
            else {
               throw new RuntimeException("invalid tool type qualifier");
            }

            if (getOspAuthzManager().isAuthorized((String)i.next(), getIdManager().getId(qualifier))) {
               return true;
            }
         }
      }

      return false;
   }

   protected ToolCategory[] createUncategorized(int index) {
      try {
         ToolCategory category = (ToolCategory) ToolCategory.UNCATEGORIZED.clone();
         category.setOrder(index);
         return new ToolCategory[]{category};

      }
      catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public List getToolsForPage(String pageId) {
      SitePage page = getSiteService().findPage(pageId);
      return page.getTools();
   }

   public Site getSite(String siteId) {
      try {
         return getSiteService().getSite(siteId);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   public SitePage getSitePage(String pageId) {
      return getSiteService().findPage(pageId);
   }

   public String getPageCategory(String siteId, String pageId) {
      Site site = getSite(siteId);
      SitePage page = site.getPage(pageId);
      SiteType siteType = (SiteType) getSiteTypes().get(decorateSiteType(site));

      ToolCategory[] categories = findCategories(page, siteType, 0);

      if (categories.length == 0){
         return ToolCategory.UNCATEGORIZED.getKey();
      }

      return categories[0].getKey();
   }

   public String decorateSiteType(String siteTypeKey) {
      return TYPE_PREFIX + siteTypeKey;
   }

   public String decorateSiteType(Site site) {
      if (getSiteService().isUserSite(site.getId())){
         return SiteType.MY_WORKSPACE.getKey();
      }
      else if (site.getType() != null) {
         String siteType = decorateSiteType(site.getType());

         return siteType;
      }
      else {
         return findSpecialSiteType(site);
      }
   }

   protected String findSpecialSiteType(Site site) {
      for (Iterator i=getSiteTypes().values().iterator();i.hasNext();) {
         SiteType type = (SiteType) i.next();
         if (type.getSpecialSites() != null && type.getSpecialSites().contains(site.getId())) {
            return type.getKey();
         }
      }
      return null;
   }

   public SiteType getSiteType(String siteTypeKey) {
      for (Iterator i=getSiteTypes().values().iterator();i.hasNext();) {
         SiteType siteType = (SiteType) i.next();
         if (siteType.getKey().equals(siteTypeKey)) {
            return siteType;
         }
      }
      return null;
   }

   public ToolCategory getToolCategory(String siteTypeKey, String toolCategoryKey) {
      SiteType siteType = getSiteType(siteTypeKey);
      for (Iterator i = siteType.getToolCategories().iterator();i.hasNext();) {
         ToolCategory toolCategory = (ToolCategory) i.next();
         if (toolCategory.getKey().equals(toolCategoryKey)) {
            return toolCategory;
         }
      }
      return null;
   }

   public boolean isAvailable(String toolId, String siteId) {
      Site site = getSite(siteId);

      ToolConfiguration tool = site.getToolForCommonId(toolId);

      // todo check permissions
      return tool != null;
   }

   public SitePage getPage(String toolId, String siteId) {
      Site site = getSite(siteId);
      ToolConfiguration tool = site.getToolForCommonId(toolId);

      if (tool == null) {
         return null;
      }

      return getSitePage(tool.getPageId());
   }

   public boolean isUserInRole(String roleId, String siteId) {
      Site site = getSite(siteId);
      Role role = site.getUserRole(getCurrentUser().getId());
      return role.getId().equals(roleId);
   }

   public List getRoles(String siteId) {
      Site site = getSite(siteId);
      List roles = new ArrayList();
      roles.add(site.getUserRole(getCurrentUser().getId()));
      return roles;
   }

   public UserDirectoryService getUserDirectoryService() {
      return userDirectoryService;
   }

   public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

   public Map getSiteTypes() {
      return siteTypes;
   }

   public void setSiteTypes(Map siteTypes) {
      this.siteTypes = siteTypes;
   }

   public org.sakaiproject.metaobj.security.AuthorizationFacade getSakaiAuthzManager() {
      return sakaiAuthzManager;
   }

   public void setSakaiAuthzManager(org.sakaiproject.metaobj.security.AuthorizationFacade sakaiAuthzManager) {
      this.sakaiAuthzManager = sakaiAuthzManager;
   }

   public org.theospi.portfolio.security.AuthorizationFacade getOspAuthzManager() {
      return ospAuthzManager;
   }

   public void setOspAuthzManager(org.theospi.portfolio.security.AuthorizationFacade ospAuthzManager) {
      this.ospAuthzManager = ospAuthzManager;
   }

   public boolean isDisplayToolCategories() {
      return displayToolCategories;
   }

   public void setDisplayToolCategories(boolean displayToolCategories) {
      this.displayToolCategories = displayToolCategories;
   }

   public boolean isDisplaySiteTypes() {
      return displaySiteTypes;
   }

   public Collection getCategoriesInNeedOfFiles() {
      if (isCleanedDb()) {
         return loadPages(getHibernateTemplate().loadAll(ToolCategory.class));
      }
      else {
         return null;
      }
   }

   protected Collection loadPages(List list) {
      for (Iterator<ToolCategory> i = list.iterator();i.hasNext();) {
         i.next().getPages().size();
      }

      return list;
   }

   public void saveToolCategories(Collection toolCategories) {
      getHibernateTemplate().saveOrUpdateAll(toolCategories);
   }

   public void setDisplaySiteTypes(boolean displaySiteTypes) {
      this.displaySiteTypes = displaySiteTypes;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public boolean isUseDb() {
      return useDb;
   }

   public byte[] getCategoryPage(String href) {
      List categories = (List) getHibernateTemplate().find(" from ToolCategory where type_key=?", href);

      if (categories.size() > 0) {
         ToolCategory cat = (ToolCategory) categories.get(0);

         String localeStr = rl.getLocale().toString();

         if (cat.getPages().get(localeStr) != null) {
            return (byte[]) cat.getPages().get(localeStr);
         }

         localeStr = localeStr.replaceFirst("_.*","");

         if (cat.getPages().get(localeStr) != null) {
            return (byte[]) cat.getPages().get(localeStr);
         }

         return (byte[]) cat.getPages().get("");
      }

      return new byte[0];
   }

   public void setUseDb(boolean useDb) {
      this.useDb = useDb;
   }

   public boolean isReloadDb() {
      return reloadDb;
   }

   public void setReloadDb(boolean reloadDb) {
      this.reloadDb = reloadDb;
   }

   public boolean isCleanedDb() {
      return cleanedDb;
   }

   public void setCleanedDb(boolean cleanedDb) {
      this.cleanedDb = cleanedDb;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/api-impl/src/java/org/theospi/portfolio/worksite/mgt/impl/WorksiteListGenerator.java $
* $Id: WorksiteListGenerator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.worksite.mgt.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.list.impl.WorksiteBaseGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.shared.model.SortableListObject;

public class WorksiteListGenerator extends WorksiteBaseGenerator implements ListGenerator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private static final String SITE_ID_PARAM = "selectedSiteId";

   private WorksiteManager worksiteManager;
   
   public void init(){
      logger.info("init()");
       super.init();
   }

   /**
    * @return the current user's list of objects
    *         (whatever that means to the implentation)
    */
   public List getObjects() {
      
      List sites = getWorksiteManager().getUserSites(null, getListService().getSiteTypeList());
      List<SortableListObject> sortableSites = new ArrayList<SortableListObject>(sites.size());
      
      for (Iterator i = sites.iterator(); i.hasNext();) {
         Site site = (Site)i.next();
         Agent owner = new AgentImpl(getIdManager().getId(site.getCreatedBy().getId()));
         try {
            SortableListObject obj = new SortableListObject(site.getId(), 
                        site.getTitle(), site.getDescription(), 
                        owner, site, site.getType(), new Date(site.getModifiedTime().getTime()));
            sortableSites.add(obj);
         } catch (UserNotDefinedException e) {
            logger.warn("User with id " + site.getCreatedBy().getId() + " does not exist.");
         }
      }
      
      return sortableSites;
   }

   public ToolConfiguration getToolInfo(Map request) {
      String siteId = (String) request.get(SITE_ID_PARAM);

      Site site = getWorksiteManager().getSite(siteId);
      List pages = site.getPages();
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage)i.next();
         if (page.getTitle().equals("Home")) {
            return (ToolConfiguration)page.getTools().get(0);
         }
      }

      return null;
   }

   public boolean isNewWindow(Object entry) {
      return false;
   }

   /**
    * @param entry
    * @return
    */
   public Map getToolParams(Object entry) {
      SortableListObject site = (SortableListObject)entry;
      Map<String, String> model = new HashMap<String, String>();

      model.put(SITE_ID_PARAM, site.getId());

      return model;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   protected String getSiteId(Object entity) {
      SortableListObject site = (SortableListObject) entity;
      return site.getId();
   }

   protected String getPageId(Object entity) {
      SortableListObject obj = (SortableListObject) entity;
      Site site = getWorksiteManager().getSite(obj.getId());
      List pages = site.getPages();
      if (pages.size() > 0) {
         return ((SitePage)pages.get(0)).getId();

      }
      return null;
   }
}

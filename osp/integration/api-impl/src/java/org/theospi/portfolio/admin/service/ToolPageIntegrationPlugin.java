/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/integration/api-impl/src/java/org/theospi/portfolio/admin/service/ToolPageIntegrationPlugin.java $
* $Id: ToolPageIntegrationPlugin.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.theospi.portfolio.shared.model.OspException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ToolPageIntegrationPlugin extends IntegrationPluginBase {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private ToolManager toolManager;
   private SiteService siteService;

   protected boolean currentlyIncluded(IntegrationOption option) {
      if (option instanceof ExistingWorksitePageOption) {
         return includedInExistingWorksite((ExistingWorksitePageOption)option);
      }

      PageOption page = (PageOption)option;
      return (loadPage(page.getWorksiteId(), page) != null);
   }

   protected boolean includedInExistingWorksite(ExistingWorksitePageOption option) {
      List sites = getSiteService().getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (isType(site, option)) {
            if (!checkSite(site, option)) {
               return false;
            }
         }
      }

      return true;
   }

   protected boolean isType(Site site, ExistingWorksitePageOption option) {
      if (option.getWorksiteType().equals("user")) {
         return getSiteService().isUserSite(site.getId());
      }
      else {
         return site.isType(option.getWorksiteType());
      }
   }

   protected boolean checkSite(Site site, PageOption option) {
      List pages = site.getPages();
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage)i.next();
         if (page.getTitle().equals(option.getPageName())) {
            return true;
         }
      }

      return false;
   }

   protected SitePage loadPage(String siteId, PageOption pageOption) {
      Site site;
      try {
         site = getSiteService().getSite(siteId);
         List pages = site.getPages();
         for (Iterator i=pages.iterator();i.hasNext();) {
            SitePage page = (SitePage)i.next();
            if (page.getTitle().equals(pageOption.getPageName())) {
               return page;
            }
         }
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return null;
   }

   public IntegrationOption updateOption(IntegrationOption option) {
      PageOption page = (PageOption)option;

      if (!currentlyIncluded(page) && option.isInclude()) {
         addPage(page);
      }
      else if (currentlyIncluded(page) && !option.isInclude()) {
         removePage(page);
      }

      return option;
   }

   public boolean executeOption(IntegrationOption option) {
      //PageOption pageOption = (PageOption)option;

      /*
      for (Iterator i=pageOption.getTools().iterator();i.hasNext();) {
         ToolOption toolOption = (ToolOption) i.next();
         if (getToolManager().getTool(toolOption.getToolId()) == null) {
            return false;
         }
      }
      */

      // also check the existing tools, if any
      /*
      try {
         Site site = getSiteService().getSite(pageOption.getWorksiteId());

         for (Iterator i=site.getPages().iterator();i.hasNext();) {
            SitePage page = (SitePage) i.next();
            for (Iterator j=page.getTools().iterator();j.hasNext();) {
               ToolConfiguration tool = (ToolConfiguration) j.next();
               if (tool.getTool() == null) {
                  return false;
               }
            }
         }
      } catch (IdUnusedException e) {
         logger.warn("", e);
      }
      */

      updateOption(option);
      return true;
   }

   protected void addPage(PageOption page) {
      if (page instanceof ExistingWorksitePageOption) {
         ExistingWorksitePageOption option = (ExistingWorksitePageOption)page;
         List sites = getSiteService().getSites(SiteService.SelectionType.ANY,
               null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

         for (Iterator i=sites.iterator();i.hasNext();) {
            Site site = (Site)i.next();
            if (isType(site, option)) {
               addPage(site.getId(), option);
            }
         }
      }
      else {
         addPage(page.getWorksiteId(), page);
      }
   }

   protected void addPage(String siteId, PageOption page) {

      if (loadPage(siteId, page) != null) {
         return;
      }

      Site site;
      try {
         site = getSiteService().getSite(siteId);

         SitePage pageEdit = site.addPage();
         pageEdit.setTitle(page.getPageName());
         pageEdit.setLayout(page.getLayout());

         for (Iterator i=page.getTools().iterator();i.hasNext();) {
            addTool(pageEdit, (ToolOption)i.next());
         }

         for (int i=0;i<page.getPositionFromEnd();i++){
            pageEdit.moveUp();
         }

         getSiteService().save(site);
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (PermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected void addTool(SitePage pageEdit, ToolOption toolOption) {
      Tool reg = new ToolWrapper(toolOption.getToolId());
      ToolConfiguration tool = pageEdit.addTool(reg);
      tool.setTitle(toolOption.getTitle());
      tool.setLayoutHints(toolOption.getLayoutHints());
      Properties props = tool.getPlacementConfig();

      for (Iterator i = toolOption.getInitProperties().entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         props.setProperty((String)entry.getKey(),
            (String)entry.getValue());
      }
   }

   protected void removePage(PageOption page) {
      if (page instanceof ExistingWorksitePageOption) {
         ExistingWorksitePageOption option = (ExistingWorksitePageOption)page;
         List sites = getSiteService().getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
               null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

         for (Iterator i=sites.iterator();i.hasNext();) {
            Site site = (Site)i.next();
            if (isType(site, option)) {
               removePage(site.getId(), option);
            }
         }
      }
      else {
         removePage(page.getWorksiteId(), page);
      }
   }

   protected void removePage(String siteId, PageOption page) {
      SitePage sitePage = loadPage(siteId, page);

      Site site;
      try {
         site = getSiteService().getSite(sitePage.getContainingSite().getId());
         SitePage pageEdit = site.getPage(sitePage.getId());

         site.removePage(pageEdit);
         getSiteService().save(site);
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (PermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

}

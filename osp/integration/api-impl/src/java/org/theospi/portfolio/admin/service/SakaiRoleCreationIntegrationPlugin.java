/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/integration/api-impl/src/java/org/theospi/portfolio/admin/service/SakaiRoleCreationIntegrationPlugin.java $
* $Id: SakaiRoleCreationIntegrationPlugin.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import org.sakaiproject.authz.api.*;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.theospi.portfolio.shared.model.OspException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SakaiRoleCreationIntegrationPlugin extends IntegrationPluginBase {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private WorksiteManager worksiteManager;

   protected boolean currentlyIncluded(IntegrationOption option) {
      RoleIntegrationOption roleOption = (RoleIntegrationOption)option;

      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         return existingWorksitesHasRole(
            (ExistingWorksitesRoleIntegrationOption)roleOption);
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      Role role = realm.getRole(roleOption.getRoleId());
      return (role != null);
   }

   protected boolean existingWorksitesHasRole(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            if (!checkSite(site, roleOption)) {
               return false;
            }
         }
      }

      return true;
   }

   protected boolean checkSite(Site site, ExistingWorksitesRoleIntegrationOption roleOption) {
      AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());

      return (siteRealm.getRole(roleOption.getRoleId()) != null);
   }

   public IntegrationOption updateOption(IntegrationOption option) {
      RoleIntegrationOption roleOption = (RoleIntegrationOption)option;

      if (option.isInclude() && !currentlyIncluded(roleOption)) {
         addRole(roleOption);
      }
      else if (currentlyIncluded(roleOption)) {
         removeRole(roleOption);
      }

      return option;
   }

   public boolean executeOption(IntegrationOption option) {
      updateOption(option);
      return true;
   }

   protected void addRole(RoleIntegrationOption roleOption) {
      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         addRoleToAllWorksites((ExistingWorksitesRoleIntegrationOption)roleOption);
         return;
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      addRole(realm, roleOption);
   }

   protected void addRoleToAllWorksites(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());
            addRole(siteRealm, roleOption);
         }
      }
   }

   protected void addRole(AuthzGroup realm, RoleIntegrationOption roleOption) {
      AuthzGroup edit = null;
      Role copy = realm.getRole(roleOption.getCopyOf());

      try {
         edit = AuthzGroupService.getAuthzGroup(realm.getId());
         Role newRole = edit.addRole(roleOption.getRoleId(), copy);

         if (roleOption.getPermissionsOn() != null) {
            newRole.allowFunctions(new HashSet(roleOption.getPermissionsOn()));
         }

         if (roleOption.getPermissionsOff() != null) {
            newRole.disallowFunctions(new HashSet(roleOption.getPermissionsOff()));
         }

         AuthzGroupService.save(edit);
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AuthzPermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (RoleAlreadyDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected void removeRole(RoleIntegrationOption roleOption) {
      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         removeRoleFromAllWorksites((ExistingWorksitesRoleIntegrationOption)roleOption);
         return;
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      removeRole(realm, roleOption);
   }

   protected void removeRoleFromAllWorksites(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());
            removeRole(siteRealm, roleOption);
         }
      }
   }

   protected void removeRole(AuthzGroup realm, RoleIntegrationOption roleOption) {
      AuthzGroup edit = null;
      Role remove = realm.getRole(roleOption.getRoleId());

      try {
         edit = AuthzGroupService.getAuthzGroup(realm.getId());
         edit.removeRole(remove.getDescription());
         AuthzGroupService.save(edit);
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AuthzPermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

}

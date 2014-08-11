/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/integration/api-impl/src/java/org/theospi/portfolio/admin/service/SiteIntegrationPlugin.java $
* $Id: SiteIntegrationPlugin.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.admin.service;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.theospi.portfolio.shared.model.OspException;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 1:38:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SiteIntegrationPlugin extends IntegrationPluginBase {

   private SiteService siteService;

   public IntegrationOption updateOption(IntegrationOption option) {
      SiteOption siteOption = (SiteOption) option;

      try {
         Site site = getSiteService().getSite(siteOption.getSiteId());
         if (site != null) {
            return siteOption;
         }
      } catch (IdUnusedException e) {
         // no site found... this means we should go on and create it.
      }

      try {
         Site site = getSiteService().addSite(siteOption.getSiteId(), siteOption.getSiteType());

         site.setTitle(siteOption.getSiteTitle());
         site.setDescription(siteOption.getSiteDescription());
         site.setPublished(true);
         
         getSiteService().save(site);
      } catch (IdInvalidException e) {
         throw new OspException(e);
      } catch (IdUsedException e) {
         throw new OspException(e);
      } catch (PermissionException e) {
         throw new OspException(e);
      } catch (IdUnusedException e) {
         throw new OspException(e);
      }

      return siteOption;
   }

   public boolean executeOption(IntegrationOption option) {
      updateOption(option);
      return true;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
}

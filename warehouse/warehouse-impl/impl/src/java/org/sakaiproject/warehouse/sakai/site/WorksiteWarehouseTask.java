/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-2.9.1/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/sakai/site/WorksiteWarehouseTask.java $
* $Id: WorksiteWarehouseTask.java 59691 2009-04-03 23:46:45Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.warehouse.sakai.site;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorksiteWarehouseTask extends BaseWarehouseTask {

   private SiteService siteService;

   protected Collection getItems() {
      List sites = getSiteService().getSites(SelectionType.ANY, null, null, null, SortType.NONE, null);
      
      for(Iterator i = sites.iterator(); i.hasNext(); ) {
         Site site = (Site)i.next();
         
         //  get Members is a HashSet of BaseMember
         Collection members = site.getMembers();
         members.size();
      }
      
      return sites;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

}

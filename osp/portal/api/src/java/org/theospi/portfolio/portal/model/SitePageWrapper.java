/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/portal/api/src/java/org/theospi/portfolio/portal/model/SitePageWrapper.java $
* $Id: SitePageWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.portal.model;

import org.sakaiproject.site.api.SitePage;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 15, 2006
 * Time: 8:53:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SitePageWrapper {

   private SitePage page;
   private int order;

   public SitePageWrapper(SitePage page, int order) {
      this.page = page;
      this.order = order;
   }

   public SitePage getPage() {
      return page;
   }

   public void setPage(SitePage page) {
      this.page = page;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }
}

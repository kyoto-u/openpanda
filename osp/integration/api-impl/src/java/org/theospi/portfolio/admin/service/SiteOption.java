/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/integration/api-impl/src/java/org/theospi/portfolio/admin/service/SiteOption.java $
* $Id: SiteOption.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.theospi.portfolio.admin.model.IntegrationOption;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 11:41:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteOption extends IntegrationOption {

   private String siteId;
   private String siteType;
   private String realmTemplate;

   private String siteTitle;
   private String siteDescription;

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getSiteType() {
      return siteType;
   }

   public void setSiteType(String siteType) {
      this.siteType = siteType;
   }

   public String getRealmTemplate() {
      return realmTemplate;
   }

   public void setRealmTemplate(String realmTemplate) {
      this.realmTemplate = realmTemplate;
   }

   public String getSiteTitle() {
      return siteTitle;
   }

   public void setSiteTitle(String siteTitle) {
      this.siteTitle = siteTitle;
   }

   public String getSiteDescription() {
      return siteDescription;
   }

   public void setSiteDescription(String siteDescription) {
      this.siteDescription = siteDescription;
   }

}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/FormConsumptionDetail.java $
* $Id: FormConsumptionDetail.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

/**
 * 
 */
package org.sakaiproject.metaobj.shared.model;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * @author chrismaurer
 *
 */
public class FormConsumptionDetail {

   private String formDefId;
   private String type;
   private String detail1;
   private String detail2;
   private String siteId;
   private String siteName;
   
   public FormConsumptionDetail() {
      
   }
   public FormConsumptionDetail(Id formDefId, Id siteId) {
      this(formDefId.getValue(), siteId.getValue());
   }
   public FormConsumptionDetail(Id formDefId, String siteId) {
      this(formDefId.getValue(), siteId);
   }
   public FormConsumptionDetail(String formDefId, String siteId) {
      this(formDefId, siteId, null, null,  null);
   }
   public FormConsumptionDetail(Id formDefId, Id siteId, String type) {
      this(formDefId.getValue(), siteId.getValue(), type);
   }
   public FormConsumptionDetail(Id formDefId, String siteId, String type) {
      this(formDefId.getValue(), siteId, type);
   }
   public FormConsumptionDetail(String formDefId, String siteId, String type) {
      this(formDefId, siteId, type, null,  null);
   }
   public FormConsumptionDetail(Id formDefId, Id siteId, String type, String detail1) {
      this(formDefId.getValue(), siteId.getValue(), type, detail1);
   }
   public FormConsumptionDetail(Id formDefId, String siteId, String type, String detail1) {
      this(formDefId.getValue(), siteId, type, detail1);
   }
   public FormConsumptionDetail(String formDefId, String siteId, String type, String detail1) {
      this(formDefId, siteId, type, detail1, null);
   }
   public FormConsumptionDetail(Id formDefId, Id siteId, String type, String detail1, String detail2) {
      this(formDefId.getValue(), siteId.getValue(), type, detail1, detail2);
   }
   public FormConsumptionDetail(Id formDefId, String siteId, String type, String detail1, String detail2) {
      this(formDefId.getValue(), siteId, type, detail1, detail2);
   }
   public FormConsumptionDetail(String formDefId, Id siteId, String type, String detail1, String detail2) {
      this(formDefId, siteId.getValue(), type, detail1, detail2);
   }
   
   public FormConsumptionDetail(String formDefId, String siteId, String type, String detail1, String detail2) {
      this.formDefId = formDefId;
      this.type = type;
      this.detail1 = detail1;
      this.detail2 = detail2;
      this.siteId = siteId;
      try {
         Site site = SiteService.getSite(siteId);
         String localSiteName = site.getTitle();
         
         if (SiteService.isUserSite(siteId)) {
            //I think this means that the siteId is ~<uesrId>
            try {
               String userId = siteId.substring(1);
               User user = UserDirectoryService.getUser(userId);
               localSiteName = site.getTitle() + ": " + user.getDisplayName();
            } catch (UserNotDefinedException e) {
               // guess this wasn't really a user's my workspace?
               // Maybe they were deleted or something?
               //TODO add logging?
            }            
         }
         
         this.siteName = localSiteName;
         
      } catch (IdUnusedException e) {
         this.siteName = siteId;
      }
      
   }

   /**
    * @return the formDefId
    */
   public String getFormDefId() {
      return formDefId;
   }

   /**
    * @param formDefId the formDefId to set
    */
   public void setFormDefId(String formDefId) {
      this.formDefId = formDefId;
   }

   /**
    * @return the siteId
    */
   public String getSiteId() {
      return siteId;
   }

   /**
    * @param siteId the siteId to set
    */
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   /**
    * @return the siteName
    */
   public String getSiteName() {
      return siteName;
   }

   /**
    * @param siteName the siteName to set
    */
   public void setSiteName(String siteName) {
      this.siteName = siteName;
   }

   /**
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * @return the detail1
    */
   public String getDetail1() {
      return detail1;
   }

   /**
    * @param detail1 the detail1 to set
    */
   public void setDetail1(String detail1) {
      this.detail1 = detail1;
   }

   /**
    * @return the detail2
    */
   public String getDetail2() {
      return detail2;
   }

   /**
    * @param detail2 the detail2 to set
    */
   public void setDetail2(String detail2) {
      this.detail2 = detail2;
   }
   
   
}

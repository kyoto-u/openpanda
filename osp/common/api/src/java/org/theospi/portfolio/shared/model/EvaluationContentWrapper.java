/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/common/api/src/java/org/theospi/portfolio/shared/model/EvaluationContentWrapper.java $
* $Id: EvaluationContentWrapper.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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

package org.theospi.portfolio.shared.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

public abstract class EvaluationContentWrapper {

   private Id id;
   private String title;
   private User owner;
   private Date submittedDate;
   private String evalType;
   private String url;
   private Set urlParams = new HashSet();
   private String siteTitle;
   private String siteId;
   protected final Log logger = LogFactory.getLog(getClass());
   private boolean hideOwnerDisplay = false;
   
   public EvaluationContentWrapper( Id id, String title, Agent owner, Date submittedDate, String siteId) throws UserNotDefinedException {
      this.id = id;
      this.title = title;
      this.submittedDate = submittedDate;
      
      this.owner = UserDirectoryService.getUser(owner.getId().getValue());
      this.siteTitle = fetchSiteName(siteId);
      this.siteId = siteId;
   }
   
   public class ParamBean {
      
      private String key;
      private String value;
      
      public ParamBean(String key, String value) {
         this.key = key;
         this.value = value;
      }
      
      /**
       * @return Returns the key.
       */
      public String getKey() {
         return key;
      }

      /**
       * @param key The key to set.
       */
      public void setKey(String key) {
         this.key = key;
      }

      /**
       * @return Returns the value.
       */
      public String getValue() {
         return value;
      }

      /**
       * @param value The value to set.
       */
      public void setValue(String value) {
         this.value = value;
      }
      
   }
   
   
   public String fetchSiteName(String siteId) {
      String title = null;
      try {
         Site site = SiteService.getSite(siteId);
         title = site.getTitle();
      } catch (IdUnusedException e) {
         logger.warn(this+".fetchSiteName",e);
      }
      return title;
   }
   
   /**
    * @return Returns the owner.
    */
   public User getOwner() {
      return owner;
   }
   /**
    * @param owner The owner to set.
    */
   public void setOwner(User owner) {
      this.owner = owner;
   }
   /**
    * @return Returns the submittedDate.
    */
   public Date getSubmittedDate() {
      return submittedDate;
   }
   /**
    * @param submittedDate The submittedDate to set.
    */
   public void setSubmittedDate(Date submittedDate) {
      this.submittedDate = submittedDate;
   }
   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }
   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }

   /**
    * @return Returns the evalType.
    */
   public String getEvalType() {
      return evalType;
   }

   /**
    * @param evalType The evalType to set.
    */
   public void setEvalType(String evalType) {
      this.evalType = evalType;
   }

   /**
    * @return Returns the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * @param url The url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }

   /**
    * returns the url parameters to be passed along
    * @return Set of ParamBean.
    */
   public Set getUrlParams() {
      return urlParams;
   }

   /**
    * @param urlParams The urlParams to set.
    */
   public void setUrlParams(Set urlParams) {
      this.urlParams = urlParams;
   }

   /**
    * @return the siteTitle
    */
   public String getSiteTitle() {
      return siteTitle;
   }

   /**
    * @param siteTitle the siteTitle to set
    */
   public void setSiteTitle(String siteTitle) {
      this.siteTitle = siteTitle;
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
    * 
    * @return
    */
   public boolean isHideOwnerDisplay() {
	   return hideOwnerDisplay;
   }

   /**
    * 
    * @param hideOwnerDisplay
    */
   public void setHideOwnerDisplay(boolean hideOwnerDisplay) {
	   this.hideOwnerDisplay = hideOwnerDisplay;
   }
}

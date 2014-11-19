/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/api/src/java/org/theospi/portfolio/shared/model/SortableListObject.java $
* $Id: SortableListObject.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

/**
 * 
 */
package org.theospi.portfolio.shared.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.list.intf.DecoratedListItem;
import org.theospi.portfolio.list.intf.ListItemUtils;


public class SortableListObject implements DecoratedListItem {

   private String id;
   private String title;
   private String description;
   private User owner;
   private Site site;
   private String type;
   private String typeRaw;
   private String modified;
   private Date modifiedRaw;
   private ListItemUtils listItemUtils;
   
   public SortableListObject() {}
   
   public SortableListObject(String id, String title, String description, Agent owner,
         Site site, String typeRaw, Date modifiedRaw) throws UserNotDefinedException {
      this.id = id;
      this.title = title;
      this.description = description;
      this.owner = UserDirectoryService.getUser(owner.getId().getValue());
      this.site = site;
      this.typeRaw = typeRaw;
      this.modifiedRaw = modifiedRaw;
   }
   
   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }
   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }
   /**
    * @return the owner
    */
   public User getOwner() {
      return owner;
   }
   /**
    * @param owner the owner to set
    */
   public void setOwner(User owner) {
      this.owner = owner;
   }
   /**
    * @return the site
    */
   public Site getSite() {
      return site;
   }
   /**
    * @param site the site to set
    */
   public void setSite(Site site) {
      this.site = site;
   }
   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the type
    */
   public String getTypeRaw() {
      return typeRaw;
   }

   /**
    * @param type the type to set
    */
   public void setTypeRaw(String typeRaw) {
      this.typeRaw = typeRaw;
   }
   
   /**
    * @return the type
    */
   public String getType() {
      //return type;
      String retValue = getTypeRaw();
      if (listItemUtils.lookUpInBundle("type"))
         retValue = listItemUtils.formatMessage(ensureNotNull(getTypeRaw()), new Object[]{});
      
      return retValue;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * @return the modified
    */
   public Date getModifiedRaw() {
      return modifiedRaw;
   }

   /**
    * @param modified the modified to set
    */
   public void setModifiedRaw(Date modifiedRaw) {
      this.modifiedRaw = modifiedRaw;
   }
   
   
   public String getModified() {
      return listItemUtils.formatMessage("date_format", new Object[]{getModifiedRaw()});
   }
   
   /**
    * @param modified the modified to set
    */
   public void setModified(String modified) {
      this.modified = modified;
   }
   
   public ListItemUtils getListItemUtils() {
      return listItemUtils;
  }

  public void setListItemUtils(ListItemUtils listItemUtils) {
      this.listItemUtils = listItemUtils;
  }
  
  protected String ensureNotNull(String value) {
     return (value == null) ? "" : value;
  }
   
}

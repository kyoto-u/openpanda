/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/api/src/java/org/theospi/portfolio/portal/model/SiteType.java $
* $Id:SiteType.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.portal.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 2:31:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class
   SiteType extends IdentifiableObject implements Comparable {

   private String key;
   private String description;
   private String skin;
   private int order;
   private int firstCategory = 0;
   private int lastCategory = 0;
   private List toolCategories;
   private List specialSites;
   private String name;
   private boolean hidden = false;
   private boolean displayTab = true;
   public static final SiteType OTHER = new SiteType("org.theospi.portfolio.portal.other", "other", Integer.MAX_VALUE);
   public static final SiteType MY_WORKSPACE = new SiteType("org.theospi.portfolio.portal.myWorkspace", "workspace", 0);
   public static final SiteType GATEWAY = new SiteType("org.theospi.portfolio.portal.gateway", "gateway", 0);

   public SiteType() {
   }

   public SiteType(String key, String name, int order) {
      this.name = name;
      this.key = key;
      this.order = order;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String getSkin() {
      return skin;
   }

   public void setSkin(String skin) {
      this.skin = skin;
   }

   public int compareTo(Object o) {
      Integer order = Integer.valueOf(getOrder());
      Integer other = Integer.valueOf(((SiteType)o).getOrder());
      return order.compareTo(other);
   }

   public List getToolCategories() {
      return toolCategories;
   }

   public void setToolCategories(List toolCategories) {
      this.toolCategories = toolCategories;
   }

   public int getFirstCategory() {
      return firstCategory;
   }

   public void setFirstCategory(int firstCategory) {
      this.firstCategory = firstCategory;
   }

   public int getLastCategory() {
      return lastCategory;
   }

   public void setLastCategory(int lastCategory) {
      this.lastCategory = lastCategory;
   }

   public List getSpecialSites() {
      return specialSites;
   }

   public void setSpecialSites(List specialSites) {
      this.specialSites = specialSites;
   }

   /**
    * @return the hidden
    */
   public boolean isHidden() {
      return hidden;
   }

   /**
    * @param hidden the hidden to set
    */
   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public boolean isDisplayTab() {
      return displayTab;
   }

   public void setDisplayTab(boolean displayTab) {
      this.displayTab = displayTab;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      if (!super.equals(o)) {
         return false;
      }

      final SiteType siteType = (SiteType) o;

      if (!key.equals(siteType.key)) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 29 * result + key.hashCode();
      return result;
   }

}

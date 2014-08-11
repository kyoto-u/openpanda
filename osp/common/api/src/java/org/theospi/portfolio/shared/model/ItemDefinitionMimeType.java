/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/common/api/src/java/org/theospi/portfolio/shared/model/ItemDefinitionMimeType.java $
* $Id: ItemDefinitionMimeType.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.shared.model;

import java.io.Serializable;

public class ItemDefinitionMimeType implements Serializable {
   private String primary;
   private String secondary;
   static final long serialVersionUID = -6220810277272518156l;

   public ItemDefinitionMimeType() {
   }

   public ItemDefinitionMimeType(String primary, String secondary) {
      this.primary = primary;
      this.secondary = secondary;
   }

   public String getSecondary() {
      return secondary;
   }

   public void setSecondary(String secondary) {
      this.secondary = secondary;
   }

   public String getPrimary() {
      return primary;
   }

   public void setPrimary(String primary) {
      this.primary = primary;
   }

   public String getValue() {
      return this.toString();
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append(getPrimary());
      if (getSecondary() != null && getSecondary().length() > 0) {
         buffer.append("/");
         buffer.append(getSecondary());
      }
      return buffer.toString();
   }

   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ItemDefinitionMimeType)) return false;

      final ItemDefinitionMimeType itemDefinitionMimeType = (ItemDefinitionMimeType) o;

      if (!primary.equals(itemDefinitionMimeType.primary)) return false;
      if (secondary != null ? !secondary.equals(itemDefinitionMimeType.secondary) : itemDefinitionMimeType.secondary != null) return false;

      return true;
   }

   public int hashCode() {
      int result;
      result = primary.hashCode();
      result = 29 * result + (secondary != null ? secondary.hashCode() : 0);
      return result;
   }
}

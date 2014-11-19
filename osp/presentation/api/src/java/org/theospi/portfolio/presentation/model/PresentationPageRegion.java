/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationPageRegion.java $
* $Id: PresentationPageRegion.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPageRegion extends IdentifiableObject implements Serializable {
   
   private Id id;
   private PresentationPage page;
   private String regionId;
   private List items = new ArrayList();
   private String type = "text";
   private String helpText;

   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public List getItems() {
      return items;
   }
   public void setItems(List items) {
      this.items = items;
   }
   public PresentationPage getPage() {
      return page;
   }
   public void setPage(PresentationPage page) {
      this.page = page;
   }
   public String getRegionId() {
      return regionId;
   }
   public void setRegionId(String regionId) {
      this.regionId = regionId;
   }

   public void reorderItems() {
      int index = 0;
      for (Iterator i=getItems().iterator();i.hasNext();) {
         PresentationPageItem item = (PresentationPageItem) i.next();
         item.setRegionItemSeq(index);
         index++;
      }
   }

   public void addBlank() {
      PresentationPageItem item = new PresentationPageItem();
      item.setRegion(this);
      item.setLayoutRegionId(this.getRegionId());
      item.setType(getType());
      item.setValue(getHelpText());
      getItems().add(item);
      reorderItems();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getHelpText() {
      return helpText;
   }

   public void setHelpText(String helpText) {
      this.helpText = helpText;
   }

   public int hashCode() {
      if (id != null) {
         return super.hashCode();
      }
      else {
         return regionId.hashCode();
      }
   }
}

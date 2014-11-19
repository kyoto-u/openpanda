/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationPageItem.java $
* $Id: PresentationPageItem.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPageItem extends IdentifiableObject implements Serializable {

  
   private Id id;
   private PresentationPageRegion region;
   private String layoutRegionId;
   private int regionItemSeq;
   private String type;
   private String value;
   private Set properties;
   
   
   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public String getLayoutRegionId() {
      return layoutRegionId;
   }
   public void setLayoutRegionId(String layoutRegionId) {
      this.layoutRegionId = layoutRegionId;
   }
   public PresentationPageRegion getRegion() {
      return region;
   }
   public void setRegion(PresentationPageRegion region) {
      this.region = region;
   }
   public Set getProperties() {
      return properties;
   }
   public void setProperties(Set properties) {
      this.properties = properties;
   }
   public int getRegionItemSeq() {
      return regionItemSeq;
   }
   public void setRegionItemSeq(int regionItemSeq) {
      this.regionItemSeq = regionItemSeq;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }
   
}

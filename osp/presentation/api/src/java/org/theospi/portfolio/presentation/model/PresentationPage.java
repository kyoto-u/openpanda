/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationPage.java $
* $Id: PresentationPage.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.tool.api.Tool;
import org.theospi.portfolio.style.model.Style;

public class PresentationPage extends IdentifiableObject implements Serializable, Comparable {
   
   private Id id;
   private String title;
   private String description;
   private String keywords;
   private Presentation presentation;
   private PresentationLayout layout;
   private Style style;
   private int sequence;
   private Set regions = new HashSet();
   private Date created;
   private Date modified;
   private boolean newObject;

   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public Set getRegions() {
      return regions;
   }
   public void setRegions(Set regions) {
      this.regions = regions;
   }

   public PresentationLayout getLayout() {
      return layout;
   }

   public void setLayout(PresentationLayout layout) {
      this.layout = layout;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public Style getStyle() {
      return style;
   }
   public void setStyle(Style style) {
      this.style = style;
   }   
   
   public Presentation getPresentation() {
      return presentation;
   }
   public void setPresentation(Presentation presentation) {
      this.presentation = presentation;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }

   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public String getUrl() {
      return "viewPresentation.osp?id=" + getPresentation().getId().getValue() +
         "&page=" + getId().getValue() + "&" + Tool.PLACEMENT_ID + "=" + getPresentation().getToolId();
   }

   public int compareTo(Object o) {
      PresentationPage other = (PresentationPage) o;
      Integer seq = Integer.valueOf(getSequence());
      Integer seqOther = Integer.valueOf(other.getSequence());
      return seq.compareTo(seqOther);
   }

}

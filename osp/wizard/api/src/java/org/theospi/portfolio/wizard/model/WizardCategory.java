/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/api/src/java/org/theospi/portfolio/wizard/model/WizardCategory.java $
* $Id:WizardCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 10:18:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class WizardCategory extends IdentifiableObject {

   private String title;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private Wizard wizard;
   private WizardCategory parentCategory;
   private int sequence = 0;

   private List childCategories;
   private List childPages;

   public WizardCategory() {
   }

   public WizardCategory(Wizard wizard) {
      this.wizard = wizard;
      setChildCategories(new ArrayList());
      setChildPages(new ArrayList());
      setCreated(new Date());
      setModified(new Date());
   }
   
   public boolean equals(Object obj) {
	   if (this == obj)
		   return true;
	   if (!super.equals(obj))
		   return false;
	   if (getClass() != obj.getClass())
		   return false;
	   WizardCategory other = (WizardCategory) obj;
	   if (childCategories == null) {
		   if (other.childCategories != null)
			   return false;
	   } else if (!childCategories.equals(other.childCategories))
		   return false;
	   if (childPages == null) {
		   if (other.childPages != null)
			   return false;
	   } else if (!childPages.equals(other.childPages))
		   return false;
	   if (created == null) {
		   if (other.created != null)
			   return false;
	   } else if (!created.equals(other.created))
		   return false;
	   if (description == null) {
		   if (other.description != null)
			   return false;
	   } else if (!description.equals(other.description))
		   return false;
	   if (keywords == null) {
		   if (other.keywords != null)
			   return false;
	   } else if (!keywords.equals(other.keywords))
		   return false;
	   if (modified == null) {
		   if (other.modified != null)
			   return false;
	   } else if (!modified.equals(other.modified))
		   return false;
	   if (parentCategory == null) {
		   if (other.parentCategory != null)
			   return false;
	   } else if (!parentCategory.equals(other.parentCategory))
		   return false;
	   if (sequence != other.sequence)
		   return false;
	   if (title == null) {
		   if (other.title != null)
			   return false;
	   } else if (!title.equals(other.title))
		   return false;
	   if (wizard == null) {
		   if (other.wizard != null)
			   return false;
	   } else if (!wizard.equals(other.wizard))
		   return false;
	   return true;
   }
   
   public int hashCode() {
	   final int prime = 31;
	   int result = super.hashCode();
	   result = prime * result
	   + ((childCategories == null) ? 0 : childCategories.hashCode());
	   result = prime * result
	   + ((childPages == null) ? 0 : childPages.hashCode());
	   result = prime * result + ((created == null) ? 0 : created.hashCode());
	   result = prime * result
	   + ((description == null) ? 0 : description.hashCode());
	   result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
	   result = prime * result + ((modified == null) ? 0 : modified.hashCode());
	   result = prime * result
	   + ((parentCategory == null) ? 0 : parentCategory.hashCode());
	   result = prime * result + sequence;
	   result = prime * result + ((title == null) ? 0 : title.hashCode());
	   result = prime * result + ((wizard == null) ? 0 : wizard.hashCode());
	   return result;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
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

   /**
    * @return List of WizardCategory
    */
   public List getChildCategories() {
      return childCategories;
   }

   /**
    * @param childCategories List of WizardCategory
    */
   public void setChildCategories(List childCategories) {
      this.childCategories = childCategories;
   }

   public List getChildPages() {
      return childPages;
   }

   public void setChildPages(List childPages) {
      this.childPages = childPages;
   }

   public Wizard getWizard() {
      return wizard;
   }

   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public WizardCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(WizardCategory parentCategory) {
      this.parentCategory = parentCategory;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/api/src/java/org/theospi/portfolio/wizard/model/CompletedWizardCategory.java $
* $Id:CompletedWizardCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 23, 2006
 * Time: 3:07:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizardCategory extends IdentifiableObject {

   private WizardCategory category;
   private CompletedWizard wizard;
   private List childPages;
   private List childCategories;
   private int sequence;
   private CompletedWizardCategory parentCategory;

   private boolean expanded = false;

   public CompletedWizardCategory() {
   }

   public CompletedWizardCategory(CompletedWizard wizard, WizardCategory category) {
      this.wizard = wizard;
      this.category = category;
      setSequence(category.getSequence());
      setChildPages(initChildPages());
      setChildCategories(initChildCategories());
   }

   protected List initChildCategories() {
      List categories = new ArrayList();

      for (Iterator i=category.getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         CompletedWizardCategory completed = new CompletedWizardCategory(wizard, category);
         completed.setParentCategory(this);
         categories.add(completed);
      }

      return categories;
   }

   protected List initChildPages() {
      List pages = new ArrayList();

      for (Iterator i=category.getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         CompletedWizardPage completedPage = new CompletedWizardPage(page, this);
         pages.add(completedPage);
      }

      return pages;
   }

   public WizardCategory getCategory() {
      return category;
   }

   public void setCategory(WizardCategory category) {
      this.category = category;
   }

   public CompletedWizard getWizard() {
      return wizard;
   }

   public void setWizard(CompletedWizard wizard) {
      this.wizard = wizard;
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public List getChildPages() {
      return childPages;
   }

   public void setChildPages(List childPages) {
      this.childPages = childPages;
   }

   public List getChildCategories() {
      return childCategories;
   }

   public void setChildCategories(List childCategories) {
      this.childCategories = childCategories;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public CompletedWizardCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(CompletedWizardCategory parentCategory) {
      this.parentCategory = parentCategory;
   }
}

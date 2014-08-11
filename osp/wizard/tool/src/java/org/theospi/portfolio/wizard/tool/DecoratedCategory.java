/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCategory.java $
* $Id:DecoratedCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardCategory;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 11:44:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCategory extends DecoratedCategoryChild {

   public static final String NEW_PAGE = "org.theospi.portfolio.wizard.tool.DecoratedCategory.newPage";

   private WizardCategory base;
   private List categoryPageList;
   private boolean selected;

   private DecoratedCategory parentCategory = null;

   public DecoratedCategory(WizardCategory base, WizardTool tool) {
      super(tool, 0);
      this.base = base;
   }

   public DecoratedCategory(DecoratedCategory parentCategory, WizardCategory base, WizardTool tool, int indent) {
      super(tool, indent);
      this.parentCategory = parentCategory;
      this.base = base;
   }

   public WizardCategory getBase() {
      return base;
   }

   public void setBase(WizardCategory base) {
      this.base = base;
   }

   
   /** 
    * This returns the concat description string.  This is currently acceptable
    * because the wizard description is not html 
    * @return String
    */
   public String getDescription() {
      String desc = base.getDescription();
      if(desc != null && desc.length() > 100)
         return desc.substring(0, 100) + "...";
      return desc;
   }

   public String processActionNewPage() {
      if(getBase().getWizard().isPublished())
         return null;
      boolean defaults = getParent().getMatrixManager().isEnableDafaultMatrixOptions();
      WizardPageSequence wizardPage =
            new WizardPageSequence(new WizardPageDefinition(getBase().getWizard().getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL) ? WizardPageDefinition.WPD_WIZARD_HIER_TYPE : WizardPageDefinition.WPD_WIZARD_SEQ_TYPE, 
            		defaults, defaults, defaults, defaults, defaults, defaults, defaults));
      String siteId = getParent().getWorksite().getId();
      wizardPage.getWizardPageDefinition().setSiteId(siteId);
      wizardPage.getWizardPageDefinition().setNewId(getParent().getIdManager().createId());
      wizardPage.setCategory(getBase());

      getParent().getCurrent().getRootCategory().setCategoryPageList(null);

      return new DecoratedWizardPage(this, wizardPage, getParent(), getIndent() + 1).processActionEdit(true);
   }

   protected void resequencePages() {
      int index = 0;
      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         page.setSequence(index);
         index++;
      }
      getParent().getCurrent().getRootCategory().setCategoryPageList(null);
   }

   protected void resequenceCategories() {
      int index = 0;
      for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         category.setSequence(index);
         index++;
      }
      getParent().getCurrent().getRootCategory().setCategoryPageList(null);
   }

   public List getCategoryPageList() {
      if (categoryPageList == null) {
         ToolSession session = SessionManager.getCurrentToolSession();
         if (session.getAttribute(NEW_PAGE) != null &&
               session.getAttribute(WizardPageHelper.CANCELED) == null) {
            WizardPageSequence page = (WizardPageSequence) session.getAttribute(NEW_PAGE);
            page.setSequence(page.getCategory().getChildPages().size());
            page.getCategory().getChildPages().add(page);
            session.removeAttribute(NEW_PAGE);
         }
         else if (session.getAttribute(WizardPageHelper.CANCELED) != null) {
            session.removeAttribute(NEW_PAGE);
            session.removeAttribute(WizardPageHelper.CANCELED);
         }

         categoryPageList = new ArrayList();
         addCategoriesPages(categoryPageList);
      }
      return categoryPageList;
   }

   protected List addCategoriesPages(List categoryPages) {
      if (getParent().getCurrent().getBase().getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL)) {
         if(parentCategory == null)
            categoryPages.add(getParent().getCurrent());
         for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
            WizardCategory category = (WizardCategory) i.next();
            DecoratedCategory decoratedCategory = new DecoratedCategory(this, category, getParent(), getIndent()+1);
            categoryPages.add(decoratedCategory);
            decoratedCategory.addCategoriesPages(categoryPages);
         }
      }

      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         categoryPages.add(new DecoratedWizardPage(this, page, getParent(), getIndent()+1));
      }
      return categoryPages;
   }

   public void setCategoryPageList(List categoryPageList) {
      this.categoryPageList = categoryPageList;
   }

   public DecoratedCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(DecoratedCategory parentCategory) {
      this.parentCategory = parentCategory;
   }

   public String processActionSave() {
      List parentCategories = getParentCategory().getBase().getChildCategories();
      
      if (!parentCategories.contains(getBase())) {
         parentCategories.add(getBase());
         getBase().setParentCategory(getParentCategory().getBase());
      }

      getParentCategory().resequenceCategories();

      return "editWizardPages";
   }

   public String processActionCancel() {
      getParent().setCurrentCategory(null);
      return "editWizardPages";
   }

   public String getTitle() {
      return getBase().getTitle();
   }

   public String processActionEdit() {
      getParent().setCurrentCategory(this);
      return "editWizardCategory";
   }

   public String processActionDelete() {
      if(getBase().getWizard().isPublished())
         return null;
      DecoratedCategory parentCategory = getParentCategory();
      parentCategory.getBase().getChildCategories().remove(getBase());
      parentCategory.resequenceCategories();
      if(getBase().getId() != null)
         getParent().getDeletedItems().add(getBase());
      return "continue";
   }
   
   public String processActionConfirmDelete() {
      getParent().setCurrentCategory(this);
      return "confirmDeleteCategory";
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getBase().getParentCategory().getChildCategories(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getParentCategory().resequenceCategories();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getBase().getParentCategory().getChildCategories().size() - 1) {
         Collections.swap(getBase().getParentCategory().getChildCategories(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getParentCategory().resequenceCategories();
      }
      return null;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isFirst() {
      return getBase().getSequence() == 0;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getBase().getParentCategory().getChildCategories().size() - 1;
   }

   public String processActionNewCategory() {
      if(getBase().getWizard().isPublished())
         return null;
      WizardCategory wizardCategory = new WizardCategory(getBase().getWizard());
      getParent().setCurrentCategory(
            new DecoratedCategory(this, wizardCategory, getParent(), getIndent() + 1));
      return "editWizardCategory";
   }

   public boolean isCategory() {
      return true;
   }

   public boolean isContainerForMove() {
      if (getParent().getMoveCategoryChild() == null) {
         return false;
      }
      DecoratedCategoryChild child = getParent().getMoveCategoryChild();
      if (child instanceof DecoratedCategory) {
         DecoratedCategory category = (DecoratedCategory) child;
         return category.getParentCategory() != this && category != this;
      }
      else if (child instanceof DecoratedWizardPage){
         DecoratedWizardPage page = (DecoratedWizardPage) child;
         return page.getCategory() != this;
      }
      return false;
   }

   public String processActionMoveTo() {
      if(getBase().getWizard().isPublished())
         return null;
      DecoratedCategoryChild child = getParent().getMoveCategoryChild();
      child.setMoveTarget(false);
      if (child instanceof DecoratedCategory) {
         DecoratedCategory category = (DecoratedCategory) child;
         DecoratedCategory oldParent = category.getParentCategory();
         oldParent.getBase().getChildCategories().remove(category.getBase());
         getBase().getChildCategories().add(category.getBase());
         category.getBase().setParentCategory(getBase());
         oldParent.resequenceCategories();
         resequenceCategories();
      }
      else if (child instanceof DecoratedWizardPage) {
         DecoratedWizardPage page = (DecoratedWizardPage) child;
         DecoratedCategory oldParent = page.getCategory();
         oldParent.getBase().getChildPages().remove(page.getBase());
         getBase().getChildPages().add(page.getBase());
         page.getBase().setCategory(getBase());
         oldParent.resequencePages();
         resequencePages();
      }
      child.setMoveTarget(false);
      getParent().setMoveCategoryChild(null);
      return null;
   }

   public boolean getHasChildren() {
      return getBase().getChildPages().size() > 0 ||
            getBase().getChildCategories().size() > 0;
   }
   public DecoratedCategory getCategory()
   {
      return null;
   }
   public boolean isWizard() {
      return false;
   }
}

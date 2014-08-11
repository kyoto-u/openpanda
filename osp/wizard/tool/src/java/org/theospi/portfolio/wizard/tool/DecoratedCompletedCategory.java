/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCompletedCategory.java $
* $Id:DecoratedCompletedCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.model.CompletedWizardCategory;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardCategory;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 9:06:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedCategory {

   private WizardTool parent;
   private DecoratedCategory category;
   private CompletedWizardCategory base;
   private final String classInfo = "completedCategory";

   private List categoryPageList = null;

   public DecoratedCompletedCategory() {
   }

   public DecoratedCompletedCategory(WizardTool parent, DecoratedCategory category, CompletedWizardCategory base) {
      this.parent = parent;
      this.category = category;
      this.base = base;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedCategory getCategory() {
      return category;
   }

   public void setCategory(DecoratedCategory category) {
      this.category = category;
   }

   public CompletedWizardCategory getBase() {
      return base;
   }

   public void setBase(CompletedWizardCategory base) {
      this.base = base;
   }

   public List getCategoryPageList() {

	   getParent().getCurrent().processActionRunWizardHelper();
	   setBase(getParent().getCurrent().getRunningWizard().getBase().getRootCategory());

	   categoryPageList = new ArrayList();
	   addCategoriesPages(categoryPageList);

      return categoryPageList;
   }

   public void setCategoryPageList(List categoryPageList) {
	   this.categoryPageList = categoryPageList;
   }

   protected List addCategoriesPages(List categoryPages) {

	   if (getParent().getCurrent().getBase().getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL)) {
		   for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
			   CompletedWizardCategory category = (CompletedWizardCategory) i.next();
			   DecoratedCategory decoratedCategory = new DecoratedCategory(
					   this.getCategory(), category.getCategory(), getParent(), getCategory().getIndent()+1);
			   DecoratedCompletedCategory completed = new DecoratedCompletedCategory(getParent(), decoratedCategory, category);
			   categoryPages.add(completed);
			   completed.addCategoriesPages(categoryPages);
		   }
	   }

	   for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
		   CompletedWizardPage page = (CompletedWizardPage) i.next();
		   DecoratedWizardPage decoratedPage = new DecoratedWizardPage(this.getCategory(),
				   page.getWizardPageDefinition(), getParent(), getCategory().getIndent()+1);
		   DecoratedCompletedPage completedPage = new DecoratedCompletedPage(getParent(), decoratedPage, page);
		   categoryPages.add(completedPage);
	   }
	   return categoryPages;


   }

   public DecoratedCategoryChild getCategoryChild() {
      return (DecoratedCategoryChild)category;
   }

   public String processActionExpandToggle() {
      getBase().setExpanded(!getBase().isExpanded());
      getParent().getCurrent().getRunningWizard().getRootCategory().setCategoryPageList(null);
      return null;
   }
   
   public String getClassInfo(){
	   return this.classInfo;
   }
   
}

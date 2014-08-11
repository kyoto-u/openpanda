/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedWizardPage.java $
* $Id:DecoratedWizardPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.util.Collections;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 11:44:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizardPage extends DecoratedCategoryChild {

   private DecoratedCategory category = null;
   private WizardPageSequence base;
   private boolean selected = false;

   public DecoratedWizardPage(DecoratedCategory category, WizardPageSequence base, WizardTool parent, int indent) {
      super(parent, indent);
      this.base = base;
      this.category = category;
   }

   public WizardPageSequence getBase() {
      return base;
   }

   public void setBase(WizardPageSequence base) {
      this.base = base;
   }

   public String getTitle() {
      return getBase().getWizardPageDefinition().getTitle();
   }

   /**
    * This can't be concat-ed because it is in html
    * @return String
    */
   public String getDescription() {
      String desc = getBase().getWizardPageDefinition().getDescription();
      //if(desc.length() > 100)
      //   return desc.substring(0, 100) + "...";
      return desc;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getBase().getCategory().getChildPages(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getCategory().resequencePages();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getBase().getCategory().getChildPages().size() - 1) {
         Collections.swap(getBase().getCategory().getChildPages(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getCategory().resequencePages();
      }
      return null;
   }

   public boolean isFirst() {
      return getBase().getSequence() == 0;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getBase().getCategory().getChildPages().size() - 1;
   }
   public boolean isWizard() {
      return false;
   }
   public DecoratedCategory getCategory() {
      return category;
   }

   public void setCategory(DecoratedCategory category) {
      this.category = category;
   }

   public String processActionEdit() {
      return processActionEdit(false);
   }

   public String processExecPage() {
      return getParent().processExecPage(base);
   }

   public String processActionConfirmDelete() {
      getParent().setCurrentPage(this);
      return "confirmDeletePage";
   }

   public String processActionDelete() {
      DecoratedCategory parentCategory = getCategory();
      parentCategory.getBase().getChildPages().remove(getBase());
      parentCategory.resequencePages();
      if(getBase().getId() != null)
         getParent().getDeletedItems().add(getBase());
      return "continue";
   }

   public String processActionEdit(boolean isNew) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, getBase().getWizardPageDefinition());

      if (isNew) {
         session.setAttribute(DecoratedCategory.NEW_PAGE, getBase());
      }

      try {
         context.redirect("osp.wizard.page.def.helper/wizardPageDefinition.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public boolean getHasChildren() {
      return false;
   }
}

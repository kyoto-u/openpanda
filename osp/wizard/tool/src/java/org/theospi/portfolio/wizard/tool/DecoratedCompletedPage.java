/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCompletedPage.java $
* $Id:DecoratedCompletedPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 9:06:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedPage {

   private WizardTool parent;
   private DecoratedWizardPage page;
   private CompletedWizardPage base;
   private final String classInfo = "completedPage";

   public DecoratedCompletedPage() {
   }

   public DecoratedCompletedPage(WizardTool parent, DecoratedWizardPage page, CompletedWizardPage base) {
      this.parent = parent;
      this.page = page;
      this.base = base;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedWizardPage getPage() {
      return page;
   }

   public void setPage(DecoratedWizardPage page) {
      this.page = page;
   }

   public CompletedWizardPage getBase() {
      return base;
   }

   public void setBase(CompletedWizardPage base) {
      this.base = base;
   }

   public DecoratedCategoryChild getCategoryChild() {
      return (DecoratedCategoryChild)page;
   }

   public String processActionEdit() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      WizardPage page = getParent().getMatrixManager().getWizardPage(getBase().getWizardPage().getId());
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, page);
      String redirectAddress = "osp.wizard.page.helper/wizardPage.osp";
      
      if (!parent.getCurrentUserId().equalsIgnoreCase(SessionManager.getCurrentSessionUserId()))
         session.setAttribute("readOnlyMatrix", "true");
      session.setAttribute(WizardPageHelper.WIZARD_OWNER, parent.getCurrent().getRunningWizard().getBase().getOwner());

      if (WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL.equals(
            getBase().getCategory().getWizard().getWizard().getType())) {
         session.setAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES, getPageList());
         session.setAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP,
               Integer.valueOf(getBase().getSequence()));
         redirectAddress = "osp.wizard.page.helper/sequentialWizardPage.osp";
      }

      try {
         context.redirect(redirectAddress);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   protected List getPageList() {
      List pageList = new ArrayList();

      for (Iterator i=getBase().getCategory().getChildPages().iterator();i.hasNext();) {
         CompletedWizardPage page = (CompletedWizardPage) i.next();
         pageList.add(page.getWizardPage());
      }
      return pageList;
   }
   
   public String getClassInfo(){
	   return this.classInfo;
   }
   
   public String getStatusThroughBundle(){
	   return this.getParent().getMessageFromBundle("PAGE_STATUS", 
			   new Object[]{this.getParent().getMessageFromBundle(this.getBase().getWizardPage().getStatus())});
   }
}

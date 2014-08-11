/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCompletedWizard.java $
* $Id:DecoratedCompletedWizard.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import javax.faces.model.SelectItem;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 23, 2006
 * Time: 5:51:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedWizard {

   private WizardTool parent;
   private DecoratedWizard wizard;
   private CompletedWizard base;
   private DecoratedCompletedCategory rootCategory;
   
   private List reflections = null;
   private List evaluations = null;
   private List reviews = null;

   private int submittedPages = 0;
   private List statusArray;
   
   private String changeOption = "thisUserOnly";
   

   public DecoratedCompletedWizard() {

   }

   public DecoratedCompletedWizard(WizardTool parent, DecoratedWizard wizard, CompletedWizard base) {
      this.parent = parent;
      this.wizard = wizard;
      this.base = base;
      if (base != null) {
         setRootCategory(new DecoratedCompletedCategory(
               parent, wizard.getRootCategory(), base.getRootCategory()));
         setSubmittedPages(parent.getWizardManager().getSubmittedPageCount(base));
      }
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedWizard getWizard() {
      return wizard;
   }

   public void setWizard(DecoratedWizard wizard) {
      this.wizard = wizard;
   }

   public CompletedWizard getBase() {
      return base;
   }

   public void setBase(CompletedWizard base) {
      this.base = base;
   }

   public DecoratedCompletedCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(DecoratedCompletedCategory rootCategory) {
      this.rootCategory = rootCategory;
   }
   
   public String processSubmitWizard() {
      getParent().processSubmitWizard(getBase());
      return "submitted";
   }
   
   public List getReflections() {
      if (reflections == null) {
         reflections = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.REFLECTION_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      }
      return reflections;
   }
   public List getEvaluations() {
      if (evaluations == null) {
         evaluations = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.EVALUATION_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      }
      return evaluations;
   }
   public List getReviews() {
      if (reviews == null) {
         reviews = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.FEEDBACK_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      }
      return reviews;
   }
   public boolean getIsReadOnly() {
      Agent completedWizardAgent = getBase().getOwner();
      Agent currentAgent = getParent().getAuthManager().getAgent();
      return !completedWizardAgent.equals(currentAgent);
   }

   public int getSubmittedPages() {
      return submittedPages;
   }

   public void setSubmittedPages(int submittedPages) {
      this.submittedPages = submittedPages;
   }
   
   public List getStatusLists(){	   
	   if(statusArray == null){
		   statusArray = new ArrayList(4);
		   statusArray.add(new SelectItem(MatrixFunctionConstants.READY_STATUS,
				   this.getParent().getMessageFromBundle(MatrixFunctionConstants.READY_STATUS)));
		   statusArray.add(new SelectItem(MatrixFunctionConstants.PENDING_STATUS,
				   this.getParent().getMessageFromBundle(MatrixFunctionConstants.PENDING_STATUS)));
		   statusArray.add(new SelectItem(MatrixFunctionConstants.COMPLETE_STATUS,
				   this.getParent().getMessageFromBundle(MatrixFunctionConstants.COMPLETE_STATUS)));
		   statusArray.add(new SelectItem(MatrixFunctionConstants.LOCKED_STATUS,
				   this.getParent().getMessageFromBundle(MatrixFunctionConstants.LOCKED_STATUS)));
		   statusArray.add(new SelectItem(MatrixFunctionConstants.RETURNED_STATUS,
				   this.getParent().getMessageFromBundle(MatrixFunctionConstants.RETURNED_STATUS)));
	   }
	   return statusArray;
   }
   
   
   public String processManageStatus(){
	     
	   this.getParent().getWizardManager().saveWizard(this.getBase());
	   
   
	   if ("allUsers".equalsIgnoreCase(getChangeOption())) {
		   String status=  this.getBase().getStatus();

		   List<CompletedWizard> allWizs = this.getParent().getWizardManager().getCompletedWizardsByWizardId(this.getBase().getWizard().getId().toString());
		   for (Iterator<CompletedWizard> iter = allWizs.iterator(); iter.hasNext();) {
			   CompletedWizard iterWiz = (CompletedWizard) iter.next();
			   iterWiz.setStatus(status);
			   this.getParent().getWizardManager().saveWizard(iterWiz);
		   }
	   }

	   return "runWizard";
   }


public String getChangeOption() {
	return changeOption;
}

public void setChangeOption(String changeOption) {
	this.changeOption = changeOption;
}
   

}

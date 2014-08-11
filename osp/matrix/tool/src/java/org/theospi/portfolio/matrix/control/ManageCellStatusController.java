/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ManageCellStatusController.java $
* $Id: ManageCellStatusController.java 70140 2009-12-18 20:33:49Z bkirschn@umich.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;

public class ManageCellStatusController implements Controller {

   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private LockManager lockManager = null;
   private ReviewManager reviewManager = null;
   private AgentManager agentManager = null;
   private WizardManager wizardManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      String viewAppend = "";
      Id id = idManager.getId((String)request.get("page_id"));
      
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("page_id", id);
      WizardPage page = getMatrixManager().getWizardPage(id);
      
      List<String> statusArray = new ArrayList<String>(4);
      statusArray.add(MatrixFunctionConstants.READY_STATUS);
      statusArray.add(MatrixFunctionConstants.PENDING_STATUS);
      statusArray.add(MatrixFunctionConstants.COMPLETE_STATUS);
      statusArray.add(MatrixFunctionConstants.LOCKED_STATUS);
      statusArray.add(MatrixFunctionConstants.RETURNED_STATUS);
      
      model.put("statuses", statusArray);
      model.put("readOnlyMatrix", (String)request.get("readOnlyMatrix"));
      
      Site site = null;
      try {
    	  site = SiteService.getSite(page.getPageDefinition().getSiteId());
    	  
    	  if (site.hasGroups())
    		  model.put("groups", getMatrixManager().getGroupList(site, false));
    	  
      } catch (IdUnusedException e) {
    	  // TODO Auto-generated catch block
    	  e.printStackTrace();
      }
      
      
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      
      String changeOption = (String)request.get("changeOption");
      
      boolean setSingle = "changeUserOnly".equalsIgnoreCase(changeOption) ? true : false;
      boolean setAll = "changeAll".equalsIgnoreCase(changeOption) ? true : false;
      boolean setGroup = "changeGroup".equalsIgnoreCase(changeOption) ? true : false;
      String groupId = (String)request.get("groupId");
      String newStatusValue = (String)request.get("newStatusValue");
      
      String isWizard = (String)request.get("isWizard");
      String sequential = (String)request.get("sequential");
      if (isWizard != null) {
         model.put("isWizard", isWizard);
      }

      if (sequential == null || sequential.equals("")) {
         sequential = "false";
      }
      if (sequential != null) {
         model.put("sequential", sequential);
         if (Boolean.parseBoolean(isWizard) && !Boolean.parseBoolean(sequential)) {
            viewAppend = "Hier";
         }
      }
      
      if (cancel != null) {
         viewName = "done";
      }
      else if (next != null && setSingle) {
         viewName = "done" + viewAppend;
         setPageStatus(page, newStatusValue);
      }
      else if (next != null && setAll) {
    	 ensureUserDataExists(page.getPageDefinition().getSiteId(), page, null);
         List<WizardPage> allPages = getMatrixManager().getPagesByPageDef(page.getPageDefinition().getId());
         viewName = "done" + viewAppend;
         for (WizardPage iterPage : allPages) {
            setPageStatus(iterPage, newStatusValue);
         }
      }
      else if (next != null && setGroup && groupId != null) {
     	 ensureUserDataExists(site.getId(), page, groupId);
     	 Group group = site.getGroup(groupId);
     	 boolean ungrouped = group == null && groupId.equalsIgnoreCase("ungrouped");
     	  List<WizardPage> allPages = getMatrixManager().getPagesByPageDef(page.getPageDefinition().getId());
          viewName = "done" + viewAppend;
          for (WizardPage iterPage : allPages) {
        	  String userId = iterPage.getOwner().getId().getValue();
             if ((ungrouped && site.getGroupsWithMember(userId).isEmpty()) || (group != null && group.getMember(userId) != null)) {
            	 setPageStatus(iterPage, newStatusValue);
             }
          }
       }

      session.put(WizardPageHelper.WIZARD_OWNER, page.getOwner());
      return new ModelAndView(viewName, model);
   }
   
   protected void setPageStatus(WizardPage page, String status) {
      //Set the status only if it needs to be changed
      if (!page.getStatus().equals(status)) {
         page.setStatus(status);
         getMatrixManager().storePage(page);
         if (status.equals(MatrixFunctionConstants.READY_STATUS)) {
            //Unlock page's content
            for (Iterator<Attachment> iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            for (Iterator<WizardPageForm> iter2 = page.getPageForms().iterator(); iter2.hasNext();) {
               WizardPageForm form = (WizardPageForm) iter2.next();
               getLockManager().removeLock(form.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            //unlock reflection form too 
            List<Review> reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
                  MatrixContentEntityProducer.MATRIX_PRODUCER);
            for (Iterator<Review> iter3 = reflections.iterator(); iter3.hasNext();) {
               Review review = (Review)iter3.next();
               getLockManager().removeLock(review.getReviewContent().getValue(), 
                     page.getId().getValue());
            }
         }
         else {
            //lock everything
            for (Iterator<Attachment> iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().lockObject(att.getArtifactId().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
            for (Iterator<WizardPageForm> iter2 = page.getPageForms().iterator(); iter2.hasNext();) {
               WizardPageForm form = (WizardPageForm) iter2.next();
               getLockManager().lockObject(form.getArtifactId().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
            //lock reflection form too 
            List<Review> reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
                  MatrixContentEntityProducer.MATRIX_PRODUCER);
            for (Iterator<Review> iter3 = reflections.iterator(); iter3.hasNext();) {
               Review review = (Review)iter3.next();
               getLockManager().lockObject(review.getReviewContent().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
         }
      }
   }
   
   protected void ensureUserDataExists(String siteId, WizardPage page, String groupId) {
	   String type = page.getPageDefinition().getType();
	   try {
		   Site site = SiteService.getSite(siteId);
		   Set<Member> members = new HashSet<Member>();
		   if (groupId == null) {
			   members = site.getMembers();
		   }
		   else {
			   Group group = site.getGroup(groupId);
			   if (group != null) {
				   members = group.getMembers();
			   }
		   }

		   if (WizardPageDefinition.WPD_MATRIX_TYPE.equals(type)) {
			   createMissingMatrices(members, page);
		   }
		   else {
			   createMissingCompletedWizards(members, page);
		   }
	   } catch (IdUnusedException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
   
   protected void createMissingMatrices(Set<Member> members, WizardPage page) {
	   ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(page.getPageDefinition().getId());
	   Set<Agent> agentsToCreate = getAgentsWithNoMatrix(convertMembersToAgents(members), sCell.getScaffolding().getId());
	   for (Agent agent : agentsToCreate) {
		   getMatrixManager().createMatrix(agent, sCell.getScaffolding());
	   }
   }
   
   protected void createMissingCompletedWizards(Set<Member> members, WizardPage page) {
	   CompletedWizard cw = getWizardManager().getCompletedWizardByPage(page.getId());
	   Set<Agent> agentsToCreate = getAgentsWithNoWizard(convertMembersToAgents(members), cw.getWizard().getId());
	   for (Agent agent : agentsToCreate) {
		   getWizardManager().getCompletedWizard(cw.getWizard(), agent.getId().getValue(), true);
	   }
   }
   
   protected Set<Agent> convertMembersToAgents(Set<Member> members) {
	   Set<Agent> agents = new HashSet<Agent>(members.size());
	   for (Member member : members) {
		   Agent agent = getAgentManager().getAgent(member.getUserId());
		   agents.add(agent);
	   }
	   return agents;
   }
   
	/**
	 * Lookup all matrices and then remove the ones found from the passed agents list
	 * @param agents All current members of the site containing the passed scaffolding id
	 * @param scaffoldingId
	 * @return
	 */
   protected Set<Agent> getAgentsWithNoMatrix(Set<Agent> agents, Id scaffoldingId) {
	   Set<Agent> agentsLeft = agents;
	   List<Matrix> matrices = getMatrixManager().getMatrices(scaffoldingId);
	   for (Matrix matrix : matrices) {
		   agentsLeft.remove(matrix.getOwner());
	   }
	   return agentsLeft;
   }
   
	/**
	 * Lookup all completed wizards and then remove the ones found from the passed agents list
	 * @param agents All current members of the site containing the passed wizard id
	 * @param wizardId
	 * @return
	 */
  protected Set<Agent> getAgentsWithNoWizard(Set<Agent> agents, Id wizardId) {
	   Set<Agent> agentsLeft = agents;
	   List<CompletedWizard> cWizards = getWizardManager().getCompletedWizardsByWizardId(wizardId.getValue());
	   for (CompletedWizard cw : cWizards) {
		   agentsLeft.remove(cw.getOwner());
	   }
	   return agentsLeft;
  }

   public IdManager getIdManager() {
      return idManager;
   }
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * @return the reviewManager
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager the reviewManager to set
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   public AgentManager getAgentManager() {
	   return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
	   this.agentManager = agentManager;
   }

   public WizardManager getWizardManager() {
	   return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
	   this.wizardManager = wizardManager;
   }
}

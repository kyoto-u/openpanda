/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/matrix/api-impl/src/java/org/theospi/portfolio/workflow/impl/WorkflowManagerImpl.java $
* $Id: WorkflowManagerImpl.java 68687 2009-11-09 16:45:06Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.workflow.impl;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;

public class WorkflowManagerImpl extends HibernateDaoSupport implements WorkflowManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   
   public Workflow createNew(String description, String siteId, Id securityQualifier, String securityViewFunction, String securityEditFunction) {
      // TODO Auto-generated method stub
      return null;
   }

   public Workflow getWorkflow(Id workflowId) {
      Workflow workflow = (Workflow)getHibernateTemplate().get(Workflow.class, workflowId);

      if (workflow == null) {
         return null;
      }
/*
      if (workflow.getSecurityQualifier() != null) {
         getAuthorizationFacade().checkPermission(workflow.getSecurityViewFunction(),
               workflow.getSecurityQualifier());
      }
*/
      return workflow;
   }

   public Workflow saveWorkflow(Workflow workflow) {
      if (workflow.isNewObject()) {
         workflow.setNewId(workflow.getId());
         workflow.setId(null);
         getHibernateTemplate().save(workflow);
         workflow.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(workflow);
      }

      return workflow;
   }

   public void deleteWorkflow(Workflow workflow) {
      //for(Iterator iter = workflow.getItems().iterator(); iter.hasNext();) {
      //   WorkflowItem item = (WorkflowItem)iter.next();
      //   getHibernateTemplate().delete(item);
      //}
      workflow.getItems().clear();
      getHibernateTemplate().delete(workflow);
      //getHibernateTemplate().
      
   }

   public Reference decorateReference(Workflow workflow, String reference) {
      // TODO Auto-generated method stub
      return null;
   }

   public Workflow getWorkflow(String id) {
      return getWorkflow(getIdManager().getId(id));
   }
   
   public Set createEvalWorkflows(ObjectWithWorkflow obj) {
	   return createEvalWorkflows(obj, obj.getEvaluationDevice());
   }
   
   public Set createEvalWorkflows(ObjectWithWorkflow obj, Id evalId) {
      if (obj instanceof WizardPageDefinition)
         return createEvalWorkflowsHelper(obj, evalId);
      else
         return createEvalWorkflowsWizard((Wizard)obj);
   }
   
   protected Set createEvalWorkflowsHelper(ObjectWithWorkflow wpd, Id eval) {
      Set workflows = wpd.getEvalWorkflows();
      if (validEval(eval) && workflows.size() == 0) {
         Workflow w_none = new Workflow("No Workflow", wpd);
         Workflow w_complete = new Workflow("Complete Workflow", wpd);
         Workflow w_return = new Workflow("Return Workflow", wpd);
         Workflow w_returned = new Workflow("Returned Workflow", wpd);
         
         Id id = wpd.getId() != null ? wpd.getId() : wpd.getNewId();
         
         w_complete.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
               id, MatrixFunctionConstants.COMPLETE_STATUS));
         w_return.add(new WorkflowItem(WorkflowItem.CONTENT_LOCKING_WORKFLOW, 
               id, WorkflowItem.CONTENT_LOCKING_UNLOCK));
         w_return.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
               id, MatrixFunctionConstants.READY_STATUS));
         w_returned.add(new WorkflowItem(WorkflowItem.CONTENT_LOCKING_WORKFLOW, 
                 id, WorkflowItem.CONTENT_LOCKING_UNLOCK));
         w_returned.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
                 id, MatrixFunctionConstants.RETURNED_STATUS));
         
         workflows.add(w_none);
         workflows.add(w_complete);
         workflows.add(w_return);
         workflows.add(w_returned);
         
      }
      else if (validEval(eval)) {
         workflows = new HashSet();
      }
      return workflows;
   }
   
   protected boolean validEval(Id eval) {
	   boolean retVal = false;
	   if (eval != null && eval.getValue() != null && !eval.getValue().equals("")) {
		   retVal = true;
	   }
	   return retVal;
   }
   
   protected Set createEvalWorkflowsWizard(Wizard wizard) {
      Set workflows = wizard.getEvalWorkflows();
      Id eval = wizard.getEvaluationDevice();
      /*
      for (Iterator iter = wizard.getSupportItems().iterator(); iter.hasNext();) {
         WizardSupportItem wsi = (WizardSupportItem)iter.next();
         String type = wsi.getGenericType();
         if (type.equals(WizardFunctionConstants.EVALUATION_TYPE)) {            
            eval = wsi.getContentType();
            break;
         }
      }      
      */
      if (validEval(eval) && workflows.size() == 0) {
         Workflow w_none = new Workflow("No Workflow", wizard);
         Workflow w_complete = new Workflow("Complete Workflow", wizard);
         Workflow w_return = new Workflow("Return Workflow", wizard);
         Workflow w_returned = new Workflow("Returned Workflow", wizard);
         
         Id id = wizard.getId() != null ? wizard.getId() : wizard.getNewId();
         
         w_complete.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
               id, MatrixFunctionConstants.COMPLETE_STATUS));
         w_return.add(new WorkflowItem(WorkflowItem.CONTENT_LOCKING_WORKFLOW, 
               id, WorkflowItem.CONTENT_LOCKING_UNLOCK));
         w_return.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
               id, MatrixFunctionConstants.READY_STATUS));
         w_returned.add(new WorkflowItem(WorkflowItem.CONTENT_LOCKING_WORKFLOW, 
                 id, WorkflowItem.CONTENT_LOCKING_UNLOCK));
         w_returned.add(new WorkflowItem(WorkflowItem.STATUS_CHANGE_WORKFLOW, 
                 id, MatrixFunctionConstants.RETURNED_STATUS));
           
         workflows.add(w_none);
         workflows.add(w_complete);
         workflows.add(w_return);
         workflows.add(w_returned);
      }
      else if (!validEval(eval)) {
         workflows = new HashSet();
      }
      return workflows;
   }

   /**
    * @return Returns the authorizationFacade.
    */
   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   /**
    * @param authorizationFacade The authorizationFacade to set.
    */
   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return Returns the securityService.
    */
   public SecurityService getSecurityService() {
      return securityService;
   }

   /**
    * @param securityService The securityService to set.
    */
   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }
}

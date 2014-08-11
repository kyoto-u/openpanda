/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api-impl/src/java/org/theospi/portfolio/matrix/MatrixAuthorizer.java $
* $Id:MatrixAuthorizer.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix;

import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;

import java.util.Iterator;
import java.util.List;



/**
 * @author rpembry
 *         <p/>
 *         <p/>
 *         createAuth(reviewer, "review", cellId) when a cell is submitted for review
 *         call listAuth(reviewer, "review", null) to find all the cells to review
 *         isAuth(review, "review", cellId) when a reviewer attempts to review a cell
 *         Node/Repository impl will callback here to see if there is locked content that prohibits edits or deletes.
 * @author rpembry
 */
public class MatrixAuthorizer implements ApplicationAuthorizer {
   
   private MatrixManager matrixManager;
   private AuthorizationFacade explicitAuthz;
   private IdManager idManager;

   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(getClass());
   protected List functions;


   /* (non-Javadoc)
    * @see org.theospi.portfolio.security.app.ApplicationAuthorizer#isAuthorized(org.theospi.portfolio.security.AuthorizationFacade, org.theospi.portfolio.shared.model.Agent, java.lang.String, org.theospi.portfolio.shared.model.Id)
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent,
                               String function, Id id) {
      logger.debug("isAuthorized?(...) invoked in MatrixAuthorizer");
      
      
      
      if (MatrixFunctionConstants.EVALUATE_MATRIX.equals(function) ||
            MatrixFunctionConstants.REVIEW_MATRIX.equals(function)) {
         return Boolean.valueOf(facade.isAuthorized(function,id));
      }
      else if (MatrixFunctionConstants.DELETE_SCAFFOLDING_ANY.equals(function)) {
    	  Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
    	  if (scaffolding == null)
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));

    	  if (!scaffolding.isPublished() && (scaffolding.getOwner().equals(agent)) || 
    			  facade.isAuthorized(agent,function,scaffolding.getWorksiteId()))
    		  return Boolean.valueOf(true);
      }else if(MatrixFunctionConstants.DELETE_SCAFFOLDING_OWN.equals(function)) {
    	  Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
    	  if (scaffolding == null)
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));

    	  if(scaffolding.getOwner().equals(agent)){
    		  if (!scaffolding.isPublished() || 
    				  facade.isAuthorized(agent,function,scaffolding.getWorksiteId()))
    			  return Boolean.valueOf(true);
    	  }
      }
      else if (ContentHostingService.EVENT_RESOURCE_READ.equals(function)) {
         return isFileAuth(facade, agent, id);
      }
      else if (function.equals(MatrixFunctionConstants.CREATE_SCAFFOLDING)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.REVISE_SCAFFOLDING_ANY)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      }else if(function.equals(MatrixFunctionConstants.REVISE_SCAFFOLDING_OWN)) {
    	  Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
    	  if (scaffolding == null)
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));

    	  if(scaffolding.getOwner().equals(agent)){
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));
    	  }
      }
      else if (function.equals(MatrixFunctionConstants.EXPORT_SCAFFOLDING_ANY)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.EXPORT_SCAFFOLDING_OWN)) {
    	  Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
    	  if (scaffolding == null)
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));

    	  if(scaffolding.getOwner().equals(agent)){
    		  return Boolean.valueOf(facade.isAuthorized(agent,function,id));
    	  }
       }
      else if (function.equals(MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE)) {
    	  return Boolean.valueOf(true);
      }
      else if (function.equals(MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE)) {
         ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(id);
         Agent owner = null;
         if (sCell != null) {
            owner = sCell.getScaffolding().getOwner();
         }
         return Boolean.valueOf(agent.equals(owner));
      }
      else if (function.equals(MatrixFunctionConstants.EVALUATE_SPECIFIC_MATRIXCELL)) {
         WizardPage page = getMatrixManager().getWizardPage(id);
         Id siteId = getIdManager().getId(page.getPageDefinition().getSiteId());
//       make sure that the target site gets tested
         
         facade.pushAuthzGroups(siteId.getValue());
         return Boolean.valueOf(facade.isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, siteId));
      }
      else if (function.equals(MatrixFunctionConstants.ACCESS_ALL_CELLS) ||
    		  function.equals(MatrixFunctionConstants.VIEW_EVAL_OTHER) ||
    		  function.equals(MatrixFunctionConstants.VIEW_FEEDBACK_OTHER) ||
    		  function.equals(MatrixFunctionConstants.MANAGE_STATUS) ||
    		  function.equals(MatrixFunctionConstants.ACCESS_USERLIST) ||
    		  function.equals(MatrixFunctionConstants.VIEW_ALL_GROUPS) ||
    		  function.equals(MatrixFunctionConstants.CAN_USE_SCAFFOLDING)) {
    	  return Boolean.valueOf(SecurityService.unlock(agent.getId().getValue(),function,id.getValue()));
    	  //return Boolean.valueOf(getExplicitAuthz().isAuthorized(agent,function,id));
      }
            
      return null;  //don't care
   }

   protected boolean checkPerms(AuthorizationFacade facade, String[] functions, Id qualifier) {
      for (int i=0;i<functions.length;i++) {
         if (facade.isAuthorized(functions[i], qualifier)) {
            return true;
         }
      }
      return false;
   }

   public Boolean isFileAuth(AuthorizationFacade facade, Agent agent, Id artifactId) {
      // check if this id is attached to any cell
      if (artifactId == null)
         return Boolean.valueOf(true);

      List cells = getMatrixManager().getCellsByArtifact(artifactId);

      if (cells.size() == 0) {
         return null;
      }
      ScaffoldingCell sCell = ((Cell) cells.get(0)).getScaffoldingCell();
      
      if((sCell.isDefaultEvaluators() && getExplicitAuthz().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getScaffolding().getId())) ||
    		  (!sCell.isDefaultEvaluators() && getExplicitAuthz().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getId())) ||
    		  (sCell.isDefaultReviewers() && getExplicitAuthz().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getScaffolding().getId())) ||
    		  (!sCell.isDefaultReviewers() && getExplicitAuthz().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getId())) ||
    		  (getExplicitAuthz().isAuthorized(agent, MatrixFunctionConstants.ACCESS_ALL_CELLS, getIdManager().getId(sCell.getScaffolding().getReference())))){
    	  return Boolean.valueOf(true);
      }

      return null;
   }
   
   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public AuthorizationFacade getExplicitAuthz() {
      return explicitAuthz;
   }

   public void setExplicitAuthz(AuthorizationFacade explicitAuthz) {
      this.explicitAuthz = explicitAuthz;
   }

   /**
    * @return the idManager
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager the idManager to set
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

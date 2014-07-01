
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ViewScaffoldingController.java $
* $Id: ViewScaffoldingController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;

/**
 * @author chmaurer
 */
public class ViewScaffoldingController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private IdManager idManager;
   private WorksiteManager worksiteManager = null;
   private AuthorizationFacade authzManager;
   private ToolManager toolManager;
   private WorkflowManager workflowManager;
   private StyleManager styleManager;
   private AuthenticationManager authManager;

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      
      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      
      Id sId = getIdManager().getId((String)request.get("scaffolding_id"));
      Scaffolding scaffolding = getMatrixManager().getScaffolding(sId);
      
      List<Level> levels = scaffolding.getLevels();
      List<Criterion> criteria = scaffolding.getCriteria();
      List matrixContents = new ArrayList();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List<ScaffoldingCell> row = new ArrayList<ScaffoldingCell>();

      Set<ScaffoldingCell> cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());
       
      for (Iterator<Criterion> criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row = new ArrayList<ScaffoldingCell>();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator<Level> levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);

            row.add(scaffoldingCell);
         }
         matrixContents.add(row);
      }
      
      grid.setScaffolding(scaffolding);
      grid.setColumnLabels(levels);
      grid.setRowLabels(criteria);
      grid.setMatrixContents(matrixContents);
      
      //Make sure these are not in session.
      session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      
      return incomingModel;
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      
	   Agent currentAgent = getAuthManager().getAgent();
	   Map model = new HashMap();
      
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("osp_agent", currentAgent);
      model.put("tool", getToolManager().getCurrentPlacement());
      
      model.put("styles",
 	         getStyleManager().createStyleUrlList(getStyleManager().getStyles(((MatrixGridBean)command).getScaffolding().getId())));
      
      return model;
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request,
         Map session, Map application, Errors errors) {
      Map model = new HashMap();
      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "false");
      
      if(request.get("toolPermissionSaved") != null)
    	  model.put("toolPermissionSaved", request.get("toolPermissionSaved"));
      return new ModelAndView("success", model);
   }
   
   private ScaffoldingCell getScaffoldingCell(Set<ScaffoldingCell> cells, Criterion criterion, Level level) {
      for (Iterator<ScaffoldingCell> iter=cells.iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         if (scaffoldingCell.getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               scaffoldingCell.getLevel().getId().getValue().equals(level.getId().getValue())) {
            return scaffoldingCell;
         }
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
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   /**
    * @return Returns the authzManager.
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }
   /**
    * @param authzManager The authzManager to set.
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   /**
    * @return Returns the workflowManager.
    */
   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   /**
    * @param workflowManager The workflowManager to set.
    */
   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }

   public StyleManager getStyleManager() {
	   return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
	   this.styleManager = styleManager;
   }

public AuthenticationManager getAuthManager() {
	return authManager;
}

public void setAuthManager(AuthenticationManager authManager) {
	this.authManager = authManager;
}
}

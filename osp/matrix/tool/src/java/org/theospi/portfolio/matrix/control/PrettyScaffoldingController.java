
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/PrettyScaffoldingController.java $
* $Id: PrettyScaffoldingController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2007, 2008 The Sakai Foundation
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.review.mgt.ReviewManager;


/**
 * @author chmaurer
 */
public class PrettyScaffoldingController extends BaseScaffoldingController 
   implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private WorksiteManager worksiteManager = null;
   private AuthenticationManager authManager = null;
   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private ReviewManager reviewManager;


   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map<String, Object> model = new HashMap<String, Object>();

      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      
      Scaffolding scaffolding = null;
      if ( command instanceof Scaffolding )
         scaffolding = (Scaffolding)command;
      
      if ( scaffolding != null )
         model.put("isMatrixUsed", scaffolding.isPublished() && getMatrixManager().isScaffoldingUsed( scaffolding ) );
      else
         model.put("isMatrixUsed", false );
      
      return model;
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      
      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      
      Id sId = getIdManager().getId((String)request.get("scaffolding_id"));
      Scaffolding scaffolding = getMatrixManager().getScaffolding(sId);
      
      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();
      List<List> matrixContents = new ArrayList<List>();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List<ScaffoldingCell> row = new ArrayList<ScaffoldingCell>();

      Set cells = scaffolding.getScaffoldingCells();
       
      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row.clear(); // = new ArrayList<ScaffoldingCell>();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
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
   
   private ScaffoldingCell getScaffoldingCell(Set cells, Criterion criterion, Level level) {
      for (Iterator iter=cells.iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         if (scaffoldingCell.getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               scaffoldingCell.getLevel().getId().getValue().equals(level.getId().getValue())) {
            return scaffoldingCell;
         }
      }
      return null;
   }
   

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String action = (String) request.get("action");
      if (action == null) action = (String) request.get("submitAction");
      String generateAction = (String)request.get("generateAction");
      String cancelAction = (String)request.get("cancelAction");
      
      Id worksiteId = worksiteManager.getCurrentWorksiteId();
      Map<String, Object> model = new HashMap<String, Object>();
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      scaffolding.setWorksiteId(worksiteId);
      
      scaffolding.setOwner(authManager.getAgent());
      
      if (generateAction != null) {
         if (scaffolding.isPublished()) {                              
            return new ModelAndView("editScaffoldingConfirm");             
         }           
         
         scaffolding = saveScaffolding(scaffolding);
         session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
         session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         model.put("scaffolding_id", scaffolding.getId());
         return new ModelAndView("view", model);
      }
      if (cancelAction != null) {
         return new ModelAndView("return");
      }
      
      if (action != null) {
         if (action.equals("forward")) {
            String forwardView = (String)request.get("dest");
            model.put("label", request.get("label"));
            model.put("finalDest", request.get("finalDest"));
            model.put("displayText", request.get("displayText"));
            String params = (String)request.get("params");
            model.put("params", params);
            if (!params.equals("")) {
               String[] paramsList = params.split(":");
               for (int i=0; i<paramsList.length; i++) {
                  String[] pair = paramsList[i].split("=");
                  String val = null;
                  if (pair.length>1)
                     val = pair[1];
                  model.put(pair[0], val);
               }
            }
            //matrixManager.storeScaffolding(scaffolding);
            
            //touchAllCells(scaffolding);
            sessionBean.setScaffolding(scaffolding);
            model.put("scaffolding_id", scaffolding.getId());
            
            return new ModelAndView(forwardView, model);
            
         }
      }
      return new ModelAndView("success");
   }
/*
   private void touchAllScaffolding(Scaffolding scaffolding) {
	  scaffolding.getLevels().size();
 	  scaffolding.getCriteria().size();
 	 for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
         //sCell.getExpectations().size();
      }
   }
*/
   protected void touchAllCells(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
      }
      
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
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }
   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }


   public ContentHostingService getContentHosting() {
      return contentHosting;
   }


   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
   
   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

  
}

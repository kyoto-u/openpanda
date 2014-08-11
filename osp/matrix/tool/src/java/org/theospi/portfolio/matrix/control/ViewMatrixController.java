/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ViewMatrixController.java $
* $Id:ViewMatrixController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.FunctionConstants;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.assignment.AssignmentHelper;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

import org.springframework.transaction.annotation.Transactional; 

public class ViewMatrixController extends AbstractMatrixController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private SecurityService securityService;
   
   public static final String VIEW_USER = "view_user";
   
   public static final String GROUP_FILTER = "group_filter";
   
   public static final String GROUP_FILTER_BUTTON = "filter";
      
   private static int MATRIX_ROW_FOOTER = 10;
	
   private ToolManager toolManager;
   private StyleManager styleManager;
   private ReviewManager reviewManager;
   
   private WizardActivityProducer wizardActivityProducer;
   
   @Transactional
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {

      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      String strScaffoldingId = (String)request.get("scaffolding_id");
      
      if (strScaffoldingId == null) {
         Placement placement = getToolManager().getCurrentPlacement();
         strScaffoldingId = placement.getPlacementConfig().getProperty(
               MatrixManager.EXPOSED_MATRIX_KEY);
      }
      
      Id scaffoldingId = getIdManager().getId(strScaffoldingId);
      Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);
      // Check for invalid scaffolding (could happen if scaffolding is deleted)
      if (scaffolding == null )
      {
         logger.warn("Unable to find scaffolding: " + scaffoldingId );
         return incomingModel;
      }

      Agent currentAgent = getAuthManager().getAgent();
      boolean createAuthz = false;

      String filterButton = (String)request.get(GROUP_FILTER_BUTTON);

      String groupFilterRequest = (String)request.get(GROUP_FILTER);
      String groupFilterSession = (String)session.get(GROUP_FILTER);
      if (groupFilterRequest != null && filterButton != null) {
    	  //TODO: Check that this user can filter on this group
      }
      else if (groupFilterSession != null) {
    	  groupFilterRequest = groupFilterSession;
    	  //TODO: Check if there is a better way to shuttle this to referenceData without modding the bean
          request.put(GROUP_FILTER, groupFilterRequest);
      }
      session.put(GROUP_FILTER, groupFilterRequest);

      
      //TODO: Check to make sure that the session user is in the filtered group
		//If the user is, apply filter and select the user
	    //If not, apply and select the active user
        //For right now, we're resetting when filtering
      
      String userRequest = (String)request.get(VIEW_USER);
      String userSession = (String)session.get(VIEW_USER);
      if (groupFilterRequest != null && filterButton != null) {
    	  userRequest = null;
      }
      else {
	      if (userRequest != null) {
	         currentAgent = getAgentManager().getAgent(getIdManager().getId(userRequest));
	         createAuthz = true;
	      } else if(userSession != null) {
	         userRequest = userSession;
	         currentAgent = getAgentManager().getAgent(getIdManager().getId(userSession));
	         // The authorize was already created by this point
	      }
      }
      session.put(VIEW_USER, userRequest);
      
      Matrix matrix = getMatrixManager().getMatrix(scaffoldingId, currentAgent.getId());
      if (matrix == null) {
         if (currentAgent != null && !currentAgent.getId().getValue().equals("")) {
            //Don't create a matrix unless the scaffolding has been published 
            // and the user has permission to use a matrix.
            if (scaffolding.isPublished() || scaffolding.isPreview()) {
               matrix = getMatrixManager().createMatrix(currentAgent, scaffolding);
            }
         }
      }
      
      if (matrix == null) {
         grid.setScaffolding(scaffolding);
         return incomingModel;
      }

      scaffolding = matrix.getScaffolding();
      
      if (createAuthz) {
         getAuthzManager().createAuthorization(getAuthManager().getAgent(), 
                 FunctionConstants.READ_MATRIX, matrix.getId());
      }

      List<Level> levels = scaffolding.getLevels();
      List<Criterion> criteria = scaffolding.getCriteria();
      List matrixContents = new ArrayList();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List row = new ArrayList();
      
      List<Cell> cells = getMatrixManager().getCells(matrix);
       
      List<Review> reviews = getReviewManager().getReviewsByMatrix(matrix.getId().getValue());
      for (Iterator<Criterion> criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row = new ArrayList();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator<Level> levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            CellFormBean cellBean = new CellFormBean();

            Cell cell = getCell(cells, criterion, level);
            if (cell == null) {
               cell = new Cell();
               cell.getWizardPage().setOwner(matrix.getOwner());
               cell.setMatrix(matrix);
               ScaffoldingCell scaffoldingCell = getMatrixManager().getScaffoldingCell(criterion, level);
               cell.setScaffoldingCell(scaffoldingCell);
               cell.setStatus(scaffoldingCell.getInitialStatus());
               getMatrixManager().storeCell(cell);
            }
            List nodeList = new ArrayList(getMatrixManager().getPageContents(cell.getWizardPage()));
            nodeList.addAll(getMatrixManager().getPageForms(cell.getWizardPage()));
            cellBean.setCell(cell);
            cellBean.setNodes(nodeList);
            cellBean.setAssignments(getAssignments(cell.getWizardPage(), matrix.getOwner()));

            String pageId = cell.getWizardPage().getId().getValue();
            String siteId = cell.getWizardPage().getPageDefinition().getSiteId();
            List reflections = new ArrayList();
            List feedback = new ArrayList();
            List evaluations = new ArrayList();
            for (Review r : reviews) {
                if (pageId.equals(r.getParent())) {
                    if (Review.REFLECTION_TYPE == r.getType()) {
                        reflections.add(r);
                    } else if (Review.FEEDBACK_TYPE == r.getType()) {
                        feedback.add(r);
                    } else if (Review.EVALUATION_TYPE == r.getType()) {
                        evaluations.add(r);
                    }
                }
            }
            cellBean.setReflections(reflections);
            cellBean.setReviews(feedback);
            cellBean.setEvaluations(evaluations);

            if (getMatrixManager().getTaggingManager().isTaggable()) {
    			TaggableItem item = wizardActivityProducer.getItem(cell.getWizardPage());
    			
    			Set<TaggableItem> taggableItems = getMatrixManager().getTaggableItems(item, cell.getWizardPage().getPageDefinition().getReference(), cell.getWizardPage().getOwner().getId().getValue());
    			cellBean.setTaggableItems(taggableItems);    			
    		}            
            row.add(cellBean);
         }
         matrixContents.add(row);
      }


      grid.setMatrixId(matrix.getId());
      grid.setMatrixOwner(matrix.getOwner());
      grid.setScaffolding(scaffolding);
      grid.setColumnLabels(levels);
      grid.setRowLabels(criteria);
      grid.setMatrixContents(matrixContents);

      return incomingModel;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   @Transactional
   public Map referenceData(Map request, Object command, Errors errors) {
      Map<String, Object> model = new HashMap<String, Object>();
      MatrixGridBean grid = (MatrixGridBean) command;      
      Agent owner = grid.getMatrixOwner();
      Boolean readOnly = Boolean.valueOf(false);
      String worksiteId = grid.getScaffolding().getWorksiteId().getValue();

      String filteredGroup = (String) request.get(GROUP_FILTER);
      boolean allowAllGroups = getAuthzManager().isAuthorized(getAuthManager().getAgent(), MatrixFunctionConstants.VIEW_ALL_GROUPS, getIdManager().getId(grid.getScaffolding().getReference()));
      List<Group> groupList = new ArrayList<Group>(getMatrixManager().getGroupList(worksiteId, allowAllGroups));
      //Collections.sort(groupList);
      //TODO: Figure out why ClassCastExceptions fire if we do this the obvious way...  The User list sorts fine
      Collections.sort(groupList, new Comparator<Group>() {
    	  public int compare(Group arg0, Group arg1) {
    		  return arg0.getTitle().toLowerCase().compareTo(arg1.getTitle().toLowerCase());
    	  }});
      
      List userList = new ArrayList(getMatrixManager().getUserList(worksiteId, filteredGroup, allowAllGroups, groupList));
		
      Collections.sort(userList, new Comparator<User>(){

		public int compare(User arg0, User arg1) {
			return arg0.getSortName().compareToIgnoreCase(arg1.getSortName());
		}
      });
      
      model.put("members", userList);
      model.put("userGroups", groupList);
      //TODO: Address why the fn:length() function can't be loaded or another handy way to pull collection size via EL
      model.put("userGroupsCount", groupList.size());
      model.put("hasGroups", getMatrixManager().hasGroups(worksiteId));
      model.put("filteredGroup", request.get(GROUP_FILTER));
      //TODO: Clean this up for efficiency.. We're going back to the SiteService too much
      
      if ((owner != null && !owner.equals(getAuthManager().getAgent())) ||
           !getAuthzManager().isAuthorized(MatrixFunctionConstants.CAN_USE_SCAFFOLDING, getIdManager().getId(grid.getScaffolding().getReference())))
         readOnly = Boolean.valueOf(true);

      model.put("worksite", worksiteId );
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());      

      model.put("matrixOwner", owner);      
      model.put("readOnlyMatrix", readOnly);
      
      if (grid.getScaffolding() != null &&
          getCurrentSitePageId().equals(grid.getScaffolding().getExposedPageId())) 
      {
         model.put("isExposedPage", Boolean.valueOf(true));
      }
      
      model.put("styles",
    	         getStyleManager().createStyleUrlList(getStyleManager().getStyles(grid.getScaffolding().getId())));
      
      
      model.put("showFooter", getShowFooter(grid));
      
      List<String> cellsICanAccess = getCellsICanAccess(getMatrixManager().getMatrix(grid.getMatrixId()));
      model.put("cellsICanAccess", cellsICanAccess);

      HashMap<String, Boolean> accessibleCells = new HashMap<String, Boolean>();
      for (String s : cellsICanAccess) {
         accessibleCells.put(s, true);
      }
      model.put("accessibleCells", accessibleCells);
      
      return model;
   }
   
   /**
    ** Return true if matrix footer should be displayed. The footer will be displayed if the number of rows
    ** exceeds MATRIX_ROW_FOOTER0, which is configurable in sakai.properties as osp.matrixRowFooter. 
    ** If osp.matrixRowFooter is -1, the footer will never be displayed.
    **/
   protected Boolean getShowFooter(MatrixGridBean grid) {
      int matrixRowFooter = ServerConfigurationService.getInt("osp.matrixRowFooter", MATRIX_ROW_FOOTER );
      
      if ( matrixRowFooter < 0 )
         return Boolean.valueOf(false);
      else if ( grid.getMatrixContents().size() > MATRIX_ROW_FOOTER )
         return Boolean.valueOf(true);
      else
         return Boolean.valueOf(false);
   }
   
	/**
	 ** Return true if matrix owner has submitted assignments associated with this cell
	 **/
	protected List getAssignments(WizardPage wizPage, Agent owner) {
      ArrayList submissions = new ArrayList();
      
		try {
			User user = UserDirectoryService.getUser(owner.getId().getValue());
			ArrayList assignments = 
				AssignmentHelper.getSelectedAssignments(wizPage.getPageDefinition().getAttachments());
			
			for ( Iterator it=assignments.iterator(); it.hasNext(); ) {
				Assignment assign = (Assignment)it.next();
				AssignmentSubmission assignSubmission = AssignmentService.getSubmission( assign.getId(),
																												 user );

				// assignments may be (incorrectly) marked as submitted, so check for valid submit time
				if ( assignSubmission != null 
					  && assignSubmission.getSubmitted() 
					  && assignSubmission.getTimeSubmitted()!=null )
					submissions.add(assignSubmission);
			}
		}
		catch ( Exception e ) {
			logger.warn(".getAssignments: " +  e.toString());
		}
		
		return submissions;
	}

   /**
    * Extract the site page id from the current request.
    * 
    * @return The site page id implied from the current request.
    */
   protected String getCurrentSitePageId()
   {
      ToolSession ts = SessionManager.getCurrentToolSession();
      if (ts != null)
      {
         ToolConfiguration tool = SiteService.findTool(ts.getPlacementId());
         if (tool != null)
         {
            return tool.getPageId();
         }
      }

      return null;

   } // getCurrentSitePageId

   private Cell getCell(Collection<Cell> cells, Criterion criterion, Level level) {
      for (Iterator<Cell> iter=cells.iterator(); iter.hasNext();) {
         Cell cell = (Cell) iter.next();
         if (cell.getScaffoldingCell().getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               cell.getScaffoldingCell().getLevel().getId().getValue().equals(level.getId().getValue())) {
            return cell;
         }
      }
      return null;
   }

   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      //model.put("view_user", request.get("view_user"));
      return new ModelAndView("success", model);
   }
   
   private List<String> getCellsICanAccess(Matrix matrix){
	   List<String> accessIds = new ArrayList<String>();
	   
	   boolean canViewAllCells = getMatrixManager().canAccessAllMatrixCells(matrix.getScaffolding().getId());
	   
	   for (Iterator iterator = matrix.getCells().iterator(); iterator
	   .hasNext();) {
		  
		   Cell cell = (Cell) iterator.next();
		   if(!canViewAllCells){
			   if(getMatrixManager().canAccessMatrixCell(cell)){
				   accessIds.add(cell.getId().getValue());
			   }
		   }else{
			   accessIds.add(cell.getId().getValue());
		   }
	   }

	   return accessIds;
   }
 

	public ToolManager getToolManager() {
		return toolManager;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	public StyleManager getStyleManager() {
		return styleManager;
	}

	public void setStyleManager(StyleManager styleManager) {
		this.styleManager = styleManager;
	}

	public SecurityService getSecurityService() {
		return securityService;
	}
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	public ReviewManager getReviewManager() {
        return reviewManager;
    }
    public void setReviewManager(ReviewManager reviewManager) {
        this.reviewManager = reviewManager;
    }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ListReviewerItemController.java $
* $Id:ListReviewerItemController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008, 2009 The Sakai Foundation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.PreferencesService;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.EvaluationContentComparator;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;
import org.theospi.portfolio.wizard.mgt.WizardManager;

/**
 * @author chmaurer
 */
public class ListEvaluationItemController implements FormController, LoadObjectController, CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager = null;
	private WizardManager wizardManager = null;
   private IdManager idManager = null;
   private AuthenticationManager authManager = null;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private AgentManager agentManager = null;
   private ListScrollIndexer listScrollIndexer;
   private ToolManager toolManager;
  
   private final static String EVAL_PLACEMENT_PREF = "org.theospi.portfolio.evaluation.placement.";
   private final static String CURRENT_SITE_EVALS = "org.theospi.portfolio.evaluation.currentSite";
   private final static String ALL_EVALS = "org.theospi.portfolio.evaluation.allSites";
   private final static String EVAL_SITE_FETCH = "org.theospi.portfolio.evaluation.siteEvals";
   private final static String EVAL_SORT_DIRECTION = "org.theospi.portfolio.evaluation.sortDirection";
   private final static String EVAL_SORT_COLUMN    = "org.theospi.portfolio.evaluation.sortColumn";
   private final static String DEFAULT_SORT_DIRECTION = "asc";
   
   public static final String GROUP_FILTER = "group_filter";
   
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      
      List list = new ArrayList();
      String evalType = (String)request.get("evalTypeKey");
      String sortColumn = (String)request.get("sortByColumn");
      String sortDirection = (String)request.get("direction");
      
      // Save user preferences, if any have changed
      if ( evalType != null || sortColumn != null || sortDirection != null ) {
         PreferencesEdit prefEdit = getPreferencesEdit();
         
         ResourceProperties propEdit = prefEdit.getPropertiesEdit(EVAL_PLACEMENT_PREF + getToolManager().getCurrentPlacement().getId());
      
         if (evalType != null)
            propEdit.addProperty(EVAL_SITE_FETCH, evalType);
      
         if (sortColumn != null)
            propEdit.addProperty(EVAL_SORT_COLUMN, sortColumn);
         
         if (sortDirection != null)
            propEdit.addProperty(EVAL_SORT_DIRECTION, sortDirection);

         try {
            PreferencesService.commit(prefEdit);
         }
         catch (Exception e) {
            logger.warn("Problem saving preferences for site evals in setSortDirection().", e);
         }
      }
      
      // Get default or pre-existing values for any unspecified preferences
      Preferences userPreferences = PreferencesService.getPreferences(authManager.getAgent().getId().getValue());
      ResourceProperties evalPrefs = userPreferences.getProperties(EVAL_PLACEMENT_PREF + getToolManager().getCurrentPlacement().getId());
      if (sortColumn == null)
         sortColumn = getSortColumn(evalPrefs);
         
      if (sortDirection == null)
         sortDirection = getSortDirection(evalPrefs);
         
      evalType = getUserEvalProperty(evalPrefs);
      
      // Get list of reviewer items
      if (ALL_EVALS.equals(evalType)) {
         list = wizardManager.getEvaluatableItems(authManager.getAgent());
      }
      else {
         List<String> siteIds = new ArrayList<String>(1);
         siteIds.add( worksiteManager.getCurrentWorksiteId().getValue() );
         list = wizardManager.getEvaluatableItems(authManager.getAgent(), siteIds);
      }
      
      // Sort list of reviewer items
      boolean asc = sortDirection.equalsIgnoreCase(DEFAULT_SORT_DIRECTION);
      Collections.sort(list, new EvaluationContentComparator(sortColumn, asc));
      list = getListScrollIndexer().indexList(request, request, list);

      return list; /* goes into 'reviewerItems'  */
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      return new ArrayList();
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      
      String action = (String)request.get("action");
      String view = "success";

      Map model = new HashMap();
      
      if("open".equals(action)) {
         String id = (String)request.get("id");
         List list = (List)requestModel;
         
         if(id != null) {
            for(Iterator i = list.iterator(); i.hasNext(); ) {
               EvaluationContentWrapper wrapper = (EvaluationContentWrapper)i.next();
               
               if(id.equals(wrapper.getId().getValue() + "_" + wrapper.getOwner().getId())) {
                  view = wrapper.getUrl();
                  if (view == null) break;
                  for(Iterator params = wrapper.getUrlParams().iterator(); params.hasNext(); ) {
                     EvaluationContentWrapper.ParamBean param = (EvaluationContentWrapper.ParamBean)params.next();
                     
                     model.put(param.getKey(), param.getValue());
                  }
                  
                  //Clear out the hier page if there is one & clear the set of seq pages
                  session.remove(WizardPageHelper.WIZARD_PAGE);
                  session.remove(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);
                  
                  session.put("is_eval_page_id", wrapper.getId().getValue());
                  break;
               }
            }
         }
      }
      
      return new ModelAndView(view, model);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      Preferences userPreferences = PreferencesService.getPreferences(authManager.getAgent().getId().getValue());
      ResourceProperties evalPrefs = userPreferences.getProperties(EVAL_PLACEMENT_PREF + getToolManager().getCurrentPlacement().getId());
      
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());
      model.put("currentUser", authManager.getAgent());
      model.put("direction", getSortDirection(evalPrefs));
      model.put("sortByColumn", getSortColumn(evalPrefs));
      model.put("evalType", getUserEvalProperty(evalPrefs));
      model.put("currentSiteEvalsKey", CURRENT_SITE_EVALS);
      model.put("allEvalsKey", ALL_EVALS);
      
      boolean userSite = SiteService.isUserSite(getWorksiteManager().getCurrentWorksiteId().getValue());
      model.put("isUserSite", userSite);
      
      boolean hasGroups = getMatrixManager().hasGroups(worksiteId);
      model.put("hasGroups", hasGroups);
      
      
      boolean allowAllGroups = true;
      List<Group> groupList = new ArrayList<Group>(getMatrixManager().getGroupList(worksiteId, allowAllGroups));
      //Collections.sort(groupList);
      //TODO: Figure out why ClassCastExceptions fire if we do this the obvious way...  The User list sorts fine
      Collections.sort(groupList, new Comparator<Group>() {
    	  public int compare(Group arg0, Group arg1) {
    		  return arg0.getTitle().toLowerCase().compareTo(arg1.getTitle().toLowerCase());
    	  }});
      
      
      model.put("userGroups", groupList);
      model.put("userGroupsCount", groupList.size());
      
      String filteredGroup = (String) request.get(GROUP_FILTER);
      model.put("filteredGroup", filteredGroup);


      if(hasGroups){
    	  List userList = new ArrayList(getMatrixManager().getUserList(worksiteId, filteredGroup, allowAllGroups, groupList));

    	  model.put("members", userList);

    	  //Hold permission results for each scaffolding for quicker accessing
    	  Map<String, Boolean> allGroupsForScaffolding = new HashMap<String, Boolean>();
    	  
    	  List<EvaluationContentWrapper> removeList = new ArrayList();
    	  
    	  //loop through each returned EvaluationContentWrapper
    	  for(Iterator i = ((List) command).iterator(); i.hasNext(); ) {
    		  EvaluationContentWrapper wrapper = (EvaluationContentWrapper)i.next();
    		  //if filteredGroup "All Groups" is selected and this is a matrix evaluation item
    		  if((filteredGroup == null || "".equals(filteredGroup)) && Cell.TYPE.equals( wrapper.getEvalType())){
    			  	  //grab the scaffolding for the matrix
    				  Scaffolding scaffolding = getMatrixManager()
							.getScaffoldingCellByWizardPageDef(
									getMatrixManager().getWizardPage(
											wrapper.getId())
											.getPageDefinition().getId())
							.getScaffolding();
    				  boolean viewAllGroups = false;
    				  //if this scaffolding is not cached in the map, then add it
    				  if(!allGroupsForScaffolding.containsKey(scaffolding.getReference())){
    					  viewAllGroups= getAuthzManager().isAuthorized(MatrixFunctionConstants.VIEW_ALL_GROUPS, getIdManager().getId(scaffolding.getReference()));
    					  allGroupsForScaffolding.put(scaffolding.getReference(), viewAllGroups);
    				  }else{
    					  viewAllGroups = allGroupsForScaffolding.get(scaffolding.getReference());
    				  }
    				  
    				  //if the current user doesn't have view all groups permission for this scaffolding,
    				  //then you need to check to see if the user has the ability to view the owner within
    				  //their own groups
    				  if(!viewAllGroups){
    					  if(!userList.contains(wrapper.getOwner())){
    						  removeList.add(wrapper);
    					  }
    				  }
    		  }else if(!userList.contains(wrapper.getOwner())){
    			  //a specific group was selected 
    			  //and the wrapper owner doesn't exist in the group
    			  
    			  //So add the evaluation item to the remove list if the owner is part of the viewable group(s)
    			  removeList.add(wrapper);
    		  }
    	  }
    	  
    	  //go through each removeList item and remove the evaluation in the command var.
    	  for (EvaluationContentWrapper evaluationContentWrapper : removeList) {
    		  ((List) command).remove(evaluationContentWrapper);
    	  }
      }
      
      
      return model;
   }

   /** Returns user preference for sort direction
    **/
   private String getSortDirection( ResourceProperties evalPrefs ) {
      String sortDirection = evalPrefs.getProperty(EVAL_SORT_DIRECTION);
      if ( sortDirection != null )
         return sortDirection;
      else
         return DEFAULT_SORT_DIRECTION;
   }

   /** Returns user preference for sort column
    **/
   private String getSortColumn( ResourceProperties evalPrefs ) {
      String sortColumn = evalPrefs.getProperty(EVAL_SORT_COLUMN);
      if ( sortColumn != null )
         return sortColumn;
      else
         return EvaluationContentComparator.SORT_TITLE;
   }
    
   /** Returns user preference for evaluations to display (all sites or just current site)
    **/
   private String getUserEvalProperty( ResourceProperties evalPrefs ) {
      String defaultProp = CURRENT_SITE_EVALS;
      
      //If the site is a my workapace site, default to all sites
      if ( SiteService.isUserSite(getWorksiteManager().getCurrentWorksiteId().getValue()) )
         return ALL_EVALS;
         
      String prop = evalPrefs.getProperty(EVAL_SITE_FETCH);
      if (prop != null) 
         return prop;
      else
         return defaultProp;
   }

   /** Return PreferencesEdit object for current user
    **/
   private PreferencesEdit getPreferencesEdit() {
      PreferencesEdit prefEdit = null;
      try {
         prefEdit = (PreferencesEdit) PreferencesService.add(authManager.getAgent().getId().getValue());
      } catch (PermissionException e) {
         logger.warn("Problem saving preferences for site evals in getPreferences().", e);
      } catch (IdUsedException e) {
         // Preferences already exist, just edit
         try {
            prefEdit = (PreferencesEdit) PreferencesService.edit(authManager.getAgent().getId().getValue());
         } catch (PermissionException e1) {
            logger.warn("Problem saving preferences for site evals in getPreferences().", e1);
         } catch (InUseException e1) {
            logger.warn("Problem saving preferences for site evals in getPreferences().", e1);
         } catch (IdUnusedException e1) {
            // This should be safe to ignore since we got here because it existed
            logger.warn("Problem saving preferences for site evals in getPreferences().", e1);
         }
      }
      return prefEdit;
   }
   
   private Boolean isMaintainer() {
      return Boolean.valueOf(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(getToolManager().getCurrentPlacement().getContext())));
   }

 
   
   
   /**
    * @return
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param manager
    */
   public void setMatrixManager(MatrixManager manager) {
      matrixManager = manager;
   }

   /**
    * @return
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param manager
    */
   public void setIdManager(IdManager manager) {
      idManager = manager;
   }

   /**
    * @return
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param manager
    */
   public void setAuthManager(AuthenticationManager manager) {
      authManager = manager;
   }

   /**
    * @return Returns the agentManager.
    */

   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
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

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}
   
}


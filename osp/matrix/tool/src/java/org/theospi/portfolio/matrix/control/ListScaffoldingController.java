/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ListScaffoldingController.java $
* $Id:ListScaffoldingController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008, 2009 The Sakai Foundation
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.UserNotificationPreferencesRegistration;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Scaffolding;

public class ListScaffoldingController extends AbstractMatrixController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private SiteService siteService;
	private IdManager idManager;
	private UserNotificationPreferencesRegistration matrixPreferencesConfig;
	// Sort strings
	static final String SORT = "sort", ASCENDING = "ascending",  TITLE = "title", OWNER = "owner",
						PUBLISHED = "published", MODIFIED = "modified", WORKSITE = "worksite";
	
	private String previewAuthz = ServerConfigurationService.getString("osp.preview.permission", null );

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable<String, Object> model = new Hashtable<String, Object>();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getId();
      Id worksiteId = getWorksiteManager().getCurrentWorksiteId();
      String sortBy = TITLE;
      boolean sortAscending = true;
		List scaffolding = null;

		if ( isOnWorkspaceTab() )
		{
			scaffolding = getMatrixManager().findAvailableScaffolding(getUserWorksites(), currentAgent, false);
		}
		else
		{
			boolean listPreview = true;
			if ( previewAuthz != null )
				listPreview = getAuthzManager().isAuthorized( previewAuthz, worksiteId );
			scaffolding = getMatrixManager().findAvailableScaffolding(worksiteId.getValue(), currentAgent, listPreview);
		}
		
		if(request.get(SORT) != null){
			sortBy = request.get(SORT).toString();
			sortAscending = Boolean.parseBoolean(request.get(ASCENDING).toString());
		}
		scaffolding = sortScaffolding(scaffolding, sortBy, sortAscending);
		List decoratedScaffolding = new ArrayList();
		
		for (Iterator iterator = scaffolding.iterator(); iterator.hasNext();) {
			Scaffolding s = (Scaffolding) iterator.next();
			decoratedScaffolding.add(new DecoratedScaffolding(s));			
		}
		
      // When selecting a matrix the user should start with a fresh user
      session.remove(ViewMatrixController.VIEW_USER);
      //used to determine to display pager
      model.put("scaffoldingListSize", scaffolding.size());
      model.put("sortBy", sortBy);
      model.put("sortAscending", sortAscending);
      model.put("scaffolding",
         getListScrollIndexer().indexList(request, model, decoratedScaffolding));

      model.put("worksite", getWorksiteManager().getSite(worksiteId.getValue()));
      model.put("tool", getWorksiteManager().getTool(currentToolId));
      model.put("isMaintainer", isMaintainer());
      model.put("osp_agent", currentAgent);
		model.put("myworkspace", isOnWorkspaceTab() );
      
      model.put("useExperimentalMatrix", getMatrixManager().isUseExperimentalMatrix());
      
      if(request.get("toolPermissionSaved") != null)
    	  model.put("toolPermissionSaved", request.get("toolPermissionSaved"));

      String typeKey = getMatrixPreferencesConfig().getType();
      model.put("typeKey", typeKey);
     
      
      return new ModelAndView("success", model);
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
	
   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
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
	
   public void setMatrixPreferencesConfig(UserNotificationPreferencesRegistration matrixPreferencesConfig) {
	   this.matrixPreferencesConfig = matrixPreferencesConfig;
   }

   public UserNotificationPreferencesRegistration getMatrixPreferencesConfig() {
	   return matrixPreferencesConfig;
   }

   /**
    * See if the current tab is the workspace tab.
    * @return true if we are currently on the "My Workspace" tab.
    */
   private boolean isOnWorkspaceTab()
   {
      return siteService.isUserSite(ToolManager.getCurrentPlacement().getContext());
   }
	
	/**
	 ** Return list of worksite Ids for current user
	 **/
	private List getUserWorksites()
	{		 
		List siteList = siteService.getSites(SiteService.SelectionType.ACCESS,
														 null, null, null, 
														 SiteService.SortType.TITLE_ASC, null);
      List siteStrIds = new ArrayList(siteList.size());
		for (Iterator it = siteList.iterator(); it.hasNext();) 
		{
			Site site = (Site) it.next();
         String siteId = site.getId();
			siteStrIds.add( idManager.getId(siteId) );
		}
		
		return siteStrIds;
	}
	
	public List sortScaffolding(List<Scaffolding> list, final String sort, final boolean ascending) {
		Collections.sort(list, new Comparator<Scaffolding>() {
			public int compare(Scaffolding o1, Scaffolding o2) {
				Scaffolding s1 = null;
				Scaffolding s2 = null;
				if (ascending) {
					s1 = o1;
					s2 = o2;
				} else {
					s2 = o1;
					s1 = o2;
				}
				if (sort.equalsIgnoreCase(TITLE)) {
					return s1.getTitle().compareToIgnoreCase(
							s2.getTitle());
				}else if (sort.equalsIgnoreCase(OWNER)) {
					return s1.getOwner().getDisplayName().compareToIgnoreCase(
							s2.getOwner().getDisplayName());
				}else if (sort.equalsIgnoreCase(PUBLISHED)) {
					return Boolean.toString(s1.isPublished()).compareToIgnoreCase(
							Boolean.toString(s2.isPublished()));				
				}else if (sort.equalsIgnoreCase(MODIFIED)) {
					if(s1.getModifiedDate() == null){
						return -1;
					}
					if(s2.getModifiedDate() == null){
						return 1;
					}
					return s1.getModifiedDate().compareTo(
							s2.getModifiedDate());				
				}else if (sort.equalsIgnoreCase(WORKSITE)) {
					return s1.getWorksiteName().compareToIgnoreCase(
							s2.getWorksiteName());
				}else {				
					return s1.getTitle().compareToIgnoreCase(
							s2.getTitle());
				}
			}
		});
		
		return list;
	}
	
	public class DecoratedScaffolding {
		private Scaffolding scaffolding;

		public DecoratedScaffolding(Scaffolding scaffolding){
			this.scaffolding = scaffolding;
		}
		
		public Scaffolding getScaffolding(){
			return scaffolding;
		}
		
		public String getScaffoldingToolUrl(){

			String url;
			try {
				url = ServerConfigurationService.getPortalUrl() + "/directtool/" + getSiteService().getSite(scaffolding.getWorksiteId().getValue()).getToolForCommonId("osp.matrix").getId() + "/listScaffolding.osp";
				return url;
			} catch (IdUnusedException e) {
				e.printStackTrace();
			}
			
			return "";
		}
	}
}

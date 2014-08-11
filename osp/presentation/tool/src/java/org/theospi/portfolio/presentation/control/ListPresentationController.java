/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ListPresentationController.java $
* $Id:ListPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.cover.PreferencesService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.presentation.support.PresentationService;

public class ListPresentationController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private ServerConfigurationService serverConfigurationService;
   protected PresentationService presentationService;
   
   private final static String PORTFOLIO_PREFERENCES = "org.theospi.portfolio.presentation.placement.";
   private final static String PREF_HIDDEN = "org.theospi.portfolio.presentation.hidden.";
   private final static String PREF_FILTER = "org.theospi.portfolio.presentation.filter.";
   private final static String PREF_SORT_ORDER = "org.theospi.portfolio.presentation.sortOrder.";
   private final static String PREF_SORT_KEY = "org.theospi.portfolio.presentation.sortKey.";
   
   private final static String PREF_FILTER_VALUE_ALL    = "all"; // deprecated - but keep for parsing old preferences
   private final static String PREF_FILTER_VALUE_PUBLIC = "public";
   private final static String PREF_FILTER_VALUE_MINE   = "mine";
   private final static String PREF_FILTER_VALUE_SHARED = "shared";
   
   private final static String SORTCOLUMN_KEY = "sortOn";
   private static final Object SORTORDER_KEY = "sortorder";
   private static final String SORTORDER_ISASCENDING_KEY = "sortOrderIsAscending";
 
   // NOTE that this list needs to be synced with the values in the listPresention.jsp 
   private static final String SORTORDER_ASCENDING = "ascending";
   private static final String NAME_COLUMNKEY = "name";
   private static final String DATEMODIFIED_COLUMNKEY = "dateModified";
   private static final String OWNER_COLUMNKEY = "owner";
   private static final String REVIEWED_COLUMNKEY = "reviewed";
   
   private static final Map<String, Comparator<Presentation>> sortName2PresentationComparator = initSortName2PresentationComparator();
   
   private static final Map<String, Comparator<Presentation>> initSortName2PresentationComparator() {
      Map<String, Comparator<Presentation>> result = new HashMap<String, Comparator<Presentation>>(4);
      result.put(NAME_COLUMNKEY,
                 new PresentationComparators.ByNameComparator());
      result.put(DATEMODIFIED_COLUMNKEY,
                 new PresentationComparators.ByDateModifiedComparator());
      result.put(OWNER_COLUMNKEY,
                 new PresentationComparators.ByOwnerComparator());
      result.put(REVIEWED_COLUMNKEY,
                 new PresentationComparators.ByReviewedComparator());
      return result;
   }
 
   @SuppressWarnings("unchecked")
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable<String, Object> model = new Hashtable<String, Object>();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getId();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      String showHidden = getUserPreferenceProperty(PREF_HIDDEN, 
                                                    (String)request.get("showHiddenKey"),
                                                    PresentationManager.PRESENTATION_VIEW_VISIBLE);
      String filterList = getUserPreferenceProperty(PREF_FILTER, 
                                                    (String)request.get("filterListKey"),
                                                    PREF_FILTER_VALUE_MINE);
      String sortColumn = getUserPreferenceProperty(PREF_SORT_KEY, 
                                                    (String)request.get(SORTCOLUMN_KEY),
                                                    NAME_COLUMNKEY);
      String sortOrder = getUserPreferenceProperty(PREF_SORT_ORDER, 
                                                   (String)request.get(SORTORDER_KEY),
                                                   SORTORDER_ASCENDING);
                                                   
      // Reset deprecated 'ALL' preference with 'MINE' preference
      if ( filterList.equals(PREF_FILTER_VALUE_ALL) )
         filterList = PREF_FILTER_VALUE_MINE;
         
      Collection presentations = null;
      String filterToolId = null;
      
      boolean viewAll = getServerConfigurationService().getBoolean("osp.presentation.viewall", false) &&
         getAuthzManager().isAuthorized(PresentationFunctionConstants.REVIEW_PRESENTATION,
                                        getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
      
      // If not on MyWorkspace, grab presentations for this tool only
      if ( ! isOnWorkspaceTab() )
         filterToolId = currentToolId;
        
      if ( filterList.equals(PREF_FILTER_VALUE_MINE) )
      {
         presentations = getPresentationManager().findOwnerPresentations(currentAgent, filterToolId, showHidden);
      }
      else if ( filterList.equals(PREF_FILTER_VALUE_SHARED) )
      {
         if ( viewAll && !isOnWorkspaceTab() )
            presentations = getPresentationManager().findOtherPresentationsUnrestricted(currentAgent, filterToolId, showHidden);
         else
            presentations = getPresentationManager().findSharedPresentations(currentAgent, filterToolId, showHidden);
      }
      else // ( filterList.equals(PREF_FILTER_VALUE_PUBLIC) )
      {
         presentations = getPresentationManager().findPublicPresentations(currentAgent, filterToolId, showHidden);
      }

       // Sort the presentations
      Boolean sortOrderIsAscending = SORTORDER_ASCENDING.equals(sortOrder) ? true : false;
      sortPresentations(presentations, sortColumn, sortOrderIsAscending);
       
      List presSubList = getListScrollIndexer().indexList(request, model, new ArrayList(presentations), true);
      model.put("presentations", getPresentationData(presSubList) );

      List templates = presentationService.getAvailableTemplates();
      model.put("createAvailable", presentationService.isFreeFormEnabled() || templates.size() > 0);
       
      model.put(SORTORDER_ISASCENDING_KEY, sortOrderIsAscending);
      model.put(SORTCOLUMN_KEY, sortColumn);
      model.put("baseUrl", PresentationService.VIEW_PRESENTATION_URL);
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(currentToolId));
      model.put("isMaintainer", isMaintainer());
      model.put("osp_agent", currentAgent);
      model.put("showHidden", showHidden);
      model.put("filterList", filterList);
      model.put("myworkspace", isOnWorkspaceTab() );
      model.put("lastViewKey", SpringTool.LAST_VIEW_VISITED);
      return new ModelAndView("success", model);
   }
   
   /** Sort given collection of presentations, using given sortColumn in ascending or descending order
    **
    **/
   private void sortPresentations(final Collection<Presentation> presentations,
                                  final String sortColumn, final Boolean inAscendingOrder) 
   {
      Comparator<Presentation> comparator = sortName2PresentationComparator.get(sortColumn);
      
      if (comparator != null) {
         if (!inAscendingOrder) {
            comparator = Collections.reverseOrder(comparator);
         }
         Collections.sort((List)presentations, comparator);
      } 
      else {
         logger.error("no comparator defined for column " + sortColumn);
      }
   }
   
   /**
    ** If prefValue provided, save it and return, 
    ** otherwise retrieve stored prefValue for given prefKey
    **
    ** @param prefKey preference key
    ** @param prefValue optional new value to save
    ** @param dfltValue default value
    */
   protected String getUserPreferenceProperty(String prefKey, String prefValue, String dfltValue) 
   {
      
      String propsName = PORTFOLIO_PREFERENCES + ToolManager.getCurrentPlacement().getId();
      String userId    = getAuthManager().getAgent().getId().getValue();

      // If no preference was provided in request, then get saved preference
      if ( prefValue == null ) {
         Preferences userPreferences = PreferencesService.getPreferences(userId);
         ResourceProperties portfolioPrefs = userPreferences.getProperties(propsName);
         prefValue = portfolioPrefs.getProperty(prefKey);
         
         // If prefValue found, just return
         if ( prefValue != null )
            return prefValue;
               
         // Otherwise, use the default value and continue to save
         else
            prefValue = dfltValue;
      }
       
      // Otherwise, save preference and return  
      PreferencesEdit prefEdit = null;
      try {
         prefEdit = (PreferencesEdit) PreferencesService.add(userId);
      } 
      catch (PermissionException e) {
         logger.warn(e.toString());
      } 
      catch (IdUsedException e) {
         // Preferences already exist, just edit
         try {
            prefEdit = (PreferencesEdit) PreferencesService.edit(userId);
         } 
         catch (Exception e2) {
            logger.warn(e2.toString());
         } 
      }
      
      if (prefEdit != null) {
         try {
            ResourceProperties propEdit = prefEdit.getPropertiesEdit(propsName);
            propEdit.addProperty(prefKey, prefValue);
            PreferencesService.commit(prefEdit);
         }
         catch (Exception e) {
            logger.warn(e.toString());
         }
      }
      
      return prefValue;
   }

   /**
    * See if the current tab is the workspace tab.
    * @return true if we are currently on the "My Workspace" tab.
    */
   private boolean isOnWorkspaceTab()
   {
      return SiteService.isUserSite(ToolManager.getCurrentPlacement().getContext());
   }
	
   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
    public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

   public void setPresentationService(PresentationService presentationService) {
      this.presentationService = presentationService;
   }
   
   
   /** Given a list of presentations, this method returns a list of PresentationDataBean objects
    **/
   public List getPresentationData( List presList ) {
      ArrayList presData = new ArrayList( presList.size() );
      for (Iterator it = presList.iterator(); it.hasNext();) {
         Presentation pres = (Presentation) it.next();
         presData.add( new PresentationDataBean( pres ) );
      }
      return presData;
   }
   
   /** This class provides auxiliary data (comments, shared status) for a given presentation
    **/
   public class PresentationDataBean {
      Presentation m_presentation;
      int m_commentNum;
      boolean m_shared = false;
      boolean m_public = false;
      boolean m_collab = false;
      
      public PresentationDataBean( Presentation presentation ) {
         m_presentation = presentation;
         
         // determine shared attributes (public is considered shared)
         if ( presentation.getIsPublic() ) {
            m_public = true;
         }
         
         List authzs = getAuthzManager().getAuthorizations(null, AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO, presentation.getId());
         if (authzs.size() > 0)
            m_shared = true;
            
         // Determine if user can collaboratively edit this portfolio
         if ( presentation.getIsCollab() &&
              getAuthzManager().isAuthorized(PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId()) ) {
                    m_collab = true;
         }
         
         // find number of comments
         List comments = getPresentationManager().getPresentationComments( presentation.getId(), getAuthManager().getAgent() );
         if ( comments.size() > 0 || presentation.isAllowComments() ) {
            m_commentNum = comments.size();
         }
         else {
            m_commentNum = -1; // comments not allowed
         }
      }
      
      public Presentation getPresentation() {
         return m_presentation;
      }
      
      public int getCommentNum() {
         return m_commentNum;
      }
      
      public String getCommentNumAsString() {
         StringBuilder commentStr = new StringBuilder();
         if ( m_commentNum >= 0 ) {
            commentStr.append( "(" );
            commentStr.append( String.valueOf(m_commentNum) );
            commentStr.append( ")" );
         }
         return commentStr.toString();
      }
      
      public boolean getShared() {
         return m_shared;
      }
      
      public boolean getPublicPresentation() {
         return m_public;
      }
      
      public boolean getIsCollab() {
         return m_collab;
      }
   }
	
}

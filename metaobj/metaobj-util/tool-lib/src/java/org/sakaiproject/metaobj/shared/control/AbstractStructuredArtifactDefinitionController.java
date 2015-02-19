/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/AbstractStructuredArtifactDefinitionController.java $
 * $Id: AbstractStructuredArtifactDefinitionController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.servlet.ModelAndView;

abstract public class AbstractStructuredArtifactDefinitionController extends AbstractFormController {
   protected final Log logger = LogFactory.getLog(getClass());
   private HomeFactory homeFactory;
   private AuthenticationManager authManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private IdManager idManager;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private EntityManager entityManager;
   private String toolId;
   private ListScrollIndexer listScrollIndexer;
   private ResourceLoader rl = new ResourceLoader("messages");

   public void checkPermission(String function) throws AuthorizationFailedException {
      if (getStructuredArtifactDefinitionManager().isGlobal()) {
         getAuthzManager().checkPermission(function, getIdManager().getId(StructuredArtifactDefinitionManager.GLOBAL_SAD_QUALIFIER));
      }
      else {
         getAuthzManager().checkPermission(function, getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
      }

   }
   
   protected boolean isAllowed(String function) {
	   boolean isAllowed = false;   
	   if (getStructuredArtifactDefinitionManager().isGlobal()) {
		   isAllowed = getAuthzManager().isAuthorized(function, getIdManager().getId(StructuredArtifactDefinitionManager.GLOBAL_SAD_QUALIFIER));
	   }
	   else {
		   isAllowed = getAuthzManager().isAuthorized(function, getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
	   }
	   return isAllowed;
   }

   protected Boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext())));
   }

   protected ModelAndView prepareListView(Map request, String recentId) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("isMaintainer", isMaintainer());
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("sites", getUserSites());
      ToolConfiguration tool = getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getId());
      model.put("tool", tool);
      model.put("currentAgent", getAuthManager().getAgent());

      boolean global = getStructuredArtifactDefinitionManager().isGlobal();
      model.put("isGlobal", new Boolean(global));

      if (global) {
         model.put("authZqualifier", getIdManager().getId(StructuredArtifactDefinitionManager.GLOBAL_SAD_QUALIFIER));
      }
      else {
         if (tool != null) {
            model.put("authZqualifier", getIdManager().getId(tool.getId()));
         }
         else {
            model.put("authZqualifier", getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
         }
      }

      List types;
      if (getStructuredArtifactDefinitionManager().isGlobal()) {
         types = getStructuredArtifactDefinitionManager().findGlobalHomes();
      }
      else {
         types = getStructuredArtifactDefinitionManager().findAvailableHomes(
            getWorksiteManager().getCurrentWorksiteId(), getAuthManager().getAgent().getId().getValue(), true, false);
      }

      Collections.sort(types);
      List typesList = new ArrayList(types);
      if (recentId != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getTypeIndex(typesList,
               recentId));
         model.put("newFormId", recentId);
      }

      types = getListScrollIndexer().indexList(request, model, typesList);

      model.put("types", types);
      
      Boolean showUsage = false;
      if (tool != null) {
         showUsage = Boolean.parseBoolean((String)tool.getConfig().getProperty("display.usage"));
      }
      model.put("toolShowUsage", showUsage);

      return new ModelAndView("success", model);
   }

   protected int getTypeIndex(List typesList, String recentId) {
      for (int i = 0; i < typesList.size(); i++) {
         StructuredArtifactDefinitionBean home = (StructuredArtifactDefinitionBean) typesList.get(i);
         if (home.getType().getId().getValue().equals(recentId)) {
            return i;
         }
      }

      return 0;
   }

   /**
    * @return collection of site ids user belongs to, as Strings
    */
   protected Map getUserSites() {
      Collection sites = getWorksiteManager().getUserSites();
      Map userSites = new HashMap();
      for (Iterator i = sites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         userSites.put(site.getId(), site);
      }
      return userSites;
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

   /**
    * @return Returns the homeFactory.
    */
   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   /**
    * @param homeFactory The homeFactory to set.
    */
   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
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

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   
   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public String getToolId() {
      return toolId;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
   
   protected String getMessage(String key) {
      return rl.getString(key);
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/wizard/api-impl/src/java/org/theospi/portfolio/wizard/WizardListGenerator.java $
* $Id: WizardListGenerator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.SortableListObject;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;

public class WizardListGenerator extends BaseListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   
   private static final String SITE_ID_PARAM = "selectedSiteId";
   private static final String WIZARD_ID_PARAM = "wizardId";
   private static final String MATRIX_ID_PARAM = "matrixId";

   private WizardManager wizardManager;
   private MatrixManager matrixManager;
   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager;
   private IdManager idManager;
   private AuthorizationFacade authzManager;
   private List displayTypes;

   public void init(){
      logger.info("init()"); 
      super.init();
   }
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public List getObjects() {
      List userSites = getWorksiteManager().getUserSites(null, getListService().getSiteTypeList());
      List<String> siteIds = new ArrayList<String>(userSites.size());
      Map<String, Site> siteMap = new HashMap<String, Site>();
      
      for (Iterator i = userSites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         siteIds.add(site.getId());
         siteMap.put(site.getId(), site);
      }
      
      List tempWizardList = new ArrayList();
      if (getDisplayTypes().contains("wizards")) tempWizardList = getWizardManager().findPublishedWizards(siteIds, true);
      List tempMatrixList = new ArrayList();
      if (getDisplayTypes().contains("matrices")) tempMatrixList = getMatrixManager().findPublishedScaffolding(siteIds);
      
      List<SortableListObject> objects = new ArrayList<SortableListObject>();
      
      objects.addAll(verifyWizards(tempWizardList, siteMap));
      objects.addAll(verifyMatrices(tempMatrixList, siteMap));      

      return objects;
   }
   
   protected List<SortableListObject> verifyWizards(List allWizards, Map siteMap) {
      List<SortableListObject> retWizards = new ArrayList<SortableListObject>();
      Map<String, Boolean> sitePermCache = new HashMap<String, Boolean>();
      
      for (Iterator i = allWizards.iterator(); i.hasNext();) {
         Wizard wizard = (Wizard)i.next();
         String siteId = wizard.getSiteId();
         //make sure that the target site gets tested
         getAuthzManager().pushAuthzGroups(siteId);
         
         //Need to make sure the current user can actually have one of their own here, 
         // so only check if they can "use"
         // But check from the cache first
         Boolean authzCheck = sitePermCache.get(siteId);
         if (authzCheck == null) {
            authzCheck = getAuthzManager().isAuthorized(WizardFunctionConstants.VIEW_WIZARD, 
                  idManager.getId(siteId));
            sitePermCache.put(siteId, authzCheck);
            logger.debug("Pushing site into cache for WizardListGenerator (wizards): " + siteId);
         }
         if (authzCheck) {
            Site site = (Site)siteMap.get(siteId);
            try {
               SortableListObject wiz = new SortableListObject(wizard.getId().getValue(), 
                     wizard.getName(), wizard.getDescription(), 
                     wizard.getOwner(), site, wizard.getType(), wizard.getModified());
               retWizards.add(wiz);
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + wizard.getOwner().getId() + " does not exist.");
            }
            
         }
      }
      return retWizards;
   }

   protected List<SortableListObject> verifyMatrices(List allMatrices, Map siteMap) {
      List<SortableListObject> retMatrices = new ArrayList<SortableListObject>();
      Map<String, Boolean> sitePermCache = new HashMap<String, Boolean>();
      
      for (Iterator i = allMatrices.iterator(); i.hasNext();) {
         Scaffolding scaffolding = (Scaffolding)i.next();
         Id siteId = scaffolding.getWorksiteId();
         //make sure that the target site gets tested
         getAuthzManager().pushAuthzGroups(siteId.getValue());

         //Need to make sure the current user can actually have one of their own here, 
         // so only check if they can "use"
//       But check from the cache first
         Boolean authzCheck = sitePermCache.get(siteId.getValue());
         if (authzCheck == null) {
            authzCheck = getAuthzManager().isAuthorized(MatrixFunctionConstants.CAN_USE_SCAFFOLDING, 
                  getIdManager().getId(scaffolding.getReference()));
            sitePermCache.put(siteId.getValue(), authzCheck);
            logger.debug("Pushing site into cache for WizardListGenerator (matrices): " + siteId);
         }
         if (authzCheck) {
            Site site = (Site)siteMap.get(siteId.getValue());
            try {
               SortableListObject scaff = new SortableListObject(scaffolding.getId().getValue(), 
                     scaffolding.getTitle(), scaffolding.getDescription(), 
                     scaffolding.getOwner(), site, MatrixFunctionConstants.SCAFFOLDING_PREFIX, null);
               retMatrices.add(scaff);
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + scaffolding.getOwner().getId() + " does not exist.");
            }
            
         }
      }
      return retMatrices;
   }
   
   public boolean isNewWindow(Object entry) {
      return false;
   }

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    *
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry) {
      SortableListObject obj = (SortableListObject)entry;
      String urlEnd = "";
      String placement = "";
      
      if (obj.getTypeRaw().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL) || 
            obj.getTypeRaw().equals(WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL)) {
         urlEnd = "/osp.wizard.run.helper/runWizardGuidance?session.CURRENT_WIZARD_ID=" + 
            obj.getId() + "&session.WIZARD_USER_ID=" + SessionManager.getCurrentSessionUserId() +
            "&session.WIZARD_RESET_CURRENT=true";
      
         ToolConfiguration toolConfig = obj.getSite().getToolForCommonId("osp.wizard");
         placement = toolConfig.getId();
      }
      else if (obj.getTypeRaw().equals(MatrixFunctionConstants.SCAFFOLDING_PREFIX)) {
         urlEnd = "/viewMatrix.osp?1=1&scaffolding_id=" + obj.getId();
         
         ToolConfiguration toolConfig = obj.getSite().getToolForCommonId("osp.matrix");
         placement = toolConfig.getId();
      }
      
      String url = ServerConfigurationService.getPortalUrl() + "/directtool/" + placement + urlEnd;
      
      return url;
   }
   
   public Map getToolParams(Object entry) {
      Map<String, Object> params = new HashMap<String, Object>();
      SortableListObject obj = (SortableListObject) entry;      
      
      if (obj.getTypeRaw().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL) || 
            obj.getTypeRaw().equals(WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL)) {
         params.put(WIZARD_ID_PARAM, obj.getId());
      }
      else if (obj.getTypeRaw().equals(MatrixFunctionConstants.SCAFFOLDING_PREFIX)) {
         params.put(MATRIX_ID_PARAM, obj.getId());
      }
      
      params.put(SITE_ID_PARAM, idManager.getId(obj.getSite().getId()));
      return params;
   }

    public ToolConfiguration getToolInfo(Map request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setToolState(String toolId, Map request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
   /**
    * @return the wizardManager
    */
   public WizardManager getWizardManager() {
      return wizardManager;
   }
   /**
    * @param wizardManager the wizardManager to set
    */
   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
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
   /**
    * @return the authzManager
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }
   /**
    * @param authzManager the authzManager to set
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
   /**
    * @return the displayTypes
    */
   public List getDisplayTypes() {
      return displayTypes;
   }
   /**
    * @param displayTypes the displayTypes to set
    */
   public void setDisplayTypes(List displayTypes) {
      this.displayTypes = displayTypes;
   }
   /**
    * @return the matrixManager
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   /**
    * @param matrixManager the matrixManager to set
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/model/SimpleToolPermissionManager.java $
* $Id:SimpleToolPermissionManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.authz.cover.FunctionManager;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.mgt.ToolPermissionManager;
import org.theospi.portfolio.worksite.intf.ToolEventListener;
import org.theospi.portfolio.worksite.model.SiteTool;

public class SimpleToolPermissionManager implements ToolEventListener, ToolPermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Map defaultPermissions;
   private Map siteTypePermissions;
   private AgentManager agentManager;
   private PermissionManager permissionManager;
   private String permissionEditName;
   private IdManager idManager;
   private List functions = new ArrayList();

   // set default permissions with some overrides from sakai.properties
   // example:
   // glossary.permissions.override=false
   // glossary.permissions.map.count=2
   // glossary.permissions.map.1=Coordinator
   // glossary.permissions.map.1.siteTypes=portfolio,project
   // glossary.permissions.map.1.value.count=4
   // glossary.permissions.map.1.value.1=osp.help.glossary.delete
   // glossary.permissions.map.1.value.2=osp.help.glossary.add
   // glossary.permissions.map.1.value.3=osp.help.glossary.edit
   // glossary.permissions.map.1.value.4=osp.help.glossary.export
   // glossary.permissions.map.2=Assistant
   // glossary.permissions.map.2.siteTypes=portfolio
   // glossary.permissions.map.2.value.count=4
   // glossary.permissions.map.2.value.1=osp.help.glossary.delete
   // glossary.permissions.map.2.value.2=osp.help.glossary.add
   // glossary.permissions.map.2.value.3=osp.help.glossary.edit
   // glossary.permissions.map.2.value.4=osp.help.glossary.export
   public void init() {
      logger.debug("Entering SimpleToolPermissionManager.init() for " + getPermissionEditName());
      
      String roles[] = ServerConfigurationService.getStrings(getPermissionEditName() + ".permissions.map");
      if (roles != null)
      {
         //Check to see if we should override the system configured defaults or 
         // just append the custom ones to the list
         String override = ServerConfigurationService.getString(getPermissionEditName() + ".permissions.override");
         if (override.equalsIgnoreCase("true"))
            defaultPermissions.clear();
         
         for (int i = 0; i < roles.length; i++)
         {
            String perms[] = ServerConfigurationService.getStrings(getPermissionEditName() + ".permissions.map." + String.valueOf(i+1) + ".value");
            String siteTypes = ServerConfigurationService.getString(getPermissionEditName() + ".permissions.map." + String.valueOf(i+1) + ".siteTypes");
            String siteTypeList[] = siteTypes.split(",");
            List permList =  Arrays.asList(perms);
            CrossRealmRoleWrapper crrw = new CrossRealmRoleWrapper();
            Map roleMap = new HashMap();
            List roleList = new ArrayList();
            roleList.add(roles[i]);
            for (int j=0; j < siteTypeList.length; j++) {
               roleMap.put(siteTypeList[j], roleList);
            }
            crrw.setSiteTypeRoles(roleMap);
            defaultPermissions.put(crrw, permList);
            
         }
      }
      
      registerFunctions();
   }

   protected void registerFunctions() {
      for (Iterator<String> i=functions.iterator();i.hasNext();) {
         FunctionManager.registerFunction(i.next());
      }
   }

   /**
    * sets up the default perms for a tool.  Use's the tool id as the qualifier.
    * Assumes that if no perms exist for the tool, the perms should be set to the defaults.
    * @param toolConfig
    */
   public void toolSiteChanged(ToolConfiguration toolConfig) {
      Id toolId = getIdManager().getId(toolConfig.getId());
      PermissionsEdit edit = new PermissionsEdit();
      edit.setQualifier(toolId);
      edit.setName(getPermissionEditName());
      Site containingSite = toolConfig.getContainingPage().getContainingSite();
      if (!isSpecial(containingSite)) {
         edit.setSiteId(containingSite.getId());
         getPermissionManager().fillPermissions(edit);
         if (edit.getPermissions() == null || edit.getPermissions().size() == 0){
            createDefaultPermissions(edit.getSiteId(), toolId, containingSite.getType());
         }
      }
   }
   
   /**
    * sets up the default perms for a helper tool.  Uses the site id as the qualifier.
    * Assumes that if no perms exist for the tool, the perms should be set to the defaults.
    * @param site
    */
   public void helperSiteChanged(Site site) {
      if (!isSpecial(site)) {
         Id siteId = getIdManager().getId(site.getId());
         PermissionsEdit edit = new PermissionsEdit();
         edit.setQualifier(siteId);
         edit.setName(getPermissionEditName());
         edit.setSiteId(site.getId());
         getPermissionManager().fillPermissions(edit);
         if (edit.getPermissions() == null || edit.getPermissions().size() == 0){
            createDefaultPermissions(edit.getSiteId(), siteId, site.getType());
         }
      }
   }

   protected boolean isSpecial(Site site) {
      return SiteService.getInstance().isSpecialSite(site.getId());         
   }

   public void toolRemoved(SiteTool siteTool) {
      // todo remove all authz
   }

   protected void createDefaultPermissions(String worksiteId, Id qualifier, String siteType) {
//      PermissionsEdit edit = setupPermissions(worksiteId, qualifier, siteType);
//      edit.setName(getPermissionEditName());
//      getPermissionManager().updatePermissions(edit);
   }

   protected PermissionsEdit setupPermissions(String worksiteId, Id qualifier, String siteType) {

      List permissions = new ArrayList();
      PermissionsEdit edit = new PermissionsEdit();
      edit.setQualifier(qualifier);
      edit.setSiteId(worksiteId);
      Map permissionsMap = getSiteTypePermissionsMap(siteType);
      for (Iterator i=permissionsMap.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         String agentName = (String)entry.getKey();
         List functions = (List)entry.getValue();
         processFunctions(permissions, agentName, functions, worksiteId);
      }

      edit.setPermissions(permissions);
      return edit;
   }

   protected Map getSiteTypePermissionsMap(String siteType) {
      if (getSiteTypePermissions() != null) {
         Map map = (Map) getSiteTypePermissions().get(siteType);
         if (map != null) {
            return map;
         }
      }

      Map perms = getDefaultPermissions();
      Map returned = new Hashtable();

      for (Iterator i=perms.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         CrossRealmRoleWrapper roleWrapper = (CrossRealmRoleWrapper) entry.getKey();
         if (roleWrapper.getSiteTypeRoles().get(siteType) != null) {
            List roles = (List) roleWrapper.getSiteTypeRoles().get(siteType);
            for (Iterator j=roles.iterator();j.hasNext();) {
               returned.put(j.next(), entry.getValue());
            }
         }
      }

      return returned;
   }

   protected void processFunctions(List permissions, String roleName, List functions, String worksiteId) {
      Agent agent = getAgentManager().getWorksiteRole(roleName, worksiteId);

      if (agent != null) {
         for (Iterator i=functions.iterator();i.hasNext();) {
            Permission permission = new Permission();
            permission.setAgent(agent);
            permission.setFunction((String)i.next());
            permissions.add(permission);
         }
      }
   }

   public Map getDefaultPermissions() {
      return defaultPermissions;
   }

   public void setDefaultPermissions(Map defaultPermissions) {
      this.defaultPermissions = defaultPermissions;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public PermissionManager getPermissionManager() {
      return permissionManager;
   }

   public void setPermissionManager(PermissionManager permissionManager) {
      this.permissionManager = permissionManager;
   }

   public String getPermissionEditName() {
      return permissionEditName;
   }

   public void setPermissionEditName(String permissionEditName) {
      this.permissionEditName = permissionEditName;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public List getFunctions(PermissionsEdit edit) {
      return functions;
   }

   public List getReadOnlyQualifiers(PermissionsEdit edit) {
      return new ArrayList();
   }

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool) {
      getPermissionManager().duplicatePermissions(
         getIdManager().getId(fromTool.getId()),
         getIdManager().getId(toTool.getId()),
         toTool.getContainingPage().getContainingSite());
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public Map getSiteTypePermissions() {
      return siteTypePermissions;
   }

   public void setSiteTypePermissions(Map siteTypePermissions) {
      this.siteTypePermissions = siteTypePermissions;
   }

}

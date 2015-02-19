/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/security/impl/sakai/AgentManager.java $
 * $Id: AgentManager.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.metaobj.security.impl.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.AnonymousAgent;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserNotDefinedException;

import org.apache.commons.codec.digest.DigestUtils;

public class AgentManager extends SecurityBase implements org.sakaiproject.metaobj.shared.mgt.AgentManager {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private UserDirectoryService directoryService;

   private int PASSWORD_LENGTH = 8;

   /**
    * @param id
    * @return
    */
   public Agent getAgent(Id id) {
      //TODO: Figure out what's different between an all-null AgentWrapper and "anonymous"
      if (id != null && id.equals(AnonymousAgent.ANONYMOUS_AGENT_ID)) {
         return getAnonymousAgent();
      }

      Throwable exception = null;

      Agent returned = null;

      if (id != null) {
         try {
            returned = getAgentInternal(id.getValue());
         }
         catch (IdUnusedException e) {
            exception = e;
         }
      }

      if (returned != null) {
         return returned;
      }

      if (exception != null) {
         logger.warn("Unable to find user: " + id + " " + exception.toString());
      }
      else {
         logger.warn("Unable to find user: " + id);
      }

      return new AgentWrapper(null, null, null, null, null);
   }

   public Agent getAgent(String username) {
      if (username.equals(AnonymousAgent.ANONYMOUS_AGENT_ID.getValue())) {
         return getAnonymousAgent();
      }
      Throwable exception = null;

      Agent returned = null;
      try {
         returned = getAgentInternal(username);
      }
      catch (IdUnusedException e) {
         exception = e;
      }

      if (returned != null) {
         return returned;
      }

      if (exception != null) {
         logger.warn("Unable to find user: " + username + " " + exception.toString());
      }
      else {
         logger.warn("Unable to find user: " + username);
      }

      return new AgentWrapper(null, null, null, null, null);
   }

   public Agent getWorksiteRole(String roleName) {
      return getWorksiteRole(roleName, ToolManager.getCurrentPlacement().getContext());
   }

   public List getWorksiteRoles(String siteId) {
      List roles = new ArrayList();
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" + siteId);
         for (Iterator i = siteRealm.getRoles().iterator(); i.hasNext();) {
            Role sakaiRole = (Role) i.next();
            roles.add(convertRole(sakaiRole, siteRealm));
         }
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      return roles;
   }

   public Agent getWorksiteRole(String roleName, String siteId) {
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               siteId);

         Role sakaiRole = siteRealm.getRole(roleName);

         return convertRole(sakaiRole, siteRealm);
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public Agent getTempWorksiteRole(String roleName, String siteId) {
      Id roleId = getOspiIdManager().getId("/site/" + siteId + "/" + roleName);

      //TODO using the same value for both id and eid
      return new RoleWrapper(roleId, roleId, null, null);
   }
   
   public Agent getRealmRole(String roleName, String realmId) {
      try {
         AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);
         Role role = realm.getRole(roleName);
         return convertRole(role, realm);
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected Agent getAgentInternal(String username) throws IdUnusedException {
      if (username == null) {
         return null;
      }
      if (username.startsWith("/site/")) {
         return getRole(username);
      }

      User sakaiUser = null;

      try {
    	    sakaiUser = getDirectoryService().getUser(username);
      }
      catch (UserNotDefinedException e) {
    	    throw new IdUnusedException(e.getId());
      }
      return morphAgent(sakaiUser);
   }

   protected Agent getRole(String username) throws IdUnusedException {
      String roleName;
      String siteId;

      int pos = username.lastIndexOf('/');
      siteId = username.substring(0, pos);
      roleName = username.substring(pos + 1);

      Role role = null;
      AuthzGroup realm = null;
      try {
        realm = AuthzGroupService.getAuthzGroup(siteId);
      }
      catch (GroupNotDefinedException e) {
    	    throw new IdUnusedException (e.getId());
      }
      if (realm != null)
          role = realm.getRole(roleName);

      if (role == null || realm == null) {
         return null;
      }
      return convertRole(role, realm);
   }

   /**
    * @param siteId
    * @return list of agents that are participants in the given siteId
    */
   public List getWorksiteAgents(String siteId) {
      List users = new ArrayList();
      List participants = new ArrayList();
      String realmId = "/site/" + siteId;
      try {
         AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);
         users.addAll(getDirectoryService().getUsers(realm.getUsers()));
         Collections.sort(users);
         for (int i = 0; i < users.size(); i++) {
            User user = (User) users.get(i);
            participants.add(morphAgent(user));
         }
      }
      catch (GroupNotDefinedException e) {
         logger.warn("" + realmId);
      }
      return participants;
   }

   public Agent getAnonymousAgent() {
      return new AnonymousAgent();
   }

   public Agent getAdminAgent() {
      return getAgent("admin");
   }

   /**
    * if type is null return all records
    *
    * @param type   added typed list
    * @param object
    * @return
    */
   public List findByProperty(String type, Object object) {
      if (type.equals(TYPE_DISPLAY_NAME)) {
         try {
            List users = new ArrayList();
            users.add(morphAgent(getDirectoryService().getUser((String) object)));
            return users;
         }
         catch (UserNotDefinedException e) {
            // user not found, return null
            return null;
         }
      }
      else if (type.equals(TYPE_EID)) {
         try {
            List users = new ArrayList();
            users.add(morphAgent(getDirectoryService().getUserByEid((String) object)));
            return users;
         }
         catch (UserNotDefinedException e) {
            // user not found, return null
            return null;
         }
      }
      else if (type.equals(TYPE_EMAIL)) {
         List users = new ArrayList();
         Collection directoryUsers = getDirectoryService().findUsersByEmail((String) object);
         if ((directoryUsers == null) || (directoryUsers.isEmpty())) {
            return null;
         }
         for (Iterator i = directoryUsers.iterator(); i.hasNext();) {
            User u = (User) i.next();
            users.add(morphAgent(u));
         }
         return users;
      }
      return null;
   }

   public Agent createAgent(String displayName, Id id)	{
      try {
         UserEdit uEdit = getDirectoryService().addUser(id.getValue(), id.getValue());
         uEdit.setEmail(id.getValue());  // id is the user email
         uEdit.setType("guest"); 

         String pw = passwordGenerator();
         uEdit.setPassword(pw);
         getDirectoryService().commitEdit(uEdit);

         AgentImpl agent = new AgentImpl();
         agent.setDisplayName(displayName);
         agent.setRole(Agent.ROLE_GUEST);
         agent.setId(id);
         agent.setPassword(pw);
         agent.setMd5Password(DigestUtils.md5Hex(pw));

         return agent;
      }
      catch (Exception e) {
         logger.warn("Unable to create guest user: " + id, e);
         return null;
      }
   }

   private String passwordGenerator() {
      Random rand = new Random();
      char[] pass = new char[PASSWORD_LENGTH];
      for (int i = 0; i < PASSWORD_LENGTH; i++) {
         int val = rand.nextInt(52);
         // need to add appropriate values to get to the ascii values
         if (val < 26)
            val += 65;
         else
            val += 71;
         pass[i] = (char) val;
      }
      return new String(pass);
   }

   public UserDirectoryService getDirectoryService() {
      return directoryService;
   }

   public void setDirectoryService(UserDirectoryService directoryService) {
      this.directoryService = directoryService;
   }

   /**
    * @param agent
    */
   public void deleteAgent(Agent agent) {
      throw new UnsupportedOperationException();
   }

   public void updateAgent(Agent agent) {
      throw new UnsupportedOperationException();
   }

}

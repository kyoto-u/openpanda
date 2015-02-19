/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/security/impl/sakai/RoleWrapper.java $
 * $Id: RoleWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008, 2009 The Sakai Foundation
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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspRole;

public class RoleWrapper implements OspRole {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id = null;
   private Id eid = null;
   private Role sakaiRole = null;
   private AuthzGroup sakaiRealm = null;

   public RoleWrapper(Id id, Id eid, Role sakaiRole, AuthzGroup sakaiRealm) {
      this.id = id;
      this.eid = eid;
      this.sakaiRealm = sakaiRealm;
      this.sakaiRole = sakaiRole;
   }

   public Id getId() {
      return id;
   }
   
   public Id getEid() {
      return eid;
   }

   public Artifact getProfile() {
      return null;
   }

   public void setProfile(Artifact profile) {

   }

   public Object getProperty(String key) {
      return null;
   }

   public String getDisplayName() {
      return getSakaiRole().getId();
   }

   public boolean isInRole(String role) {
      return role.equals(id.getValue());
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return id.getValue();
   }

   public List getWorksiteRoles(String worksiteId) {
      return new ArrayList();
   }

   public List getWorksiteRoles() {
      return new ArrayList();
   }

   public boolean isRole() {
      return true;
   }

   public AuthzGroup getSakaiRealm() {
      return sakaiRealm;
   }

   public Role getSakaiRole() {
      return sakaiRole;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof RoleWrapper)) {
         return false;
      }

      final RoleWrapper roleWrapper = (RoleWrapper) o;

      if (id != null ? !id.equals(roleWrapper.id) : roleWrapper.id != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return (id != null ? id.hashCode() : 0);
   }

   /**
    * Returns the name of this principal.
    *
    * @return the name of this principal.
    */
   public String getName() {
      return getDisplayName();
   }

   /**
    * gets the name of the role idependant of the site it belongs to
    *
    * @return
    */
   public String getRoleName() {
      return getSakaiRole().getId();
   }
	
   public String getPassword() {
      return null; // not implemented
   }
}

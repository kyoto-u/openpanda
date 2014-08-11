/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/model/PermissionsEdit.java $
* $Id:PermissionsEdit.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

public class PermissionsEdit {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String name = null;
   private Id qualifier = null;
   private List permissions = null;
   private String siteId;

   public PermissionsEdit() {
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public PermissionsEdit(String name, Id qualifier, String siteId, List permissions) {
      this.name = name;
      this.permissions = permissions;
      this.qualifier = qualifier;
      this.siteId = siteId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List getPermissions() {
      return permissions;
   }

   public void setPermissions(List permissions) {
      this.permissions = permissions;
   }

   public Id getQualifier() {
      return qualifier;
   }

   public void setQualifier(Id qualifier) {
      this.qualifier = qualifier;
   }

   public Object clone() {
      return new PermissionsEdit(getName(), getQualifier(), getSiteId(), getPermissions());
   }
}

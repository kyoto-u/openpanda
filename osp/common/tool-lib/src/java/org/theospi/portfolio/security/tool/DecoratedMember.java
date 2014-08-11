/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/security/tool/DecoratedMember.java $
* $Id:DecoratedMember.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.tool;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 3:56:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedMember {

   private Agent base;
   private AudienceTool parent;
   private boolean selected = false;

   public DecoratedMember(AudienceTool parent, Agent base) {
      this.base = base;
      this.parent = parent;
   }

   public String getDisplayName() {
      if (base.isRole()) {
         return parent.getMessageFromBundle("decorated_role_format",
               new Object[]{base.getDisplayName()});
      }
      else {
         if (base.getEid() != null) {
            return parent.getMessageFromBundle("decorated_user_format",
                new Object[]{base.getDisplayName(), base.getEid().getValue()});
         }
         else {
             return parent.getMessageFromBundle("decorated_guest_format",
               new Object[]{base.getDisplayName()});
         }

      }
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public Agent getBase() {
      return base;
   }

   public void setBase(Agent base) {
      this.base = base;
   }

   public AudienceTool getParent() {
      return parent;
   }

   public void setParent(AudienceTool parent) {
      this.parent = parent;
   }

   public Agent getRole() {
      List roles = getBase().getWorksiteRoles(getParent().getSite().getId());
      if (roles.size() > 0) {
         return (Agent)roles.get(0);
      }
      return null;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof DecoratedMember)) {
         return false;
      }

      final DecoratedMember decoratedMember = (DecoratedMember) o;

      if (!base.equals(decoratedMember.base)) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return base.hashCode();
   }
   
   public String getEmail() {
       if (base.isRole()) {
           return "ROLE" + "." + base.getDisplayName();
       } else {
           try {
               return UserDirectoryService.getUserByEid(base.getEid().toString()).getEmail();
           }

           catch (UserNotDefinedException e) {
               return "";
           }
       }

   }
}

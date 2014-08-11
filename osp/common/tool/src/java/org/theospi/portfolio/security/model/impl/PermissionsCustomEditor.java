/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool/src/java/org/theospi/portfolio/security/model/impl/PermissionsCustomEditor.java $
* $Id:PermissionsCustomEditor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.model.impl;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.theospi.portfolio.security.model.Permission;

public class PermissionsCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor  {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager = null;

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(new ArrayList());
      } else {
         String[] items = text.split(",");
         List permissions = new ArrayList();
         for (int i = 0; i < items.length; i++) {
            if (items[i].length() == 0) {
               continue;
            }
            String[] values = items[i].split("~");
            String role = values[0];
            String function = values[1];
            Agent agent = getAgentManager().getWorksiteRole(role);
            permissions.add(new Permission(agent, function));
         }
         setValue(permissions);
      }
   }

   public String getAsText() {
      if (getValue() == null) {
         return null;
      }

      StringBuilder buffer = new StringBuilder();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         buffer.append(i.next().toString());
         if (i.hasNext()) {
            buffer.append(",");
         }
      }
      return buffer.toString();
   }

   public Class getType() {
      return List.class;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/security/mgt/impl/AddPermissionTool.java $
 * $Id: AddPermissionTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.security.mgt.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.mgt.PermissionManager;

public class AddPermissionTool {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private PermissionManager manager;
   private Map additionalTools;

   public void init() {
      if (manager instanceof PermissionManagerImpl) {
         ((PermissionManagerImpl) manager).getTools().putAll(additionalTools);
      }
   }

   public Map getAdditionalTools() {
      return additionalTools;
   }

   public void setAdditionalTools(Map additionalTools) {
      this.additionalTools = additionalTools;
   }

   public PermissionManager getManager() {
      return manager;
   }

   public void setManager(PermissionManager manager) {
      this.manager = manager;
   }
}

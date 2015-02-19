/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/matrix/tool/src/java/org/theospi/portfolio/matrix/control/AbstractMatrixController.java $
* $Id: AbstractMatrixController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.matrix.control;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.utils.mvc.impl.servlet.AbstractFormController;

public abstract class AbstractMatrixController extends AbstractFormController implements Controller {

   private MatrixManager matrixManager;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private AgentManager agentManager = null;
   
   
   protected Boolean isMaintainer(){
      return Boolean.valueOf(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext())));
   }
   
   /**
    * @return
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param manager
    */
   public void setAuthManager(AuthenticationManager manager) {
      authManager = manager;
   }

   /**
    * @return
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param manager
    */
   public void setIdManager(IdManager manager) {
      idManager = manager;
   }

   /**
    * @return
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   /**
    * @param facade
    */
   public void setAuthzManager(AuthorizationFacade facade) {
      authzManager = facade;
   }

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   /**
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   /**
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }
   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }
}

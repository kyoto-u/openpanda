/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/common/api-impl/src/java/org/theospi/portfolio/style/impl/StyleAuthorizerImpl.java $
* $Id: StyleAuthorizerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.theospi.portfolio.style.impl;

import java.util.List;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;
import org.theospi.portfolio.style.StyleFunctionConstants;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;

public class StyleAuthorizerImpl implements ApplicationAuthorizer {
   
   private StyleManager styleManager;
   private IdManager idManager;
   private List functions;
   
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent,
         String function, Id id) {
      if (function.equals(StyleFunctionConstants.CREATE_STYLE)) {
         return Boolean.valueOf(facade.isAuthorized(agent, function, id));
      } else if (function.equals(StyleFunctionConstants.EDIT_STYLE)) {
         return isStyleAuth(facade, id, agent, function);
      } else if (function.equals(StyleFunctionConstants.PUBLISH_STYLE)) {
         return isStyleAuth(facade, id, agent, function);
      } else if (function.equals(StyleFunctionConstants.GLOBAL_PUBLISH_STYLE)) {
         return isStyleAuth(facade, id, agent, function);
      } else if (function.equals(StyleFunctionConstants.SUGGEST_GLOBAL_PUBLISH_STYLE)) {
         return isStyleAuth(facade, id, agent, function);
      } else if (function.equals(StyleFunctionConstants.DELETE_STYLE)) {
         return isStyleAuth(facade, id, agent, function);
      } else {
         return null;
      }
   }
   
   protected Boolean isStyleAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function){
      Style style = getStyleManager().getLightWeightStyle(qualifier);
      if (style == null) {
         return Boolean.valueOf(facade.isAuthorized(function,qualifier));
      }
      //owner can do anything
      if (agent.equals(style.getOwner())){
         return Boolean.valueOf(true);
      }
      Id siteId = getIdManager().getId(style.getSiteId());
      return Boolean.valueOf(facade.isAuthorized(function,siteId));
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }

}

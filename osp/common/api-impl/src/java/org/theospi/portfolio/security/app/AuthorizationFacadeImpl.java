/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/app/AuthorizationFacadeImpl.java $
* $Id:AuthorizationFacadeImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 4:31:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationFacadeImpl implements AuthorizationFacade, AppAuthFacade {

   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
     .getLog(getClass());

   private AuthenticationManager authManager = null;
   private AuthorizationFacade explicitAuthz = null;

   private Map authorizorMap=new HashMap();

   /**
    * order needs to be maintained here.
    */
   private List applicationAuthorizers = new ArrayList();

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(function, id)) {
         throw new AuthorizationFailedException(function, id);
      }
   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(agent, function, id)) {
         throw new AuthorizationFailedException(agent, function, id);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isAuthorized(String function, Id id) {
      return isAuthorized(authManager.getAgent(), function, id);
   }

   /**
    * Builds and caches an ordered list of all ApplicationAuthorizors that consume a given function.
    * @param function - function
    * @return List - of pertinent ApplicationAuthorizors
    */
   protected synchronized List registerFunction(String function) {
      if (logger.isDebugEnabled()) {
         logger.debug("registerFunction("+function+")");
      }

      List result=new ArrayList();

      for (Iterator i = getApplicationAuthorizers().iterator(); i.hasNext();) {
         OrderedAuthorizer appAuth = (OrderedAuthorizer)i.next();
         if (appAuth.getAuthorizer().getFunctions().contains(function)) {
            if (logger.isDebugEnabled()) {
               logger.debug("registerFunction: adding "+appAuth.getClass().getName()+")");
            }
            result.add(appAuth.getAuthorizer());
         }
      }
      authorizorMap.put(function,result);
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isAuthorized(Agent agent, String function, Id id) {

      if (logger.isDebugEnabled()) {
         logger.debug("isAuthorized("+agent+","+function+","+id+")");
      }

      List appAuthz=(List) authorizorMap.get(function);

      if (appAuthz==null) {
         synchronized(authorizorMap) {
            appAuthz=(List) authorizorMap.get(function);
            if (appAuthz==null) {
               appAuthz=registerFunction(function);
            }
         }
      }

      for (Iterator i = appAuthz.iterator(); i.hasNext();) {
         ApplicationAuthorizer appAuth =
            (ApplicationAuthorizer) i.next();

         if (logger.isDebugEnabled()) {
            logger.debug("isAuthorized() is calling: "+appAuth.getClass().getName());
         }
         Boolean auth = appAuth.isAuthorized(getExplicitAuthz(), agent, function, id);

         if (auth != null) {
            return auth.booleanValue();
         }
      }

      // fall through to explicit authorization,. no application is aware
      // of this request.
      return getExplicitAuthz().isAuthorized(agent, function, id);
   }

   /**
    * at least one param must be non-null
    *
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public List getAuthorizations(Agent agent, String function, Id id) {
      return getExplicitAuthz().getAuthorizations(agent, function, id);
   }

   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      getExplicitAuthz().createAuthorization(agent, function, id);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      getExplicitAuthz().deleteAuthorization(agent, function, id);
   }

   public void deleteAuthorizations(Id qualifier) {
      getExplicitAuthz().deleteAuthorizations(qualifier);
   }

   public void pushAuthzGroups(Collection authzGroups) {
      getExplicitAuthz().pushAuthzGroups(authzGroups);
   }

   public void pushAuthzGroups(String siteId) {
      getExplicitAuthz().pushAuthzGroups(siteId);
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public AuthorizationFacade getExplicitAuthz() {
      return explicitAuthz;
   }

   public void setExplicitAuthz(AuthorizationFacade explicitAuthz) {
      this.explicitAuthz = explicitAuthz;
   }

   public List getApplicationAuthorizers() {
      return applicationAuthorizers;
   }

   public void setApplicationAuthorizers(List applicationAuthorizers) {
      this.applicationAuthorizers = applicationAuthorizers;
   }

   public void addAppAuthorizers(List appAuthorizers) {
      SortedSet sorted = new TreeSet();
      sorted.addAll(getApplicationAuthorizers());
      sorted.addAll(appAuthorizers);
      setApplicationAuthorizers(new ArrayList(sorted));
   }

}

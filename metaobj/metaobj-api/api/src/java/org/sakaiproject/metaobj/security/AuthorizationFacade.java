/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/security/AuthorizationFacade.java $
 * $Id: AuthorizationFacade.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.security;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 11:28:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AuthorizationFacade {

   public void checkPermission(String function, Id id) throws AuthorizationFailedException;

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException;

   /**
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(String function, Id id);

   /**
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(Agent agent, String function, Id id);

   /**
    * at least one param must be non-null
    *
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public List getAuthorizations(Agent agent, String function, Id id);

   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id);

   public void deleteAuthorization(Agent agent, String function, Id id);

   public void deleteAuthorizations(Id qualifier);

   public void pushAuthzGroups(Collection authzGroups);

   void pushAuthzGroups(String siteId);
}

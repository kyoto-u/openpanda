/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/mgt/OspHttpAccessBase.java $
* $Id:OspHttpAccessBase.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.security.mgt;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.theospi.portfolio.security.AuthorizationFacade;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 12:49:04 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OspHttpAccessBase extends HttpAccessBase {

   private AuthorizationFacade authzManager;

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
                            Collection copyrightAcceptedRefs) 
   		throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      ReferenceParser parser =
            new ReferenceParser(ref.getReference(), ref.getEntityProducer());
      authzManager.pushAuthzGroups(parser.getSiteId());

      super.handleAccess(req, res, ref, copyrightAcceptedRefs);
   }


   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
}

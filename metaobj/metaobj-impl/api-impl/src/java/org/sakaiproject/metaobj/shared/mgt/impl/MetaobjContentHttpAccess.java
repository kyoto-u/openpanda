/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/msub/iu.edu/oncourse/trunk/metaobj/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/MetaobjHttpAccess.java $
 * $Id: MetaobjHttpAccess.java 60509 2009-04-21 21:27:16Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.impl;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 1:25:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaobjContentHttpAccess extends HttpAccessBase {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
                            Collection copyrightAcceptedRefs) throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      ToolSession toolSession = SessionManager.getCurrentToolSession();

      if (toolSession == null) {
         toolSession = SessionManager.getCurrentSession().getToolSession(req.getSession(true).hashCode() + "");
         SessionManager.setCurrentToolSession(toolSession);
      }

      super.handleAccess(req, res, ref, copyrightAcceptedRefs); 
   }

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
	   logger.debug(".checkSource(): " + parser.getId());
	   logger.debug(".checkSource(); ref: " + ref.getReference());
	   getStructuredArtifactDefinitionManager().checkFormAccess(parser.getId());
   }

   public void init() {
      logger.info("init()");
   }

   public void setStructuredArtifactDefinitionManager(
		   StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
	   this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
	   return structuredArtifactDefinitionManager;
   }

}

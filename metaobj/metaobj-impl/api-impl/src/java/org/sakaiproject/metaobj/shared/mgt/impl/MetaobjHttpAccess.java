/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/MetaobjHttpAccess.java $
 * $Id: MetaobjHttpAccess.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.ToolException;
import org.sakaiproject.tool.cover.ActiveToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.util.Web;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.content.cover.ContentHostingService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 1:25:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaobjHttpAccess extends HttpAccessBase {

   protected final transient Log logger = LogFactory.getLog(getClass());

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
                            Collection copyrightAcceptedRefs) throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      String helperId = "sakai.metaobj.formView";

      ToolSession toolSession = SessionManager.getCurrentToolSession();

      if (toolSession == null) {
         toolSession = SessionManager.getCurrentSession().getToolSession(req.getSession(true).hashCode() + "");
         SessionManager.setCurrentToolSession(toolSession);
      }

      ActiveTool helperTool = ActiveToolManager.getActiveTool(helperId);
      toolSession.setAttribute(helperTool.getId() + Tool.HELPER_DONE_URL,
         "javascript:alert('hi')");

      toolSession.setAttribute(ResourceEditingHelper.ATTACHMENT_ID, ref.getId());

      String context = req.getContextPath() + req.getServletPath();
      String toolPath = "/formView.osp";
      try {
          helperTool.help(req, res, context, toolPath);
      }
      catch (ToolException e) {
         throw new EntityPermissionException(SessionManager.getCurrentSessionUserId(), 
                                             ContentHostingService.AUTH_RESOURCE_READ, ref.getReference());
      }
   }

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {

   }

   public void init() {
      logger.info("init()");
   }

}

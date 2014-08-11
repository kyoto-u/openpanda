/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/MatrixHttpAccess.java $
* $Id:MatrixHttpAccess.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.model.impl;

import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.security.mgt.OspHttpAccessBase;
import org.theospi.portfolio.style.mgt.StyleManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 5:30:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixHttpAccess extends OspHttpAccessBase {

   private IdManager idManager;
   private MatrixManager matrixManager;
   private StyleManager styleManager;

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {

      getMatrixManager().checkPageAccess(parser.getId());
   }

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      ToolSession toolSession = SessionManager.getCurrentToolSession();

      if (toolSession == null) {
         toolSession = SessionManager.getCurrentSession().getToolSession(req.getSession(true).hashCode() + "");
         SessionManager.setCurrentToolSession(toolSession);
      }

      toolSession.setAttribute(FormHelper.FORM_STYLES,
         getStyleManager().createStyleUrlList(getStyleManager().getStyles(getIdManager().getId(createParser(ref).getId()))));

      super.handleAccess(req, res, ref, copyrightAcceptedRefs);
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }

}

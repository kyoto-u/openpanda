/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool/src/java/org/theospi/portfolio/help/control/HelpController.java $
* $Id:HelpController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.control;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.help.model.HelpManager;
import org.theospi.portfolio.security.AuthorizationFacade;

public class HelpController implements Controller {
   private HelpManager helpManager;
   private WorksiteManager worksiteManager;
   private AuthorizationFacade authzManager;
   private IdManager idManager;
   private List toolInit;

   public static final String TRANSFER_CONTROLLER_SESSION_MESSAGE = "transferMessage";
   public static final String TRANSFER_MESSAGE_IMPORT_SUCCESS = "msgImportSuccess";
   public static final String TRANSFER_MESSAGE_IMPORT_BAD_FILE = "msgImportBadFile";
   public static final String TRANSFER_MESSAGE_IMPORT_FAILED = "msgImportFailed";
   public static final String TRANSFER_MESSAGE_IMPORT_BAD_PARSE = "msgImportBadParse";
   
   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      return new ModelAndView("success");
   }

   public HelpManager getHelpManager() {
      return helpManager;
   }

   public void setHelpManager(HelpManager helpManager) {
      this.helpManager = helpManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public List getToolInit() {
      return toolInit;
   }

   public void setToolInit(List toolInit) {
      this.toolInit = toolInit;
   }
}

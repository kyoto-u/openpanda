/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool/src/java/org/theospi/portfolio/security/control/PermissionsController.java $
* $Id:PermissionsController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.control;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.model.PermissionsEdit;

public class PermissionsController extends AbstractFormController implements FormController, LoadObjectController {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private PermissionManager permissionManager;
   private SecurityService securityService;
   
   private static final String REALM_UPDATE_PERMISSION = "realm.upd";

   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    *
    * @param request
    * @param command
    * @param errors
    * @return Map
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new Hashtable();

      PermissionsEdit edit = (PermissionsEdit)command;
      model.put("toolFunctions", getPermissionManager().getAppFunctions(edit));
      model.put("roles", getPermissionManager().getWorksiteRoles(edit));

      if (request.get("message") != null) {
         model.put("message", request.get("message"));
      }
      
      return model;
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
		if (request.get(getPermissionManager().RETURN_KEY) != null
				&& !"".equals(request.get(getPermissionManager().RETURN_KEY))
				&& request.get(getPermissionManager().RETURN_KEY) instanceof String)
			return new ModelAndView("helperDone", (String) request
					.get(getPermissionManager().RETURN_KEY), request
					.get(getPermissionManager().RETURN_KEY_VALUE));
		else
			return new ModelAndView("helperDone");
	}

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      PermissionsEdit edit = (PermissionsEdit)incomingModel;
      edit.setSiteId(ToolManager.getCurrentPlacement().getContext());
      
      return getPermissionManager().fillPermissions(edit, useQualifier(edit));
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PermissionsEdit edit = (PermissionsEdit)requestModel;
      
      getSecurityService().pushAdvisor(new SimpleSecurityAdvisor(
    		  SessionManager.getCurrentSessionUserId(), 
    		  REALM_UPDATE_PERMISSION, "/realm/" + edit.getQualifier().getValue()));
      
      getPermissionManager().updatePermissions(edit, useQualifier(edit));
      
      getSecurityService().popAdvisor();
      
      Map returnMap = new HashMap();
      returnMap.put("toolPermissionSaved", request.get("toolPermissionsSaved"));

      if (request.get(getPermissionManager().RETURN_KEY) != null
				&& !"".equals(request.get(getPermissionManager().RETURN_KEY)))
			returnMap.put(request.get(getPermissionManager().RETURN_KEY),
					request.get(getPermissionManager().RETURN_KEY_VALUE));

		return new ModelAndView("helperDone", returnMap);
	}
   
   /**
    * Determine if the qualifier is different than the site id
    * @param edit
    * @return
    */
   private boolean useQualifier(PermissionsEdit edit) {
	   boolean retVal = false;
	   if (edit.getSiteId() != null && edit.getQualifier() != null) {
		   retVal = !edit.getSiteId().equals(edit.getQualifier().getValue());
	   }
	   return retVal;
   }

   public PermissionManager getPermissionManager() {
      return permissionManager;
   }

   public void setPermissionManager(PermissionManager permissionManager) {
      this.permissionManager = permissionManager;
   }

   public SecurityService getSecurityService() {
	   return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
	   this.securityService = securityService;
   }
   
   /**
	 * A simple SecurityAdviser that can be used to override permissions for one user for one function.
	 */
	protected class SimpleSecurityAdvisor implements SecurityAdvisor
	{
		protected String m_userId;
		protected String m_function;
		protected String m_reference;

		public SimpleSecurityAdvisor(String userId, String function, String reference)
		{
			m_userId = userId;
			m_function = function;
			m_reference = reference;
		}

		public SecurityAdvice isAllowed(String userId, String function, String reference)
		{
			SecurityAdvice rv = SecurityAdvice.PASS;
			if (m_userId.equals(userId) && m_function.equals(function) && m_reference.equals(reference))
			{
				rv = SecurityAdvice.ALLOWED;
			}
			return rv;
		}
	}
}

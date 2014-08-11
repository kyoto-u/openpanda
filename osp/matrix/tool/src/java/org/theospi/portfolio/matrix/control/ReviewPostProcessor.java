/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ReviewPostProcessor.java $
* $Id: ReviewPostProcessor.java 68687 2009-11-09 16:45:06Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.workflow.model.Workflow;

public class ReviewPostProcessor  implements Controller {
   
   private IdManager idManager = null;
   private SecurityService securityService;
   private AuthenticationManager authManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map<String, Object> model = new HashMap<String, Object>();
      String strId = (String)request.get("workflowId");
      Id objId = idManager.getId((String)request.get("objId"));
      String view = "success";
      if (strId != null) {
         Id id = idManager.getId(strId);
         String manager = (String)request.get("manager");
         getWorkflowEnabledManager(manager).processWorkflow(id, objId);
         model.put("page_id", objId);
      }
      else {
         Set workflows = (Set)session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS);
         List wfList = Arrays.asList(workflows.toArray());
         if(!isSuperUser()){
      	   wfList = removeResetWorkflow(wfList);
         }
         Collections.sort(wfList, Workflow.getComparator());
         
         model.put("workflows", wfList);
         model.put("obj_id", objId);
         model.put("manager", request.get("manager"));
         view="enter";
      }
      return new ModelAndView(view, model);
   }

   private List removeResetWorkflow(List wfList){
	   List newList = new ArrayList();
	   for (Iterator iter = wfList.iterator(); iter.hasNext();) {
		   Workflow wf = (Workflow) iter.next();
		   if(!wf.getTitle().equals("Return Workflow")){
			  newList.add(wf);
		   }
	   }
	   
	   return newList;
   }
   
   private boolean isSuperUser(){
	     return (getSecurityService().isSuperUser(authManager.getAgent().getId().getValue())) ? true : false;
   }
   
   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   
         
   protected WorkflowEnabledManager getWorkflowEnabledManager(String name) {
      return (WorkflowEnabledManager)ComponentManager.get(name);
   }

public SecurityService getSecurityService() {
	return securityService;
}

public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
}

public AuthenticationManager getAuthManager() {
	return authManager;
}

public void setAuthManager(AuthenticationManager authManager) {
	this.authManager = authManager;
}  

}

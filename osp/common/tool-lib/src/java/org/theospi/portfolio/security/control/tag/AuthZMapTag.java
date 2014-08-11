/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/security/control/tag/AuthZMapTag.java $
* $Id:AuthZMapTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.control.tag;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.model.AuthZMap;

public class AuthZMapTag extends TagSupport {

   private Object qualifier;
   private boolean useSite;
   private String prefix;
   private String var;
   private int scope;

   protected final transient Log logger = LogFactory.getLog(getClass());

   public AuthZMapTag() {
      init();
   }

   /**
    * Default processing of the start tag, returning SKIP_BODY.
    *
    * @return SKIP_BODY
    * @throws javax.servlet.jsp.JspException if an error occurs while processing this tag
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */

   public int doStartTag() throws JspException {
      Map authz = new AuthZMap(getAuthzFacade(), getPrefix(), evaluateQualifier());

      pageContext.setAttribute(getVar(), authz, getScope());

      return super.doStartTag();
   }

   /**
    * Release state.
    *
    * @see javax.servlet.jsp.tagext.Tag#release()
    */

   public void release() {
      super.release();
      init();
   }

   protected AuthorizationFacade getAuthzFacade() {
      return (AuthorizationFacade)ComponentManager.getInstance().get("authzManager");
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Id evaluateQualifier() throws JspException {
      Id qual;
      if (isUseSite()) {
         qual = getIdManager().getId(ToolManager.getCurrentPlacement().getContext());
      }
      else if (qualifier == null){
         qual = getIdManager().getId(ToolManager.getCurrentPlacement().getId());
      }
      else {
    	  if (qualifier instanceof Id) {
    		  qual = (Id)qualifier;
    	  }
    	  else {
    		  qual = getIdManager().getId((String)qualifier);
    	  }         
      }
      return qual;
   }

   public void setQualifier(Object qualifier) {
      this.qualifier = qualifier;
   }

   public int getScope() {
      return scope;
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public void setScope(String scope) {
      if (scope.equalsIgnoreCase("page"))
         this.scope = PageContext.PAGE_SCOPE;
      else if (scope.equalsIgnoreCase("request"))
         this.scope = PageContext.REQUEST_SCOPE;
      else if (scope.equalsIgnoreCase("session"))
         this.scope = PageContext.SESSION_SCOPE;
      else if (scope.equalsIgnoreCase("application"))
         this.scope = PageContext.APPLICATION_SCOPE;

      // TODO: Add error handling?  Needs direction from spec.
   }

   // initializes internal state
   protected void init() {
      var = "can";
      scope = PageContext.PAGE_SCOPE;
      qualifier = null;
      setUseSite(false);
   }

   protected IdManager getIdManager() {
      return (IdManager)ComponentManager.getInstance().get("idManager");
   }

   public boolean isUseSite() {
      return useSite;
   }

   public void setUseSite(boolean useSite) {
      this.useSite = useSite;
   }

}

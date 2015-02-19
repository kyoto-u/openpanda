/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/tag/SakaiUrlTag.java $
 * $Id: SakaiUrlTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control.tag;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.rt.core.UrlTag;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.util.Web;

public class SakaiUrlTag extends UrlTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   protected boolean includeParams = true;
   protected boolean includeQuestion = true;

   public int doStartTag() throws JspException {
      int returned = super.doStartTag();

      if (includeParams) {
         Map params = getPortalParamManager().getParams(pageContext.getRequest());

         if (params.size() == 0) {
            addParameter("1", "1");
         }

         for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            try {
               addParameter((String) entry.getKey(), (String) entry.getValue());
            }
            catch (RuntimeException exp) {
               logger.error("", exp);
               throw exp;
            }
         }
      }

      return returned;
   }

   public int doEndTag() throws JspException {
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();


      req.setAttribute(Tool.NATIVE_URL, null); //signal to WrappedRequest that we want the Sakai managed

      String toolContext = req.getContextPath();
      String toolPath = req.getPathInfo();

      req.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);


      if (context == null && value.startsWith("/")) {
      }
      else {
         if (!value.startsWith(".")) { //excluding dots allows relative links such as ../../sakai-legacy/images/pdf.gif
            //value="/member/"+value;
            value = "/" + value;
         }
      }

      context = toolContext;

      logger.debug("tag value=" + value +
            " context=" + context + " web util val=" + Web.returnUrl(req, value));

      if (!includeQuestion) {
         BodyContent content = pageContext.pushBody();

         int returnVal = super.doEndTag();

         String contentValue = content.getString();
         int question = contentValue.indexOf('?');
         if (question != -1) {
            contentValue = contentValue.replace('?', '&');
         }

         logger.debug("tag before: " + contentValue);
         contentValue = ((HttpServletResponse)
               pageContext.getResponse()).encodeURL(contentValue);
         logger.debug("tag after: " + contentValue);

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         }
         catch (IOException ex) {
            throw new OspException("", ex);
         }

         return returnVal;
      }
      else {
         return super.doEndTag();
      }

   }

   public void setIncludeParams(boolean includeParams) {
      this.includeParams = includeParams;
   }

   public void setIncludeQuestion(boolean includeQuestion) {
      this.includeQuestion = includeQuestion;
   }

   public void release() {
      context = null;
      includeParams = true;
      includeQuestion = true;
      super.release();
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }

}

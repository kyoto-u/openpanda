/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/tag/Message.java $
 * $Id: Message.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;
import org.apache.taglibs.standard.tag.rt.fmt.MessageTag;
import org.sakaiproject.metaobj.shared.model.OspException;

public class Message extends MessageTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String text = null;
   private String localVar = null;
   private int localScope = PageContext.PAGE_SCOPE;  // 'scope' attribute


   // Releases any resources we may have (or inherit)
   public void release() {
      localScope = PageContext.PAGE_SCOPE;
      text = null;
      localVar = null;
      super.release();
   }

   public void setScope(String scope) {
      super.setScope(scope);
      localScope = Util.getScope(scope);
   }

   public void setVar(String var) {
      super.setVar(var);
      localVar = var;
   }

   public int doEndTag() throws JspException {

      BodyContent content = null;

      if (localVar == null) {
         content = pageContext.pushBody();
      }

      int returnVal = super.doEndTag();

      if (localVar != null) {
         String varValue = (String) pageContext.getAttribute(localVar, localScope);
         if (varValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
               varValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
               text != null) {
            varValue = text;
            pageContext.setAttribute(localVar, varValue, localScope);
         }
      }
      else {
         String contentValue = content.getString();
         contentValue = content.getString();
         if (contentValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
               contentValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
               text != null) {
            contentValue = text;
         }

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         }
         catch (IOException ex) {
            throw new OspException("", ex);
         }
      }

      return returnVal;
   }


   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }
}

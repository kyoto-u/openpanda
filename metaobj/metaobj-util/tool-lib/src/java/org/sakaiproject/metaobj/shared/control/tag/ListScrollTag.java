/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/tag/ListScrollTag.java $
 * $Id: ListScrollTag.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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
import java.text.MessageFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.util.ResourceLoader;

public class ListScrollTag extends BodyTagSupport {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ResourceLoader myResources = new ResourceLoader("org.sakaiproject.metaobj.bundle.Messages");

   private String listUrl;
   private ListScroll listScroll;
   private String listScrollExpression = DEFAULT_LIST_SCROLL;
   private static final String DEFAULT_LIST_SCROLL = "${listScroll}";
   private String className;

   /**
    * Default processing of the start tag returning EVAL_BODY_BUFFERED.
    *
    * @return EVAL_BODY_BUFFERED
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.BodyTag#doStartTag
    */

   public int doStartTag() throws JspException {
      evaluateExpressions();
      return EVAL_BODY_BUFFERED;
   }

   /**
    * Default processing of the end tag returning EVAL_PAGE.
    *
    * @return EVAL_PAGE
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.Tag#doEndTag
    */

   public int doEndTag() throws JspException {
   
      // Don't display paging if only one page needed to display
      if ( listScroll.getHideOnePageScroll() &&
           listScroll.getFirstItem() <= 1  &&
           listScroll.getLastItem() == listScroll.getTotal() )
      {
         listScroll = null;
         listScrollExpression = DEFAULT_LIST_SCROLL;
         listUrl = null;
         return EVAL_PAGE;
      }
      
      JspWriter writer = pageContext.getOut();

      String first = myResources.getString("listScroll_first");
      String previous = myResources.getString("listScroll_previous");
      String next = myResources.getString("listScroll_next");
      String last = myResources.getString("listScroll_last");
      String viewing = MessageFormat.format(myResources.getString("listScroll_viewing"), 
            new Object[]{listScroll.getFirstItem(), listScroll.getLastItem(), listScroll.getTotal()});
      
      try {

         writer.write("<div ");
         if (className != null) {
            writer.write("class=\"" + className + "\"");
         }
         writer.write(">");

         //  <input type="button" value="Next" onclick="window.document.location='url'">
         writer.write("<div class=\"instruction\">");
         writer.write(viewing);
         writer.write("</div>");
         writer.write("<input type=\"button\" value=\"" + first + "\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=0");
         writer.write("\'\"");
         if (listScroll.getPrevIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" />");

         writer.write("&nbsp;");

         writer.write("<input type=\"button\" value=\"" + previous + "\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=" + listScroll.getPrevIndex());
         writer.write("\'\"");
         if (listScroll.getPrevIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" />");

         
         
         
         
         writer.write("<input type=\"button\" value=\"" + next + "\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=" + listScroll.getNextIndex());
         writer.write("\'\"");
         if (listScroll.getNextIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" />");

         writer.write("&nbsp;");

         writer.write("<input type=\"button\" value=\"" + last + "\" onclick=\"window.document.location=\'");
         writer.write(listUrl + "&" + ListScroll.STARTING_INDEX_TAG + "=" + Integer.MAX_VALUE);
         writer.write("\'\"");
         if (listScroll.getNextIndex() == -1) {
            writer.write(" disabled=\"disabled\" ");
         }
         writer.write(" />");

         writer.write("</div>");

         writer.write("<br />");

      }
      catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }

      listScroll = null;
      listScrollExpression = DEFAULT_LIST_SCROLL;
      listUrl = null;

      return EVAL_PAGE;
   }

   /**
    * Release state.
    *
    * @see javax.servlet.jsp.tagext.Tag#release
    */

   public void release() {
      super.release();
      listScroll = null;
      listScrollExpression = DEFAULT_LIST_SCROLL;
      listUrl = null;
   }

   protected void evaluateExpressions() throws JspException {
      if (listScroll == null) {
         listScroll = (ListScroll) ExpressionUtil.evalNotNull("listScroll", "listScroll", listScrollExpression,
               ListScroll.class, this, pageContext);
      }

      listUrl = (String) ExpressionUtil.evalNotNull("listScroll",
            "listUrl", listUrl, String.class, this, pageContext);
   }

   public void setListScroll(String listScrollExpression) {
      this.listScrollExpression = listScrollExpression;
   }

   public void setListScroll(ListScroll listScroll) {
      this.listScroll = listScroll;
   }

   public void setListUrl(String listUrl) {
      this.listUrl = listUrl;
   }

   public void setClassName(String className) {
      this.className = className;
   }
}

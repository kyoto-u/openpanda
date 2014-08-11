/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool-lib/src/java/org/theospi/portfolio/help/control/GlossaryTag.java $
* $Id:GlossaryTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;
import org.theospi.portfolio.help.helper.HelpTagHelper;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Matches keywords in the body to those in the glossary,
 * and places links around the keywords which link the glossary entries.
 * The glossary entry text is also available via a hover.
 * Linking or hovering can be turned on/off using the link and hover attributes
 * Linking is on by default, hover is off be default.
 * Use true/false as the attributes values to modify these from the defaults.
 * Hovering requires the following two lines be placed in the jsp, making sure
 * the path to the eport.js file is correct: <br/><br/>
 * <p/>
 * &lt;script language="JavaScript" src="../js/eport.js"&gt;&lt;/script&gt; <br/>
 * &lt;div id="tooltip" style="position:absolute;visibility:hidden;border:1px solid black;font-size:10px;layer-background-color:lightyellow;background-color:lightyellow;padding:1px"&gt;&lt;/div&gt; <br/>
 */
public class GlossaryTag extends BodyTagSupport {
   private boolean firstOnly = false;
   private boolean hover = false;
   private boolean link = true;
   private String glossaryLink;
   protected final Log logger = LogFactory.getLog(getClass());

   private static final String TERMS_TAG = "org.theospi.portfolio.help.control.GlossaryTag.terms";

   /**
    * Default processing of the start tag returning EVAL_BODY_BUFFERED.
    *
    * @return EVAL_BODY_BUFFERED
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.BodyTag#doStartTag
    */

   public int doStartTag() throws JspException {
      try {
         pageContext.getOut().write("" +
            "<div id=\"tooltip\" style=\"position:absolute;visibility:hidden;" +
            "border:1px solid black;font-size:10px;layer-background-color:lightyellow;" +
            "background-color:lightyellow;padding:1px\"></div>" +
            "<script type=\"text/javascript\" src=\"/osp-common-tool/js/eport.js\"></script>");
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      return super.doStartTag();
   }

   public int doAfterBody() throws JspException {
      BodyContent body = getBodyContent();
      JspWriter out = body.getEnclosingWriter();
      Reader reader = body.getReader();
      Set termSet = getTerms();
      GlossaryEntry[] terms = new GlossaryEntry[termSet.size()];
      terms = (GlossaryEntry[]) termSet.toArray(terms);

      try {
         
         HelpTagHelper.renderHelp(reader, body.getBufferSize() - body.getRemaining(), out, terms, firstOnly, hover, link);
      } catch (IOException ioe) {
         logger.error(ioe.getMessage(), ioe);
      } finally {
         body.clearBody(); // Clear for next evaluation
      }
      return (SKIP_BODY);
   }

   protected Set getTerms() {
      if (pageContext.getAttribute(TERMS_TAG) != null) {
         return (Set)pageContext.getAttribute(TERMS_TAG);
      }
      Set returned = getHelpManager().getSortedWorksiteTerms();
      pageContext.setAttribute(TERMS_TAG, returned);
      return returned;
   }

   public HelpManager getHelpManager() {
      return (HelpManager) ComponentManager.getInstance().get("helpManager");
   }


   public boolean isHover() {
      return hover;
   }

   public void setHover(boolean hover) {
      this.hover = hover;
   }

   public boolean isLink() {
      return link;
   }

   public void setLink(boolean link) {
      this.link = link;
   }

   public String getGlossaryLink() {
      return glossaryLink;
   }

   public void setGlossaryLink(String glossaryLink) {
      this.glossaryLink = glossaryLink;
   }

   public boolean isFirstOnly() {
      return firstOnly;
   }

   public void setFirstOnly(boolean firstOnly) {
      this.firstOnly = firstOnly;
   }
}

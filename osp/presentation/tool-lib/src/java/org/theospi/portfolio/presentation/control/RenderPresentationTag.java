/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool-lib/src/java/org/theospi/portfolio/presentation/control/RenderPresentationTag.java $
* $Id:RenderPresentationTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 26, 2004
 * Time: 7:00:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class RenderPresentationTag extends TagSupport {

   private Transformer template = null;
   private Document doc = null;
   private URIResolver uriResolver;

   public final int doStartTag() throws JspException {
      if(doc != null) {
         // transform xml and spit it out
         try {
            template.setURIResolver(uriResolver);
            template.transform(new JDOMSource(doc),
               new StreamResult(pageContext.getOut()));
         } catch (TransformerException e) {
            throw new JspException(e);
         }
      }
      return EVAL_BODY_INCLUDE;
   }

   public Transformer getTemplate() {
      return template;
   }

   public void setTemplate(Object template) {
      setTemplate((Transformer) template);
   }

   public void setTemplate(Transformer template) {
      this.template = template;
   }

   public Document getDoc() {
      return doc;
   }

   public void setDoc(Object doc) {
      if(doc instanceof Document)
         setDoc((Document) doc);
   }

   public void setDoc(Document doc) {
      this.doc = doc;
   }

   public URIResolver getUriResolver() {
      return uriResolver;
   }

   public void setUriResolver(URIResolver uriResolver) {
      this.uriResolver = uriResolver;
   }
   
   public void setUriResolver(Object uriResolver) {
      setUriResolver((URIResolver) uriResolver);
   }

}

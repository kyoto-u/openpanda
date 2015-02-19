/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/XsltView.java $
 * $Id: XsltView.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.metaobj.shared.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import org.springframework.web.servlet.view.xslt.AbstractXsltView;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 7, 2006
 * Time: 1:56:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltView extends AbstractXsltView {

   public static final String VIEW_DOCUMENT = "viewDocument";

   protected Source createXsltSource(Map model, String root, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
      Document doc = (Document) model.get(VIEW_DOCUMENT);
      return new JDOMSource(doc);
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/ForwardView.java $
 * $Id: ForwardView.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 18, 2004
 * Time: 3:12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForwardView extends AbstractUrlBasedView {

   private String action = "";

   /**
    * Subclasses must implement this method to render the view.
    * <p>The first take will be preparing the request: This may include setting
    * the model elements as request attributes, e.g. in the case of a JSP view.
    *
    * @param model    combined output Map, with dynamic values taking precedence
    *                 over static attributes
    * @param request  current HTTP request
    * @param response current HTTP response
    * @throws Exception if rendering failed
    */
   protected void renderMergedOutputModel(Map model, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
      request.setAttribute("action", getUrl());

      for (Iterator i = model.entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         request.setAttribute((String) entry.getKey(), entry.getValue());
      }

      request.getRequestDispatcher(getUrl()).forward(request, response);
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }
}

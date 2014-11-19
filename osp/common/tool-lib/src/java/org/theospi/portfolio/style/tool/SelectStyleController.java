/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/tool-lib/src/java/org/theospi/portfolio/style/tool/SelectStyleController.java $
* $Id: SelectStyleController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.theospi.portfolio.style.tool;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;

public class SelectStyleController extends AbstractStyleController {
   
   protected final Log logger = LogFactory.getLog(getClass());
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      String styleId = (String)request.get("style_id");
      String selectAction = (String)request.get("selectAction");
      
      if (selectAction != null && selectAction.equals("on")) {
         Style style = getStyleManager().getStyle(getIdManager().getId(styleId));
         session.put(StyleHelper.CURRENT_STYLE, style);
      }
      else if (selectAction != null && selectAction.equals("off")){
         session.remove(StyleHelper.CURRENT_STYLE);
         session.put(StyleHelper.UNSELECTED_STYLE, "true");
      }

      return new ModelAndView("success");
   }
}

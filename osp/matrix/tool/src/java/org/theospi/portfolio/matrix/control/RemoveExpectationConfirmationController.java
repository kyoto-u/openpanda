/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/RemoveExpectationConfirmationController.java $
* $Id:RemoveExpectationConfirmationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;


public class RemoveExpectationConfirmationController implements CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
   private IdManager idManager = null;
   
   public Object formBackingObject(Map request, Map session, Map application) {
      return new HashMap();
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      Id id = idManager.getId((String)request.get("scaffoldingCell_id"));
      
      Map model = new HashMap();
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      if (cancel != null) {
         viewName = "cancel";
         model.put("scaffoldingCell_id", id);
      }
      else if (next != null) {
         viewName = (String)request.get("finalDest");
         String params = (String)request.get("params");
         if (!params.equals("")) {
            String[] paramsList = params.split(":");
            for (int i=0; i<paramsList.length; i++) {
               String[] pair = paramsList[i].split("=");
               String val = null;
               if (pair.length>1)
                  val = pair[1];
               model.put(pair[0], val);
            }
         }
      }

      return new ModelAndView(viewName, model);
   }


   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

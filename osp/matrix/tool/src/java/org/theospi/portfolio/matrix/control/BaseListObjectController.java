
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseListObjectController.java $
* $Id: BaseListObjectController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.CriterionTransport;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.LevelTransport;
import org.theospi.portfolio.matrix.model.Scaffolding;


/**
 * @author chmaurer
 */
public abstract class BaseListObjectController implements FormController, LoadObjectController, CustomCommandController {
   
  
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      String index = (String)request.get("index");
      if (index != null) {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
               EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         Object obj = null;
         if (incomingModel instanceof CriterionTransport) {
            obj = new CriterionTransport((Criterion)sessionBean.getScaffolding().getCriteria().get(
                  Integer.parseInt(index)));
         }
         else if (incomingModel instanceof LevelTransport) {
            obj = new LevelTransport((Level)sessionBean.getScaffolding().getLevels().get(
                     Integer.parseInt(index)));
         }
         
         return obj;
      }
      return incomingModel;
   }
   
  
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      Map model = new HashMap();
      
      String action = (String) request.get("updateAction");
      if (action != null) {
         String index = (String)request.get("index");
         if (requestModel instanceof CriterionTransport) {
            CriterionTransport obj = (CriterionTransport) requestModel;
            if (index == null) {
               scaffolding.getCriteria().add(new Criterion(obj));
            }
            else {
               int idx = Integer.parseInt(index);
               Criterion criterion = (Criterion) scaffolding.getCriteria().get(idx);
               criterion.copy(obj);
               scaffolding.getCriteria().set(idx, criterion);
            }
            sessionBean.setScaffolding(scaffolding);
         }
         else if (requestModel instanceof LevelTransport) {
            LevelTransport obj = (LevelTransport) requestModel;
            if (index == null) {
               scaffolding.add(new Level(obj));
            }
            else {
               int idx = Integer.parseInt(index);
               Level level = (Level)scaffolding.getLevels().get(idx);
               level.copy(obj);
               scaffolding.getLevels().set(idx, level);
            }
            sessionBean.setScaffolding(scaffolding);
         }         
         
         session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
               sessionBean);         
      }

      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");

      return new ModelAndView("success", model);
   }
}

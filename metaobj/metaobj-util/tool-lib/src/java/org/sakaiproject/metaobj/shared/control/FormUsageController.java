/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/FormUsageController.java $
* $Id: FormUsageController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

/**
 * 
 */
package org.sakaiproject.metaobj.shared.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.FormConsumptionDetail;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author chrismaurer
 *
 */
public class FormUsageController extends AbstractStructuredArtifactDefinitionController implements LoadObjectController {


   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      StructuredArtifactDefinitionBean home = (StructuredArtifactDefinitionBean) incomingModel;
      home = getStructuredArtifactDefinitionManager().loadHome(home.getId());
      return home;
   }
   
   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request,
         Map session, Map application, Errors errors) {
      Map<String, Object> model = new HashMap<String, Object>();
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;
      
      Collection<FormConsumptionDetail> usage = getStructuredArtifactDefinitionManager().findFormUsage(sad);
      model.put("formName", sad.getDescription());
      model.put("formId", sad.getId());
      model.put("usage",
            getListScrollIndexer().indexList(request, model, new ArrayList<FormConsumptionDetail>(usage)));
      
      return new ModelAndView("success", model);
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/PublishStructuredArtifactDefinitionController.java $
 * $Id: PublishStructuredArtifactDefinitionController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.Map;

import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class PublishStructuredArtifactDefinitionController extends AbstractStructuredArtifactDefinitionController implements LoadObjectController {
   public final static String SITE_PUBLISH_ACTION = "site_publish";
   public final static String GLOBAL_PUBLISH_ACTION = "global_publish";
   public final static String SUGGEST_GLOBAL_PUBLISH_ACTION = "suggest_global_publish";

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;
      if (sad.getAction().equals(SITE_PUBLISH_ACTION)) {
         sad.setSiteState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
         checkPermission(SharedFunctionConstants.PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }
      if (sad.getAction().equals(GLOBAL_PUBLISH_ACTION)) {
         sad.setGlobalState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
         sad.setSiteId(null);
         checkPermission(SharedFunctionConstants.PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }
      if (sad.getAction().equals(SUGGEST_GLOBAL_PUBLISH_ACTION)) {
         sad.setGlobalState(StructuredArtifactDefinitionBean.STATE_WAITING_APPROVAL);
         checkPermission(SharedFunctionConstants.SUGGEST_GLOBAL_PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }

      return prepareListView(request, sad.getId().getValue());
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      return prepareListView(request, null);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) incomingModel;
      String action = sad.getAction();
      sad = getStructuredArtifactDefinitionManager().loadHome(sad.getId());
      sad.setAction(action);
      return sad;
   }

}

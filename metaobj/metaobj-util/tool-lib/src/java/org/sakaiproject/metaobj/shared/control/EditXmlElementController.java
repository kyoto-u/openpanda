/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/EditXmlElementController.java $
 * $Id: EditXmlElementController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.tool.api.ToolSession;

import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: John Ellis
 * <p/>
 * Date: Apr 20, 2004
 * <p/>
 * Time: 3:31:25 PM
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class EditXmlElementController extends XmlControllerBase
   implements CustomCommandController, LoadObjectController, CancelableController {

   protected final Log logger = LogFactory.getLog(getClass());

   private ArtifactFinder artifactFinder;
   private IdManager idManager;

   public Object formBackingObject(Map request, Map session, Map application) {
      ElementBean returnedBean;
      if (session.get(EditedArtifactStorage.STORED_ARTIFACT_FLAG) == null) {
         if (getSchemaName(session) != null) {
            StructuredArtifactHomeInterface home = getSchema(session);
            StructuredArtifact bean = (StructuredArtifact)home.createInstance();
            bean.setParentFolder((String)session.get(FormHelper.PARENT_ID_TAG));
            EditedArtifactStorage sessionBean = new EditedArtifactStorage(bean.getCurrentSchema(),
               bean);
            session.put(EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY,
               sessionBean);
            returnedBean = bean;
         }
         else {
            return new ElementBean();
         }
      }
      else {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)session.get(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);
         returnedBean = sessionBean.getCurrentElement();
      }

      if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) != null ||
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         retrieveFileAttachments(request, session, returnedBean);
      }

      return returnedBean;
   }
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(EditedArtifactStorage.STORED_ARTIFACT_FLAG) == null) {
         StructuredArtifact bean = null;

         if (session.get(ResourceToolAction.ACTION_PIPE) == null) {
            Id id;
            String idString = getContentHostingService().getUuid(
               (String) session.get(ResourceEditingHelper.ATTACHMENT_ID));

            id = getIdManager().getId(idString);

            bean = (StructuredArtifact) getArtifactFinder().load(id);
         }
         else {
            ReadableObjectHome home = getSchema(session);
            if ( home != null )
               bean = (StructuredArtifact) home.load(null);
         }

         if ( bean == null ) {
            logger.warn(this+".fillBackingObject schema not found (perhaps multiple submits): " + getSchemaName(session));
            return new StructuredArtifact();
         }
         
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE,
            ((StructuredArtifactHomeInterface)bean.getHome()).getTypeId());

         EditedArtifactStorage sessionBean = new EditedArtifactStorage(bean.getCurrentSchema(),
            bean);
         session.put(EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY,
            sessionBean);
         return bean;
      }
      else {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)session.get(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);
         return sessionBean.getCurrentElement();
      }           
   }
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      ElementBean bean = (ElementBean) requestModel;

      if (request.get("cancel") != null) {
         session.put(FormHelper.RETURN_ACTION_TAG, FormHelper.RETURN_ACTION_CANCEL);
         session.remove(EditedArtifactStorage.STORED_ARTIFACT_FLAG);
         return new ModelAndView("success");
      }
      else if (request.get("submitButton") == null) {
         return handleNonSubmit(bean, request, session, application, errors);
      }
      
      // ignore -- perhaps multiple submits -- error logged in formBackingObject()
      if ( bean.getCurrentSchema() == null ) {
         return new ModelAndView("success"); 
      }
      
      getValidator().validate(bean, errors, true);
      if (errors.hasErrors()) {
         logger.warn(this+"validate failed for: " + getSchemaName(session));
         return new ModelAndView("success");
      }
      
      WritableObjectHome home = getSchema(session);
      try {
         home.store((StructuredArtifact)bean);
      } catch (PersistenceException e) {
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
            e.getDefaultMessage());
      }
      session.put(FormHelper.FORM_SAVE_SUCCESS, ((StructuredArtifact)bean).getId().getValue());
      session.put(FormHelper.RETURN_REFERENCE_TAG, ((StructuredArtifact)bean).getId().getValue());
      session.put(FormHelper.RETURN_ACTION_TAG, FormHelper.RETURN_ACTION_SAVE);
      session.remove(EditedArtifactStorage.STORED_ARTIFACT_FLAG);
      return new ModelAndView("success", "schema",
         getSchemaName(session));
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

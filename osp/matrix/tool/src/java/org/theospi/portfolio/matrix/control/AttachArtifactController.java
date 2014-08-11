/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/AttachArtifactController.java $
* $Id:AttachArtifactController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.WizardPage;

public class AttachArtifactController implements Controller, LoadObjectController, CancelableController {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private SessionManager sessionManager;
   private MatrixManager matrixManager;
   private IdManager idManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   public static final Object ATTACH_ARTIFACT_FORM = "attachArtifactForm";

   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      CellAndNodeForm form = (CellAndNodeForm)incomingModel;
      String page_id = (String)request.get("page_id");
      if (page_id != null) {
         form.setPage_id(page_id);
         session.put(getModelName(), form);

         WizardPage page = matrixManager.getWizardPage(idManager.getId(page_id));
         
         Set attachments = page.getAttachments();
         List files = new ArrayList();
         for (Iterator iter=attachments.iterator(); iter.hasNext();) {
        	   Attachment att = (Attachment)iter.next();
        	   String id = getContentHosting().resolveUuid(att.getArtifactId().getValue());
            Reference ref = getEntityManager().newReference(contentHosting.getReference(id));
            files.add(ref);
         }
         
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         
         //Start in user's resources area
         //osp-ui-05
         String siteId = SiteService.getUserSiteId(getSessionManager().getCurrentSessionUserId());
         String collectionId = getContentHosting().getSiteCollection(siteId);
         session.put(FilePickerHelper.DEFAULT_COLLECTION_ID, collectionId);
      }
      else {
         form = (CellAndNodeForm)session.get(
               getModelName());
      }
      return form;
   }

   protected String getModelName() {
      return this.getClass().getName() + ".model";
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      CellAndNodeForm form = (CellAndNodeForm)requestModel;
      WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
      boolean sessionPage = true;
      if (page == null) {
         sessionPage = false;
         //page = getMatrixManager().getWizardPage(getIdManager().getId(form.getPage_id()));
      }
      page = getMatrixManager().getWizardPage(getIdManager().getId(form.getPage_id()));
      attachArtifacts(session,  page);
      if (sessionPage) {
         session.put(WizardPageHelper.WIZARD_PAGE, getMatrixManager().getWizardPage(page.getId()));
      }
      session.remove(getModelName());
      request.put(ATTACH_ARTIFACT_FORM, form);
      // track all the attachments here...
      return new ModelAndView("success", "page_id", page.getId());
   }

   protected void attachArtifacts(Map session, WizardPage page) {
      List files = (List)session.get(
            FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      Set attachments = page.getAttachments();
      List existing = new ArrayList();
      for (Iterator i = attachments.iterator();i.hasNext();) {
         Attachment attach = (Attachment)i.next();
         existing.add(attach.getArtifactId());
      }

      for (Iterator i = files.iterator();i.hasNext();) {
         Object node = i.next();
         Reference ref = null;
         if (node instanceof Reference) {
            ref = (Reference)node;
         }
         Attachment attachment = getMatrixManager().attachArtifact(
               page.getId(), ref);
         existing.remove(attachment.getArtifactId());
      }

      for (Iterator i = existing.iterator();i.hasNext();) {
         Id oldAttachment = (Id)i.next();
         getMatrixManager().detachArtifact(page.getId(), oldAttachment);
      }

      session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
   }


   public boolean isCancel(Map request) {
      ToolSession toolSession = getSessionManager().getCurrentToolSession();
      boolean returned = toolSession.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) != null;
      return returned;
   }

   public ModelAndView processCancel(Map request, Map session,
                                     Map application, Object command, Errors errors) throws Exception {
      CellAndNodeForm form = (CellAndNodeForm)command;
      session.remove(getModelName());

      Map model = new Hashtable();
      model.put("page_id", form.getPage_id());
      model.put("readOnlyMatrix", Boolean.valueOf("false"));

      return new ModelAndView("cancel", model);
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

public ContentHostingService getContentHosting() {
	return contentHosting;
}

public void setContentHosting(ContentHostingService contentHosting) {
	this.contentHosting = contentHosting;
}

public IdManager getIdManager() {
	return idManager;
}

public void setIdManager(IdManager idManager) {
	this.idManager = idManager;
}

public MatrixManager getMatrixManager() {
	return matrixManager;
}

public void setMatrixManager(MatrixManager matrixManager) {
	this.matrixManager = matrixManager;
}

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}

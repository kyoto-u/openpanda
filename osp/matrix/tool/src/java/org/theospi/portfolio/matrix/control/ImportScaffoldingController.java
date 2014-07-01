/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ImportScaffoldingController.java $
* $Id: ImportScaffoldingController.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingUploadForm;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.assignment.AssignmentHelper;

public class ImportScaffoldingController implements Controller, FormController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private MatrixManager matrixManager;
   private HomeFactory homeFactory;
   private SessionManager sessionManager;
   private ToolManager toolManager;
   private SiteService siteService;

   public Map referenceData(Map request, Object command, Errors errors) {
      ScaffoldingUploadForm scaffoldingForm = (ScaffoldingUploadForm)command;

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Reference ref = (Reference)refs.get(0);
         scaffoldingForm.setUploadedScaffolding(ref);
         Node file = getMatrixManager().getNode(ref);
         scaffoldingForm.setScaffoldingFileName(file.getDisplayName());
      }

      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      return null;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
         Map application, Errors errors) {
      
      String formAction = (String)request.get("formAction");
      if (formAction.equals("filePicker")) {
         
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, 
               ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.scaffoldingImportFile"));
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, Integer.valueOf(1));
         return new ModelAndView("filePicker");
      }
      
      session.remove(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);
      session.remove(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS);
      
      ScaffoldingUploadForm scaffoldingForm = (ScaffoldingUploadForm)requestModel;

      if (scaffoldingForm.getUploadedScaffolding() == null) {
         errors.rejectValue("uploadedScaffolding", "Required", "required");
         return null;
      }

      Scaffolding scaffolding = null;

      try {
         scaffolding = getMatrixManager().uploadScaffolding(
              scaffoldingForm.getUploadedScaffolding(), getToolManager().getCurrentPlacement().getContext());
              
         validateScaffolding( scaffolding );
      } catch (InvalidUploadException e) {
         logger.warn("Failed uploading scaffolding", e);
         errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
         return null;
      } catch (Exception e) {
         logger.error("Failed importing scaffolding", e);
         throw new OspException(e);
      }

      return new ModelAndView("success", "scaffolding_id", scaffolding.getId());
   }
   
   /**
    ** Filter out assignments outside of this worksite
    **/
   protected void validateScaffolding( Scaffolding scaffolding ) {
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         WizardPageDefinition wpd = sCell.getWizardPageDefinition();
         List<String> attachments = wpd.getAttachments();
         
         attachments = 
            AssignmentHelper.filterAssignmentsBySite( attachments, 
                                                      wpd.getSiteId() );
         wpd.setAttachments(attachments);
      }
   }

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   /**
    * @return Returns the homeFactory.
    */
   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   /**
    * @param homeFactory The homeFactory to set.
    */
   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
}

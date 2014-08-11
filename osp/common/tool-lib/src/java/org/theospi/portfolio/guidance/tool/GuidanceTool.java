/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/guidance/tool/GuidanceTool.java $
* $Id:GuidanceTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.guidance.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.site.cover.SiteService;
import org.theospi.portfolio.guidance.mgt.GuidanceHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.shared.tool.HelperToolBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 3:33:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceTool extends HelperToolBase {

   private DecoratedGuidance current = null;
   private String formTypeId = null;
   private String formId = null;
   private String formDisplayName = null;
   
   private boolean showExamples = true;
   private boolean showInstructions = true;
   private boolean showRationale = true;
   private boolean showRubric = true;
   private boolean showExpectations = true;
   
   private GuidanceManager guidanceManager;
   private ContentHostingService contentHostingService;
   
   public static final String ATTACHMENT_TYPE = "org.theospi.portfolio.guidance.attachmentType";

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public ContentHostingService getContentHostingService() {
	return contentHostingService;
   }

   public void setContentHostingService(ContentHostingService contentHostingService) {
	   this.contentHostingService = contentHostingService;
   }

   public String getGuidanceInstructions() {
      return getCurrent().getBase().getDescription();
   }

   public DecoratedGuidance getCurrent() {
      ToolSession session = SessionManager.getCurrentToolSession();

      if (session.getAttribute(GuidanceManager.CURRENT_GUIDANCE_ID) != null) {
         String id = (String)session.getAttribute(GuidanceManager.CURRENT_GUIDANCE_ID);
         current = new DecoratedGuidance(this, getGuidanceManager().getGuidance(id));
         session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE_ID);
      }
      else if (session.getAttribute(GuidanceManager.CURRENT_GUIDANCE) != null) {
         current = new DecoratedGuidance(this,
               (Guidance)session.getAttribute(GuidanceManager.CURRENT_GUIDANCE));
         guidanceManager.assureAccess(current.getBase());
         session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE);
      }
      else if (current != null) {
         guidanceManager.assureAccess(current.getBase());
      }

      return current;
   }

   public String processActionManageAttachments(String type) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());
      session.setAttribute(GuidanceTool.ATTACHMENT_TYPE, type);
      GuidanceItem item = getCurrent().getBase().getItem(type);

      List attachments = item.getAttachments();
      List attachmentRefs = EntityManager.newReferenceList();

      for (Iterator i=attachments.iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment)i.next();
         attachmentRefs.add(attachment.getBaseReference().getBase());
      }
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, attachmentRefs);

      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }


   public String processActionSave() {
      getGuidanceManager().saveGuidance(getCurrent().getBase());

      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, getCurrent().getBase());
      cleanup(session);
      
      return returnToCaller();
   }

   public String processActionCancel() {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE);
      session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE_ID);
      cleanup(session);
      current = null;
      return returnToCaller();
   }
   
   protected void cleanup(ToolSession toolSession) {
      toolSession.removeAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG);
      toolSession.removeAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG);
      toolSession.removeAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG);
      toolSession.removeAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG);
      toolSession.removeAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG);
      toolSession.removeAttribute(GuidanceHelper.CONTEXT);
      toolSession.removeAttribute(GuidanceHelper.CONTEXT2);
   }

   public Reference decorateReference(String reference) {
      return getGuidanceManager().decorateReference(getCurrent().getBase(), reference);
   }

   /**
    * sample
    * @return
    */
   public String getLastSavedId() {
      ToolSession session = SessionManager.getCurrentToolSession();
      Guidance guidance = (Guidance) session.getAttribute(GuidanceManager.CURRENT_GUIDANCE);
      if (guidance != null) {
         session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE);
         return guidance.getId().getValue();
      }
      return "none";
   }

   /**
    * sample
    * @return
    */
   public List getSampleGuidances() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List returned = new ArrayList();
      List orig = getGuidanceManager().listGuidances(currentSiteId);

      for (Iterator i=orig.iterator();i.hasNext();) {
         Guidance guidance = (Guidance)i.next();
         returned.add(new DecoratedGuidance(this, guidance));
      }
      return returned;
   }
   
   public boolean isInstructionsRendered() {
      //boolean showInstructions = true; 
      if (getAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG) != null) {
         if (getAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG) instanceof Boolean) {
            showInstructions = ((Boolean) getAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG)).booleanValue();
         } else {
            showInstructions =
               "true".equalsIgnoreCase((String) getAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG));
         }
         removeAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG);
      }
      return showInstructions;
   }
   
   public boolean isExamplesRendered() {
      //boolean showExamples = true; 
      if (getAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG) != null) {
         if (getAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG) instanceof Boolean) {
            showExamples = ((Boolean) getAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG)).booleanValue();
         } else {
            showExamples =
               "true".equalsIgnoreCase((String) getAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG));
         }
         removeAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG);
      }
      return showExamples;
   }
   
   public boolean isRationaleRendered() {
      //boolean showRationale = true; 
      if (getAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG) != null) {
         if (getAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG) instanceof Boolean) {
            showRationale = ((Boolean) getAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG)).booleanValue();
         } else {
            showRationale =
               "true".equalsIgnoreCase((String) getAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG));
         }
         removeAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG);
      }
      return showRationale;
   }
   
   public boolean isRubricRendered() {
	      //boolean showRationale = true; 
	      if (getAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG) != null) {
	         if (getAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG) instanceof Boolean) {
	            showRubric = ((Boolean) getAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG)).booleanValue();
	         } else {
	        	 showRubric =
	               "true".equalsIgnoreCase((String) getAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG));
	         }
	         removeAttribute(GuidanceHelper.SHOW_RUBRIC_FLAG);
	      }
	      return showRubric;
	   }
   
   public boolean isExpectationsRendered() {
	      //boolean showRationale = true; 
	      if (getAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG) != null) {
	         if (getAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG) instanceof Boolean) {
	            showExpectations = ((Boolean) getAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG)).booleanValue();
	         } else {
	        	 showExpectations =
	               "true".equalsIgnoreCase((String) getAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG));
	         }
	         removeAttribute(GuidanceHelper.SHOW_EXPECTATIONS_FLAG);
	      }
	      return showExpectations;
	   }

   /**
    * sample
    * @param guidance
    * @return
    */
   public String processActionEdit(Guidance guidance) {
      guidance = getGuidanceManager().getGuidance(guidance.getId());
      invokeTool(guidance, new HashMap());
      return null;
   }
   
   public String processActionEditInstruction(Guidance guidance) {
      guidance = getGuidanceManager().getGuidance(guidance.getId());

      Map typeFlags = new HashMap();
      
      typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "true");
      typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
      invokeTool(guidance, typeFlags);
      return null;
   }
   
   public String processActionEditExample(Guidance guidance) {
      guidance = getGuidanceManager().getGuidance(guidance.getId());

      Map typeFlags = new HashMap();
      
      typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "true");
      typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
      invokeTool(guidance, typeFlags);
      return null;
   }
   
   public String processActionEditRationale(Guidance guidance) {
      guidance = getGuidanceManager().getGuidance(guidance.getId());

      Map typeFlags = new HashMap();
      
      typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "true");
      typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
      invokeTool(guidance, typeFlags);
      return null;
   }

   public String processActionEditRubric(Guidance guidance) {
	   guidance = getGuidanceManager().getGuidance(guidance.getId());

	   Map typeFlags = new HashMap();

	   typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "true");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
	   invokeTool(guidance, typeFlags);
	   return null;
   }

   public String processActionEditExpectations(Guidance guidance) {
	   guidance = getGuidanceManager().getGuidance(guidance.getId());

	   Map typeFlags = new HashMap();

	   typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "true");
	   invokeTool(guidance, typeFlags);
	   return null;
   }

   /**
    * sample
    * @param guidance
    * @return
    */
   public String processActionDelete(Guidance guidance) {
      getGuidanceManager().deleteGuidance(guidance);
      current = null;
      return "list";
   }

   /**
    * sample
    * @param guidance
    * @return
    */
   public String processActionView(Guidance guidance) {
      guidance = getGuidanceManager().getGuidance(guidance.getId());
      invokeToolView(guidance.getId().getValue());
      return null;
   }

   /**
    * sample
    * @return
    */
   public String processActionNew() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

      invokeTool(newGuidance, new HashMap());

      return null;
   }
   
   public String processActionNewInstruction() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

      Map typeFlags = new HashMap();
      
      typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "true");
      typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
      invokeTool(newGuidance, typeFlags);

      return null;
   }
   
   public String processActionNewExample() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

      Map typeFlags = new HashMap();
      
      typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "true");
      typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
      typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
      invokeTool(newGuidance, typeFlags);

      return null;
   }

   public String processActionNewRationale() {
	   Placement placement = ToolManager.getCurrentPlacement();
	   String currentSite = placement.getContext();
	   Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

	   Map typeFlags = new HashMap();

	   typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "true");
	   typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
	   invokeTool(newGuidance, typeFlags);

	   return null;
   }

   public String processActionNewRubric() {
	   Placement placement = ToolManager.getCurrentPlacement();
	   String currentSite = placement.getContext();
	   Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

	   Map typeFlags = new HashMap();

	   typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "true");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "false");
	   invokeTool(newGuidance, typeFlags);

	   return null;
   }

   public String processActionNewExpectations() {
	   Placement placement = ToolManager.getCurrentPlacement();
	   String currentSite = placement.getContext();
	   Guidance newGuidance = getGuidanceManager().createNew("Sample Guidance", currentSite, null, "", "");

	   Map typeFlags = new HashMap();

	   typeFlags.put(GuidanceHelper.SHOW_RATIONALE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_RUBRIC_FLAG, "false");
	   typeFlags.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, "true");
	   invokeTool(newGuidance, typeFlags);

	   return null;
   }
   /**
    * sample
    * @param guidance
    */
   protected void invokeTool(Guidance guidance, Map typeFlags) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);
      
      for (Iterator iter = typeFlags.entrySet().iterator(); iter.hasNext();) {
         Entry entry = (Entry) iter.next();
         session.setAttribute(entry.getKey().toString(), entry.getValue().toString());
      }

      try {
         context.redirect("osp.guidance.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   /**
    * sample
    * @param id
    */
   protected void invokeToolView(String id) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE_ID, id);

      try {
         context.redirect("osp.guidance.helper/view");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public String processTestResourceHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);
      session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
      session.setAttribute(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
      session.setAttribute(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, getFormDisplayName());

      try {
         session.setAttribute(FormHelper.PARENT_ID_TAG,
            getUserCollection().getId());
         context.redirect("sakai.metaobj.form.helper/formHelper");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      } catch (IdUnusedException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      } catch (TypeException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      } catch (PermissionException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public String processTestResourceEditHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
      session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
      session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
      session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
         ResourceEditingHelper.CREATE_TYPE_FORM);
      session.setAttribute(ResourceEditingHelper.ATTACHMENT_ID, getFormId());

      try {
         context.redirect("sakai.metaobj.form.helper/formEditHelper");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public String processTestResourceViewHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
      session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
      session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
      session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
         ResourceEditingHelper.CREATE_TYPE_FORM);
      session.setAttribute(ResourceEditingHelper.ATTACHMENT_ID, getFormId());

      try {
         context.redirect("sakai.metaobj.formView.helper/formView.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public String getFormTypeId() {
      return formTypeId;
   }

   public void setFormTypeId(String formTypeId) {
      this.formTypeId = formTypeId;
   }

   public String getFormId() {
      return formId;
   }

   public void setFormId(String formId) {
      this.formId = formId;
   }

   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHostingService().getSiteCollection(wsId);
      ContentCollection collection = getContentHostingService().getCollection(wsCollectionId);
      return collection;
   }

   public String getFormDisplayName() {
      return formDisplayName;
   }

   public void setFormDisplayName(String formDisplayName) {
      this.formDisplayName = formDisplayName;
   }
   /**
    * Context (GuidanceHelper.CONTEXT) is used to describe the page/tool
    * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
    * and context 2 is used for the subtitle (ex. matrix cell or wizard page).  If left
    * blank, then nothing displays on the page.
    * @return
    */

   public String getPageContext(){
	   String context = (String) getAttribute(GuidanceHelper.CONTEXT);
	   return context != null ? context : "";
   }

   /**
    * Context2 (GuidanceHelper.CONTEXT2) is used to describe the page/tool
    * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
    * and Context2 is used for the subtitle (ex. matrix cell or wizard page).  If left
    * blank, then nothing displays on the page.
    * @return
    */
   public String getPageContext2(){
	   String context2 = (String) getAttribute(GuidanceHelper.CONTEXT2);
	   return context2 != null ? context2 : "";
   }


}

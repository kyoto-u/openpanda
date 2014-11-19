/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/matrix/tool/src/java/org/theospi/portfolio/matrix/control/MatrixValidator.java $
* $Id: MatrixValidator.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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


import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.util.FormattedText;
import org.springframework.validation.Errors;
import org.theospi.portfolio.matrix.model.CriterionTransport;
import org.theospi.portfolio.matrix.model.LevelTransport;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.ScaffoldingUploadForm;
import org.theospi.utils.mvc.impl.ValidatorBase;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 2:37:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixValidator extends ValidatorBase {
   
   private AuthenticationManager authManager;
   
   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      if (MatrixFormBean.class.isAssignableFrom(clazz)) return true;
      else if (ScaffoldingUploadForm.class.isAssignableFrom(clazz)) return true;
      else if (Scaffolding.class.isAssignableFrom(clazz)) return true;
      else if (ScaffoldingCell.class.isAssignableFrom(clazz)) return true;
      else if (LevelTransport.class.isAssignableFrom(clazz)) return true;
      else if (CriterionTransport.class.isAssignableFrom(clazz)) return true;
      else if (CellAndNodeForm.class.isAssignableFrom(clazz)) return true;
      else if (MatrixGridBean.class.isAssignableFrom(clazz)) return true;
      else return false;
   }

   /**
    * Validate a presentation object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      //if (obj instanceof ScaffoldingUploadForm) 
      //   validateScaffoldingImport((ScaffoldingUploadForm)obj, errors);
      if (obj instanceof CriterionTransport)
         validateCriterion((CriterionTransport)obj, errors);
      else if (obj instanceof LevelTransport)
         validateLevel((LevelTransport)obj, errors);
      else if (obj instanceof Scaffolding) {
         Scaffolding scaffolding = (Scaffolding) obj;
         if (scaffolding.isValidate())
            validateScaffolding(scaffolding, errors);
      }
      else if (obj instanceof ScaffoldingCell) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) obj;
         if (scaffoldingCell.isValidate())
            validateScaffoldingCell(scaffoldingCell, errors);
      }
      else if (obj instanceof CellAndNodeForm)
         validateCellAttachment((CellAndNodeForm)obj, errors);
      else if (obj instanceof MatrixGridBean)
         validateScaffolding(((MatrixGridBean)obj).getScaffolding(), errors);
   }
  /* 
   private void validateScaffoldingImport(ScaffoldingUploadForm obj, Errors errors) {
      RepositoryNode node = (RepositoryNode)getRepositoryManager().getRootNode(getAuthManager().getAgent());
      if (node.hasChild(obj.getDisplayName())) {
         errors.rejectValue("displayName", "duplicate", "duplicate");
      }
      if (!obj.getUploadedScaffoldingForm().getContentType().equals("text/xml")) {
         errors.rejectValue("uploadedScaffoldingForm", "invalid file", "invalid file");
      }
   }
   */
   
   protected void validateCellAttachment(CellAndNodeForm form, Errors errors) {
      if (form.getNode_id() == null || form.getNode_id().equals("")) {
         errors.rejectValue("node_id", "error.required", "required");
      }
   }
   
   protected void validateScaffoldingCell(ScaffoldingCell scaffoldingCell, Errors errors) {
      if (scaffoldingCell.getInitialStatus() == null ||
            scaffoldingCell.getInitialStatus().equals("")) {
         errors.rejectValue("initialStatus", "error.required", "required");
      }
      if (scaffoldingCell.getTitle() == null ||
            scaffoldingCell.getTitle().trim().equals("")) {
         errors.rejectValue("title", "error.required", "required");
      }
      
      if (scaffoldingCell.getWizardPageDefinition().getDescription() != null) {
         StringBuilder sbError = new StringBuilder();
         String tempDesc = FormattedText.processFormattedText(
            scaffoldingCell.getWizardPageDefinition().getDescription(), sbError);
         
         if (sbError.length() > 0) {
            errors.rejectValue("wizardPageDefinition.description", "error.html.format", sbError.toString());  
         }
         else {
            scaffoldingCell.getWizardPageDefinition().setDescription(tempDesc);
         }
      }
      
   }

   protected void validateCriterion(CriterionTransport criterion, Errors errors) {
      if (criterion.getDescription() == null || criterion.getDescription().trim().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
   }

   protected void validateLevel(LevelTransport level, Errors errors) {
      if (level.getDescription() == null || level.getDescription().trim().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
   }
   
   protected void validateScaffolding(Scaffolding scaffolding, Errors errors) {
      if (scaffolding.getTitle() == null || scaffolding.getTitle().trim().equals("")) {
         errors.rejectValue("title", "error.required", "required");
      }
      if (scaffolding.getLevels() == null || scaffolding.getLevels().size() == 0) {
         errors.rejectValue("levels", "error.required", "required");
      }
      if (scaffolding.getCriteria() == null || scaffolding.getCriteria().size() == 0) {
         errors.rejectValue("criteria", "error.required", "required");
      }
      if (scaffolding.getDescription() != null) {
         StringBuilder sbError = new StringBuilder();
         String tempDesc = FormattedText.processFormattedText(scaffolding.getDescription(), sbError);
         
         if (sbError.length() > 0) {
            errors.rejectValue("description", "error.html.format", sbError.toString());  
         }
         else {
            scaffolding.setDescription(tempDesc);
         }
      }
   }
   
   /*
   private String stripHtml(String input) {
      return input.replaceAll("<[\\w/]+[^<>]*>", "");     
   }
*/
   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }
   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }
}

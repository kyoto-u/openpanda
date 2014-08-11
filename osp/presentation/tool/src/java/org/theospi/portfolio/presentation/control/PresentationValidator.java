/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/PresentationValidator.java $
* $Id:PresentationValidator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import org.jdom.Element;
import org.jdom.IllegalNameException;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.springframework.validation.Errors;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.utils.mvc.impl.ValidatorBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 2:37:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationValidator extends ValidatorBase {

   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      if (PresentationTemplate.class.isAssignableFrom(clazz)) return true;
      if (PresentationItemDefinition.class.isAssignableFrom(clazz)) return true;
      if (Presentation.class.isAssignableFrom(clazz)) return true;
      if (PresentationItem.class.isAssignableFrom(clazz)) return true;
      if (TemplateFileRef.class.isAssignableFrom(clazz)) return true;
      if (PresentationLayout.class.isAssignableFrom(clazz)) return true;
      return false;
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
      if (obj instanceof PresentationTemplate) validateTemplate(obj, errors);
      if (obj instanceof PresentationItem) validateItem(obj, errors);
      if (obj instanceof PresentationItemDefinition) validateItemDefinition(obj, errors);
      if (obj instanceof Presentation) validatePresentation(obj, errors);
      if (obj instanceof TemplateFileRef) validateTemplateFileRef((TemplateFileRef)obj, errors);
      if (obj instanceof PresentationLayout) validateLayout((PresentationLayout)obj, errors);
   }

   protected void validateTemplateFileRef(TemplateFileRef templateFileRef, Errors errors) {
      if (templateFileRef.getUsage() == null ||
         templateFileRef.getUsage().equals("")) {
         errors.rejectValue("usage", "error.required", "required");
      } else if (!isValidXMLElementName(templateFileRef.getUsage())){
            errors.rejectValue("usage", "error.invalidXmlElementName", "invalid name");
      }
      if (templateFileRef.getFileId() == null) {
         errors.rejectValue("fileId", "error.required", "required");
      }
   }

   protected void validateTemplateFirstPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;
      if (template.getName() == null || template.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "required");
      }
      if (template.getDescription() != null && template.getDescription().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
      }
   }

   protected void validateTemplateSecondPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;
      if (template.getRenderer() == null ||
            template.getRenderer().getValue() == null ||
            template.getRenderer().getValue().length() == 0) {
         errors.rejectValue("renderer", "error.required", "required");
      }
   }

   protected void validateTemplateThirdPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;

      if (template.getItem().getAction() != null && template.getItem().getAction().equals("addItem")){
         if (template.getItem().getType() == null || template.getItem().getType().length() == 0){
            errors.rejectValue("item.type", "error.required", "required");
         }
         if (template.getItem().getName() == null || template.getItem().getName().length() == 0){
            errors.rejectValue("item.name", "error.required", "required");
         } else if (!isValidXMLElementName(template.getItem().getName())){
            errors.rejectValue("item.name", "error.invalidXmlElementName", "invalid name");
         }
         if (template.getItem().getTitle() == null || template.getItem().getTitle().length() == 0){
            errors.rejectValue("item.title", "error.required", "required");
         }
      }
   }

   protected void validateTemplateFourthPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;

      if (template.getFileRef().getAction() != null && template.getFileRef().getAction().equals("addFile")){
         if (template.getFileRef().getUsage() == null || template.getFileRef().getUsage().length() == 0){
            errors.rejectValue("fileRef.usage", "error.required", "required");
         } else if (!isValidXMLElementName(template.getFileRef().getUsage())){
               errors.rejectValue("fileRef.usage", "error.invalidXmlElementName", "invalid name");
         }
         if (template.getFileRef().getFileId() == null ||
               template.getFileRef().getFileId().length() == 0){
            errors.rejectValue("fileRef.fileId", "error.required", "required");
         }
      }
   }

   protected void validateTemplate(Object obj, Errors errors) {
      validateTemplateFirstPage(obj, errors);
      validateTemplateSecondPage(obj, errors);
      validateTemplateThirdPage(obj, errors);
      validateTemplateFourthPage(obj, errors);
   }

   protected void validateItem(Object obj, Errors errors) {
      PresentationItem item = (PresentationItem) obj;

   }

   protected boolean isValidXMLElementName(String name){
      try {
         Element element = new Element(name);
      } catch (IllegalNameException e){
         return false;
      }
      return true;
   }

   protected void validateItemDefinition(Object obj, Errors errors) {
      PresentationItemDefinition itemDef = (PresentationItemDefinition) obj;
      if (itemDef.getName() == null || itemDef.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "name is required");
      }
      if (itemDef.getTitle() == null || itemDef.getTitle().length() == 0) {
         errors.rejectValue("title", "error.required", "title is required");
      }

   }

   protected void validatePresentation(Object obj, Errors errors) {
      validatePresentationInitialPage(obj, errors);
      validatePresentationFirstPage(obj, errors);
      validatePresentationSecondPage(obj, errors);
      validatePresentationThirdPage(obj, errors);
   }

   protected void validatePresentationInitialPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;
      if (!presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         if (presentation.getTemplate().getId() == null ||
            presentation.getTemplate().getId().getValue() == null ||
            presentation.getTemplate().getId().getValue().length() == 0) {
            errors.rejectValue("template.id", "error.portfolioTypeRequired", "You must select a portfolio type.");
         }
      }
   }
   
   protected void validatePresentationFirstPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;
      if (presentation.getName() == null || presentation.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "name is required");
      } else {
    	  if (presentation.getName().length() > 255) {
    		  errors.rejectValue("name", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters"); 
    	  }
      }

      //FIXME: This has got to go -- changing values in validation is a bad idea
      if (presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         presentation.getTemplate().setId(Presentation.FREEFORM_TEMPLATE_ID);
      }

      if (presentation.getTemplate().getId() == null ||
         presentation.getTemplate().getId().getValue() == null ||
         presentation.getTemplate().getId().getValue().length() == 0) {
         errors.rejectValue("template.id", "error.required", "template is required");
      }

      if (presentation.getExpiresOnBean() != null){
         presentation.setExpiresOn(presentation.getExpiresOnBean().getDate());
      }
      
      if (presentation.getDescription() != null && presentation.getDescription().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
      }
      
   }

   protected void validatePresentationSecondPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;
   }

   protected void validatePresentationThirdPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;

   }

   public void validatePresentationProperties(Presentation presentation, Errors errors) {
      TypedMap properties = presentation.getProperties();
      if (properties != null && presentation.getTemplate().getDocumentRoot().length() != 0) {
         PresentationPropertiesValidator propertyValidator = new PresentationPropertiesValidator();
         pushNestedPath("properties.", errors);
         propertyValidator.validate(properties, errors);
         popNestedPath(errors);
      }
   }
   
   protected void validateLayout(PresentationLayout layout, Errors errors) {
      if (layout.isValidate()) {
         if (layout.getName() == null || layout.getName().length() == 0) {
            errors.rejectValue("name", "error.required", "name is required");
         }
         if (layout.getXhtmlFileId() == null || 
               layout.getXhtmlFileId().getValue() == null || 
               layout.getXhtmlFileId().getValue().length() == 0) {
            errors.rejectValue("xhtmlFileId", "error.required", "XHTML file is required");
         }
         if (layout.getDescription() != null && layout.getDescription().length() > 255) {
            errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
         }
      }
   }
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/XmlValidator.java $
 * $Id: XmlValidator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.sakaiproject.metaobj.utils.mvc.impl.ValidatorBase;
import org.springframework.validation.Errors;

public class XmlValidator extends ValidatorBase {
   protected final Log logger = LogFactory.getLog(getClass());
   protected String parentPrefix = "";
   //private FileNameValidator fileNameValidator;

   public XmlValidator() {
   }

   public XmlValidator(String parentPrefix) {
      this.parentPrefix = parentPrefix;
   }

   public boolean supports(Class clazz) {
      return (ElementBean.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      validate(obj, errors, false);
   }

   public void validate(Object obj, Errors errors, boolean checkListNumbers) {
      ElementBean elementBean = (ElementBean) obj;

      StructuredArtifactValidationService service = getStructuredArtifactValidationService();
      List errorList = service.validate(elementBean);

      for (Iterator i = errorList.iterator(); i.hasNext();) {
         ValidationError error = (ValidationError) i.next();
         errors.rejectValue(error.getFieldName(), error.getErrorCode(),
               error.getErrorInfo(), error.getDefaultMessage());
      }
   }

   protected StructuredArtifactValidationService getStructuredArtifactValidationService() {
      return (StructuredArtifactValidationService) ComponentManager.getInstance().get(StructuredArtifactValidationService.class);
   }

   /*
   protected void validateDisplayName(ElementBean elementBean, Errors errors) {
      // don't care about display name here
   }

   protected void validateElement(Element rootElement, SchemaNode childSchema,
                                  Object value, Errors errors) {
      validateChildElement(rootElement.getChild(childSchema.getName()),
         childSchema, value, errors);
   }


   protected void validateChildElement(Element childElement, SchemaNode childSchema,
                                     Object value, Errors errors) {
      if (childElement != null) {
         String stringValue = null;
         if (value != null && value instanceof String) {
            stringValue = (String) value;
            value = childSchema.getActualNormalizedValue(stringValue);
         }

         childElement.setText(childSchema.getSchemaNormalizedValue(value));
      } else if (childSchema.getMinOccurs() > 0) {
         errors.rejectValue(childSchema.getName(),
            "required value {0}",
            new Object[]{childSchema.getName()},
            MessageFormat.format("required value {0}", new Object[]{childSchema.getName()}));
      }
   }

   protected boolean checkWrappedField(SchemaNode childSchema, ElementBean elementBean, Errors errors) {

      Class childClass = elementBean.getType(childSchema.getName());
      Object value = elementBean.get(childSchema.getName());

      if (!(value instanceof FieldValueWrapper)) {
         return false;
      }

      FieldValueWrapper beanValue = (FieldValueWrapper) elementBean.get(childSchema.getName());

      if (beanValue.getValue() == null) {
         if (childSchema.getMinOccurs() > 0) {
            throw new NormalizationException("Required field", "required field {0}",
               new Object[]{childSchema.getName()});
         } else {
            elementBean.getBaseElement().removeChild(childSchema.getName());
            return true;
         }
      }

      // check date...
      this.pushNestedPath(childSchema.getName(), errors);
      beanValue.validate(errors);
      this.popNestedPath(errors);

      Element dateElement = elementBean.getBaseElement().getChild(childSchema.getName());

      if (dateElement == null) {
         dateElement = new Element(childSchema.getName());
         elementBean.getBaseElement().addContent(dateElement);
      }
      dateElement.setText(childSchema.getSchemaNormalizedValue(beanValue.getValue()));

      return true;
   }

//   public FileNameValidator getFileNameValidator() {
//      return fileNameValidator;
//   }
//
//   public void setFileNameValidator(FileNameValidator fileNameValidator) {
//      this.fileNameValidator = fileNameValidator;
//   }

 */
}

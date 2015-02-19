/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/StructuredArtifactValidationServiceImpl.java $
 * $Id: StructuredArtifactValidationServiceImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.sakaiproject.metaobj.shared.model.ElementListBeanWrapper;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 3:07:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactValidationServiceImpl implements StructuredArtifactValidationService {

   /**
    * Validate this element from the root.
    *
    * @param element filled in element to be validated.
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element) {
      List errors = new ArrayList();

      return validate(element, null, errors);
   }

   /**
    * Validate this element from the root.
    *
    * @param element    filled in element to be validated.
    * @param parentName this is the name of the parent of this object.
    *                   All fields that have errors will have this name prepended with a "."
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element, String parentName) {
      List errors = new ArrayList();

      return validate(element, parentName, errors);
   }

   protected List validate(ElementBean elementBean, String parentName, List errors) {
      SchemaNode currentNode = elementBean.getCurrentSchema();
      Element rootElement = elementBean.currentElement();

      List children = currentNode.getChildren();
      for (Iterator i = children.iterator(); i.hasNext();) {
         SchemaNode childSchema = (SchemaNode) i.next();

         try {
            if (checkWrappedField(childSchema, elementBean, errors)) {
               continue;
            }

            Object value = elementBean.get(childSchema.getName());

            if (value instanceof ElementBean) {
               validate((ElementBean) value,
                     composeName(parentName, childSchema.getName()), errors);
               if (childSchema.isDataNode()) {
                  ElementBean bean = (ElementBean) value;
                  value = bean.currentElement().getTextTrim();
                  if (value.toString().length() == 0) {
                     value = null;
                  }
                  validateElement(rootElement, childSchema, value, parentName, errors);
               }
            }
            else if (value instanceof ElementListBean) {
               boolean found = false;
               for (Iterator iter = ((ElementListBean) value).iterator(); iter.hasNext();) {
                  found = true;
                  Object currentValue = iter.next();
                  validate((ElementBean) currentValue,
                        composeName(parentName, childSchema.getName()), errors);
                  if (childSchema.isDataNode()) {
                     ElementBean bean = (ElementBean) currentValue;
                     currentValue = bean.currentElement().getTextTrim();
                     if (currentValue.toString().length() == 0) {
                        currentValue = null;
                     }
                     try {
                        validateChildElement(bean.currentElement(),
                              childSchema, currentValue, parentName, errors);
                     }
                     catch (NormalizationException exp) {
                        errors.add(new ValidationError(childSchema.getLabel(),
                              composeName(parentName, childSchema.getName()),
                              exp.getErrorCode(),
                              exp.getErrorInfo(),
                              MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
                     }
                  }
               }
               if (!found) {
                  try {
                     validateChildElement(null,
                           childSchema, null, parentName, errors);
                  }
                  catch (NormalizationException exp) {
                     errors.add(new ValidationError(childSchema.getLabel(),
                           composeName(parentName, childSchema.getName()),
                           exp.getErrorCode(),
                           exp.getErrorInfo(),
                           MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
                  }
               }
            }
            else if (value instanceof ElementListBeanWrapper) {
               ((ElementListBeanWrapper)value).validate(errors);
               if (((ElementListBeanWrapper)value).getList().size() < childSchema.getMinOccurs()) {
                  errors.add(new ValidationError(childSchema.getLabel(),
                        composeName(parentName, childSchema.getName()),
                        NormalizationException.REQIRED_FIELD_ERROR_CODE,
                        new Object[0], NormalizationException.REQIRED_FIELD_ERROR_CODE));
               }
            }
            else if (childSchema.isAttribute()) {
               Attribute childAttribute = rootElement.getAttribute(childSchema.getName());

               if (childAttribute != null) {
                  String stringValue = null;
                  if (value != null && value instanceof String) {
                     stringValue = (String) value;
                     value = childSchema.getActualNormalizedValue(stringValue);
                  }

                  childAttribute.setValue(childSchema.getSchemaNormalizedValue(value));
               }
               else if (childSchema.getMinOccurs() > 0) {
                  errors.add(new ValidationError(childSchema.getLabel(),
                        composeName(parentName, childSchema.getName()),
                        NormalizationException.REQIRED_FIELD_ERROR_CODE,
                        new Object[0], NormalizationException.REQIRED_FIELD_ERROR_CODE));
               }

            }
            else {
               validateElement(rootElement, childSchema, value, parentName, errors);
            }
         }
         catch (NormalizationException exp) {
            errors.add(new ValidationError(childSchema.getLabel(),
                  composeName(parentName, childSchema.getName()),
                  exp.getErrorCode(),
                  exp.getErrorInfo(),
                  MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
         }
      }

      return errors;
   }

   protected void validateChildElement(Element parent, Element childElement, SchemaNode childSchema,
                                       Object value, String parentName, List errors) {
      if (value instanceof FieldValueWrapper) {
         FieldValueWrapper fieldValueWrapper = ((FieldValueWrapper) value);
         fieldValueWrapper.validate(childSchema.getName(), errors, childSchema.getLabel());
         value = fieldValueWrapper.getValue();
         if (value != null && childElement == null) {
            childElement = new Element(childSchema.getName());
            parent.addContent(childElement);
         }
      }
      validateChildElement(childElement, childSchema, value, parentName, errors);
   }

   protected void validateChildElement(Element childElement, SchemaNode childSchema,
                                          Object value, String parentName, List errors) {
      if (childElement != null) {
         String stringValue = null;
         if (value != null && value instanceof String) {
            stringValue = (String) value;
            value = childSchema.getActualNormalizedValue(stringValue);
         }

         childElement.setText(childSchema.getSchemaNormalizedValue(value));
      }
      else if (childSchema.getMinOccurs() > 0) {
         errors.add(new ValidationError(childSchema.getLabel(),
               composeName(parentName, childSchema.getName()),
               NormalizationException.REQIRED_FIELD_ERROR_CODE,
               new Object[0], NormalizationException.REQIRED_FIELD_ERROR_CODE));
      }
   }

   protected void validateElement(Element rootElement, SchemaNode childSchema,
                                  Object value, String parentName, List errors) {
      validateChildElement(rootElement, rootElement.getChild(childSchema.getName()),
            childSchema, value, parentName, errors);
   }

   protected String composeName(String parentName, String name) {
      if (parentName == null) {
         return name;
      }
      else {
         return parentName + "." + name;
      }
   }

   protected boolean checkWrappedField(SchemaNode childSchema, ElementBean elementBean, List errors) {
      return false;
   }
}

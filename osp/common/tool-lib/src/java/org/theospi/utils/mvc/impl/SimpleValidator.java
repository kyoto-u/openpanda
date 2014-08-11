/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/SimpleValidator.java $
* $Id:SimpleValidator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.utils.mvc.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.theospi.portfolio.shared.model.OspException;

public class SimpleValidator extends ValidatorBase implements Validator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List requiredFields;
   private String messageCode = "Required";
   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      return true;
   }

   /**
    * Validate an object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      for (Iterator i=requiredFields.iterator();i.hasNext();) {
         String field = (String)i.next();
         validate(field, obj, errors);
      }
   }

   protected void validate(String field, Object obj, Errors errors) {
      if (obj instanceof Map) {
         Map map = (Map)obj;
         if (map.get(field) == null) {
            errors.rejectValue(field, messageCode, messageCode);
         }
      }
      else {
         PropertyDescriptor prop = null;
         try {
            prop = new PropertyDescriptor(field, obj.getClass());

            Object value = prop.getReadMethod().invoke(obj, new Object[]{});
            if (value == null || value.toString().length() == 0) {
               errors.rejectValue(field, messageCode, messageCode);
            }
         } catch (IntrospectionException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (IllegalAccessException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (InvocationTargetException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }
   }

   public String getMessageCode() {
      return messageCode;
   }

   public void setMessageCode(String messageCode) {
      this.messageCode = messageCode;
   }

   public List getRequiredFields() {
      return requiredFields;
   }

   public void setRequiredFields(List requiredFields) {
      this.requiredFields = requiredFields;
   }
}

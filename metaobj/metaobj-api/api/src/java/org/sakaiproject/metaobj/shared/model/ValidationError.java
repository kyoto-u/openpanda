/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/ValidationError.java $
 * $Id: ValidationError.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 2:51:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This object contains information reguarding an error on an individual field
 * that occurs during validation
 *
 * @see ElementBean
 */
public class ValidationError {

   private String fieldName;
   private String errorCode;
   private Object[] errorInfo;
   private String defaultMessage;
   private String label;

   /**
    * Construct a ValidationError with all the required parameters
    *
    * @param fieldName      the name of the field within this element.  if the field
    *                       is from a nested element, the parent field name will be prepended with a "."
    * @param errorCode      Code that is suitable for dereferencing into a properties file for
    *                       i8n purposes.  errorCode will contain the proper formatting for use as a default message.
    *                       for instance, "Value {1} for field {0} must match pattern {2}".  With the error info, this could be
    *                       used by a message formater.
    * @param errorInfo      an array of information related to the error.
    * @param defaultMessage the fields applied to the error code.
    */
   public ValidationError(String label, String fieldName, String errorCode,
                          Object[] errorInfo, String defaultMessage) {
      this.label = label;
      this.fieldName = fieldName;
      this.errorCode = errorCode;
      this.errorInfo = composeErrorInfo(errorInfo);
      this.defaultMessage = defaultMessage;
   }

   protected Object[] composeErrorInfo(Object[] errorInfo) {
      if (errorInfo == null || errorInfo.length == 0) {
         return new Object[]{getLabel()};
      }

      List info = new ArrayList(Arrays.asList(errorInfo));
      info.add(0, getLabel());
      return info.toArray();
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public String getErrorCode() {
      return errorCode;
   }

   public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
   }

   public Object[] getErrorInfo() {
      return errorInfo;
   }

   public void setErrorInfo(Object[] errorInfo) {
      this.errorInfo = errorInfo;
   }

   public String getDefaultMessage() {
      return defaultMessage;
   }

   public void setDefaultMessage(String defaultMessage) {
      this.defaultMessage = defaultMessage;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }
}

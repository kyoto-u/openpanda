/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/PersistenceException.java $
 * $Id: PersistenceException.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.model;

import java.text.MessageFormat;

public class PersistenceException extends OspException {

   private String errorCode;
   private Object[] errorInfo;
   private String field;


   public PersistenceException(String errorCode, Object[] errorInfo,
                               String field) {
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(String message, String errorCode,
                               Object[] errorInfo, String field) {
      super(message);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(String message, Throwable cause,
                               String errorCode, Object[] errorInfo, String field) {
      super(message, cause);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(Throwable cause, String errorCode,
                               Object[] errorInfo, String field) {
      super(cause);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }


   public String getErrorCode() {
      return errorCode;
   }

   public Object[] getErrorInfo() {
      return errorInfo;
   }

   public String getField() {
      return field;
   }

   public String getDefaultMessage() {
      return MessageFormat.format(getErrorCode(), getErrorInfo());
   }
}

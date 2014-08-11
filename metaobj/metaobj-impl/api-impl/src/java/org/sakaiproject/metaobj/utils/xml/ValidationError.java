/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/branches/sakai-2.8.x/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/ValidationError.java $
 * $Id: ValidationError.java 59676 2009-04-03 23:18:23Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.xml;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:24:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationError {

   private ValidatedNode currentElement;
   private String errorCode;
   private Object[] errorInfo;

   public ValidationError(String errorCode, Object[] errorInfo) {
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
   }

   public ValidationError(ValidatedNode currentElement, String errorCode, Object[] errorInfo) {
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.currentElement = currentElement;
   }

   public String getErrorCode() {
      return errorCode;
   }

   public Object[] getErrorInfo() {
      return errorInfo;
   }

   public ValidatedNode getValidatedElement() {
      return currentElement;
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/OspException.java $
 * $Id: OspException.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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

public class OspException
      extends RuntimeException {
   public static final String CREATE_FAILED = "Create failed ";
   public static final String CREATE_STREAM_FAILED = "Create stream failed ";
   public static final String DELETE_FAILED = "Delete failed";
   public static final String MOVE_FAILED = "Move failed ";
   public static final String RENAME_FAILED = "Rename failed ";
   public static final String WRITE_STREAM_FAILED = "Create stream failed ";

   /**
    *
    */
   public OspException() {
      super();
   }

   /**
    * @param cause
    */
   public OspException(Throwable cause) {
      super(cause);
   }

   /**
    * @param message
    */
   public OspException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public OspException(String message, Throwable cause) {
      super(message, cause);
   }

}


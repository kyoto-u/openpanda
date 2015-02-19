/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/xml/NormalizationException.java $
 * $Id: NormalizationException.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.xml;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:28:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class NormalizationException extends RuntimeException {

   public static final String INVALID_LENGTH_ERROR_CODE = "INVALID_LENGTH_ERROR_CODE";
   public static final String INVALID_LENGTH_TOO_LONG_ERROR_CODE = "INVALID_LENGTH_TOO_LONG_ERROR_CODE";
   public static final String INVALID_LENGTH_TOO_SHORT_ERROR_CODE = "INVALID_LENGTH_TOO_SHORT_ERROR_CODE";
   public static final String INVALID_PATTERN_MATCH_ERROR_CODE = "INVALID_PATTERN_MATCH_ERROR_CODE";
   public static final String NOT_IN_ENUMERATION_ERROR_CODE = "NOT_IN_ENUMERATION_ERROR_CODE";

   public static final String DATE_TOO_LATE_ERROR_CODE = "DATE_TOO_LATE_ERROR_CODE";
   public static final String DATE_TOO_EARLY_ERROR_CODE = "DATE_TOO_EARLY_ERROR_CODE";
   public static final String DATE_AFTER_ERROR_CODE = "DATE_AFTER_ERROR_CODE";
   public static final String DATE_BEFORE_ERROR_CODE = "DATE_BEFORE_ERROR_CODE";
   public static final String DATE_INVALID_ERROR_CODE = "DATE_INVALID_ERROR_CODE";

   public static final String REQIRED_FIELD_ERROR_CODE = "REQIRED_FIELD_ERROR_CODE";
   
   public static final String RICH_TEXT_FORMAT_PASSTHROUGH = "RICH_TEXT_FORMAT_PASSTHROUGH";

   public static final String TOO_LARGE_INCLUSIVE_ERROR_CODE = "TOO_LARGE_INCLUSIVE_ERROR_CODE";
   public static final String TOO_SMALL_INCLUSIVE_ERROR_CODE = "TOO_SMALL_INCLUSIVE_ERROR_CODE";
   public static final String TOO_LARGE_ERROR_CODE = "TOO_LARGE_ERROR_CODE";
   public static final String TOO_SMALL_ERROR_CODE = "TOO_SMALL_ERROR_CODE";
   public static final String TOO_MANY_DIGITS_ERROR_CODE = "TOO_MANY_DIGITS_ERROR_CODE";
   public static final String INVALID_DECIMAL_NUMBER_ERROR_CODE = "INVALID_DECIMAL_NUMBER_ERROR_CODE";
   public static final String INVALID_NUMBER_ERROR_CODE = "INVALID_NUMBER_ERROR_CODE";

   public static final String INVALID_TYPE_ERROR_CODE = "INVALID_TYPE_ERROR_CODE";

   public static final String INVALID_URI = "INVALID_URI";

   private String errorCode;
   private Object[] errorInfo;

   /**
    * Constructs a new runtime exception with <code>null</code> as its
    * detail message.  The cause is not initialized, and may subsequently be
    * initialized by a call to {@link #initCause}.
    */
   public NormalizationException() {
      super();
   }

   /**
    * Constructs a new runtime exception with the specified detail message.
    * The cause is not initialized, and may subsequently be initialized by a
    * call to {@link #initCause}.
    *
    * @param message the detail message. The detail message is saved for
    *                later retrieval by the {@link #getMessage()} method.
    */
   public NormalizationException(String message) {
      super(message);
   }

   /**
    * Constructs a new runtime exception with the specified detail message and
    * cause.  <p>Note that the detail message associated with
    * <code>cause</code> is <i>not</i> automatically incorporated in
    * this runtime exception's detail message.
    *
    * @param message the detail message (which is saved for later retrieval
    *                by the {@link #getMessage()} method).
    * @param cause   the cause (which is saved for later retrieval by the
    *                {@link #getCause()} method).  (A <tt>null</tt> value is
    *                permitted, and indicates that the cause is nonexistent or
    *                unknown.)
    * @since 1.4
    */
   public NormalizationException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a new runtime exception with the specified cause and a
    * detail message of <tt>(cause==null ? null : cause.toString())</tt>
    * (which typically contains the class and detail message of
    * <tt>cause</tt>).  This constructor is useful for runtime exceptions
    * that are little more than wrappers for other throwables.
    *
    * @param cause the cause (which is saved for later retrieval by the
    *              {@link #getCause()} method).  (A <tt>null</tt> value is
    *              permitted, and indicates that the cause is nonexistent or
    *              unknown.)
    * @since 1.4
    */
   public NormalizationException(Throwable cause) {
      super(cause);
   }

   public NormalizationException(String message, String errorCode, Object[] errorInfo) {
      super(message);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
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

}

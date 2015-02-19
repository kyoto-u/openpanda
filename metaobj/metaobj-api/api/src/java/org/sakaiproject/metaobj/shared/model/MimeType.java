/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/MimeType.java $
 * $Id: MimeType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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


/**
 * @author rpembry
 *         <p/>
 *         <p/>
 *         Based roughly on javax.activation.MimeType, which is
 *         "A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045 and 2046."
 */
public class MimeType {

   public final static MimeType MIMETYPE_PDF = new MimeType("application", "pdf");

   private String primaryType;
   private String subType;

   public MimeType() {
      ;
   }

   public MimeType(String rawdata) {
      setValue(rawdata);
   }

   public MimeType(String primaryType, String subType) {
      this.primaryType = primaryType;
      this.subType = subType;
   }

   public void setValue(String value) {
      String[] parts = value.split("/");
      this.primaryType = parts[0];
      if (parts.length > 1) {
         this.subType = parts[1];
      }
   }

   /**
    * @return Returns the primaryType.
    */
   public String getPrimaryType() {
      return primaryType;
   }

   /**
    * @param primaryType The primaryType to set.
    */
   public void setPrimaryType(String primaryType) {
      this.primaryType = primaryType;
   }

   /**
    * @return Returns the subType.
    */
   public String getSubType() {
      return subType;
   }

   /**
    * @param subType The subType to set.
    */
   public void setSubType(String subType) {
      this.subType = subType;
   }

   public String getDescription() {
      return getValue();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
   */
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof MimeType)) {
         return false;
      }

      final MimeType mimeType = (MimeType) o;

      if (primaryType != null ? !primaryType.equals(mimeType.primaryType) : mimeType.primaryType != null) {
         return false;
      }
      if (subType != null ? !subType.equals(mimeType.subType) : mimeType.subType != null) {
         return false;
      }

      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      return this.getValue().hashCode();
   }

   public String getValue() {
      return this.primaryType + "/" + this.subType;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return this.getValue();
   }
}

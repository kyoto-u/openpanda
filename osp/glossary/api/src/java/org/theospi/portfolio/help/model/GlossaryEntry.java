/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/glossary/api/src/java/org/theospi/portfolio/help/model/GlossaryEntry.java $
* $Id: GlossaryEntry.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.help.model;



public class GlossaryEntry extends GlossaryBase {
   private String term;
   private String description;
   private String worksiteId;
	private static int MAX_LENGTH = 255;

   private GlossaryDescription longDescriptionObject = new GlossaryDescription();

   public GlossaryEntry(){}

   public GlossaryEntry(String term, String description){
      this.term = term;
      this.description = description;
   }

   public String getTerm() {
      return term;
   }

   public void setTerm(String term) {
      // term title limited to 255 characters
		int maxLength = term.length() > MAX_LENGTH ? MAX_LENGTH : term.length();
      this.term = term.trim().substring(0,maxLength);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      // short description limited to 255 characters
		int maxLength = description.length() > MAX_LENGTH ? MAX_LENGTH : description.length();
      this.description = description.substring(0,maxLength);
   }

   public String getWorksiteId() {
      return worksiteId;
   }

   public void setWorksiteId(String worksiteId) {
      this.worksiteId = worksiteId;
   }

   public String getLongDescription() {
      return longDescriptionObject.getLongDescription();
   }

   public void setLongDescriptionObject(GlossaryDescription longDescriptionObject) {
      this.longDescriptionObject = longDescriptionObject;
   }

   public GlossaryDescription getLongDescriptionObject() {
      return longDescriptionObject;
   }

   public void setLongDescription(String longDescription) {
      this.longDescriptionObject.setLongDescription(longDescription);
   }

   /**
    * Returns a string representation of the object. In general, the
    * <code>toString</code> method returns a string that
    * "textually represents" this object. The result should
    * be a concise but informative representation that is easy for a
    * person to read.
    * It is recommended that all subclasses override this method.
    * <p/>
    * The <code>toString</code> method for class <code>Object</code>
    * returns a string consisting of the name of the class of which the
    * object is an instance, the at-sign character `<code>@</code>', and
    * the unsigned hexadecimal representation of the hash code of the
    * object. In other words, this method returns a string equal to the
    * value of:
    * <blockquote>
    * <pre>
    * getClass().getName() + '@' + Integer.toHexString(hashCode())
    * </pre></blockquote>
    *
    * @return a string representation of the object.
    */
   public String toString() {
      return getTerm();
   }
   
}

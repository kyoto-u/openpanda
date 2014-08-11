/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/glossary/api/src/java/org/theospi/portfolio/help/model/GlossaryUploadForm.java $
* $Id: GlossaryUploadForm.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.help.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlossaryUploadForm {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String uploadedGlossary;
   private String submitAction;
   private boolean ignoreExistingTerms;
   transient private boolean validate = true;

   public String getUploadedGlossary() {
      return uploadedGlossary;
   }

   public void setUploadedGlossary(String uploadedGlossary) {
      this.uploadedGlossary = uploadedGlossary;
   }

   public String getSubmitAction() {
      return submitAction;
   }

   public void setSubmitAction(String submitAction) {
      this.submitAction = submitAction;
   }

   public boolean getReplaceExistingTerms() {
      return !ignoreExistingTerms;
   }

   public void setReplaceExistingTerms(boolean replaceExistingTerms) {
      this.ignoreExistingTerms = !replaceExistingTerms;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

}

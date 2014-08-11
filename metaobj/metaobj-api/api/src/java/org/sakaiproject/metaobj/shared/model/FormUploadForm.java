/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/admin-tools/su/src/java/org/sakaiproject/tool/su/SuTool.java $
 * $Id: SuTool.java 6970 2006-03-23 23:25:04Z zach.thomas@txstate.edu $
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

package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FormUploadForm {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String uploadedForm;
   private String submitAction;
   private boolean ignoreExistingTerms;
   transient private boolean validate = true;

   public String getUploadedForm() {
      return uploadedForm;
   }

   public void setUploadedForm(String uploadedForm) {
      this.uploadedForm = uploadedForm;
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

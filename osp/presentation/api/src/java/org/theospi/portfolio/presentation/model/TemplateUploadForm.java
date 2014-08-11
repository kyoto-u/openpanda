/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/presentation/api/src/java/org/theospi/portfolio/presentation/model/TemplateUploadForm.java $
* $Id: TemplateUploadForm.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.presentation.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

public class TemplateUploadForm {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id uploadedTemplate;
   private String submitAction;
   transient private boolean validate = true;

   public Id getUploadedTemplate() {
      return uploadedTemplate;
   }

   public void setUploadedTemplate(Id uploadedTemplate) {
      this.uploadedTemplate = uploadedTemplate;
   }

   public String getSubmitAction() {
      return submitAction;
   }

   public void setSubmitAction(String submitAction) {
      this.submitAction = submitAction;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

}

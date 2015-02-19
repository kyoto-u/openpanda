/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/StructuredArtifactDefinitionValidator.java $
 * $Id: StructuredArtifactDefinitionValidator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 3:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactDefinitionValidator implements Validator {

   public static final String PICK_SCHEMA_ACTION = "pickSchema";
   public static final String PICK_TRANSFORM_ACTION = "pickTransform";
   public static final String PICK_ALTCREATEXSLT_ACTION = "pickAltCreate";
   public static final String PICK_ALTVIEWXSLT_ACTION = "pickAltView";
   
   private static ResourceLoader myResources = new ResourceLoader("org.sakaiproject.metaobj.bundle.Messages");

   public boolean supports(Class clazz) {
      return (StructuredArtifactDefinitionBean.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      if (obj instanceof StructuredArtifactDefinitionBean) {
         StructuredArtifactDefinitionBean artifactHome = (StructuredArtifactDefinitionBean) obj;

         if (PICK_SCHEMA_ACTION.equals(artifactHome.getFilePickerAction()) ||
               PICK_TRANSFORM_ACTION.equals(artifactHome.getFilePickerAction()) ||
               PICK_ALTCREATEXSLT_ACTION.equals(artifactHome.getFilePickerAction()) ||
               PICK_ALTVIEWXSLT_ACTION.equals(artifactHome.getFilePickerAction())) {
            return;
         }

         if ((artifactHome.getSchemaFile() == null ||
               artifactHome.getSchemaFile().getValue() == null ||
               artifactHome.getSchemaFile().getValue().length() == 0)
               && artifactHome.getSchema() == null) {
            errors.rejectValue("schemaFile", "errors.required", myResources.getFormattedMessage("errors.required", new Object[]{myResources.getString("schemaFile")}));
         }
         if (artifactHome.getDocumentRoot() == null ||
               artifactHome.getDocumentRoot().length() == 0) {
            errors.rejectValue("documentRoot", "errors.required", myResources.getFormattedMessage("errors.required", new Object[]{myResources.getString("docRootNode")}));
         }
         if (artifactHome.getDescription() == null ||
               artifactHome.getDescription().length() == 0) {
            errors.rejectValue("description", "errors.required", myResources.getFormattedMessage("errors.required", new Object[]{myResources.getString("name")}));
         }
      }
   }
}

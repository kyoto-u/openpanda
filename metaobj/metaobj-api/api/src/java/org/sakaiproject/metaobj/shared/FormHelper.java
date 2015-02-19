/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/FormHelper.java $
 * $Id: FormHelper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 * **********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
 ******************************************************************************/

package org.sakaiproject.metaobj.shared;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Oct 24, 2006
 * Time: 7:28:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FormHelper {

   public static final String PARENT_ID_TAG = "metaobj.parentId";

   public static final String RETURN_ACTION_TAG = "metaobj.returnAction";

   public static final String RETURN_ACTION_SAVE = "metaobj.save";

   public static final String RETURN_ACTION_CANCEL = "metaobj.cancel";

   public static final String RETURN_REFERENCE_TAG = "metaobj.returnReference";

   public static final String PREVIEW_HOME_TAG = "metaobj.previewHome";

   public static final String PREVIEW_TRANSFORM_STREAM_TAG = "metaobj.previewTransform";

   public static final String NEW_FORM_DISPLAY_NAME_TAG = "metaobj.displayName";
   
   public static final String FORM_STYLES = "metaobj.styles";

   public static final String FORM_SAVE_SUCCESS = "metaobj.saveSuccess";

   public static final String FORM_SAVE_ATTEMPT = "metaobj.saveAttempt";
   
   public static final String URL_DECORATION = "metaobj.urlDecoration";
   
   public static final String PRESENTATION_ID = "_Presentation:Id";

   public static final String PRESENTATION_TEMPLATE_ID = "_PresentationTemplate:Id";

   public static final String PRESENTATION_ITEM_DEF_ID = "_PresentationItemDef:Id";

   public static final String PRESENTATION_ITEM_DEF_NAME = "_PresentationItemDef:Name";

   public static final String XSL_SITE_ID = "sakaiSiteId";

   public static final String XSL_PRESENTATION_TYPE = "sakaiPresentationType";

   public static final String XSL_PRESENTATION_ID = "sakaiPresentationId";

   public static final String XSL_PRESENTATION_ITEM_ID = "sakaiPresentationItemId";

   public static final String XSL_PRESENTATION_ITEM_NAME = "sakaiPresentationItemName";

   public static final String XSL_FORM_TYPE = "metaobjFormType";

   public static final String XSL_ARTIFACT_ID = "metaobjArtifactId";

   public static final String XSL_ARTIFACT_REFERENCE = "metaobjArtifactReference";

   public static final String XSL_OBJECT_ID = "sakaiObjectId";

   public static final String XSL_OBJECT_TITLE = "sakaiObjectTitle";

   public static final String XSL_WIZARD_PAGE_ID = "sakaiWizardPageId";

}

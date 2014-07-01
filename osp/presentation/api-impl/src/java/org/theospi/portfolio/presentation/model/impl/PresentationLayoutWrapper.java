/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationLayoutWrapper.java $
* $Id: PresentationLayoutWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.model.impl;

import org.theospi.portfolio.presentation.model.PresentationLayout;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 30, 2006
 * Time: 10:25:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationLayoutWrapper extends PresentationLayout {

   private String idValue;
   private String layoutFileLocation;
   private String previewFileLocation;
   private String previewFileType;
   private String previewFileName;

   public String getLayoutFileLocation() {
      return layoutFileLocation;
   }

   public void setLayoutFileLocation(String layoutFileLocation) {
      this.layoutFileLocation = layoutFileLocation;
   }

   public String getPreviewFileLocation() {
      return previewFileLocation;
   }

   public void setPreviewFileLocation(String previewFileLocation) {
      this.previewFileLocation = previewFileLocation;
   }

   public String getIdValue() {
      return idValue;
   }

   public void setIdValue(String idValue) {
      this.idValue = idValue;
   }

   public String getPreviewFileType() {
      return previewFileType;
   }

   public void setPreviewFileType(String previewFileType) {
      this.previewFileType = previewFileType;
   }

   public String getPreviewFileName() {
      return previewFileName;
   }

   public void setPreviewFileName(String previewFileName) {
      this.previewFileName = previewFileName;
   }
}

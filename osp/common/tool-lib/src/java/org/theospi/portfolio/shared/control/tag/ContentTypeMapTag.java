/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/control/tag/ContentTypeMapTag.java $
* $Id:ContentTypeMapTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.control.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.sakaiproject.content.api.ContentTypeImageService;
import org.sakaiproject.metaobj.shared.model.MimeType;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 28, 2005
 * Time: 9:23:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentTypeMapTag extends TagSupport {

   private static final String MAP_TYPE_IMAGE = "image";
   private static final String MAP_TYPE_NAME = "name";
   private static final String MAP_TYPE_EXTENSION = "extension";

   private MimeType fileType;
   private String mapType;

   public ContentTypeMapTag() {
      init();
   }

   public int doStartTag() throws JspException {
       String result = getValue(fileType.getValue(), mapType, getImageTypeService());
      try {
         pageContext.getOut().write(result);
      }
      catch (IOException e) {
         throw new JspException(e);
      }
      return super.doStartTag();
   }

   protected void init() {
      mapType = MAP_TYPE_IMAGE;
   }

   protected String getValue(String fileType, String mapType, ContentTypeImageService service) {
      if (mapType.equals(MAP_TYPE_IMAGE)) {
         return service.getContentTypeImage(fileType);
      }
      else if (mapType.equals(MAP_TYPE_NAME)) {
         return service.getContentTypeDisplayName(fileType);
      }
      else if (mapType.equals(MAP_TYPE_EXTENSION)) {
         return service.getContentTypeExtension(fileType);
      }
      else {
         return null;
      }
   }

   protected ContentTypeImageService getImageTypeService() {
      return org.sakaiproject.content.cover.ContentTypeImageService.getInstance();
   }

   public MimeType getFileType() {
      return fileType;
   }

   public void setFileType(MimeType fileType) {
      this.fileType = fileType;
   }

   public String getMapType() {
      return mapType;
   }

   public void setMapType(String mapType) {
      this.mapType = mapType;
   }

}

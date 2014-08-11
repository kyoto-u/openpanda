/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/RefCustomEditor.java $
* $Id:RefCustomEditor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.utils.mvc.impl;

import java.beans.PropertyEditorSupport;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 8, 2005
 * Time: 11:10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefCustomEditor  extends PropertyEditorSupport implements TypedPropertyEditor {

   public Class getType() {
      return Reference.class;
   }

   public String getAsText() {
      Object value = getValue();
      if (value instanceof Reference && value != null) {
         return ((Reference)value).getReference();
      }
      else {
         return "";
      }
   }

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.equals("")) {
         setValue(null);
      }
      else {
         setValue(EntityManager.newReference(text));
      }
   }
}

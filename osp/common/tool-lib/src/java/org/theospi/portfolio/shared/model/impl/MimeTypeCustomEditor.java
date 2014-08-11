/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/model/impl/MimeTypeCustomEditor.java $
* $Id:MimeTypeCustomEditor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.shared.model.impl;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MimeTypeCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final Log logger = LogFactory.getLog(this.getClass());

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(null);
      } else {
         String[] items = text.split(",");
         Collection mimeTypes = new HashSet();
         for (int i = 0; i < items.length; i++) {
            ItemDefinitionMimeType item = new ItemDefinitionMimeType();
            String[] values = items[i].split("/");
            if (values.length < 1) return;
            item.setPrimary(values[0]);
            if (values.length == 2) {
               item.setSecondary(values[1]);
            }
            mimeTypes.add(item);
         }
         setValue(mimeTypes);
      }
   }

   public String getAsText() {
      StringBuilder buffer = new StringBuilder();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         ItemDefinitionMimeType item = (ItemDefinitionMimeType) i.next();
         buffer.append(item.getPrimary());
         if (item.getSecondary() != null) {
            buffer.append("/" + item.getSecondary());
         }
      }
      return buffer.toString();
   }

   public Class getType() {
      return Set.class;
   }


}

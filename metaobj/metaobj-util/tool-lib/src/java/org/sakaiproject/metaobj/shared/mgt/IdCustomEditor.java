/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/mgt/IdCustomEditor.java $
 * $Id: IdCustomEditor.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt;

import java.beans.PropertyEditorSupport;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {

   private IdManager idManager = null;


   /**
    * Parse the Date from the given text, using the specified DateFormat.
    */
   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(null);
      }
      else {
         setValue(getIdManager().getId(text));
      }
   }

   /**
    * Format the Date as String, using the specified DateFormat.
    */
   public String getAsText() {
      if (!(getValue() instanceof Id)) {
         return null;
      }
      Id value = (Id) getValue();
      return value.getValue();
   }


   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public Class getType() {
      return Id.class;
   }
}

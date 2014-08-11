/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/tool/ToolBase.java $
* $Id:ToolBase.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.tool;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.sakaiproject.util.ResourceLoader;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 12, 2005
 * Time: 1:30:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolBase {
   //private ResourceBundle toolBundle;
   private ResourceLoader toolBundle;

   public Object createSelect(Object id, String description) {
      SelectItem item = new SelectItem(id, description);
      return item;
   }

   public String getMessageFromBundle(String key, Object[] args) {
      return MessageFormat.format(getMessageFromBundle(key), args);
   }

   public FacesMessage getFacesMessageFromBundle(String key, Object[] args) {
      return new FacesMessage(getMessageFromBundle(key, args));
   }

   public String getMessageFromBundle(String key) {
      if (toolBundle == null) {
         String bundle = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
         toolBundle = new ResourceLoader(bundle);
      /*   Locale requestLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
         if (requestLocale != null) {
            toolBundle = ResourceBundle.getBundle(
                  bundle, requestLocale);
         }
         else {
            toolBundle = ResourceBundle.getBundle(bundle);
         }*/
      }
      return toolBundle.getString(key);
   }

   protected void processChildCancel(UIComponent component) {
      if (component instanceof UIInput) {
         UIInput input = (UIInput) component;
         input.setSubmittedValue(null);
      }

      for(Iterator i=component.getChildren().iterator();i.hasNext();) {
         UIComponent child=(UIComponent)i.next();
         processChildCancel(child);
      }
   }

   protected void cancelBoundValues() {
      FacesContext facesContext=FacesContext.getCurrentInstance();
      UIViewRoot viewRoot= facesContext.getViewRoot();
      processChildCancel(viewRoot);
   }

}

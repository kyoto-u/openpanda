/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool-lib/src/java/org/theospi/portfolio/presentation/model/impl/PresentationItemCustomEditor.java $
* $Id:PresentationItemCustomEditor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model.impl;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.PresentationItem;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationItemCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final Log logger = LogFactory.getLog(this.getClass());
   private PresentationManager presentationManager;
   private IdManager idManager = null;
   private HomeFactory homeFactory;


   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0 || text.indexOf(".") == -1) {
         setValue(null);
      } else {
         String[] items = text.split(",");
         Collection presentationItems = new HashSet();
         for (int i = 0; i < items.length; i++) {
            PresentationItem item = new PresentationItem();
            String[] values = items[i].split("\\.");
            if (values.length != 2) continue;
            item.setDefinition(getPresentationManager().getPresentationItemDefinition(getIdManager().getId(values[0])));
            item.setArtifactId(getIdManager().getId(values[1]));
            presentationItems.add(item);
         }
         setValue(presentationItems);
      }
   }

   public String getAsText() {
      StringBuilder buffer = new StringBuilder();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         buffer.append(item.getDefinition().getId().getValue() + "." +
            item.getArtifactId().getValue());
      }
      return buffer.toString();
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public Class getType() {
      return Set.class;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

}

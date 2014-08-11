/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool-lib/src/java/org/theospi/portfolio/presentation/model/impl/PresentationViewerCustomEditor.java $
* $Id:PresentationViewerCustomEditor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.theospi.portfolio.presentation.PresentationManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationViewerCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final Log logger = LogFactory.getLog(this.getClass());
   private PresentationManager presentationManager;
   private IdManager idManager = null;
   private AgentManager agentManager;

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(new HashSet());
      } else {
         String[] items = text.split(",");
         Collection viewers = new HashSet();

         for (int i = 0; i < items.length; i++) {
            Agent agent = getAgentManager().getAgent(items[i]);
            if (agent == null) {
               agent = createGuest(items[i]);
            }
            viewers.add(agent);
         }
         setValue(viewers);
      }
   }

   protected Agent createGuest(String item) {
      AgentImpl viewer = new AgentImpl();
      viewer.setDisplayName(item);
      viewer.setRole(Agent.ROLE_GUEST);
      viewer.setId(getIdManager().getId(viewer.getDisplayName()));
      return viewer;
   }

   public String getAsText() {
      StringBuilder buffer = new StringBuilder();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         Agent agent = (Agent) i.next();
         buffer.append(agent.getId().getValue());
      }
      return buffer.toString();
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public Class getType() {
      return Collection.class;
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

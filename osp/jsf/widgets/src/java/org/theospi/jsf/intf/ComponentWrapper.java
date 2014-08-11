/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/intf/ComponentWrapper.java $
* $Id:ComponentWrapper.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.jsf.intf;

import javax.faces.component.UIComponent;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:42:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComponentWrapper {
   private UIComponent component;
   private XmlTagHandler handler;
   private ComponentWrapper parent;

   public ComponentWrapper(ComponentWrapper parent, UIComponent component, XmlTagHandler handler) {
      this.parent = parent;
      this.component = component;
      this.handler = handler;
   }

   public UIComponent getComponent() {
      return component;
   }

   public void setComponent(UIComponent component) {
      this.component = component;
   }

   public XmlTagHandler getHandler() {
      return handler;
   }

   public void setHandler(XmlTagHandler handler) {
      this.handler = handler;
   }

   public ComponentWrapper getParent() {
      return parent;
   }

   public void setParent(ComponentWrapper parent) {
      this.parent = parent;
   }
}

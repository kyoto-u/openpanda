/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/impl/DefaultComponentWrapper.java $
* $Id:DefaultComponentWrapper.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.jsf.impl;

import java.io.StringWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagHandler;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 3, 2006
 * Time: 12:10:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultComponentWrapper extends ComponentWrapper {

   private ResponseWriter writer;
   private StringWriter buffer;

   public DefaultComponentWrapper(ComponentWrapper parent, UIComponent component, XmlTagHandler handler) {
      super(parent, component, handler);
   }

   public ResponseWriter getWriter() {
      return writer;
   }

   public void setWriter(ResponseWriter writer) {
      this.writer = writer;
   }

   public StringWriter getBuffer() {
      return buffer;
   }

   public void setBuffer(StringWriter buffer) {
      this.buffer = buffer;
   }

}

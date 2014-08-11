/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/renderer/XmlDocumentRenderer.java $
* $Id:XmlDocumentRenderer.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.jsf.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.theospi.jsf.component.XmlDocumentComponent;
import org.theospi.jsf.util.TagUtil;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 2:28:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentRenderer extends Renderer {

   public boolean supportsComponentType(UIComponent component) {
      return (component instanceof XmlDocumentComponent);
   }

   public boolean getRendersChildren() {
      return true;
   }

   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      super.encodeChildren(context, component);
      XmlDocumentComponent docComponent = (XmlDocumentComponent) component;
     UIComponent layoutRoot = docComponent.getXmlRootComponent();
     TagUtil.renderChild(context, layoutRoot);
 }

}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/render/LayoutPageHandlerBase.java $
* $Id:LayoutPageHandlerBase.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.render;

import javax.faces.component.UIComponent;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.jsf.intf.XmlTagFactory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 6:17:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutPageHandlerBase extends DefaultXmlTagHandler {

   public LayoutPageHandlerBase(XmlTagFactory factory) {
      super(factory);
   }

   public XmlDocumentContainer getParentContainer(UIComponent current) {
      UIComponent parent = current;

      while (!(parent instanceof XmlDocumentContainer) &&
            parent != null) {
         parent = parent.getParent();
      }

      return (XmlDocumentContainer) parent;
   }

}

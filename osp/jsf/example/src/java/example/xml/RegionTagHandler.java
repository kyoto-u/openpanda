/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/example/src/java/example/xml/RegionTagHandler.java $
* $Id:RegionTagHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package example.xml;

import java.io.IOException;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 3:10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionTagHandler extends DefaultXmlTagHandler {

   public RegionTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri, String localName, String qName, Attributes attributes) throws IOException {
      UIInput container = new UIInput();
      createOutput(context, "starting region: " + attributes.getValue("id"), container);
      parent.getComponent().getChildren().add(container);
      return new ComponentWrapper(parent, container, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
      this.createOutput(context, "ending region", current.getComponent());
   }
}

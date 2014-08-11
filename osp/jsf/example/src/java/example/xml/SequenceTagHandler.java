/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/example/src/java/example/xml/SequenceTagHandler.java $
* $Id:SequenceTagHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import javax.faces.component.UIColumn;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 30, 2005
 * Time: 1:25:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTagHandler extends DefaultXmlTagHandler {

   public SequenceTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri, String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      UIData container = new UIData();
      parent.getComponent().getChildren().add(container);
      ValueBinding vb = context.getApplication().createValueBinding("#{testBean.subBeans}");
      container.setValueBinding("value", vb);
      container.setVar("testSubBean");
      container.setId(root.createUniqueId());
      UIColumn column = new UIColumn();
      column.setId(root.createUniqueId());
      container.getChildren().add(column);
      HtmlOutputLink testLink = new HtmlOutputLink();
      testLink.setValue("http://www.javasoft.com");
      HtmlOutputText text = new HtmlOutputText();
      text.setValue("test");
      testLink.getChildren().add(text);
      HtmlCommandButton button = new HtmlCommandButton();
      button.setId(root.createUniqueId());
      button.setActionListener(
            context.getApplication().createMethodBinding("#{testSubBean.processTestButton}",
                  new Class[]{ActionEvent.class}));
      button.setValue("test me");
      HtmlInputText input = new HtmlInputText();
      input.setValueBinding("value", context.getApplication().createValueBinding("#{testSubBean.index}"));
      input.setId(root.createUniqueId());
      column.getChildren().add(input);
      column.getChildren().add(button);
      HtmlOutputText testVerbatim = new HtmlOutputText();
      testVerbatim.setEscape(false);
      testVerbatim.setValue("<some>");
      column.getChildren().add(testVerbatim);
      column.getChildren().add(testLink);
      HtmlOutputText testVerbatim2 = new HtmlOutputText();
      testVerbatim2.setEscape(false);
      testVerbatim2.setValue("</some>");
      column.getChildren().add(testVerbatim2);
      return new ComponentWrapper(parent, column, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
      writeCharsToVerbatim(context, current, ch, start, length);
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
   }
}

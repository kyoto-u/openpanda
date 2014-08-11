/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/render/SequenceTagHandler.java $
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
package org.theospi.portfolio.presentation.render;

import java.io.IOException;

import javax.faces.component.UIColumn;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.presentation.component.SequenceComponent;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 30, 2005
 * Time: 1:25:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTagHandler extends LayoutPageHandlerBase {

   public SequenceTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      SequenceComponent container =
            (SequenceComponent) context.getApplication().createComponent(SequenceComponent.COMPONENT_TYPE);
      container.setId(root.createUniqueId());
      parent.getComponent().getChildren().add(container);
      XmlDocumentContainer docContainer = getParentContainer(parent.getComponent());
      String mapVar = docContainer.getVariableName();
      String regionId = attributes.getValue("firstChild");

      ValueBinding vb = context.getApplication().createValueBinding("#{"+mapVar+ "." + regionId + ".regionItemList}");
      container.setValue(vb.getValue(context));
      container.setValueBinding("value", vb);
      container.setVar("sequenceRegion");

      UIColumn column = (UIColumn) context.getApplication().createComponent(UIColumn.COMPONENT_TYPE);
      column.setId(root.createUniqueId());
      container.getChildren().add(column);

      HtmlCommandButton addButton =
            (HtmlCommandButton) context.getApplication().createComponent(HtmlCommandButton.COMPONENT_TYPE);
      addButton.setId(root.createUniqueId());
      addButton.setActionListener(
            context.getApplication().createMethodBinding("#{"+mapVar+ "." + regionId + ".addToSequence}",
                  new Class[]{ActionEvent.class}));
      // todo from bundle
      addButton.setValue("Add Row");
      parent.getComponent().getChildren().add(addButton);
      column.setHeader(addButton);

      // remove removeButton
      HtmlCommandButton removeButton =
            (HtmlCommandButton) context.getApplication().createComponent(HtmlCommandButton.COMPONENT_TYPE);
      removeButton.setId(root.createUniqueId());
      removeButton.setActionListener(
            context.getApplication().createMethodBinding("#{sequenceRegion.remove}",
                  new Class[]{ActionEvent.class}));
      // todo from bundle
      removeButton.setValue("Remove Row");
      column.getChildren().add(removeButton);

      return new ComponentWrapper(parent, column, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
      writeCharsToVerbatim(context, current, ch, start, length);
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/impl/DefaultXmlTagHandler.java $
* $Id:DefaultXmlTagHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlTagHandler;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:33:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultXmlTagHandler implements XmlTagHandler {

   private XmlTagFactory factory;

   public DefaultXmlTagHandler(XmlTagFactory factory) {
      this.factory = factory;
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {

      XmlTagHandler handler = getFactory().getHandler(uri, localName, qName);
      if (handler != null) {
         if (parent != null && parent instanceof DefaultComponentWrapper) {
            DefaultComponentWrapper wrapper = (DefaultComponentWrapper) parent;
            initWrapper(wrapper, context);
            createOutput(context, wrapper.getBuffer(), wrapper.getComponent());
            wrapper.getBuffer().getBuffer().delete(0, Integer.MAX_VALUE);
         }

         return handler.startElement(context, parent, uri, localName, qName, attributes);
      }
      else {
         ResponseWriter writer = null;
         DefaultComponentWrapper wrapper = null;
         if (parent instanceof DefaultComponentWrapper) {
            wrapper = (DefaultComponentWrapper) parent;
            initWrapper(wrapper, context);
            writer = wrapper.getWriter();
         }
         else {
            wrapper = new DefaultComponentWrapper(parent, parent.getComponent(), null);
            initWrapper(wrapper, context);
            writer = wrapper.getWriter();
         }

         writer.startElement(qName, null);
         if (attributes != null) {
            for (int i=0;i < attributes.getLength();i++) {
               writer.writeAttribute(attributes.getQName(i),
                     attributes.getValue(i), null);
            }
         }
         writer.writeText("", null);
         return wrapper;
      }
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length)
         throws IOException {
      if (current != null && current.getHandler() != null) {
         current.getHandler().characters(context, current, ch, start, length);
      }
      else if (current instanceof DefaultComponentWrapper) {
         DefaultComponentWrapper wrapper = (DefaultComponentWrapper) current;
         initWrapper(wrapper, context);
         wrapper.getWriter().write(ch, start, length);
      }
   }

   public void endElement(FacesContext context, ComponentWrapper current,
                          String uri, String localName, String qName) throws IOException {
      if (current != null && current.getHandler() != null) {
         current.getHandler().endElement(context, current, uri, localName, qName);
      }
      else if (current instanceof DefaultComponentWrapper) {
         DefaultComponentWrapper wrapper = (DefaultComponentWrapper) current;
         initWrapper(wrapper, context);
         wrapper.getWriter().endElement(qName);
      }
   }

   public void endDocument(FacesContext context, ComponentWrapper current) throws IOException {
      if (current != null && current instanceof DefaultComponentWrapper) {
         DefaultComponentWrapper wrapper = (DefaultComponentWrapper) current;
         initWrapper(wrapper, context);
         wrapper.getWriter().writeText("", null);
         createOutput(context, wrapper.getBuffer(), current.getComponent());
         wrapper.getBuffer().getBuffer().delete(0, Integer.MAX_VALUE);
      }
   }

   protected void initWrapper(DefaultComponentWrapper wrapper, FacesContext context) {
      if (wrapper.getWriter() == null) {
         wrapper.setBuffer(new StringWriter());
         wrapper.setWriter(context.getResponseWriter().cloneWithWriter(wrapper.getBuffer()));
      }
   }

   protected void writeCharsToVerbatim(FacesContext context, ComponentWrapper current,
                                       char[] ch, int start, int length) throws IOException {
      StringWriter buffer = new StringWriter();
      ResponseWriter writer = context.getResponseWriter().cloneWithWriter(buffer);
      writer.write(ch, start, length);
      createOutput(context, buffer, current.getComponent());
   }

   protected void createOutput(FacesContext context, String text, UIComponent parent) {
      UIViewRoot root = context.getViewRoot();
      HtmlOutputText outputComponent =
            (HtmlOutputText) context.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
      outputComponent.setTransient(false);
      outputComponent.setId(root.createUniqueId());
      outputComponent.setEscape(false);
      outputComponent.setValue(text);
      parent.getChildren().add(outputComponent);
   }

   protected void createOutput(FacesContext context, StringWriter buffer, UIComponent parent) {
      createOutput(context, buffer.getBuffer().toString(), parent);
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

}

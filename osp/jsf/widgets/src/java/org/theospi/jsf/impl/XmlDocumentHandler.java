/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/jsf/widgets/src/java/org/theospi/jsf/impl/XmlDocumentHandler.java $
* $Id: XmlDocumentHandler.java 64456 2009-07-01 17:19:53Z chmaurer@iupui.edu $
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
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlTagHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 1:52:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentHandler extends DefaultHandler {

   private XmlTagFactory factory;
   private FacesContext context;
   private UIComponent rootView;
   private Stack components = new Stack();
   private XmlTagHandler defaultHandler;

   public XmlDocumentHandler(FacesContext context, XmlTagFactory factory, UIComponent rootView) {
      this.context = context;
      this.factory = factory;
      this.rootView = rootView;
      this.defaultHandler = factory.getDefaultHandler();
   }

   public void startElement(String uri, String localName, String qName,
                            Attributes attributes) throws SAXException {
      try {
         ComponentWrapper wrapper = defaultHandler.startElement(context, getCurrentComponent(),
               uri, localName, qName, attributes);
         components.push(wrapper);
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   public void characters(char ch[], int start, int length) throws SAXException {
      ComponentWrapper component = (ComponentWrapper) components.peek();
      XmlTagHandler handler = component.getHandler();
      if (handler == null) {
         handler = defaultHandler;
      }
      try {
         handler.characters(context, component,  ch,  start,  length);
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      ComponentWrapper component = (ComponentWrapper) components.pop();
      XmlTagHandler handler = component.getHandler();
      if (handler == null) {
         handler = defaultHandler;
      }
      try {
         handler.endElement(context, component,  uri, localName, qName);
         if (components.empty()) {
            handler.endDocument(context, component);
         }
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   protected XmlTagHandler getCurrentHandler() {
      ComponentWrapper component = getCurrentComponent();

      if (component == null) {
         return defaultHandler;
      }
      return component.getHandler();
   }

   public ComponentWrapper getCurrentComponent() {
      ComponentWrapper component = null;
      if (!components.isEmpty()) {
         component = (ComponentWrapper) components.peek();
      }
      else {
         component = new ComponentWrapper(null, rootView, null);
      }
      return component;
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public FacesContext getContext() {
      return context;
   }

   public void setContext(FacesContext context) {
      this.context = context;
   }
   
   public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
	   CatalogResolver resolver = new CatalogResolver();

	   String url = ServerConfigurationService.getServerUrl();
	   resolver.getCatalog().parseCatalog(url + "/osp-common-tool/dtd/catalog.xml");
	   return resolver.resolveEntity(publicId, systemId);
   }

}

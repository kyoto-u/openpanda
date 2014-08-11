/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/component/XmlDocumentComponent.java $
* $Id:XmlDocumentComponent.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.jsf.component;

import java.io.InputStream;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.jsf.intf.XmlTagFactory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 2:28:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentComponent extends UIComponentBase implements XmlDocumentContainer {

   private XmlTagFactory factory;
   private InputStream xmlFile;
   private UIComponent xmlRootComponent;
   private String xmlFileId;
   private String oldXmlFileId;
   private String var;

   public XmlDocumentComponent() {
      super();
      this.setRendererType("org.theospi.XmlDocument");
   }

   public String getFamily() {
      return "org.theospi.xml";
   }

   public XmlTagFactory getFactory() {
      if (factory != null) return factory;
      ValueBinding vb = getValueBinding("factory");
      factory = (XmlTagFactory) vb.getValue(getFacesContext());
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }
   
   /**
    * Any time this is called the calling method MUST close the input stream!!
    * This has the potential of causing memory leaks if the calling method does not close the stream
    * @return InputStream
    */
   public InputStream getXmlFile() {
      ValueBinding vb = getValueBinding("xmlFile");
      return (InputStream) vb.getValue(getFacesContext());
   }

   public void setXmlFile(InputStream xmlFile) {
      this.xmlFile = xmlFile;
   }

   public UIComponent getXmlRootComponent() {
      return xmlRootComponent;
   }

   public void setXmlRootComponent(UIComponent xmlRootComponent) {
      this.xmlRootComponent = xmlRootComponent;
   }

   public String getVariableName() {
      return getVar();
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public void broadcast(FacesEvent event) throws AbortProcessingException {
      super.broadcast(event);
   }

   public Object saveState(FacesContext context) {
      return super.saveState(context);
   }

   public void restoreState(FacesContext context, Object state) {
      super.restoreState(context, state);
   }

   public void setTransient(boolean transientFlag) {
      super.setTransient(transientFlag);
   }

   public String getXmlFileId() {
      ValueBinding vb = getValueBinding("xmlFileId");
      if (vb != null) {
         return (String) vb.getValue(getFacesContext());
      }
      return null;
   }

   public void setXmlFileId(String xmlFileId) {
      this.xmlFileId = xmlFileId;
   }

   public String getOldXmlFileId() {
      return oldXmlFileId;
   }

   public void setOldXmlFileId(String oldXmlFileId) {
      this.oldXmlFileId = oldXmlFileId;
   }
}

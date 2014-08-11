/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/shared/model/impl/GenericXmlRenderer.java $
* $Id:GenericXmlRenderer.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.shared.model.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.EntityContextFinder;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.theospi.portfolio.shared.model.OspException;

/**
 * This class renders an object into an xml object.  This implementation
 * uses bean introspection to navigate the object model and convert into
 * a jdom model. In relies on the objectStructure xml file to specify which
 * properties to traverse.  The prevents circlular references from being
 * traversed.
 *
 * Valid values for type attribute are: collection, artifact, object.
 * If not specified object is assumed.
 *
 *
 */
public class GenericXmlRenderer implements PresentableObjectHome {
   protected final Log logger = LogFactory.getLog(getClass());
   private ArtifactFinder artifactFinder;
   private String objectStructure;
   private String rootName;
   private String supportedType;
   private String artifactType;

   protected Element getObjectStructureRoot(){
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131
      InputStream is = null;
      try {
         is = getClass().getResourceAsStream(getObjectStructure());
         Document doc = builder.build(is);
         return doc.getRootElement();
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
      finally {
         try {
            is.close();
         } catch (Exception e) {
            logger.warn("Error cleaning up resource:", e);
         }
      }
   }

   protected Element getXml(Object object, String container, String site, String context) {
      Element rootElement = new Element(getRootName());
      try {
         addObjectNodeInfo(rootElement, object, getObjectStructureRoot(), container, site, context);
      } catch (IntrospectionException e) {
         logger.error("",e);
      }

      return rootElement;
   }

   protected boolean isTraversableType(PropertyDescriptor descriptor, Element structure) {
      if (structure == null) {
         return false;
      }
      if (structure.getChild(descriptor.getName()) != null || structure.getName().equals(descriptor.getName())){
         return true;
      }

      return false;
   }

   protected void addObjectNodeInfo(Element parentNode, Object object, Element structure, String container, String site, String context) throws IntrospectionException {
      if (object == null) return;
      // go through each property... put each one in...
      logger.debug("adding object of class " + object.getClass());

      BeanInfo info = Introspector.getBeanInfo(object.getClass());

      PropertyDescriptor[] props = info.getPropertyDescriptors();

      for (int i = 0; i < props.length; i++) {
         PropertyDescriptor property = props[i];
         logger.debug("examining property: " + property.getName());
         if (isTraversableType(property, structure)){
            if (isCollection(property,structure)){
               addCollectionItems(parentNode, property, object, structure, container, site, context);
            } else if (isArtifact(property,structure)) {
               addArtifactItem(parentNode, property, object, container, site, context);
            } else {
               addItem(parentNode, property, object, structure, container, site, context);
            }
         } else {
            addItemToXml(parentNode, property, object);
         }

      }
   }

   protected void addItemToXml(Element parentNode, PropertyDescriptor prop, Object object) {
      String attribName = prop.getName();

      logger.debug("adding attribute: " + attribName);

      Method readMethod = prop.getReadMethod();
      if (readMethod == null || readMethod.getParameterTypes().length > 0 ||
              Collection.class.isAssignableFrom(readMethod.getReturnType())) {
         logger.debug("skipping attrib: " + attribName);
         return;
      }

      Element attribute = new Element(attribName);
      Object attribValue = null;

      try {
         attribValue = readMethod.invoke(object, (Object[]) null);
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      }
      if (attribValue != null && attribValue.toString().length() > 0) {
         logger.debug("value for attrib " + attribName + " is not null.");
         attribute.addContent(attribValue.toString());
      }

      parentNode.addContent(attribute);
   }

   protected void addItem(Element parentNode, PropertyDescriptor prop, Object object, Element structure, String container, String site, String context) {
      logger.debug("addItem()");

      Method readMethod = prop.getReadMethod();
      Element newElement = new Element(prop.getName());
      try {
         Object newObject = readMethod.invoke(object, (Object[]) null);
         parentNode.addContent(newElement);
         addObjectNodeInfo(newElement, newObject, structure.getChild(prop.getName()), container, site, context);
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      } catch (IntrospectionException e) {
         logger.error("could not get attribute", e);
      }
   }


   protected void addArtifactItem(Element parentNode, PropertyDescriptor prop, Object object, String container, String site, String context) {
      logger.debug("addArtifactItem()");

      Method readMethod = prop.getReadMethod();
      Id artifactId = null;
      try {
    	  artifactId = (Id) readMethod.invoke(object, (Object[]) null);
    	  
    	  
      } catch (IllegalAccessException e) {
    	  logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
    	  logger.error("could not get attribute", e);
      }

      logger.debug("finding artifact with id=" + artifactId);

      Artifact art = getArtifactFinder().load(artifactId);
      
      if (art.getHome() instanceof PresentableObjectHome) {
         PresentableObjectHome home = (PresentableObjectHome) art.getHome();
         Element node = home.getArtifactAsXml(art, container, site, context);
         node.setName("artifact");
         parentNode.addContent(node);
      }
   }

   protected void addCollectionItems(Element parentNode, PropertyDescriptor prop, Object object, Element structure, String container, String site, String context){
      logger.debug("addCollectionItems()");

      Method readMethod = prop.getReadMethod();
      Element newListElement = new Element(prop.getName());
      try {
         Object newObject = readMethod.invoke(object, (Object[]) null);
         parentNode.addContent(newListElement);
         Collection items = (Collection) newObject;
         int index=0;
         for (Iterator i= items.iterator(); i.hasNext();){
            Element newElement = new Element(getCollectionItemName(prop.getName()));
            newElement.setAttribute("index", String.valueOf(index));
            newListElement.addContent(newElement);
            
            Element elementStructure = structure.getChild(prop.getName());
            if (elementStructure == null && structure.getAttributeValue("isNested") != null && 
                  structure.getAttributeValue("isNested").equals("true"))
            {
               elementStructure = structure.getParentElement().getChild(prop.getName());
            }
            addObjectNodeInfo(newElement, i.next(), elementStructure, container, site, context);
            index++;
         }
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      } catch (IntrospectionException e) {
         logger.error("could not get attribute", e);
      }

   }

   protected String getCollectionItemName(String listName){
      if (listName.endsWith("s")) {
         return listName.substring(0, listName.length() -1 );
      }
      return listName;
   }

   protected boolean isArtifact(PropertyDescriptor prop, Element structure) {
      Element child = structure.getChild(prop.getName());
      String typeAttribute = child.getAttributeValue("type");
      if (typeAttribute != null && typeAttribute.equals("artifact")){
         return true;
      }
      return false;
   }

   protected boolean isCollection(PropertyDescriptor prop, Element structure) {
      Element elementStructure = structure.getChild(prop.getName());
      if (elementStructure == null && structure.getAttributeValue("isNested") != null && 
            structure.getAttributeValue("isNested").equals("true"))
      {
         elementStructure = structure.getParentElement().getChild(prop.getName());
      }
      else if (elementStructure == null) {
         return false;
      }
      String typeAttribute = elementStructure.getAttributeValue("type");
      if (typeAttribute != null && typeAttribute.equals("collection") ){
         return true;
      }
      return false;
   }


   public Element getArtifactAsXml(Artifact artifact) {
      return getArtifactAsXml(artifact, null, null, null);
   }
   
   public Element getArtifactAsXml(Artifact artifact, String container, String site, String context) {
	   try {
		   Class supportedType = Class.forName(getSupportedType());
		   if (supportedType.isAssignableFrom(artifact.getClass())) {
			   return getXml(artifact, container, site, context);
		   }
	   } catch (ClassNotFoundException e) {
		   throw new RuntimeException(getSupportedType() + " is not a valid class: " + e.getMessage(),e);
	   }

	   throw new OspException("Expecting object of type: "  + getSupportedType() + " but found object of type "  +
			   artifact.getClass());
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public String getObjectStructure() {
      return objectStructure;
   }

   public String getRootName() {
      return rootName;
   }

   public void setRootName(String rootName) {
      this.rootName = rootName;
   }

   public void setObjectStructure(String objectStructure) {
      this.objectStructure = objectStructure;
   }

   public String getSupportedType() {
      return supportedType;
   }

   public void setSupportedType(String supportedType) {
      this.supportedType = supportedType;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(String artifactType) {
      this.artifactType = artifactType;
   }
}

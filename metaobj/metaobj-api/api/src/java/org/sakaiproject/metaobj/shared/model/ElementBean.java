/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/ElementBean.java $
 * $Id: ElementBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.metaobj.shared.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.FieldValueWrapperFactory;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 10, 2004
 * Time: 3:45:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementBean extends HashMap implements TypedMap {

   protected final Log logger = LogFactory.getLog(getClass());

   private Element baseElement;
   private Map types = new HashMap();
   private SchemaNode currentSchema;
   private Map wrappedInstances = new HashMap();
   private Map wrappedLists = new HashMap();
   private boolean deferValidation = true;
   private ElementListBean parent;

   public static final String FIELD_DATA_TAG = "FIELD_DATA";

   private static FieldValueWrapperFactory wrapperFactory = null;


   public ElementBean(String elementName, SchemaNode currentSchema) {
      this.currentSchema = currentSchema;
      setBaseElement(new Element(elementName));
   }

   public ElementBean(String elementName, SchemaNode currentSchema, boolean deferValidation) {
      this.deferValidation = deferValidation;
      this.currentSchema = currentSchema;
      setBaseElement(new Element(elementName));
   }

   public ElementBean() {
      setBaseElement(new Element("empty"));
   }

   public ElementBean(Element baseElement, SchemaNode currentSchema, boolean deferValidation) {
      this.deferValidation = deferValidation;
      this.currentSchema = currentSchema;
      setBaseElement(baseElement);
   }

   public ElementBean(Element baseElement, SchemaNode currentSchema, Map wrappedInstances) {
      this.currentSchema = currentSchema;
      this.wrappedInstances = wrappedInstances;
      setBaseElement(baseElement);
   }

   public Element currentElement() {
      return baseElement;
   }

   public SchemaNode getCurrentSchema() {
      return currentSchema;
   }

   public void setCurrentSchema(SchemaNode currentSchema) {
      this.currentSchema = currentSchema;
   }

   public Object put(Object key, Object value) {
      if ( currentSchema == null ) {
         logger.debug("null schema -- ignore put for "+key);
         return null;
      }
         
      SchemaNode elementSchema = currentSchema.getChild((String) key);

      if (getWrapperFactory().checkWrapper(elementSchema.getObjectType())) {
         return this.wrappedObjectPut(key, value, elementSchema);
      }

      if (elementSchema.getMaxOccurs() == 1) {

         String normalizedValue;

         try {
            normalizedValue = elementSchema.getSchemaNormalizedValue(value);
         }
         catch (NormalizationException exp) {
            if (deferValidation) {
               normalizedValue = value.toString();
            }
            else {
               throw exp;
            }
         }

         if (elementSchema.isAttribute()) {
            Attribute oldAttribute = currentElement().getAttribute((String) key);
            if (value != null && value.toString().length() > 0) {
               logger.debug("not removing attribute" + key);
               if (oldAttribute == null) {
                  currentElement().setAttribute(key.toString(), normalizedValue);
               }
               else {
                  oldAttribute.setValue(normalizedValue);
               }
            }
            else if (oldAttribute != null) {
               logger.debug("removing attribute" + key);
               currentElement().removeAttribute(key.toString());
            }
         }
         else {
            Element oldElement = currentElement().getChild((String) key);

            if (value != null && value.toString().length() > 0) {
               if (oldElement == null) {
                  Element newElement = new Element((String) key);
                  newElement.addContent(normalizedValue);
                  currentElement().addContent(newElement);
               }
               else {
                  oldElement.setText(normalizedValue);
               }
            }
            else if (oldElement != null) {
               currentElement().removeContent(oldElement);
            }
         }
      }
      else {
         // if you got here, must be a simple element
         ElementListBean listBean = (ElementListBean) get(key);

         while (listBean.size() > 0) {
            listBean.remove(0);
         }

         if (value instanceof String[]) {

            String[] values = (String[]) value;

            for (int i = 0; i < values.length; i++) {
               if (values[i].length() > 0) {
                  ElementBean bean = listBean.createBlank();
                  bean.getBaseElement().addContent(values[i]);
                  listBean.add(bean);
               }
            }
         }
         else if (value instanceof String) {
            if (value.toString().length() > 0) {
               ElementBean bean = listBean.createBlank();
               bean.getBaseElement().addContent((String) value);
               listBean.add(bean);
            }
         }

      }

      return null;
   }

   protected FieldValueWrapperFactory getWrapperFactory() {
      if (wrapperFactory == null) {
         wrapperFactory = (FieldValueWrapperFactory) ComponentManager.getInstance().get("fieldValueWrapperFactory");
      }
      return wrapperFactory;
   }

   public static void setWrapperFactory(FieldValueWrapperFactory wrapperFactory) {
      ElementBean.wrapperFactory = wrapperFactory;
   }

   public Object remove(Object key) {
      currentElement().removeChild((String) key);
      types.remove(key);
      return null;
   }


   public Object get(Object key) {
      if ( currentSchema == null ) {
         logger.debug("null schema -- ignore get for "+key);
         return null;
      }

      SchemaNode schema = currentSchema.getChild((String) key);

      if (schema == null) {
         return null;
      }

      if (schema.getMaxOccurs() > 1 || schema.getMaxOccurs() == -1) {
         List childElements = new ArrayList();
         List rawElements = baseElement.getChildren((String) key);
         for (Iterator i = rawElements.iterator(); i.hasNext();) {
            logger.debug("got child");
            childElements.add(new ElementBean((Element) i.next(), schema, deferValidation));
         }

         if (getWrapperFactory().checkWrapper(schema.getObjectType())) {
            return wrappedListGet(childElements, schema, key);
         }
         else {
            return new ElementListBean(baseElement, childElements, schema, deferValidation);
         }
      }

      if (getWrapperFactory().checkWrapper(schema.getObjectType())) {
         return this.wrappedObjectGet(key, schema);
      }

      if (schema.isAttribute()) {
         Attribute child = baseElement.getAttribute((String) key);

         if (child == null) {
            return null;
         }
         else {
            try {
               return schema.getActualNormalizedValue(child.getValue());
            }
            catch (NormalizationException exp) {
               // This should not happen... values should already be validated...
               // just return the text itself...
               return child.getValue();
            }
         }
      }
      else {
         Element child = baseElement.getChild((String) key);

         if (child == null) {
            if (schema.getObjectType().isAssignableFrom(java.util.Map.class)) {
               child = new Element(schema.getName());
               baseElement.addContent(child);
            }
            else {
               return null;
            }
         }

         logger.debug("returning typed object");

         Class objectClass = schema.getObjectType();

         if (Map.class.isAssignableFrom(objectClass)) {
            return new ElementBean(child, schema, deferValidation);
         }
         else {
            try {
               return schema.getActualNormalizedValue(child.getText());
            }
            catch (NormalizationException exp) {
               // This should not happen... values should already be validated...
               // just return the text itself...
               return child.getText();
            }
         }
      }
   }

   protected Object wrappedListGet(List childElements, SchemaNode schema, Object key) {
      if (wrappedLists.get(key) == null) {
         wrappedLists.put(key,
            new ElementListBeanWrapper(
               new ElementListBean(baseElement, childElements, schema, deferValidation),
               (FieldValueWrapper) wrappedObjectGet(key, schema)));
      }

      return wrappedLists.get(key);
   }

   protected Object wrappedObjectPut(Object key, Object value, SchemaNode schema) {
      FieldValueWrapper wrapper = (FieldValueWrapper) wrappedInstances.get(key);

      if (wrapper == null) {
         wrapper = getWrapperFactory().wrapInstance(value);
         wrappedInstances.put(key, wrapper);
      }
      else {
         wrapper.setValue(value);
      }

      Element childElement = getBaseElement().getChild(schema.getName());

      if (childElement == null) {
         childElement = new Element(schema.getName());
         getBaseElement().addContent(childElement);
      }

      childElement.setText(schema.getSchemaNormalizedValue(value));

      return null;
   }

   protected Object wrappedObjectGet(Object key, SchemaNode schema) {
      FieldValueWrapper wrapper = (FieldValueWrapper) wrappedInstances.get(key);

      if (wrapper == null) {
         wrapper = getWrapperFactory().wrapInstance(schema.getObjectType());
         wrappedInstances.put(key, wrapper);

         Element valueElement = getBaseElement().getChild(schema.getName());

         if (valueElement != null && valueElement.getContentSize() != 0) {
            Object value = schema.getActualNormalizedValue(valueElement.getText());
            wrapper.setValue(value);
         }
      }

      return wrapper;
   }


   public Class getType(String key) {
      if ( currentSchema == null ) {
         logger.debug("null schema -- ignore getType for "+key);
         return null;
      }
      
      SchemaNode schema = currentSchema.getChild(key);

      if (schema != null) {
         if (schema.getMaxOccurs() > 1 || schema.getMaxOccurs() == -1) {
            if (getWrapperFactory().checkWrapper(schema.getObjectType())) {
               return ElementListBeanWrapper.class;
            }
            else {
               return ElementListBean.class;
            }
         }
         else {
            return schema.getObjectType();
         }
      }
      return null;
   }

   public String toString() {
      return currentElement().getText();
   }

   public void setBaseElement(Element element) {
      this.baseElement = element;
   }

   public Element getBaseElement() {
      return baseElement;
   }

   public boolean isDeferValidation() {
      return deferValidation;
   }

   public void setDeferValidation(boolean deferValidation) {
      this.deferValidation = deferValidation;
   }

   /**
    * Generate a string containing XML tags and values representing the current contents of the element
    *
    * @return An XML String representation of the object
    */
   public String toXmlString() throws PersistenceException {
      XMLOutputter outputter = new XMLOutputter();
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      try {
         Format format = Format.getPrettyFormat();
         outputter.setFormat(format);
         outputter.output(getBaseElement(), os);
         return new String(os.toByteArray());
         //return os.toByteArray();
      }
      catch (IOException e) {
         throw new PersistenceException(e, "Unable to write object", null, null);
      }

   }	  // toXmlString

   class EntrySet implements Map.Entry {

      private Object key;
      private Object value;
      //TODO what about equals and hashcode?

      public EntrySet(Object key, Object value) {
         this.key = key;
         this.value = value;
      }

      /* (non-Javadoc)
       * @see java.util.Map.Entry#getKey()
       */
      public Object getKey() {
         return key;
      }

      /* (non-Javadoc)
       * @see java.util.Map.Entry#getValue()
       */
      public Object getValue() {
         return value;
      }

      /* (non-Javadoc)
       * @see java.util.Map.Entry#setValue(java.lang.Object)
       */
      public Object setValue(Object value) {
         // TODO Maybe throw an exception instead
         this.value = value;
         return this.value;
      }

   }


   /* (non-Javadoc)
    * @see java.util.Map#entrySet()
    */
   public Set entrySet() {
      Set entries = new HashSet();
      
      if ( currentSchema == null ) {
         logger.debug("null schema -- ignore entrySet");
         return entries;
      }
      
      List children = currentSchema.getChildren();
      for (Iterator iter = children.iterator(); iter.hasNext();) {
         SchemaNode child = (SchemaNode) iter.next();
         String key = child.getName();
         Object value = get(key);
         EntrySet entry = new EntrySet(key, value);
         entries.add(entry);
      }

      return entries;
   }

   /* (non-Javadoc)
    * @see java.util.Map#keySet()
    */
   public Set keySet() {
      Set keys = new HashSet();
      for (Iterator iter = entrySet().iterator(); iter.hasNext();) {
         EntrySet child = (EntrySet) iter.next();
         keys.add(child.getKey());
      }
      return keys;
   }

   /* (non-Javadoc)
    * @see java.util.Map#values()
    */
   public Collection values() {
      Set values = new HashSet();
      for (Iterator iter = entrySet().iterator(); iter.hasNext();) {
         EntrySet child = (EntrySet) iter.next();
         Object value = child.getValue();
         values.add(value);
      }
      return values;
   }

   public ElementListBean getParent() {
      return parent;
   }

   public void setParent(ElementListBean parent) {
      this.parent = parent;
   }

   public int getIndex() {
      if (parent != null) {
         return getParent().indexOf(this);
      }
      return 0;
   }

}

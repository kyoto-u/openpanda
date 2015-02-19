/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/MapWrapper.java $
 * $Id: MapWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.mvc.impl;

import java.beans.PropertyEditor;
import java.util.Map;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValue;

public class MapWrapper extends BeanWrapperBase {
   protected final Log logger = LogFactory.getLog(getClass());

   public MapWrapper() {
   }

   /**
    * Create new BeanWrapperImpl for the given object.
    *
    * @param object object wrapped by this BeanWrapper.
    * @throws BeansException if the object cannot be wrapped by a BeanWrapper
    */
   public MapWrapper(Object object) throws BeansException {
      super.setWrappedInstance(object);
   }

   /**
    * Create new BeanWrapperImpl for the given object,
    * registering a nested path that the object is in.
    *
    * @param object     object wrapped by this BeanWrapper.
    * @param nestedPath the nested path of the object
    * @param rootObject the root object at the top of the path
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public MapWrapper(Map object, String nestedPath, Object rootObject) throws BeansException {
      super(object, nestedPath, rootObject);
   }

   public Object getPropertyValue(String propertyName) throws BeansException {
      if (!(getWrappedInstance() instanceof Map)) {
         throw new FatalBeanException(getWrappedInstance().getClass() +
               ": bean is not a map, BeanWrapperImpl might be a better choice");
      }

      if (isNestedProperty(propertyName)) {
         return super.getPropertyValue(propertyName);
      }
      else {
         return ((Map) getWrappedInstance()).get(propertyName);
      }
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      Map map = (Map) getWrappedInstance();

      if (map instanceof TypedMap) {
         if (java.util.Map.class.isAssignableFrom(((TypedMap) map).getType(nestedProperty))) {
            return new MapWrapper((Map) map.get(nestedProperty),
               parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty, getWrappedInstance());
         }
         else if (java.util.List.class.isAssignableFrom(((TypedMap)map).getType(nestedProperty))) {
            return new ListWrapper((List) map.get(nestedProperty),
               parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty, getWrappedInstance());
         }
      }

      return super.createNestedWrapper(parentPath, nestedProperty);
   }

   protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty) {
      return new MixedBeanWrapper(propertyValue, nestedProperty, getWrappedInstance());
   }


   public void setPropertyValue(String propertyName, Object value) throws BeansException {

      if (!(getWrappedInstance() instanceof Map)) {
         throw new FatalBeanException(getWrappedInstance().getClass() +
               ": bean is not a map, BeanWrapperImpl might be a better choice");
      }

      Map map = (Map) getWrappedInstance();

      if (isNestedProperty(propertyName)) {
         super.setPropertyValue(propertyName, value);
         return;
      }

      if (!(map instanceof TypedMap) ||
            ((TypedMap) map).getType(propertyName) == null) {
         return;
      }

      map.put(propertyName, value);
   }

   public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
      return null;
   }

   public Class getPropertyType(String propertyName) throws BeansException {
      if (!(getWrappedInstance() instanceof TypedMap)) {
         return String.class;
      }
      return ((TypedMap) getWrappedInstance()).getType(propertyName);
   }

   public void setPropertyValue(PropertyValue pv) throws BeansException {
      setPropertyValue(pv.getName(), pv.getValue());
   }
}

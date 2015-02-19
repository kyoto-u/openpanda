/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/MixedBeanWrapper.java $
 * $Id: MixedBeanWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotWritablePropertyException;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 3:14:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MixedBeanWrapper extends BeanWrapperBase {
   public MixedBeanWrapper() {
   }

   /**
    * Create new BeanWrapperImpl for the given object.
    *
    * @param object object wrapped by this BeanWrapper.
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public MixedBeanWrapper(Object object) throws BeansException {
      super(object);
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
   public MixedBeanWrapper(Object object, String nestedPath, Object rootObject) throws BeansException {
      super(object, nestedPath, rootObject);
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      Class type = getPropertyType(nestedProperty);

      if (type == null) {
         throw new NotWritablePropertyException(String.class, nestedProperty);
      }

      if (java.util.Map.class.isAssignableFrom(type)) {
         return new MapWrapper((Map) getPropertyValue(nestedProperty),
               parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty, getWrappedInstance());
      }
      else {
         return super.createNestedWrapper(parentPath, nestedProperty);
      }
   }

   protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty) {
      return new MixedBeanWrapper(propertyValue, nestedProperty, getWrappedInstance());
   }

}

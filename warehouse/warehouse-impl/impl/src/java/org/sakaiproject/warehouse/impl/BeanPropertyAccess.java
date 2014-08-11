/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/impl/BeanPropertyAccess.java $
* $Id:BeanPropertyAccess.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.sakaiproject.warehouse.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.sakaiproject.warehouse.service.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 5:34:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeanPropertyAccess implements PropertyAccess {

   private Map gettorMap = new Hashtable();
   private String propertyName;

   public Object getPropertyValue(Object source) throws Exception {
      if(source == null)
         throw new NullPointerException("The source object is null, getting" +
               " property \"" + propertyName + "\"");
      Method objectMethodGetProperty = getPropertyGettor(source);
      if(objectMethodGetProperty == null)
         throw new NullPointerException(source.getClass().getName() +
               " has no get for property \"" + propertyName + "\"");
      return objectMethodGetProperty.invoke(source, new Object[]{});
   }

   public String getPropertyName() {
      return propertyName;
   }

   public void setPropertyName(String propertyName) {
      this.propertyName = propertyName;
   }

   public Method getPropertyGettor(Object source) throws IntrospectionException {
      Method propertyGettor = (Method) gettorMap.get(source.getClass());
      if (propertyGettor == null) {
         BeanInfo info = Introspector.getBeanInfo(source.getClass());

         PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

         for (int i=0;i<descriptors.length;i++) {
            if (descriptors[i].getName().equals(getPropertyName())) {
               propertyGettor = descriptors[i].getReadMethod();
               gettorMap.put(source.getClass(), propertyGettor);
               break;
            }
         }
      }
      return propertyGettor;
   }

}

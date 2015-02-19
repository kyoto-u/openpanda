/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-10.4/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/impl/ReferenceHolderPropertyAccess.java $
* $Id: ReferenceHolderPropertyAccess.java 105080 2012-02-24 23:10:31Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.warehouse.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.ReferenceHolder;
import org.sakaiproject.warehouse.service.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 5:48:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceHolderPropertyAccess implements PropertyAccess {

   private Map gettorMap = new Hashtable();
   private String propertyName;

   public Object getPropertyValue(Object source) throws Exception {
      Method objectMethodGetProperty = getPropertyGettor(source);
      if(objectMethodGetProperty == null)
         throw new NullPointerException(source.getClass().getName() +
               " has no get for property \"" + propertyName + "\"");
      Object value = objectMethodGetProperty.invoke(source, new Object[]{});

      ReferenceHolder refHolder = null;

      try {
         refHolder = (ReferenceHolder)value;
      } catch(ClassCastException e) {
         throw new Exception("The source could not be cast into an ReferenceHolder for property \"" + propertyName +  "\"", e);
      }

      return refHolder.getBase().getId();
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

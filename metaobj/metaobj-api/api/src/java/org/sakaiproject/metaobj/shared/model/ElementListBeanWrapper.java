/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/ElementListBeanWrapper.java $
 * $Id: ElementListBeanWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 * **********************************************************************************
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
 ******************************************************************************/

package org.sakaiproject.metaobj.shared.model;

import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jan 15, 2007
 * Time: 10:46:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElementListBeanWrapper extends HashMap implements TypedMap {

   private ElementListBean list;
   private FieldValueWrapper wrapper;
   private List wrapperList;
   private HashMap<Object, PropertyDescriptor> descriptors =
      new HashMap<Object, PropertyDescriptor>();

   public ElementListBeanWrapper(ElementListBean list, FieldValueWrapper wrapper) {
      this.list = list;
      this.wrapper = wrapper;
      this.wrapperList = new ArrayList();
   }

   public Object get(Object key) {
      return super.get(key);
   }

   public Object put(Object key, Object newValue) {
      List values = new ArrayList();

      if (newValue instanceof String[]) {
         values = Arrays.asList((String[])newValue);
      }
      else if (newValue != null) {
         values = new ArrayList();
         values.add(newValue);
      }

      processValues(key, values);

      return super.put(key, newValue);
   }

   protected void processValues(Object key, List values) {
      boolean createWrappers = false;
      if (wrapperList.size() != values.size()) {
         wrapperList.clear();
         createWrappers = true;
      }

      for (int i=0;i<values.size();i++) {
         BeanWrapper wrapper;
         if (createWrappers) {
            wrapper = new BeanWrapperImpl(getWrapper().clone());
            wrapperList.add(wrapper);
         }
         else {
            wrapper = (BeanWrapper) wrapperList.get(i);
         }

         setValue(wrapper, key, values.get(i));
      }

   }

   protected void setValue(BeanWrapper wrapper, Object key, Object value) {
      wrapper.setPropertyValue((String) key, value);
   }

   public void validate(List errors) {
      getList().clear();
      for (Iterator<BeanWrapper> i=wrapperList.iterator();i.hasNext();) {
         FieldValueWrapper fieldWrapper = (FieldValueWrapper) i.next().getWrappedInstance();

         if (fieldWrapper.getValue() != null) {
            ElementBean bean = getList().createBlank();
            fieldWrapper.validate(bean.getCurrentSchema().getName(), errors, bean.getCurrentSchema().getLabel());
            bean.getBaseElement().addContent(
               bean.getCurrentSchema().getSchemaNormalizedValue(fieldWrapper.getValue()));
            getList().add(bean);
         }
      }
   }

   protected PropertyDescriptor getPropertyDescriptor(Object key) {
      PropertyDescriptor descriptor = descriptors.get(key);
      if (descriptor == null) {
         try {
            descriptor = new PropertyDescriptor((String)key, getWrapper().getClass());
            descriptors.put(key, descriptor);
         } catch (IntrospectionException e) {
            throw new RuntimeException(e);
         }
      }
      return descriptor;
   }

   public ElementListBean getList() {
      return list;
   }

   public void setList(ElementListBean list) {
      this.list = list;
   }

   public FieldValueWrapper getWrapper() {
      return wrapper;
   }

   public void setWrapper(FieldValueWrapper wrapper) {
      this.wrapper = wrapper;
   }

   public Class getType(String key) {
      return getPropertyDescriptor(key).getPropertyType();
   }

}

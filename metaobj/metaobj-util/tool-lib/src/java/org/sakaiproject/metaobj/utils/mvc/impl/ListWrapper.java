package org.sakaiproject.metaobj.utils.mvc.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.BeanWrapperImpl;
import org.sakaiproject.metaobj.shared.model.ElementListBean;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Sep 17, 2007
 * Time: 8:49:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListWrapper extends BeanWrapperBase {
   public ListWrapper(List object, String nestedPath, Object rootObject) {
      super(object, nestedPath, rootObject);
   }

   protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty) {
      return new MapWrapper((Map)propertyValue, nestedProperty, getWrappedInstance());
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      return super.createNestedWrapper(parentPath, nestedProperty);    //To change body of overridden methods use File | Settings | File Templates.
   }


   public Object getPropertyValue(String propertyName) throws BeansException {
      if (!(getWrappedInstance() instanceof ElementListBean)) {
         throw new FatalBeanException(getWrappedInstance().getClass() +
               ": bean is not a List, BeanWrapperImpl might be a better choice");
      }

      if (isNestedProperty(propertyName)) {
         return super.getPropertyValue(propertyName);
      }
      else {
         int index = Integer.valueOf(propertyName);
         Object wrapped = getWrappedInstance();
         
         ElementListBean wrappedList = (ElementListBean) wrapped;
         if (wrappedList.size() > index) {
            return ((List)wrapped).get(index);
         }
         else {
            Map bean = wrappedList.createBlank();
            wrappedList.add(bean);
            return bean;
         }
      }
   }

}

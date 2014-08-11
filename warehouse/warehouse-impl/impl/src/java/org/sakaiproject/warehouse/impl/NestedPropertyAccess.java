package org.sakaiproject.warehouse.impl;

import org.sakaiproject.warehouse.service.PropertyAccess;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Aug 6, 2007
 * Time: 5:39:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class NestedPropertyAccess implements PropertyAccess {

   private String propertyPath;

   public Object getPropertyValue(Object source) throws Exception {
      BeanWrapper wrapper = new BeanWrapperImpl(source);
      return wrapper.getPropertyValue(getPropertyPath());
   }

   public String getPropertyPath() {
      return propertyPath;
   }

   public void setPropertyPath(String propertyPath) {
      this.propertyPath = propertyPath;
   }
}

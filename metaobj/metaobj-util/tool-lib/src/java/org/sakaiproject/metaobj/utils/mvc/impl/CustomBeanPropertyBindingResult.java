package org.sakaiproject.metaobj.utils.mvc.impl;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.beans.BeanWrapper;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jun 10, 2007
 * Time: 6:40:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomBeanPropertyBindingResult extends BeanPropertyBindingResult {

   public CustomBeanPropertyBindingResult(Object target, String objectName) {
      super(target, objectName);
   }

   /**
    * Create a new {@link org.springframework.beans.BeanWrapper} for the underlying target object.
    *
    * @see #getTarget()
    */
   protected BeanWrapper createBeanWrapper() {
      if (getTarget() instanceof Map) {
         return new MapWrapper(getTarget());
      }
      else {
         return new MixedBeanWrapper(getTarget());
      }
   }

}

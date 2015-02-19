/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/servlet/ServletRequestBeanDataBinder.java $
 * $Id: ServletRequestBeanDataBinder.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.mvc.impl.servlet;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.sakaiproject.metaobj.utils.mvc.impl.CustomBeanPropertyBindingResult;

public class ServletRequestBeanDataBinder extends ServletRequestDataBinder {

   private AbstractPropertyBindingResult bindingResult;

   public ServletRequestBeanDataBinder(Object o, String s) {
      super(o, s);
   }

   public void bind(ServletRequest request) {
      // bind normal HTTP parameters
      bind(new ServletRequestParameterPropertyValues(request));

      // bind multipart files
      if (request instanceof MultipartHttpServletRequest) {
         MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
         bind(new MutablePropertyValues(multipartRequest.getFileMap()));
      }
   }

   /**
   * Initialize standard JavaBean property access for this DataBinder.
   * <p>This is the default; an explicit call just leads to eager initialization.
   * @see #initDirectFieldAccess()
    * */
   public void initBeanPropertyAccess() {
      Assert.isNull(this.bindingResult,
         "DataBinder is already initialized - call initBeanPropertyAccess before any other configuration methods");
      this.bindingResult = new CustomBeanPropertyBindingResult(getTarget(), 
         getObjectName());
   }

   /**
    * Return the internal BindingResult held by this DataBinder,
    * as AbstractPropertyBindingResult.
    * */
   protected AbstractPropertyBindingResult getInternalBindingResult() {
      if (this.bindingResult == null) {
         initBeanPropertyAccess();
      }
      return this.bindingResult;
   }
   
}

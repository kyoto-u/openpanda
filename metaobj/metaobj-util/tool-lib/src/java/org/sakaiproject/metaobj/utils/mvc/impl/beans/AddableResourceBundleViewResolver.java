/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/beans/AddableResourceBundleViewResolver.java $
 * $Id: AddableResourceBundleViewResolver.java 125281 2013-05-31 03:42:46Z nbotimer@unicon.net $
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

package org.sakaiproject.metaobj.utils.mvc.impl.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

public class AddableResourceBundleViewResolver extends ResourceBundleViewResolver {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List baseNames;
   private Map cachedFactories = new HashMap();
   private String defaultParentView;

   public List getBaseNames() {
      return baseNames;
   }

   public void setBaseNames(List baseNames) {
      this.baseNames = baseNames;
   }

   /**
    * Set the default parent for views defined in the ResourceBundle.
    * This avoids repeated "yyy1.parent=xxx", "yyy2.parent=xxx" definitions
    * in the bundle, especially if all defined views share the same parent.
    * <p>The parent will typically define the view class and common attributes.
    * Concrete views might simply consist of an URL definition then:
    * a la "yyy1.url=/my.jsp", "yyy2.url=/your.jsp".
    * <p>View definitions that define their own parent or carry their own
    * class can still override this. Strictly speaking, the rule that a
    * default parent setting does not apply to a bean definition that
    * carries a class is there for backwards compatiblity reasons.
    * It still matches the typical use case.
    *
    * @param defaultParentView the default parent view
    */
   public synchronized void setDefaultParentView(String defaultParentView) {
      this.defaultParentView = defaultParentView;
   }

   /**
    * Initialize the BeanFactory from the ResourceBundle, for the given locale.
    * Synchronized because of access by parallel threads.
    */
   protected synchronized BeanFactory initFactory(Locale locale) throws MissingResourceException, BeansException {
      BeanFactory parsedBundle = isCache() ? (BeanFactory) this.cachedFactories.get(locale) : null;
      if (parsedBundle != null) {
         return parsedBundle;
      }

      DefaultListableBeanFactory factory = new DefaultListableBeanFactory(getApplicationContext());
      PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(factory);
      reader.setDefaultParentBean(this.defaultParentView);
      for (Iterator i = baseNames.iterator(); i.hasNext();) {
         ResourceBundle bundle = ResourceBundle.getBundle((String) i.next(), locale,
               Thread.currentThread().getContextClassLoader());
         reader.registerBeanDefinitions(bundle);
      }
      factory.registerCustomEditor(Resource.class, (new ResourceEditor(getApplicationContext())).getClass());

      if (isCache()) {
         factory.preInstantiateSingletons();
         this.cachedFactories.put(locale, factory);
      }
      return factory;
   }

   public void destroy() throws BeansException {
      for (Iterator it = this.cachedFactories.values().iterator(); it.hasNext();) {
         ConfigurableBeanFactory factory = (ConfigurableBeanFactory) it.next();
         factory.destroySingletons();
      }
      this.cachedFactories.clear();
   }


}

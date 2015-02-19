/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/beans/AddableSessionFactoryBean.java $
 * $Id: AddableSessionFactoryBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

public class AddableSessionFactoryBean extends LocalSessionFactoryBean implements ApplicationContextAware {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ApplicationContext applicationContext;

   /**
    * To be implemented by subclasses that want to to perform custom
    * post-processing of the Configuration object after this FactoryBean
    * performed its default initialization.
    *
    * @param config the current Configuration object
    * @throws org.hibernate.HibernateException
    *          in case of Hibernate initialization errors
    */
   protected void postProcessConfiguration(Configuration config) throws HibernateException {
      super.postProcessConfiguration(config);

      Map beanMap = applicationContext.getBeansOfType(AdditionalHibernateMappings.class, true, true);

      if (beanMap == null) {
         return;
      }

      Collection beans = beanMap.values();

      try {
         for (Iterator i = beans.iterator(); i.hasNext();) {
            AdditionalHibernateMappings mappings = (AdditionalHibernateMappings) i.next();
            mappings.processConfig(config);
         }
      }
      catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   /**
    * Set the ApplicationContext that this object runs in.
    * Normally this call will be used to initialize the object.
    * <p>Invoked after population of normal bean properties but before an init
    * callback like InitializingBean's afterPropertiesSet or a custom init-method.
    * Invoked after ResourceLoaderAware's setResourceLoader.
    *
    * @param applicationContext ApplicationContext object to be used by this object
    * @throws org.springframework.context.ApplicationContextException
    *          in case of applicationContext initialization errors
    * @throws org.springframework.beans.BeansException
    *          if thrown by application applicationContext methods
    * @see org.springframework.beans.factory.BeanInitializationException
    */
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}

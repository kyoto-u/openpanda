/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/SpringBaseTest.java $
 * $Id: SpringBaseTest.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.ioc.ApplicationContextFactory;
import org.springframework.beans.factory.BeanFactory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 6, 2004
 * Time: 12:46:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SpringBaseTest extends TestCase {
   protected final Log logger = LogFactory.getLog(getClass());

   private BeanFactory beanFactory;

   protected SpringBaseTest(String s) {
      super(s);
   }

   protected SpringBaseTest() {
   }

   public BeanFactory getBeanFactory() {
      if (beanFactory == null) {
         Properties props = new Properties();
         InputStream in = this.getClass().getResourceAsStream("/test-context.properties");
         if (in != null) {
            try {
               props.load(in);
               beanFactory = ApplicationContextFactory.getInstance().createContext(props);
            }
            catch (IOException e) {
               logger.warn("Error loading /text-context.properties", e);
            }
            finally {
               try {
                  in.close();
               }
               catch (IOException e2) {
                  logger.warn("Could not close stream for /text-context.properties");
               }
            }
         }
      }
      if (beanFactory == null)
         throw new RuntimeException("problem loading /test-context.properties from classpath");
      return beanFactory;
   }

}

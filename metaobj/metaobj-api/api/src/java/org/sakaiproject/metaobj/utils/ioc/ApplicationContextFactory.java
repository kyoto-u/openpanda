/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/ioc/ApplicationContextFactory.java $
 * $Id: ApplicationContextFactory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.ioc;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>Loads the ApplicationContext from xml files listed in a property file.
 * The xml files referenced in the properties should be in the classpath.
 * The format of the property file should be as follows:</p>
 * <p/>
 * <br>1=help-config.xml
 * <br>2=help-web-config.xml
 * <br>3=home-config.xml
 * <br>...
 * <p/>
 * <p>The numbering controls the loading order of the files.  This
 * allows for overriding of beans configured in earlier xml definition files.
 * Without the numbering we can not gaurantee what order the files will load. </p>
 */
public class ApplicationContextFactory {
   private Log logger = LogFactory.getLog(this.getClass());
   private static ApplicationContextFactory factory = new ApplicationContextFactory();

   private ApplicationContextFactory() {
   }

   public static ApplicationContextFactory getInstance() {
      return factory;
   }

   /**
    * @param properties
    * @return
    */
   public ApplicationContext createContext(Properties properties) {
      try {
         return new ClassPathXmlApplicationContext(getConfigLocations(properties));
      }
      catch (Exception e) {
         throw new RuntimeException("problem loading ApplicationContext: " + e.getMessage(), e);
      }
   }

   public String[] getConfigLocations(Properties props) throws IOException {
      SortedMap configFiles = new TreeMap();
      for (Enumeration e = props.keys(); e.hasMoreElements();) {
         Integer key = new Integer((String) e.nextElement());
         String curFile = props.getProperty(key.toString());
         curFile = (!curFile.startsWith("/")) ? "/" + curFile : curFile;
         configFiles.put(key, curFile);
         logger.info("registering '" + curFile + "' in position " + key + " as spring bean definition file");
      }
      return convertToArray(configFiles.values());
   }

   protected String[] convertToArray(Collection collection) {
      return (String[]) collection.toArray(new String[collection.size()]);
   }
}

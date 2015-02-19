/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/factories/FieldValueWrapperFactoryImpl.java $
 * $Id: FieldValueWrapperFactoryImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt.factories;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.FieldValueWrapperFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 25, 2004
 * Time: 6:48:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldValueWrapperFactoryImpl implements FieldValueWrapperFactory {

   private Map wrappedClassMap = null;
   protected final Log logger = LogFactory.getLog(getClass());


   public FieldValueWrapperFactoryImpl() {
   }

   public boolean checkWrapper(Class clazz) {
      return (wrappedClassMap.get(clazz) != null);
   }

   public FieldValueWrapper wrapInstance(Class clazz) {
      Class wrapperClass = (Class) wrappedClassMap.get(clazz);

      FieldValueWrapper returnedWrapper = null;

      try {
         returnedWrapper = (FieldValueWrapper) wrapperClass.newInstance();
      }
      catch (InstantiationException e) {
         RuntimeException exp = new IllegalArgumentException("Invalid wrapper class");
         exp.initCause(e);
         throw exp;
      }
      catch (IllegalAccessException e) {
         RuntimeException exp = new IllegalArgumentException("Invalid wrapper class");
         exp.initCause(e);
         throw exp;
      }

      return returnedWrapper;
   }

   public FieldValueWrapper wrapInstance(Object obj) {

      FieldValueWrapper returnedWrapper = wrapInstance(obj.getClass());

      returnedWrapper.setValue(obj);

      return returnedWrapper;
   }


   public void setWrappedClassMap(Map wrappedClassMap) {
      this.wrappedClassMap = wrappedClassMap;
   }

   public void setWrappedClassNamesMap(Properties wrappedClassNamesMap) throws ClassNotFoundException {
      wrappedClassMap = new Hashtable();

      for (Iterator i = wrappedClassNamesMap.keySet().iterator(); i.hasNext();) {
         String key = (String) i.next();
         String value = wrappedClassNamesMap.getProperty(key);

         wrappedClassMap.put(Class.forName(key), Class.forName(value));
      }

      setWrappedClassMap(wrappedClassMap);
   }
}















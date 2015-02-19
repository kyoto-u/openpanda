/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/factories/HomeFactoryBase.java $
 * $Id: HomeFactoryBase.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt.factories;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 14, 2004
 * Time: 4:22:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HomeFactoryBase implements HomeFactory {

   private Map homesByExternalId = null;
   private Object homesByExternalIdLock = new Object();

   public void reload() {
      if (homesByExternalId == null) {
         homesByExternalId = new Hashtable();
      }
      homesByExternalId.clear();
      for (Iterator j = getHomes().entrySet().iterator(); j.hasNext();) {
         ReadableObjectHome home = (ReadableObjectHome) j.next();
         home.refresh();
         homesByExternalId.put(home.getExternalType(), home);
      }
   }

   public synchronized ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {
      if (homesByExternalId == null) {
         homesByExternalId = new Hashtable();
         for (Iterator j = getHomes().entrySet().iterator(); j.hasNext();) {
            Map.Entry entry = (Map.Entry) j.next();
            ReadableObjectHome home = (ReadableObjectHome) entry.getValue();
            homesByExternalId.put(home.getExternalType(), home);
         }
      }
      return (ReadableObjectHome) homesByExternalId.get(externalId);
   }

   protected void addHome(ReadableObjectHome newHome) {
      homesByExternalId.put(newHome.getExternalType(), newHome);
   }

   public Map getHomes(Class requiredHomeType) {
      Map newMap = new Hashtable();
      Map homes = getHomes();

      for (Iterator i = homes.entrySet().iterator(); i.hasNext();) {
          Entry entry = (Entry)i.next();

          if (requiredHomeType.isInstance(entry.getValue())) {
             newMap.put(entry.getKey(), entry.getValue());
          }
       }
      

      return newMap;
   }

   public Map getWorksiteHomes(Id worksiteId) {
      return getHomes();
   }

   public Map getWorksiteHomes(Id worksiteId, boolean includeHidden) {
      return getHomes();
   }

   public Map getWorksiteHomes(Id worksiteId, String currentUserId, boolean includeHidden) {
      return getHomes();
   }

}

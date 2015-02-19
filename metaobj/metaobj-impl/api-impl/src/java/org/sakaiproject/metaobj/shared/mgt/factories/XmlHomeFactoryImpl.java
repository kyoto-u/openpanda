/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/factories/XmlHomeFactoryImpl.java $
 * $Id: XmlHomeFactoryImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:51:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlHomeFactoryImpl extends HomeFactoryBase implements HomeFactory {

   private Map homes = null;

   public boolean handlesType(String objectType) {
      return (getHomes().keySet().contains(objectType));
   }

   public ReadableObjectHome getHome(String objectType) {
      return (ReadableObjectHome) getHomes().get(objectType);
   }

   /**
    * let injection set this for now...
    * will eventually load from the db
    */
   public Map getHomes() {
      return homes;
   }

   public void setHomes(Map homes) {
      this.homes = homes;
   }

}

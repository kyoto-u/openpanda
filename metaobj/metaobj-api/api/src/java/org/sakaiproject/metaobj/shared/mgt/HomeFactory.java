/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/HomeFactory.java $
 * $Id: HomeFactory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt;

import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:44:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HomeFactory {

   /**
    * Check to see if this home factory is responsible for the passed in object type
    *
    * @param objectType
    * @return true if this home factory handles the specified object type
    */
   public boolean handlesType(String objectType);

   /**
    * Get a home for the given object type.  The returned home may support a number of interfaces
    * depending on the features of this home.  At a minimum, the home must support
    * ReadableObjectHome interface.
    *
    * @param objectType
    * @return a home suitable for reading the object, but it may support other home interfaces
    */
   public ReadableObjectHome getHome(String objectType);

   /**
    * Find a home by an external id.  This id should be unique and naturally occuring.
    * This is used for matching up a home imported from another worksite or system when importing
    * things that use Homes (ie. presentation templates, matrices, etc)
    *
    * @param externalId naturally occuring id (like the document root and system id of an xml document)
    * @param worksiteId The worksite to import it into or null for global import
    * @return the home if found or null.
    */
   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId);

   /**
    * @param worksiteId
    * @return a map with all worksite and global homes
    */
   public Map getWorksiteHomes(Id worksiteId);

   public Map getWorksiteHomes(Id worksiteId, boolean includeHidden);

   public Map getWorksiteHomes(Id worksiteId, String currentUserId, boolean includeHidden);

   /**
    * Map of all homes.  This map will map the object type as a String to the home as a ReadableObjectHome.
    * The home may support more features.  This can be determined by checking instanceof on other home interfaces
    *
    * @return map of object type to home
    */
   public Map getHomes();

   /**
    * Map of certain homes.  This map will map the object type as a String to the home as a ReadableObjectHome.
    * The home may support more features.  This can be determined by checking instanceof on other home interfaces.
    * <p/>
    * All the homes returned will implement the requiredHomeType interface.  This method can be used to get
    * homes that support certain features.
    *
    * @param requiredHomeType interface that all returned homes will be an implementation of
    * @return map of object type to home
    */
   public Map getHomes(Class requiredHomeType);

   /**
    * forces reloading of any cached homes.  Allows configuration
    * changes to occur at runtime.
    */
   public void reload();

}

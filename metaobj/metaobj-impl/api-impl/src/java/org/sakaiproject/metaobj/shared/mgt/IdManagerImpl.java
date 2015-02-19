/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/IdManagerImpl.java $
 * $Id: IdManagerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdImpl;
import org.sakaiproject.metaobj.utils.id.guid.Guid;

/**
 * @author rpembry
 */
public class IdManagerImpl implements IdManager {
   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#getInstance()
    */
   public IdManager getInstance() {
//TODO: retrieve Singleton from Spring?
      return new IdManagerImpl();
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#getId(java.lang.String)
    */
   public Id getId(String id) {
      //return new org.sakaiproject.metaobj.shared.model.IdImpl(id);
      return new IdImpl(id, null);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#createId()
    */
   public Id createId() {
//TODO: delegate to OKI impl here
      return getId(new Guid().getString());
   }


}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/IdGenerator.java $
 * $Id: IdGenerator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

/**
 * creates unique identifiers for hibernate
 */
public class IdGenerator implements IdentifierGenerator {

   private static IdManager idManager;

   public IdGenerator() {
   }

   public Serializable generate(SessionImplementor arg0, Object arg1)
         throws HibernateException {
      return getIdManager().createId();
   }

   protected IdManager getIdManager() {
      if (idManager == null) {
         idManager = (IdManager) ComponentManager.getInstance().get("idManager");
      }
      return idManager;
   }

}

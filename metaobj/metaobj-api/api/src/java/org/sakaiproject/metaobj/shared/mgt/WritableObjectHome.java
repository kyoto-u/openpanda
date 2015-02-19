/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/WritableObjectHome.java $
 * $Id: WritableObjectHome.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.io.InputStream;

import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Type;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 8, 2004
 * Time: 5:16:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WritableObjectHome extends ReadableObjectHome {

   /**
    * Update or add the supplied object to the store.
    *
    * @param object The object to be updated or added
    * @return The object with info such as Id in the
    *         case of add.  In the case of update, the object
    *         will be returned as supplied.
    */
   public Artifact store(Artifact object) throws PersistenceException;

   public void remove(Artifact object) throws PersistenceException;

   public Artifact store(String displayName, String contentType, Type type,
                         InputStream in) throws PersistenceException;

   public Artifact update(Artifact object, InputStream in) throws PersistenceException;
}

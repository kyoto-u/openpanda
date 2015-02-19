/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/ReadableObjectHome.java $
 * $Id: ReadableObjectHome.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import java.util.Collection;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Type;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 8, 2004
 * Time: 5:16:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReadableObjectHome {

   public Type getType();


   /**
    * Used to get an externally unique type to identify this home
    * across running osp instances
    *
    * @return an externally unique type suitable for storage and later import
    */
   public String getExternalType();

   /**
    * Load the object from the
    * backing store.
    *
    * @param id Uniquely identifies the object.
    * @return The loaded object
    */
   public Artifact load(Id id) throws PersistenceException;

   /**
    * Creates an empty instance of this home's object
    *
    * @return An empty object instance
    */
   public Artifact createInstance();

   public void prepareInstance(Artifact object);

   /**
    * Creates a sample instance of the
    * object with each field filled in with some
    * representative data.
    *
    * @return An object instance with sample data filled in.
    */
   public Artifact createSample();

   /**
    * Find all the instances of this home's
    * objects that are owned by the supplied owner.
    * How do we handle permissions here?
    *
    * @param owner The owner in question.
    * @return A list of objects.
    */
   public Collection findByOwner(Agent owner) throws FinderException;

   /**
    * Determines if the supplied object is handled by this home.
    *
    * @param testObject the object to be tested.
    * @return true if the supplied object is handled by this home
    */
   public boolean isInstance(Artifact testObject);

   /**
    * re-initialize any configuration
    */
   public void refresh();

   public String getExternalUri(Id artifactId, String name);

   public InputStream getStream(Id artifactId);

   public boolean isSystemOnly();

   public Class getInterface();
}

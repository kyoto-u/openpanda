/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.2/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/ArtifactFinder.java $
 * $Id: ArtifactFinder.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.Collection;

import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;

/*
 * Common search
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/ArtifactFinder.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 105079 $
 * $Date: 2012-02-25 08:08:11 +0900 (Sat, 25 Feb 2012) $
 */

public interface ArtifactFinder {

   /**
    * search for a list of artifacts in the system owner by owner and matching the given type
    *
    * @param owner
    * @param type
    * @return
    */
   public Collection findByOwnerAndType(Id owner, String type);

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType);

   public Collection findByOwner(Id owner);

   public Collection findByWorksiteAndType(Id worksiteId, String type);

   public Collection findByWorksite(Id worksiteId);

   public Artifact load(Id artifactId);

   public Collection findByType(String type);

   /**
    * @return true if calls to find should actually load the artifacts
    */
   public boolean getLoadArtifacts();

   public void setLoadArtifacts(boolean loadArtifacts);

}

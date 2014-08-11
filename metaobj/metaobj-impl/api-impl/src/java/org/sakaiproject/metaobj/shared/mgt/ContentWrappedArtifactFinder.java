/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/shared/mgt/ContentWrappedArtifactFinder.java $
* $Id:ContentWrappedArtifactFinder.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.mgt.impl.FileArtifactFinder;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.EntityContextFinder;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 4:49:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentWrappedArtifactFinder extends FileArtifactFinder implements EntityContextFinder {

   public Artifact load(Id artifactId) {
      return super.load(artifactId);
   }

   public Artifact loadInContext(Id artifactId, String context, String siteId, String contextId) {
      Artifact art = super.load(artifactId);

      if (art instanceof ContentResourceArtifact) {
         return wrap((ContentResourceArtifact)art, context, siteId, contextId);
      }

      return art;
   }

   protected Artifact wrap(ContentResourceArtifact contentResourceArtifact,
                           String context, String siteId, String contextId) {
      ContentResource resource = contentResourceArtifact.getBase();

      ContentResource wrapped = new ContentEntityWrapper(resource,
            buildRef(context, siteId, contextId, resource));

      contentResourceArtifact.setBase(wrapped);

      return contentResourceArtifact;
   }

   protected String buildRef(String context, String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(context,  siteId, contextId, resource.getReference());
   }

}

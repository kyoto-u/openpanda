/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/PresentableObjectHome.java $
 * $Id: PresentableObjectHome.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.jdom.Element;
import org.sakaiproject.metaobj.shared.model.Artifact;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 25, 2004
 * Time: 4:16:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PresentableObjectHome {

   public Element getArtifactAsXml(Artifact art);
   
   /**
    * Add some additional information to the artifact to construct a reference like so:
    * /<container>/<site>/<context>/...
    * @param art
    * @param site
    * @param context
    * @param container
    * @return
    */
   public Element getArtifactAsXml(Artifact art, String container, String site, String context);

}

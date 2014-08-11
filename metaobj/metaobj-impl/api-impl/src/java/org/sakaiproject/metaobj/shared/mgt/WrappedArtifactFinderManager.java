/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/shared/mgt/WrappedArtifactFinderManager.java $
* $Id:WrappedArtifactFinderManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Map;

import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 6:36:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class WrappedArtifactFinderManager implements ArtifactFinderManager {

   private ArtifactFinderManager base;
   private Map substitutions;

   public ArtifactFinder getArtifactFinderByType(String key) {
      ArtifactFinder finder = (ArtifactFinder) substitutions.get(key);

      if (finder != null) {
         return finder;
      }
      return base.getArtifactFinderByType(key);
   }

   public Map getFinders() {
      return base.getFinders();
   }

   public Map getSubstitutions() {
      return substitutions;
   }

   public void setSubstitutions(Map substitutions) {
      this.substitutions = substitutions;
   }

   public ArtifactFinderManager getBase() {
      return base;
   }

   public void setBase(ArtifactFinderManager base) {
      this.base = base;
   }
}

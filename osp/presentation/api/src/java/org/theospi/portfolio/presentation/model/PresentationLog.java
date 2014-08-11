/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationLog.java $
* $Id:PresentationLog.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * what are the things we might need for logging
 * <p/>
 * userid
 * viewDate -- just the date not time
 * starttime
 * stoptime
 * userid
 */
public class PresentationLog extends IdentifiableObject {
   private Presentation presentation;
   private Date viewDate;
   private Agent viewer;

   public PresentationLog() {
   }

   public Date getViewDate() {
      return viewDate;
   }

   public void setViewDate(Date Created) {
      this.viewDate = Created;
   }

   public Presentation getPresentation() {
      return presentation;
   }

   public void setPresentation(Presentation presentation) {
      this.presentation = presentation;
   }

   public Agent getViewer() {
      return viewer;
   }

   public void setViewer(Agent viewer) {
      this.viewer = viewer;
   }
}

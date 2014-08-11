/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.control;

import java.util.Comparator;
import org.sakaiproject.metaobj.shared.model.Agent;

/**
 ** Comparator for sorting user-based Agent objects
 ** (tbd: localize sorting of names)
 **/
public class UserAgentComparator implements Comparator<Agent> {
   
   public int compare(Agent o1, Agent o2) {
      String n1 = o1.getDisplayName();
      String n2 = o2.getDisplayName();
      int i1 = n1.lastIndexOf(" ");
         int i2 = n2.lastIndexOf(" ");
         if (i1 > 0)
            n1 = n1.substring(i1 + 1) + " " + n1.substring(0, i1);
         if (i2 > 0)
            n2 = n2.substring(i2 + 1) + " " + n2.substring(0, i2);
         
         return n1.compareToIgnoreCase(n2);
   }
}
    
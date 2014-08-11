/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationAuthzMap.java $
* $Id:PresentationAuthzMap.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model.impl;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.model.Presentation;
import org.sakaiproject.metaobj.shared.model.Id;

public class PresentationAuthzMap extends HashMap {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Agent currentAgent;
   private Presentation presentation;
   private boolean owner = false;

   /**
    * Constructor for specified user and presentation
    */
   public PresentationAuthzMap(Agent currentAgent, Presentation presentation) {
      this.currentAgent = currentAgent;
      this.presentation = presentation;
      Id presOwner = presentation.getOwner().getId();
      if ( presOwner != null )
         this.owner = presOwner.equals(currentAgent.getId());
   }

   /**
    * Returns the value to which the specified key is mapped in this hashtable.
    *
    * @param key a key in the hashtable.
    * @return the value to which the key is mapped in this hashtable;
    *         <code>null</code> if the key is not mapped to any value in
    *         this hashtable.
    * @throws NullPointerException if the key is <code>null</code>.
    * @see #put(Object, Object)
    */
   public Object get(Object key) {
      if (owner) {
         return Boolean.valueOf(true); // owner can do anything
      }

      String func = PresentationFunctionConstants.PRESENTATION_PREFIX + key.toString();

      if (func.equals(PresentationFunctionConstants.VIEW_PRESENTATION)) {
         return Boolean.valueOf(true);
      }
      else {
         return Boolean.valueOf(false);
      }
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/model/AuthZMap.java $
* $Id:AuthZMap.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.model;

import java.util.HashMap;

import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;

public class AuthZMap extends HashMap {
   private AuthorizationFacade authzFacade;
   private String prefix;
   private Id qualifier;

   public AuthZMap(AuthorizationFacade authzFacade, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = "";
      this.qualifier = qualifier;
   }

   public AuthZMap(AuthorizationFacade authzFacade, String prefix, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = prefix;
      this.qualifier = qualifier;
   }

   public Object get(Object key){
      if (super.get(key) == null) {
         super.put(key, Boolean.valueOf(authzFacade.isAuthorized(prefix + key.toString(), qualifier)));
      }
      return super.get(key);      
   }
}

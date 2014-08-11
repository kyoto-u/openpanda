/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/AllowMapSecurityAdvisor.java $
* $Id:AllowMapSecurityAdvisor.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.sakaiproject.metaobj.security;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.sakaiproject.authz.api.SecurityAdvisor;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 4, 2005
 * Time: 5:02:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllowMapSecurityAdvisor implements SecurityAdvisor {

   private Map allowedReferences;

   public AllowMapSecurityAdvisor(Map allowedReferences) {
      this.allowedReferences = allowedReferences;
   }

   public AllowMapSecurityAdvisor(String function, List references) {
      this.allowedReferences = new Hashtable();
      allowedReferences.put(function, references);
   }

   public AllowMapSecurityAdvisor(String function, String reference) {
      this.allowedReferences = new Hashtable();
      List references = new ArrayList();
      references.add(reference);
      allowedReferences.put(function, references);
   }

   public SecurityAdvice isAllowed(String userId, String function, String reference) {
      List refs = (List)allowedReferences.get(function);
      if (refs != null) {
         if (refs.contains(reference)) {
            return SecurityAdvice.ALLOWED;
         }
      }

      return SecurityAdvice.PASS;
   }

   public Map getAllowedReferences() {
      return allowedReferences;
   }
}

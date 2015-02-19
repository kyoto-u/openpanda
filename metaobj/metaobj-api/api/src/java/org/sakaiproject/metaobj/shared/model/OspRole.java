/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/OspRole.java $
 * $Id: OspRole.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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


package org.sakaiproject.metaobj.shared.model;

import org.sakaiproject.authz.api.AuthzGroup;


public interface OspRole extends Agent {

   /**
    * gets the name of the role idependant of the site it belongs to
    *
    * @return
    */
   public String getRoleName();
   
   /**
    * Get the realm for this role
    *  
    * @return The Sakai AuthzGroup for the site where this role exists 
    */
   public AuthzGroup getSakaiRealm();
}

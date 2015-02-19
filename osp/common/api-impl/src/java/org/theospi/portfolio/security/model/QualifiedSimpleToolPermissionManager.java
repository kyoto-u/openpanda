/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/common/api-impl/src/java/org/theospi/portfolio/security/model/QualifiedSimpleToolPermissionManager.java $
* $Id: QualifiedSimpleToolPermissionManager.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.security.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

public class QualifiedSimpleToolPermissionManager extends SimpleToolPermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id qualifier;

   protected PermissionsEdit setupPermissions(String worksiteId, Id qualifier, String siteType) {
      return super.setupPermissions(worksiteId, this.qualifier, siteType);
   }

   public String getQualifier() {
      return qualifier.getValue();
   }

   public void setQualifier(String qualifier) {
      this.qualifier = getIdManager().getId(qualifier);
   }

}

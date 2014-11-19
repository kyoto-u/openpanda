/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/api-impl/src/java/org/theospi/portfolio/security/app/AdditionalAppAuthorizers.java $
* $Id: AdditionalAppAuthorizers.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.security.app;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 21, 2005
 * Time: 10:41:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalAppAuthorizers {

   private List additionalAppAuthorizers;
   private AppAuthFacade authzManager;

   public List getAdditionalAppAuthorizers() {
      return additionalAppAuthorizers;
   }

   public void setAdditionalAppAuthorizers(List additionalAppAuthorizers) {
      this.additionalAppAuthorizers = additionalAppAuthorizers;
   }

   public AppAuthFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AppAuthFacade authzManager) {
      this.authzManager = authzManager;
   }

   public void init() {
      getAuthzManager().addAppAuthorizers(getAdditionalAppAuthorizers());
   }

}

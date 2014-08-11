/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/integration/api-impl/src/java/org/theospi/portfolio/admin/service/ExistingWorksitePageOption.java $
* $Id: ExistingWorksitePageOption.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExistingWorksitePageOption extends PageOption {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String worksiteType = null;

   public String getWorksiteType() {
      return worksiteType;
   }

   public void setWorksiteType(String worksiteType) {
      this.worksiteType = worksiteType;
   }
   
}

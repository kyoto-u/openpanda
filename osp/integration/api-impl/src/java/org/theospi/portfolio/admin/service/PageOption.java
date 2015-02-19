/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/integration/api-impl/src/java/org/theospi/portfolio/admin/service/PageOption.java $
* $Id: PageOption.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.admin.model.IntegrationOption;

import java.util.List;

public class PageOption extends IntegrationOption {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String worksiteId;
   private String pageName;
   private List tools;
   private int layout = 1;
   private int positionFromEnd = 0;

   public String getPageName() {
      return pageName;
   }

   public void setPageName(String pageName) {
      this.pageName = pageName;
   }

   public List getTools() {
      return tools;
   }

   public void setTools(List tools) {
      this.tools = tools;
   }

   public String getWorksiteId() {
      return worksiteId;
   }

   public void setWorksiteId(String worksiteId) {
      this.worksiteId = worksiteId;
   }

   public int getLayout() {
      return layout;
   }

   public void setLayout(int layout) {
      this.layout = layout;
   }

   public int getPositionFromEnd() {
      return positionFromEnd;
   }

   public void setPositionFromEnd(int positionFromEnd) {
      this.positionFromEnd = positionFromEnd;
   }
}

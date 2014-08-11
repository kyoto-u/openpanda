/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/common/api/src/java/org/theospi/portfolio/style/mgt/AdditionalStyleConsumer.java $
* $Id: AdditionalStyleConsumer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.style.mgt;

import java.util.List;

public class AdditionalStyleConsumer {

   private List additionalConsumers;
   private StyleManager styleManager;

   public List getAdditionalConsumers() {
      return additionalConsumers;
   }

   public void setAdditionalConsumers(List additionalConsumers) {
      this.additionalConsumers = additionalConsumers;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }

   public void init() {
      getStyleManager().getConsumers().addAll(getAdditionalConsumers());
   }
}

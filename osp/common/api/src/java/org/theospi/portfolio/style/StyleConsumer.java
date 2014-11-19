/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/common/api/src/java/org/theospi/portfolio/style/StyleConsumer.java $
* $Id: StyleConsumer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.style;

import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

public interface StyleConsumer {

   public boolean checkStyleConsumption(Id styleId);

   /**
    * gets a list of styles (in order of default first) from
    * the style consumer if the object presented is owned by the
    * style consumer
    * @param objectId
    * @return a list of Style objects or null if this consumer doesn't own the id
    */
   public List getStyles(Id objectId);

}

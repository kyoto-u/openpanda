/**********************************************************************************
 *
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/intf/CustomLinkListGenerator.java,v 1.2 2005/08/30 21:27:09 jellis Exp $
 *
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
package org.theospi.portfolio.list.intf;

public interface CustomLinkListGenerator extends ListGenerator {

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry);

}

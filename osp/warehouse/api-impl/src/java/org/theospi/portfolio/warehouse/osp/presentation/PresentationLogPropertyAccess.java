/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/osp/presentation/PresentationLogPropertyAccess.java $
* $Id: PresentationLogPropertyAccess.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.warehouse.osp.presentation;

import org.theospi.portfolio.presentation.model.Presentation;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 20, 2005
 * Time: 3:29:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationLogPropertyAccess extends PresentationCommentsPropertyAccess {

   public Object getPropertyValue(Object source) throws Exception {
      Presentation pres = (Presentation) source;
      return getPresentationManager().findLogsByPresID(pres.getId());
   }

}


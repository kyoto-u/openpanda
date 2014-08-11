/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/tool/src/java/org/theospi/portfolio/portal/component/ToolComponent.java $
* $Id:ToolComponent.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.portal.component;

import javax.faces.component.UINamingContainer;

import org.sakaiproject.site.api.SitePage;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 17, 2006
 * Time: 9:47:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolComponent extends UINamingContainer {

   public static final String COMPONENT_TYPE = "org.theospi.portfolio.portal.component.ToolComponent";

   private SitePage page;

   public SitePage getPage() {
      return page;
   }

   public void setPage(SitePage page) {
      this.page = page;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/jsf/widgets/src/java/org/theospi/jsf/tag/TestComponentTag.java $
* $Id: TestComponentTag.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.jsf.tag;

import javax.faces.webapp.UIComponentTag;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 28, 2005
 * Time: 11:49:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestComponentTag extends UIComponentTag {

   public String getComponentType() {
      return "org.theospi.TestComponent";
   }

   public String getRendererType() {
      return "org.theospi.TestComponent";
   }
}

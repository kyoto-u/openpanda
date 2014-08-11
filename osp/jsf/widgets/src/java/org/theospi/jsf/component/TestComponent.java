/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/component/TestComponent.java $
* $Id:TestComponent.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.jsf.component;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 28, 2005
 * Time: 12:01:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestComponent extends UIOutput {

   private UIComponent layoutRoot = null;

   public TestComponent() {
      super();
      this.setRendererType("org.theospi.TestComponent");
   }

   public UIComponent getLayoutRoot() {
      return layoutRoot;
   }

   public void setLayoutRoot(UIComponent layoutRoot) {
      this.layoutRoot = layoutRoot;
   }

}

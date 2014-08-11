/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/component/SequenceComponentProxy.java $
* $Id:SequenceComponentProxy.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.component;

import java.io.IOException;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.theospi.jsf.util.TagUtil;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 2, 2006
 * Time: 1:23:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceComponentProxy extends UIOutput {

   public static final String COMPONENT_TYPE = "org.theospi.presentation.SequenceComponentProxy";
   private SequenceComponent base;

   public boolean getRendersChildren() {
      return true;
   }

   public void encodeChildren(FacesContext context) throws IOException {
      TagUtil.renderChild(context, getBase());
   }

   public SequenceComponent getBase() {
      return base;
   }

   public void setBase(SequenceComponent base) {
      this.base = base;
   }

}

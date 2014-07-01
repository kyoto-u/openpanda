/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/DecoratedItem.java $
* $Id: DecoratedItem.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.tool;

import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.shared.model.Node;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 4, 2006
 * Time: 5:51:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedItem {

   private PresentationItem base;
   private FreeFormTool parent;
   private Node node;
   private boolean selected;

   public DecoratedItem(PresentationItem base, FreeFormTool parent) {
      this.base = base;
      this.parent = parent;
      this.node = parent.getPresentationManager().getNode(base.getArtifactId(), parent.getPresentation());
   }

   public PresentationItem getBase() {
      return base;
   }

   public void setBase(PresentationItem base) {
      this.base = base;
   }

   public FreeFormTool getParent() {
      return parent;
   }

   public void setParent(FreeFormTool parent) {
      this.parent = parent;
   }

   public Node getNode() {
      return node;
   }

   public void setNode(Node node) {
      this.node = node;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

}

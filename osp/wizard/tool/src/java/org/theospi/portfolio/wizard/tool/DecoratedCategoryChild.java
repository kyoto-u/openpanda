/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCategoryChild.java $
* $Id: DecoratedCategoryChild.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.wizard.tool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 17, 2006
 * Time: 4:18:11 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DecoratedCategoryChild implements DecoratedListInterface {

   private static final String INDENT_CHAR = "&nbsp;&nbsp;&nbsp;";

   private String indentString;
   private int indent;
   private WizardTool parent;
   private boolean moveTarget;

   public DecoratedCategoryChild(WizardTool parent, int indent) {
      this.parent = parent;
      this.indent = indent;
      this.indentString = "";
      for (int i=0;i<indent - 1;i++) {
         this.indentString += INDENT_CHAR;
      }
   }

   public String getIndentString() {
      return indentString;
   }

   public void setIndentString(String indentString) {
      this.indentString = indentString;
   }

   public int getIndent() {
      return indent;
   }

   public void setIndent(int indent) {
      this.indent = indent;
   }

   public abstract String getTitle();

   public abstract boolean isSelected();
   public abstract void setSelected(boolean selected);

   public abstract String processActionEdit();
   public abstract String processActionDelete();

   public abstract String moveUp();
   public abstract String moveDown();

   public abstract boolean isFirst();
   public abstract boolean isLast();

   public boolean isCategory() {
      return false;
   }

   public String processActionMove() {
      setMoveTarget(true);
      getParent().setMoveCategoryChild(this);
      return null;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public boolean isMoveTarget() {
      return moveTarget;
   }

   public void setMoveTarget(boolean moveTarget) {
      this.moveTarget = moveTarget;
   }

   public String processActionCancelMove() {
      getParent().setMoveCategoryChild(null);      
      setMoveTarget(false);
      return null;
   }
}

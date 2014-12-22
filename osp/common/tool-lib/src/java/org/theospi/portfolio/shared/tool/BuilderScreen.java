/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/common/tool-lib/src/java/org/theospi/portfolio/shared/tool/BuilderScreen.java $
* $Id: BuilderScreen.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.shared.tool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 19, 2006
 * Time: 1:27:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuilderScreen {

   private BuilderTool tool;
   private String navigationKey;
   private int step = 0;

   private BuilderScreen next;
   private BuilderScreen prev;

   public BuilderScreen(String navigationKey) {
      this.navigationKey = navigationKey;
   }

   public String getNavigationKey() {
      return navigationKey;
   }

   public void setNavigationKey(String navigationKey) {
      this.navigationKey = navigationKey;
   }

   public BuilderScreen getNext() {
      return next;
   }

   public void setNext(BuilderScreen next) {
      this.next = next;
   }

   public BuilderScreen getPrev() {
      return prev;
   }

   public void setPrev(BuilderScreen prev) {
      this.prev = prev;
   }

   public BuilderTool getTool() {
      return tool;
   }

   public void setTool(BuilderTool tool) {
      this.tool = tool;
   }

   public int getStep() {
      return step;
   }

   public String getStepString() {
      return "" + (step);
   }

   public void setStep(int step) {
      this.step = step;
   }

   public BuilderScreen processActionSave(boolean forward) {
      getTool().saveScreen(this);

      return forward?getNext():getPrev();
   }

   public String processActionSaveNext() {
      BuilderScreen next = processActionSave(true);
      getTool().setCurrentScreen(next);
      return next.getNavigationKey();
   }

   public String processActionNext() {
      BuilderScreen next = getNext();
      getTool().setCurrentScreen(next);
      return next.getNavigationKey();
   }

   public String processActionSaveBack() {
      BuilderScreen prev = processActionSave(false);
      getTool().setCurrentScreen(prev);
      return prev.getNavigationKey();
   }

   public boolean isLast() {
      return getNext() == null;
   }

   public boolean isFirst() {
      return getPrev() == null;
   }

}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/tool-lib/src/java/org/theospi/portfolio/shared/tool/BuilderTool.java $
* $Id: BuilderTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
 * Time: 1:26:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BuilderTool extends HelperToolBase {

   private BuilderScreen currentScreen;
   private BuilderScreen[] screens;

   protected String startBuilder() {
      setCurrentScreen(screens[0]);
      return getCurrentScreen().getNavigationKey();
   }

   protected abstract void saveScreen(BuilderScreen screen);

   public BuilderScreen getCurrentScreen() {
      return currentScreen;
   }

   public void setCurrentScreen(BuilderScreen currentScreen) {
      this.currentScreen = currentScreen;
   }

   public BuilderScreen[] getScreens() {
      return screens;
   }

   public void setScreens(BuilderScreen[] screens) {
      for (int i=0;i<screens.length;i++) {
         BuilderScreen screen = screens[i];
         screen.setStep(i);
         screen.setTool(this);
         if (i > 0) {
            screen.setPrev(screens[i-1]);
         }

         if (i+1<screens.length) {
            screen.setNext(screens[i+1]);
         }
      }
      this.screens = screens;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedListInterface.java $
* $Id: DecoratedListInterface.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
 * This class is for defining the methods used to display the pages and categories
 * We also want to show the wizard itself in the case of the hierachical wizard
 * @author andersjb
 *
 */
public interface DecoratedListInterface {

   /**
    * tells the parent category of the instance
    * @return DecoratedCategory
    */
   public DecoratedCategory getCategory();

   /**
    * used to indent the title on the cat/page screen
    * @return String
    */
   public String getIndentString();

   /**
    * gets the title of the page/category list item
    * @return String
    */
   public String getTitle();

   /**
    * can this list element move?
    * @return boolean
    */
   public boolean isMoveTarget();

   /**
    * specifies if the instance has children
    * @return boolean
    */
   public boolean getHasChildren();

   /**
    * Tells whether this instance is the first element in the parent list
    * @return boolean
    */
   public boolean isFirst();

   /**
    * Tells whether this instance is the last element in the parent list
    * @return boolean
    */
   public boolean isLast();

   /**
    * Tells whether this instance is the wizard itself
    * @return boolean
    */
   public boolean isWizard();
}

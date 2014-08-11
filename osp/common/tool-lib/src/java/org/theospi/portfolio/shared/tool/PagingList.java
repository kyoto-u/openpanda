/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/common/tool-lib/src/java/org/theospi/portfolio/shared/tool/PagingList.java $
* $Id: PagingList.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.shared.tool;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 18, 2005
 * Time: 3:14:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagingList {

   private int firstItem = 0;
   private int pageSize = 10;

   private List wholeList;

   public PagingList(List wholeList) {
      this.wholeList = wholeList;
   }

   public int getTotalItems() {
      return wholeList.size();
   }

   public boolean isRendered() {
      return getTotalItems() > 0;
   }

   public int getFirstItem() {
      return firstItem;
   }

   public void setFirstItem(int firstItem) {
      this.firstItem = firstItem;
   }

   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public List getWholeList() {
      return wholeList;
   }

   public void setWholeList(List wholeList) {
      this.wholeList = wholeList;
   }

   public List getSubList() {
      if (pageSize == 0){
         return wholeList;
      }
      else {
         return wholeList.subList(getFirstItem(), getLastItem());
      }
   }

   public int getLastItem() {
      int lastItem = getFirstItem() + getPageSize();
      if (lastItem >= wholeList.size()) {
         lastItem = wholeList.size();
      }
      return lastItem;
   }
}

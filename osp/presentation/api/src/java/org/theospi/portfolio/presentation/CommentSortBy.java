/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/presentation/api/src/java/org/theospi/portfolio/presentation/CommentSortBy.java $
* $Id: CommentSortBy.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.presentation;

import org.theospi.portfolio.shared.model.OspException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 1, 2004
 * Time: 4:17:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommentSortBy {

   public static final String SORT_BY_DATE = "created";
   public static final String SORT_BY_CREATOR = "creator_id";
   public static final String SORT_BY_OWNER = "owner_id";
   public static final String SORT_BY_PRES_NAME = "name";
   public static final String SORT_BY_TITLE = "title";
   public static final String SORT_BY_VISIBILITY = "visibility";

   private static final String SORT_FIELD_SEARCH = "#" + SORT_BY_DATE +
      "#" + SORT_BY_CREATOR +
      "#" + SORT_BY_OWNER +
      "#" + SORT_BY_PRES_NAME +
      "#" + SORT_BY_VISIBILITY +
      "#" + SORT_BY_TITLE + "#";

   public static final String ASCENDING = "asc";
   public static final String DESCENDING = "desc";

   private static final String DIRECTION_SEARCH = "#" + ASCENDING +
      "#" + DESCENDING + "#";

   private String sortByColumn = SORT_BY_DATE;
   private String direction = DESCENDING;

   public CommentSortBy() {
   }

   public CommentSortBy(String sortByColumn) {
      setSortByColumn(sortByColumn);
   }

   public String getSortByColumn() {
      return sortByColumn;
   }

   public void setSortByColumn(String sortByColumn) {
      if (SORT_FIELD_SEARCH.indexOf("#" + sortByColumn + "#") == -1) {
         throw new OspException("Invalid sort");
      }

      this.sortByColumn = sortByColumn;
   }

   public String getDirection() {
      return direction;
   }

   public void setDirection(String direction) {
      if (DIRECTION_SEARCH.indexOf("#" + direction + "#") == -1) {
         throw new OspException("Invalid sort");
      }

      this.direction = direction;
   }
}

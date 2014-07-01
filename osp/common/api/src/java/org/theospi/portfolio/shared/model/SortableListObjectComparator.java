/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/common/api/src/java/org/theospi/portfolio/shared/model/SortableListObjectComparator.java $
* $Id: SortableListObjectComparator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.shared.model;

import java.util.Comparator;
import java.util.Date;

public class SortableListObjectComparator implements Comparator {

   public static final String SORT_TITLE = "title";
   public static final String SORT_DESCRIPTION = "description";
   public static final String SORT_OWNER = "owner.displayName";
   public static final String SORT_SITE = "site.title";
   public static final String SORT_TYPE = "type";
   public static final String SORT_MODIFIED = "modified";

   
   // the criteria
   private String criteria = null;

   // the criteria - asc
   private int asc = 1;

   /**
    * constructor
    * @param criteria The sort criteria string
    * @param asc The sort order string. "true" if ascending; "false" otherwise.
    */
   public SortableListObjectComparator(String criteria, int asc)
   {
      this.criteria = criteria;
      this.asc = asc;

   } // constructor

   /**
    * implementing the compare function
    * @param o1 The first object
    * @param o2 The second object
    * @return The compare result. 1 is o1 < o2; -1 otherwise
    */
   public int compare(Object o1, Object o2)
   {
      int result = -1;
      
      try {

         if (criteria.equals(SORT_TITLE))
         {
            // sorted by the title
            result =
               leftCheck(((SortableListObject) o1)
                  .getTitle())
                  .compareToIgnoreCase(
                        rightCheck(((SortableListObject) o2)
                        .getTitle()));
         }
         
         else if (criteria.equals(SORT_DESCRIPTION))
         {
            // sorted by the description
            result =
               leftCheck(((SortableListObject) o1)
                  .getDescription())
                  .compareToIgnoreCase(
                        rightCheck(((SortableListObject) o2)
                        .getDescription()));
         }
         else if (criteria.equals(SORT_OWNER))
         {
            // sorted by the owner
            result =
               leftCheck(((SortableListObject) o1)
                  .getOwner()
                  .getSortName())
                  .compareToIgnoreCase(
                        rightCheck(((SortableListObject) o2)
                        .getOwner()
                        .getSortName()));
         }
         else if (criteria.equals(SORT_TYPE))
         {
            // sorted by the type
            result =
               leftCheck(((SortableListObject) o1)
                  .getType())
                  .compareToIgnoreCase(
                        rightCheck(((SortableListObject) o2)
                        .getType()));
         }
         else if (criteria.equals(SORT_SITE))
         {
            // sorted by the site title
            result =
               leftCheck(((SortableListObject) o1)
                  .getSite().getTitle())
                  .compareToIgnoreCase(
                        rightCheck(((SortableListObject) o2)
                        .getSite().getTitle()));
         }
         else if (criteria.equals(SORT_MODIFIED))
         {
            // sorted by the modified date
            result =
               leftCheck(((SortableListObject) o1)
                  .getModifiedRaw())
                  .compareTo(
                        rightCheck(((SortableListObject) o2)
                        .getModifiedRaw()));
         }
      }
      catch (LeftSideNullPointerException lnpe) {
         //Don't worry about it...just return -1
         result = -1;
      }
      catch (RightSideNullPointerException rnpe) {
         //Don't worry about it...just return 1
         result = 1;
      }

      // sort ascending or descending
      return result * asc;

   } // compare
   
   protected String leftCheck(String value) throws LeftSideNullPointerException {
      if (value == null) throw new LeftSideNullPointerException("Left side of comparator is null");
      return value;
   }
   
   protected String rightCheck(String value) throws RightSideNullPointerException {
      if (value == null) throw new RightSideNullPointerException("Right side of comparator is null");
      return value;
   }
   
   protected Date leftCheck(Date value) throws LeftSideNullPointerException {
      if (value == null) throw new LeftSideNullPointerException("Left side of comparator is null");
      return value;
   }
   
   protected Date rightCheck(Date value) throws RightSideNullPointerException {
      if (value == null) throw new RightSideNullPointerException("Right side of comparator is null");
      return value;
   }
   
   
   public class LeftSideNullPointerException extends NullPointerException {
      LeftSideNullPointerException() {
         super();
      }
      
      LeftSideNullPointerException(String message) {
         super(message);
      }
      
   }
   
   public class RightSideNullPointerException extends NullPointerException {
      RightSideNullPointerException() {
         super();
      }
      
      RightSideNullPointerException(String message) {
         super(message);
      }
      
   }
   
   
}

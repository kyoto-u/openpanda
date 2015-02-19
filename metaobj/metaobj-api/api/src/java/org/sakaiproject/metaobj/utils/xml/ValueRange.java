/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/xml/ValueRange.java $
 * $Id: ValueRange.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.xml;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 19, 2005
 * Time: 4:20:00 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Class that holds information about the range restrictions that
 * apply to an element value.
 */
public class ValueRange {

   private Comparable max = null;
   private Comparable min = null;

   private boolean maxInclusive = false;
   private boolean minInclusive = false;

   /**
    * construct a new one
    *
    * @param max          the max comparable object, null if there is no upper bound
    * @param min          the min comparable object, null if there is no lower bound
    * @param maxInclusive true if value can equal max
    * @param minInclusive true if value can equal min
    */
   public ValueRange(Comparable max, Comparable min, boolean maxInclusive, boolean minInclusive) {
      this.max = max;
      this.min = min;
      this.maxInclusive = maxInclusive;
      this.minInclusive = minInclusive;
   }

   /**
    * test if the current value is in this range
    *
    * @param value value to test
    * @return true if the supplied value is in range
    */
   public boolean inRange(Comparable value) {
      if (max == null && min == null) {
         return true;
      }

      if (max == null) {
         return compartMin(value);
      }
      else if (min == null) {
         return compareMax(value);
      }
      else {
         return compareMax(value) && compartMin(value);
      }
   }

   protected boolean compareMax(Comparable value) {
      int result = value.compareTo(max);
      return (maxInclusive ? result <= 0 : result < 0);
   }

   protected boolean compartMin(Comparable value) {
      int result = value.compareTo(min);
      return (minInclusive ? result >= 0 : result > 0);
   }

   public Comparable getMax() {
      return max;
   }

   public void setMax(Comparable max) {
      this.max = max;
   }

   public Comparable getMin() {
      return min;
   }

   public void setMin(Comparable min) {
      this.min = min;
   }

   public boolean isMaxInclusive() {
      return maxInclusive;
   }

   public void setMaxInclusive(boolean maxInclusive) {
      this.maxInclusive = maxInclusive;
   }

   public boolean isMinInclusive() {
      return minInclusive;
   }

   public void setMinInclusive(boolean minInclusive) {
      this.minInclusive = minInclusive;
   }

}

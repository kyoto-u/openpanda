/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/NumberValueRange.java $
 * $Id: NumberValueRange.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.xml.impl;

import org.sakaiproject.metaobj.utils.xml.ValueRange;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 19, 2005
 * Time: 5:18:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberValueRange extends ValueRange {

   private Class rangeClass = null;

   public NumberValueRange(Comparable max, Comparable min, boolean maxInclusive, boolean minInclusive) {
      super(max, min, maxInclusive, minInclusive);
      if (max != null) {
         rangeClass = max.getClass();
      }
      else if (min != null) {
         rangeClass = min.getClass();
      }
      else {
         rangeClass = Comparable.class;
      }
   }

   public boolean inRange(Comparable value) {
      if (!rangeClass.isAssignableFrom(value.getClass())) {
         if (rangeClass.equals(Integer.class)) {
            value = createInteger(value);
         }
         else if (rangeClass.equals(Long.class)) {
            value = createLong(value);
         }
         else if (rangeClass.equals(Double.class)) {
            value = createDouble(value);
         }
         else if (rangeClass.equals(Float.class)) {
            value = createFloat(value);
         }
      }
      return super.inRange(value);
   }

   protected Integer createInteger(Comparable value) {
      Number number = (Number) value;
      return new Integer(number.intValue());
   }

   protected Long createLong(Comparable value) {
      Number number = (Number) value;
      return new Long(number.longValue());
   }

   protected Float createFloat(Comparable value) {
      Number number = (Number) value;
      return new Float(number.floatValue());
   }

   protected Double createDouble(Comparable value) {
      Number number = (Number) value;
      return new Double(number.doubleValue());
   }

}

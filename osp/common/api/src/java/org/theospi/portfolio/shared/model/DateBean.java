/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/shared/model/DateBean.java $
* $Id:DateBean.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.shared.model;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.springframework.validation.Errors;

public class DateBean implements FieldValueWrapper {
   protected final Log logger = LogFactory.getLog(getClass());
   private String month = "";
   private String year = "";
   private String day = "";
   private String hour = "";
   private String minute = "";
   private String second = "";
   boolean nullFlag = true;

   public DateBean() {
   }

   public DateBean(Date date) {
      if (date != null){
         setBackingDate(date);
      }
   }

   public void setBackingDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      setYear("" + cal.get(Calendar.YEAR));
      setMonth("" + (cal.get(Calendar.MONTH) + 1));
      setDay("" + cal.get(Calendar.DATE));
      setHour("" + cal.get(Calendar.HOUR));
      setMinute("" + cal.get(Calendar.MINUTE));
      setSecond("" + cal.get(Calendar.SECOND));
      nullFlag = false;
   }

   public String getMonth() {
      return month;
   }

   public void setMonth(String month) {
      this.month = month;
      checkFlag(month);
   }

   public String getYear() {
      return year;
   }

   public void setYear(String year) {
      this.year = year;
      checkFlag(year);
   }

   public String getDay() {
      return day;
   }

   public void setDay(String day) {
      this.day = day;
      checkFlag(day);
   }

   public String getHour() {
      return hour;
   }

   public void setHour(String hour) {
      this.hour = hour;
      checkFlag(hour);
   }

   public String getMinute() {
      return minute;
   }

   public void setMinute(String minute) {
      this.minute = minute;
      checkFlag(minute);
   }

   public String getSecond() {
      return second;
   }

   public void setSecond(String second) {
      this.second = second;
      checkFlag(second);
   }

   public void setValue(Object value) {
      setBackingDate((Date) value);
      nullFlag = (value == null);
   }

   public Object getValue() {
      return getDate();
   }

   public void validate(String fieldName, List errors, String label) {
      
   }

   public void validate(Errors errors) {
      if (nullFlag) {
         return;
      }

      try {
         Integer.parseInt(getYear());
      } catch (NumberFormatException e) {
         errors.rejectValue("year", "invalid year {0}", new Object[]{getYear()},
            MessageFormat.format("invalid year {0}", new Object[]{getYear()}));
      }
      try {
         Integer.parseInt(getMonth());
      } catch (NumberFormatException e) {
         errors.rejectValue("month", "invalid month {0}", new Object[]{getYear()},
            MessageFormat.format("invalid month {0}", new Object[]{getYear()}));
      }
      try {
         Integer.parseInt(getDay());
      } catch (NumberFormatException e) {
         errors.rejectValue("day", "invalid day {0}", new Object[]{getYear()},
            MessageFormat.format("invalid day {0}", new Object[]{getYear()}));
      }

      /*
      try {
         Integer.parseInt(getHour());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid hour: {0}",
               new Object[]{getHour()});
         errors.add(error);
      }
      try {
         Integer.parseInt(getMinute());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid minute: {0}",
               new Object[]{getMinute()});
         errors.add(error);
      }
      try {
         Integer.parseInt(getSecond());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid second: {0}",
               new Object[]{getSecond()});
         errors.add(error);
      }
      */
   }

   public Date getDate() {
      if (nullFlag) {
         return null;
      }

      return new GregorianCalendar(getValue(getYear()),
         getValue(getMonth()) - 1, // month is zero indexed
         getValue(getDay()),
         getValue(getHour()),
         getValue(getMinute()),
         getValue(getSecond())).getTime();
   }

   protected int getValue(String value) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   protected void checkFlag(String value) {
      nullFlag = (value == null || value.length() == 0);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }
   
}

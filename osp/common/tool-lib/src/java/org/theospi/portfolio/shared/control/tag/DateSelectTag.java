/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/control/tag/DateSelectTag.java $
* $Id:DateSelectTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.control.tag;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;

public class DateSelectTag extends DateSelectPopupTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String earliestYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 5);
   private String latestYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 5);
   private Date selectedDate;

   public int doStartTag() throws JspException {

      try {
         StringBuilder buffer = new StringBuilder();
         buffer.append("<select name=\"" + getMonthSelectId() + "\" id=\"" + getMonthSelectId() + "\">\n");
         buffer.append("<option value=\"\"></option>\n");
         for (int i=1; i<13; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getMonthSelected() == i){
               buffer.append(" selected=\"selected\"");
            }
            buffer.append(">" + getMonthName(i) + "</option>\n");
         }
         buffer.append("</select>");

         buffer.append("<select name=\"" + getDaySelectId() + "\" id=\"" + getDaySelectId() + "\">\n");
         buffer.append("<option value=\"\"></option>\n");
         for (int i=1; i<32; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getDaySelected() == i){
               buffer.append(" selected=\"selected\"");
            }
            buffer.append(">" + i + "</option>\n");
         }
         buffer.append("</select>");

         buffer.append("<select name=\"" + getYearSelectId() + "\" id=\"" + getYearSelectId() + "\">\n");
         buffer.append("<option value=\"\"></option>\n");
         for (int i=Integer.parseInt(earliestYear); i<Integer.parseInt(latestYear)+1; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getYearSelected() == i){
               buffer.append(" selected=\"selected\"");
            }
            buffer.append(">" + i + "</option>\n");
         }
         buffer.append("</select>");

         pageContext.getOut().write(buffer.toString());
         super.doStartTag();
      } catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }

      return EVAL_BODY_INCLUDE;
   }

   protected String getMonthName(int month) throws JspException {
      switch (month) {
         case 1 : return "JAN";
         case 2 : return "FEB";
         case 3 : return "MAR";
         case 4 : return "APR";
         case 5 : return "MAY";
         case 6 : return "JUN";
         case 7 : return "JUL";
         case 8 : return "AUG";
         case 9 : return "SEP";
         case 10 : return "OCT";
         case 11 : return "NOV";
         case 12 : return "DEC";
      }
      throw new JspException(month + " is not a valid month");
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }

   protected Calendar getCalendar(){
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(selectedDate);
      return calendar;
   }

   protected int getMonthSelected(){
      if (selectedDate == null) return -1;
      return getCalendar().get(Calendar.MONTH)+1; // Calendar indexes months starting at 0
   }

   protected int getYearSelected(){
      if (selectedDate == null) return -1;
      return getCalendar().get(Calendar.YEAR);
   }

   protected int getDaySelected(){
      if (selectedDate == null) return -1;
      return getCalendar().get(Calendar.DAY_OF_MONTH);
   }

   public String getEarliestYear() {
      return earliestYear;
   }

   public void setEarliestYear(String earliestYear) {
      this.earliestYear = earliestYear;
   }

   public String getLatestYear() {
      return latestYear;
   }

   public void setLatestYear(String lastestYear) {
      this.latestYear = lastestYear;
   }

   public Date getSelectedDate() {
      return selectedDate;
   }

   public void setSelectedDate(Date selectedDate) {
      this.selectedDate = selectedDate;
   }
}

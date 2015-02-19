/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/DateFormatterElementType.java $
 * $Id: DateFormatterElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValueRange;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 17, 2004
 * Time: 3:21:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateFormatterElementType extends FormatterElementType {

   private static final String DATE_FORMAT = "yyyy-MM-dd";
   private static final String TIME_FORMAT = "hh:mm:ss";
   private static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" +
         TIME_FORMAT;

   private static final String DATE_TYPE = "xs:date";
   private static final String TIME_TYPE = "xs:time";
   private static final String DATE_TIME_TYPE = "xs:dateTime";

   private Format formatter = null;
   private String format = null;

   private Date maxIncl = null;
   private Date minIncl = null;
   private Date maxExcl = null;
   private Date minExcl = null;

   private ValueRange range = null;

   public DateFormatterElementType(String typeName, Element schemaElement,
                                   SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      if (typeName.equals(DATE_TYPE)) {
         format = DATE_FORMAT;
      }
      else if (typeName.equals(TIME_TYPE)) {
         format = TIME_FORMAT;
      }
      else if (typeName.equals(DATE_TIME_TYPE)) {
         format = DATE_TIME_FORMAT;
      }
      else {
         throw new IllegalArgumentException();
      }

      formatter = new SimpleDateFormat(format);

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            try {
               maxIncl = (Date) getFormattedRestriction(restrictions, "maxInclusive", xsdNamespace);
               minIncl = (Date) getFormattedRestriction(restrictions, "minInclusive", xsdNamespace);
               maxExcl = (Date) getFormattedRestriction(restrictions, "maxExclusive", xsdNamespace);
               minExcl = (Date) getFormattedRestriction(restrictions, "minExclusive", xsdNamespace);
            }
            catch (ParseException e) {
               throw new SchemaInvalidException(e);
            }
         }
      }

      if (maxIncl != null || minIncl != null ||
            maxExcl != null || minExcl != null) {
         // one must not be null, create a range
         Comparable min = minIncl;
         if (min == null) {
            min = minExcl;
         }

         Comparable max = maxIncl;
         if (max == null) {
            max = maxExcl;
         }

         range = new ValueRange(max, min, maxIncl != null, minIncl != null);
      }
   }

   protected Format getFormatter() {
      return formatter;
   }

   protected Object checkConstraints(Object o) {
      Date date = (Date) o;

      if (maxIncl != null && date.after(maxIncl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_AFTER_ERROR_CODE, new Object[]{o, maxIncl});
      }

      if (minIncl != null && date.before(minIncl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_BEFORE_ERROR_CODE, new Object[]{o, minIncl});
      }

      if (maxExcl != null && !date.after(maxExcl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_TOO_LATE_ERROR_CODE, new Object[]{o, maxExcl});
      }

      if (minExcl != null && !date.before(minExcl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_TOO_EARLY_ERROR_CODE, new Object[]{o, minExcl});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid date/time",
            NormalizationException.DATE_INVALID_ERROR_CODE, new Object[]{value, format});
   }

   public Class getObjectType() {
      return Date.class;
   }

   public ValueRange getRange() {
      return range;
   }
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/NumberElementType.java $
 * $Id: NumberElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import java.text.NumberFormat;
import java.text.ParseException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValueRange;
import org.sakaiproject.util.ResourceLoader;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 16, 2004
 * Time: 4:05:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberElementType extends FormatterElementType {

   protected Number maxIncl = null;
   protected Number minIncl = null;
   protected Number maxExcl = null;
   protected Number minExcl = null;
   protected int totalDigits = -1;

   private ValueRange range = null;

   private Format format = null;

   public NumberElementType(String typeName, Element schemaElement, SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      format = NumberFormat.getIntegerInstance(new ResourceLoader().getLocale());

      totalDigits = (new String(Long.MAX_VALUE + "")).length();

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            try {
               maxIncl = (Number) getFormattedRestriction(restrictions, "maxInclusive", xsdNamespace);
               minIncl = (Number) getFormattedRestriction(restrictions, "minInclusive", xsdNamespace);
               maxExcl = (Number) getFormattedRestriction(restrictions, "maxExclusive", xsdNamespace);
               minExcl = (Number) getFormattedRestriction(restrictions, "minExclusive", xsdNamespace);
            }
            catch (ParseException e) {
               throw new SchemaInvalidException(e);
            }

            totalDigits = processIntRestriction(restrictions, "totalDigits", xsdNamespace, totalDigits);
         }
      }

      if (maxIncl != null || minIncl != null ||
            maxExcl != null || minExcl != null) {
         // one must not be null, create a range
         Comparable min = (Comparable) minIncl;
         if (min == null) {
            min = (Comparable) minExcl;
         }

         Comparable max = (Comparable) maxIncl;
         if (max == null) {
            max = (Comparable) maxExcl;
         }

         range = new NumberValueRange(max, min, maxIncl != null, minIncl != null);
      }
   }

   protected Format getFormatter() {
      return format;
   }

   protected Object checkConstraints(Object o) {

      int i = ((Number) o).intValue();

      if (maxIncl != null && i > maxIncl.intValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_INCLUSIVE_ERROR_CODE, new Object[]{o, maxIncl});
      }

      if (minIncl != null && i < minIncl.intValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_SMALL_INCLUSIVE_ERROR_CODE, new Object[]{o, minIncl});
      }

      if (maxExcl != null && i >= maxExcl.intValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_ERROR_CODE, new Object[]{o, maxExcl});
      }

      if (minExcl != null && i <= minExcl.intValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_SMALL_ERROR_CODE, new Object[]{o, minExcl});
      }

      if (totalDigits != -1 && o.toString().length() > totalDigits) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_MANY_DIGITS_ERROR_CODE, new Object[]{o, new Integer(totalDigits)});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid number",
            NormalizationException.INVALID_NUMBER_ERROR_CODE, new Object[]{value});
   }

   public Class getObjectType() {
      return Number.class;
   }

   public int getMaxLength() {
      return totalDigits;
   }

   public ValueRange getRange() {
      return range;
   }

}

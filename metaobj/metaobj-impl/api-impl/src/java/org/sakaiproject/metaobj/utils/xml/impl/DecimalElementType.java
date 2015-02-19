/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/DecimalElementType.java $
 * $Id: DecimalElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 19, 2004
 * Time: 10:09:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecimalElementType extends NumberElementType {

   private Format format = null;
   private int fractionDigits = -1;

   public DecimalElementType(String typeName, Element schemaElement,
                             SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            fractionDigits = processIntRestriction(restrictions, "fractionDigits", xsdNamespace, fractionDigits);

            if (fractionDigits != -1) {
               ((DecimalFormat) getFormatter()).setMaximumFractionDigits(fractionDigits);
               ((DecimalFormat) getFormatter()).setMinimumFractionDigits(fractionDigits);
            }
         }
      }

   }


   protected Format getFormatter() {
      if (format == null) {
         format = new DecimalFormat();
      }
      return format;
   }

   protected Object checkConstraints(Object o) {

      double i = ((Number) o).doubleValue();

      if (maxIncl != null && i > maxIncl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_INCLUSIVE_ERROR_CODE, new Object[]{o, maxIncl});
      }

      if (minIncl != null && i < minIncl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_SMALL_INCLUSIVE_ERROR_CODE, new Object[]{o, minIncl});
      }

      if (maxExcl != null && i >= maxExcl.doubleValue()) {
         throw new NormalizationException("Invalid number",
               NormalizationException.TOO_LARGE_ERROR_CODE, new Object[]{o, maxExcl});
      }

      if (minExcl != null && i <= minExcl.doubleValue()) {
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
            NormalizationException.INVALID_DECIMAL_NUMBER_ERROR_CODE, new Object[]{value});
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/ElementTypeFactory.java $
 * $Id: ElementTypeFactory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 6:02:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementTypeFactory {

   private static ElementTypeFactory instance = new ElementTypeFactory();

   private static final String INT_TYPES = "" +
         "        xs:byte" + //A signed 8-bit integer \n" +
         "        xs:int" + //A signed 32-bit integer \n" +
         "        xs:integer" + //An integer value \n" +
         "        xs:long" + //A signed 64-bit integer \n" +
         "        xs:negativeInteger" + //An integer containing only negative values ( .., -2, -1.) \n" +
         "        xs:nonNegativeInteger" + //An integer containing only non-negative values (0, 1, 2, ..) \n" +
         "        xs:nonPositiveInteger" + //An integer containing only non-positive values (.., -2, -1, 0) \n" +
         "        xs:positiveInteger" + //An integer containing only positive values (1, 2, ..) \n" +
         "        xs:short" + //A signed 16-bit integer \n" +
         "        xs:unsignedLong" + //An unsigned 64-bit integer \n" +
         "        xs:unsignedInt" + //An unsigned 32-bit integer \n" +
         "        xs:unsignedShort" + //An unsigned 16-bit integer \n" +
         "        xs:unsignedByte"; //An unsigned 8-bit integer";

   private static final String DECIMAL_TYPES = "" +
         "        xs:decimal";

   private static final String BOOLEAN_TYPES = "" +
         "        xs:boolean";

   private static final String DATE_TYPES = "" +
         "        xs:date" +
         "        xs:time" +
         "        xs:dateTime";

   private static final String URI_TYPES = "" +
         "        xs:anyURI";

   protected ElementTypeFactory() {

   }

   public static ElementTypeFactory getInstance() {
      return instance;
   }

   public BaseElementType createElementType(Element schemaElement, SchemaNode parentNode,
                                            Namespace xsdNamespace) {
      String typeName = "unknown";

      if (schemaElement.getAttributeValue("type") != null) {
         typeName = schemaElement.getAttributeValue("type");
      }
      else {
         Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
         if (simpleType == null) {
            simpleType = schemaElement.getChild("simpleContent", xsdNamespace);
         }

         if (simpleType != null) {
            Element restrictions = simpleType.getChild("restriction", xsdNamespace);
            if (restrictions != null) {
               typeName = restrictions.getAttributeValue("base");
            }
            else {
               typeName = simpleType.getChild("extension", xsdNamespace).getAttributeValue("base");
            }
         }
      }

      if (INT_TYPES.indexOf(typeName) != -1) {
         return new NumberElementType(typeName, schemaElement,
               parentNode, xsdNamespace).postInit(xsdNamespace);
      }
      else if (DECIMAL_TYPES.indexOf(typeName) != -1) {
         return new DecimalElementType(typeName, schemaElement,
               parentNode, xsdNamespace).postInit(xsdNamespace);
      }
      else if (BOOLEAN_TYPES.indexOf(typeName) != -1) {
         return new BooleanElementType(typeName, schemaElement,
               parentNode, xsdNamespace).postInit(xsdNamespace);
      }
      else if (DATE_TYPES.indexOf(typeName) != -1) {
         return new DateFormatterElementType(typeName, schemaElement,
               parentNode, xsdNamespace).postInit(xsdNamespace);
      }
      else if (URI_TYPES.indexOf(typeName) != -1) {
         return new UriElementType(typeName, schemaElement,
               parentNode, xsdNamespace).postInit(xsdNamespace);
      }

      // default to BaseElementType
      return new BaseElementType(typeName, schemaElement, parentNode, xsdNamespace).postInit(xsdNamespace);
   }

}

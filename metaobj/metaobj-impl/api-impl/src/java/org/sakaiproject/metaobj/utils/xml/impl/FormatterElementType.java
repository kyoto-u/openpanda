/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/FormatterElementType.java $
 * $Id: FormatterElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 16, 2004
 * Time: 2:48:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FormatterElementType extends BaseElementType {

   public FormatterElementType(String typeName, Element schemaElement, SchemaNode parentNode,
                               Namespace xsdNamespace) {

      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   protected abstract Format getFormatter();

   protected abstract Object checkConstraints(Object o);

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      if (!getObjectType().isInstance(value)) {
         throw new NormalizationException("Invalid object type",
               NormalizationException.INVALID_TYPE_ERROR_CODE,
               new Object[]{value, getObjectType()});
      }

      value = checkConstraints(value);

      try {
         return getFormatter().format(value);
      }
      catch (Exception e) {
         throw new NormalizationException(e);
      }
   }


   public String getSchemaNormalizedValue(String value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      try {
         return getFormatter().format(checkConstraints(getFormatter().parseObject(value)));
      }
      catch (ParseException e) {
         return parserException(value, e);
      }
      catch (Exception e) {
         throw new NormalizationException(e);
      }
   }

   protected Object getFormattedRestriction(Element restrictions, String name,
                                            Namespace xsdNamespace) throws ParseException {
      String value = processStringRestriction(restrictions, name, xsdNamespace);

      if (value == null) {
         return null;
      }

      return getFormatter().parseObject(value);
   }

   protected abstract String parserException(String value, ParseException e);

   public Object getActualNormalizedValue(String value) {
      try {
         return checkConstraints(getFormatter().parseObject(value));
      }
      catch (ParseException e) {
         return parserException(value, e);
      }
   }

   public abstract Class getObjectType();

}

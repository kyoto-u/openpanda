/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/SimpleSchemaNodeImpl.java $
 * $Id: SimpleSchemaNodeImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.ElementType;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;
import org.sakaiproject.metaobj.utils.xml.ValidationError;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 3:47:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSchemaNodeImpl extends SchemaNodeImpl {

   private BaseElementType type;
   private boolean isAttribute = false;

   /*
   public SimpleSchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps)
      throws SchemaInvalidException {

      super(schemaElement, globalMaps);
   }
   */


   public SimpleSchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps, boolean isAttribute)
         throws SchemaInvalidException {

      super(schemaElement, globalMaps);
      this.isAttribute = isAttribute;
   }


   protected void initSchemaElement() {
      super.initSchemaElement();
      type = ElementTypeFactory.getInstance().createElementType(getSchemaElement(),
            this, xsdNamespace);
   }

   /**
    * Validates the passed in node and all children.
    * Will also normalize any values.
    *
    * @param node a jdom element to validate
    * @return the validated Element wrapped
    *         in a ValidatedNode class
    */
   public ValidatedNode validateAndNormalize(Element node) {
      return type.validateAndNormalize(node);
   }

   public ValidatedNode validateAndNormalize(Attribute node) {
      ValidatedNodeImpl validatedNode =
            new ValidatedNodeImpl(this, null);

      if (!isAttribute()) {
         validatedNode.getErrors().add(new ValidationError(validatedNode, "not an attribute", new Object[]{}));
      }
      return type.validateAndNormalize(node);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (value instanceof String) {
         return type.getSchemaNormalizedValue((String) value);
      }
      else {
         return type.getSchemaNormalizedValue(value);
      }
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      return type.getActualNormalizedValue(value);
   }

   public Class getObjectType() {
      return type.getObjectType();
   }

   public int getMaxLength() {
      return type.maxLength;
   }

   public ElementType getType() {
      return type;
   }

   public List getEnumeration() {
      return type.getEnumeration();
   }

   public boolean isAttribute() {
      return isAttribute;
   }

   public boolean isDataNode() {
      return true;
   }

}

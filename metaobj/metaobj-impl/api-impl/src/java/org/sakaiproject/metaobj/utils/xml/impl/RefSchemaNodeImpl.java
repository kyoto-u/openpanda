/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/RefSchemaNodeImpl.java $
 * $Id: RefSchemaNodeImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
import java.util.Map;

import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.ElementType;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 6:53:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefSchemaNodeImpl extends SchemaNodeImpl {

   private String refName = null;
   private Map localGlobalElements;

   public RefSchemaNodeImpl(String refName, Element schemaElement,
                            GlobalMaps globalMaps) throws SchemaInvalidException {
      super(schemaElement, globalMaps);
      localGlobalElements = globalMaps.globalElements;
      this.refName = refName;
   }

   protected SchemaNode getActualNode() {
      return (SchemaNode) localGlobalElements.get(refName);
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
      return getActualNode().validateAndNormalize(node);
   }

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName) {
      return getActualNode().getChild(elementName);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      return getActualNode().getSchemaNormalizedValue(value);
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      return getActualNode().getActualNormalizedValue(value);
   }

   public List getChildren() {
      return getActualNode().getChildren();
   }

   public ElementType getType() {
      if (getActualNode() instanceof SimpleSchemaNodeImpl) {
         return ((SimpleSchemaNodeImpl) getActualNode()).getType();
      }

      return null;
   }

   public String getName() {
      return getActualNode().getName();
   }

   public Class getObjectType() {
      return getActualNode().getObjectType();
   }

   public List getEnumeration() {
      return getActualNode().getEnumeration();
   }

   public boolean isDataNode() {
      return getActualNode().isDataNode();
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/ValidatedNodeImpl.java $
 * $Id: ValidatedNodeImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 12:10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatedNodeImpl implements ValidatedNode {

   private SchemaNode parentSchema;
   private Element currentElement;
   private List currentErrors = new ArrayList();
   private List children = new ArrayList();
   private Object normalizedValue;

   public ValidatedNodeImpl(SchemaNode parentSchema,
                            Element currentElement) {

      this.parentSchema = parentSchema;
      this.currentElement = currentElement;
   }


   /**
    * Get the schema responsible for this node.
    *
    * @return
    */
   public SchemaNode getSchema() {
      return parentSchema;
   }

   /**
    * Get the named child node as a validated node
    *
    * @param elementName
    * @return
    */
   public ValidatedNode getChild(String elementName) {

      for (Iterator i = children.iterator(); i.hasNext();) {
         ValidatedNode currentNode = (ValidatedNode) i.next();

         if (elementName.equals(currentNode.getElement().getName())) {
            return currentNode;
         }
      }

      return null;
   }

   /**
    * Get all the direct children of this node as
    * a list of ValidatedNode objects
    *
    * @return
    */
   public List getChildren() {
      return children;
   }

   /**
    * Get all the named direct children of this node
    * as a list of ValidatedNode objects.
    *
    * @param elementName
    * @return
    */
   public List getChildren(String elementName) {

      List namedList = new ArrayList();

      for (Iterator i = children.iterator(); i.hasNext();) {
         ValidatedNode currentNode = (ValidatedNode) i.next();

         if (elementName.equals(currentNode.getElement().getName())) {
            namedList.add(currentNode);
         }
      }

      return namedList;
   }

   /**
    * Get the normalized value of this element as an object.
    * Note: in the case of complex nodes, this could return
    * a List of ValidatedNode objects (the children of this node)
    *
    * @return
    */
   public Object getNormalizedValue() {
      return normalizedValue;
   }

   public void setNormalizedValue(Object normalizedValue) {
      this.normalizedValue = normalizedValue;
   }

   /**
    * The errors associated with this node if any or null if the
    * node validated completely.
    *
    * @return
    */
   public List getErrors() {
      return currentErrors;
   }

   /**
    * This returnes the element associated with this node.
    *
    * @return
    */
   public Element getElement() {
      return currentElement;
   }
}

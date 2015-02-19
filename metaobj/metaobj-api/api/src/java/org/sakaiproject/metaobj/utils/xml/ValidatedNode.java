/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/xml/ValidatedNode.java $
 * $Id: ValidatedNode.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.xml;

import java.util.List;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ValidatedNode {

   /**
    * Get the schema responsible for this node.
    *
    * @return
    */
   public SchemaNode getSchema();

   /**
    * Get the named child node as a validated node
    *
    * @param elementName
    * @return
    */
   public ValidatedNode getChild(String elementName);

   /**
    * Get all the direct children of this node as
    * a list of ValidatedNode objects
    *
    * @return
    */
   public List getChildren();

   /**
    * Get all the named direct children of this node
    * as a list of ValidatedNode objects.
    *
    * @param elementName
    * @return
    */
   public List getChildren(String elementName);

   /**
    * Get the normalized value of this element as an object.
    * Note: in the case of complex nodes, this could return
    * either a List of ValidatedNode objects (the children of this node)
    *
    * @return
    */
   public Object getNormalizedValue();

   /**
    * The list of errors associated with this node
    *
    * @return
    */
   public List getErrors();

   /**
    * This returnes the element associated with this node.
    *
    * @return
    */
   public Element getElement();

}

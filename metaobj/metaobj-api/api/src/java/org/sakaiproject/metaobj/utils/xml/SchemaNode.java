/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/xml/SchemaNode.java $
 * $Id: SchemaNode.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SchemaNode extends Serializable {

   /**
    * Get the namespace for this schema
    *
    * @return the target namespace of this xsd schema
    */
   public Namespace getTargetNamespace();

   /**
    * Validates the passed in node and all children.
    * Will also normalize any values.
    *
    * @param node a jdom element to validate
    * @return the validated Element wrapped
    *         in a ValidatedNode class
    */
   public ValidatedNode validateAndNormalize(Element node);

   public ValidatedNode validateAndNormalize(Attribute node);

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName);

   /**
    * Retuns the max number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMaxOccurs();

   /**
    * Returns the min number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMinOccurs();

   public String getSchemaNormalizedValue(Object value)
         throws NormalizationException;

   public Object getActualNormalizedValue(String value)
         throws NormalizationException;

   public String getName();

   public Class getObjectType();

   public List getChildren();

   public Collection getRootChildren();

   public String getDocumentAnnotation(String source);

   public String getAppAnnotation(String source);

   public Map getDocumentAnnotations();

   public Map getAppAnnotations();

   public List getEnumeration();

   public boolean hasEnumerations();

   public boolean isAttribute();

   public boolean isDataNode();

   public Element getSchemaElement();

   public ElementType getType();

   public String getLabel();

}

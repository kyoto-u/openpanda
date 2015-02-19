/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/CustomTypeSchemaNodeImpl.java $
 * $Id: CustomTypeSchemaNodeImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

public class CustomTypeSchemaNodeImpl extends RefSchemaNodeImpl {
   protected final Log logger = LogFactory.getLog(getClass());
   private String name = null;
   private Map localGlobalElements;
   private String refName;
   private boolean isAttribute = false;

   public CustomTypeSchemaNodeImpl(Element schemaElement,
                                   GlobalMaps globalMaps, String type,
                                   boolean isAttribute) throws SchemaInvalidException {
      // use the type to look up the real SchemaNode
      super(type, schemaElement, globalMaps);
      refName = type;
      localGlobalElements = globalMaps.globalCustomTypes;
      name = schemaElement.getAttributeValue("name");
      this.isAttribute = isAttribute;
   }

   public String getName() {
      return name;
   }

   protected SchemaNode getActualNode() {
      return (SchemaNode) localGlobalElements.get(refName);
   }

   public boolean isAttribute() {
      return isAttribute;
   }

   public List getEnumeration() {
      return getActualNode().getEnumeration();
   }
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/RefAttributeSchemaNodeImpl.java $
 * $Id: RefAttributeSchemaNodeImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;

public class RefAttributeSchemaNodeImpl extends RefSchemaNodeImpl {
   protected final Log logger = LogFactory.getLog(getClass());
   private boolean required = false;

   public RefAttributeSchemaNodeImpl(String refName, Element schemaElement,
                                     SchemaNodeImpl.GlobalMaps globalMaps) throws SchemaInvalidException {
      super(refName, schemaElement, globalMaps);
      required = "required".equals(getSchemaElement().getAttributeValue("use"));
   }

   /**
    * Retuns the max number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMaxOccurs() {
      return 1;
   }

   /**
    * Returns the min number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMinOccurs() {
      return required ? 1 : 0;
   }

   public boolean isAttribute() {
      return true;
   }
}

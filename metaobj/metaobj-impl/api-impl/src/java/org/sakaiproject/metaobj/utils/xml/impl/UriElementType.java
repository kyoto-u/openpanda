/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/impl/UriElementType.java $
 * $Id: UriElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.net.URI;
import java.net.URISyntaxException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 25, 2006
 * Time: 10:33:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class UriElementType extends BaseElementType {

   private static final String SAKAI_REF_SCHEME = "sakairef";

   public UriElementType(String typeName, Element schemaElement, SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   public Class getObjectType() {
      return URI.class;
   }

   public Object getActualNormalizedValue(String value) {
      try {
         if (value.startsWith("/")) {
            return new URI(SAKAI_REF_SCHEME, value, null);
         }
         else {
            return new URI(value);
         }
      }
      catch (URISyntaxException e) {
         throw new NormalizationException("Invalid URI", NormalizationException.INVALID_URI, new Object[]{value});
      }
   }

   public String getSchemaNormalizedValue(String value) throws NormalizationException {
      return getSchemaNormalizedValue(getActualNormalizedValue(value));
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (value != null) {

         URI uri = (URI) value;
         if (uri.getScheme().equals(SAKAI_REF_SCHEME)) {
            return uri.getSchemeSpecificPart();
         }
         else {
            return uri.toString();
         }
      }
      else {
         return null;
      }
   }

}

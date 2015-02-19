/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/xml/SchemaFactory.java $
 * $Id: SchemaFactory.java 120216 2013-02-18 19:44:04Z ottenhoff@longsight.com $
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

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.metaobj.utils.xml.impl.SchemaNodeImpl;

public class SchemaFactory {
   static private SchemaFactory schemaFactory = new SchemaFactory();

   private Hashtable schemas = new Hashtable();

   private SchemaFactory() {
   }

   public static SchemaFactory getInstance() {
      return schemaFactory;
   }

   public SchemaNode getSchema(java.io.File in) {
      if (schemas.get(in) != null) {
         return (SchemaNode) schemas.get(in);
      }

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(in, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in) {

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(in, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.Reader characterStream, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(characterStream, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.net.URL url) {
      if (schemas.get(url) != null) {
         return (SchemaNode) schemas.get(url);
      }

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = builder.build(url);
         SchemaNode node = new SchemaNodeImpl(doc, this, url);
         schemas.put(url, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getRelativeSchema(String schemaLocation, Object path) {
      try {
         if (path instanceof URL) {
            URL urlPath = (URL) path;
            URL schemaUrl = new URL(urlPath, schemaLocation);
            return getSchema(schemaUrl);
         }
         else if (path instanceof File) {
            File filePath = (File) path;
            File schemaFile = new File(filePath.getParentFile(), schemaLocation);
            return getSchema(schemaFile);
         }
         else {
            URL urlPath = new URL(path.toString());
            URL schemaUrl = new URL(urlPath, schemaLocation);
            return getSchema(schemaUrl);
         }
      }
      catch (Exception exp) {
         throw new SchemaInvalidException(exp);
      }
   }

   public void reload() {
      schemas = new Hashtable();
   }

}

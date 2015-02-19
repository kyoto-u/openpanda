/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/SchemaBean.java $
 * $Id: SchemaBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 20, 2004
 * Time: 5:43:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaBean {

   private SchemaNode schema;
   private String schemaName;
   private String parentName = "";
   private boolean rootNodeFlag = false;
   private Map annotations = null;
   private SchemaBean parent = null;
   protected final Log logger = LogFactory.getLog(getClass());
   private boolean documentRoot = false;
   private String description;

   public SchemaBean(String rootNode, SchemaNode schema, String schemaName, String schemaDescription) {
      this.schema = schema.getChild(rootNode);
      this.schemaName = schemaName;
      this.description = schemaDescription;
      rootNodeFlag = true;
      this.parent = new SchemaBean(schema, schemaName, null, schemaDescription);
      documentRoot = true;
   }

   public SchemaBean(SchemaNode schema, String schemaName, String parentName, String schemaDescription) {
      this.schema = schema;
      this.schemaName = schemaName;
      this.parentName = parentName;
      this.description = schemaDescription;

      if (parentName == null) {
         rootNodeFlag = true;
      }
   }

   public List getFields() {
      logger.debug("schema name" + schema.getName());
      List fieldList = schema.getChildren();
      List returnedList = new ArrayList();

      for (Iterator i = fieldList.iterator(); i.hasNext();) {
         returnedList.add(new SchemaBean((SchemaNode) i.next(),
               schemaName, getFieldNamePath(), description));
      }

      return returnedList;
   }

   public String getFieldName() {
      return schema.getName();
   }

   public String getFieldNamePath() {
      return getFieldNamePath(false);
   }

   public String getFieldNamePathReadOnly() {
      return getFieldNamePath(true);
   }

   public String getFieldNamePath(boolean viewOnly) {
      if (rootNodeFlag) {
         return "";
      }

      String returnedPath = parentName;

      if (returnedPath != null && returnedPath.length() > 0) {
         if (viewOnly) {
            returnedPath += "']['";
         }
         else {
            returnedPath += ".";
         }
      }
      else if (returnedPath == null) {
         returnedPath = "";
      }

      return returnedPath + getFieldName();
   }

   public SchemaNode getSchema() {
      return schema;
   }

   public void setSchema(SchemaNode schema) {
      this.schema = schema;
   }

   public String getSchemaName() {
      return schemaName;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public Map getAnnotations() {
      if (annotations != null) {
         return annotations;
      }

      annotations = new Hashtable();

      for (Iterator i = schema.getDocumentAnnotations().entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();

         if (entry.getKey().toString().startsWith("ospi.")) {
            annotations.put(entry.getKey().toString().substring(5),
                  entry.getValue());
         }
         else if (entry.getKey().toString().startsWith("sakai.")) {
            annotations.put(entry.getKey().toString().substring(6),
                  entry.getValue());
         }
      }

      return annotations;
   }

   public SchemaBean getParent() {
      return parent;
   }

   public void setParent(SchemaBean parent) {
      this.parent = parent;
   }

   public boolean isDocumentRoot() {
      return documentRoot;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}

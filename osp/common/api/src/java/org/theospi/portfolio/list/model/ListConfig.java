/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/common/api/src/java/org/theospi/portfolio/list/model/ListConfig.java $
* $Id: ListConfig.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.list.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

public class ListConfig {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id;
   private Id toolId;
   private Agent owner;
   private String title;
   private List selectedColumns = new ArrayList();
   private int height;
   private int rows;

   // not persisted, used on UI
   private List columns;

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getRows() {
      return rows;
   }

   public void setRows(int rows) {
      this.rows = rows;
   }

   public List getColumns() {
      return columns;
   }

   public void setColumns(List columns) {
      this.columns = columns;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Id getToolId() {
      return toolId;
   }

   public void setToolId(Id toolId) {
      this.toolId = toolId;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public List getSelectedColumns() {
      return selectedColumns;
   }

   public void setSelectedColumns(List selectedColumns) {
      this.selectedColumns = selectedColumns;
   }

   public Map getSelected() {
      return new ColumnMap(getSelectedColumns());
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   private class ColumnMap extends HashMap {

      private List selectedColumns;
      private final Column FAKE_COLUMN = new Column("empty", false);

      /**
       * Constructs an empty <tt>HashMap</tt> with the default initial capacity
       * (16) and the default load factor (0.75).
       */
      public ColumnMap(List selectedColumns) {
         this.selectedColumns = selectedColumns;
      }

      public Object get(Object indexString) {
         int index = Integer.parseInt(indexString.toString());

         if (selectedColumns.size() > index) {
            return new Column((String)selectedColumns.get(index),
               true);
         }
         else {
            return FAKE_COLUMN;
         }
      }

      /* (non-Javadoc)
       * @see java.util.HashMap#entrySet()
       */
      public Set entrySet() {
         Set entries = new HashSet();
         for (int i=0; i<this.selectedColumns.size(); i++) {
            entries.add((Column)get(i));
         }
         return entries;
      }
      
      

   }

}

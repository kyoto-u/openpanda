/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/impl/BaseChildWarehouseTask.java $
* $Id:BaseChildWarehouseTask.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package org.sakaiproject.warehouse.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.quartz.JobExecutionException;
import org.sakaiproject.warehouse.service.ItemIndexInParentPropertyAccess;
import org.sakaiproject.warehouse.service.ParentPropertyAccess;
import org.sakaiproject.warehouse.service.*;
import org.sakaiproject.warehouse.service.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:58:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseChildWarehouseTask implements ChildWarehouseTask {

   private List fields;
   private String insertStmt;
   private String clearStmt;
   private List complexFields;
   private int batchSize = 1000;
   private boolean isPrepared = false;
   
   /**
    * This is run after prepare
    */
   public void execute(Object parent, Collection items, Connection connection)
         throws JobExecutionException {
      PreparedStatement ps = null;

      isPrepared = false;
      try {
         int current = 0;
         ps = connection.prepareStatement(getInsertStmt());
         for (Iterator i=items.iterator();i.hasNext();) {
            processItem(parent, i.next(), ps, current);
            ps.addBatch();
            current++;
            if (current > batchSize) {
               current = 0;
               ps.executeBatch();
            }
            ps.clearParameters();
         }
         if (current > 0) {
            ps.executeBatch();
         }
      }
      catch (SQLException e) {
         throw new JobExecutionException(new Exception("query: " + getInsertStmt(), e));
      } catch (NullPointerException e) {
            throw new JobExecutionException(new Exception(
                  "The BaseChildWarehouseTask.execute method parameter items is null. query identifier: " +
                  getInsertStmt(), e));
      }
      finally {
         try {
            ps.close();
         }
         catch (Exception e) {
            // nothing to do here.
         }
      }

   }

   /**
    * This method is run before execute.  It ensures that the prepare functionality is only executed once
    * @param Connection clears the database.
    */
   public void prepare(Connection connection) {
      try {
         if(isPrepared) return;
         
         connection.createStatement().execute(getClearStmt());
         isPrepared = true;

         if (getComplexFields() != null) {
            for (Iterator i=getComplexFields().iterator();i.hasNext();) {
               ChildFieldWrapper wrapper = (ChildFieldWrapper)i.next();
               wrapper.getTask().prepare(connection);
            }
         }
      }
      catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   protected void processItem(Object parent, Object item, PreparedStatement ps, int itemIndex)
         throws JobExecutionException {

      try {
         int index = 1;
         for (Iterator i=getFields().iterator();i.hasNext();) {
            Object o = i.next();
            if (o instanceof PropertyAccess) {
               PropertyAccess pa = (PropertyAccess)o;
               ps.setObject(index, pa.getPropertyValue(item));
            }
            else if (o instanceof ParentPropertyAccess) {
               ParentPropertyAccess pa = (ParentPropertyAccess)o;
               ps.setObject(index, pa.getPropertyValue(parent, item));
            } else if (o instanceof ItemIndexInParentPropertyAccess){
               ps.setInt(index, itemIndex);
            }
            index++;
         }

         // now, lets look for complex fields
         if (getComplexFields() != null) {
            for (Iterator i=getComplexFields().iterator();i.hasNext();) {
               ChildFieldWrapper wrapper = (ChildFieldWrapper)i.next();
               
               Object property = wrapper.getPropertyAccess().getPropertyValue(item);
               Collection items = null;
               
               //If the complex field isn't a Collection then
               // build a collection out of the single class 
               // instance in the complex field
               if(property instanceof Collection) {
                  items = (Collection)property;
               } else {
                  items = new ArrayList();
                  if(property != null)
                     items.add(property);
               }
               
               // item becomes the new parent, items is the complex field (Collection)
               wrapper.getTask().execute(item, items, ps.getConnection());
            }
         }
      }
      catch (Exception e) {
         throw new JobExecutionException("error trying to prepare '" + insertStmt + "'", e, false);
      }
   }

   public List getFields() {
      return fields;
   }

   public void setFields(List fields) {
      this.fields = fields;
   }

   public String getInsertStmt() {
      return insertStmt;
   }

   public void setInsertStmt(String insertStmt) {
      this.insertStmt = insertStmt;
   }

   public List getComplexFields() {
      return complexFields;
   }

   public void setComplexFields(List complexFields) {
      this.complexFields = complexFields;
   }

   public int getBatchSize() {
      return batchSize;
   }

   public void setBatchSize(int batchSize) {
      this.batchSize = batchSize;
   }

   public String getClearStmt() {
      return clearStmt;
   }

   public void setClearStmt(String clearStmt) {
      this.clearStmt = clearStmt;
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/impl/BaseWarehouseTask.java $
* $Id:BaseWarehouseTask.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.sakaiproject.warehouse.util.db.DbLoader;
import org.sakaiproject.warehouse.service.WarehouseTask;
import org.sakaiproject.warehouse.service.ChildWarehouseTask;
import org.sakaiproject.warehouse.service.DataWarehouseManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:51:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseWarehouseTask implements WarehouseTask {

   protected final Log logger = LogFactory.getLog(getClass());

   private DataSource dataSource;
   private ChildWarehouseTask task;
   private String tableDdlResource;
   private DataWarehouseManager dataWarehouseManager;

   public void execute() throws JobExecutionException {
      Connection connection = null;

      try {
         connection = getDataSource().getConnection();
         connection.setAutoCommit(true);
         task.prepare(connection);
         task.execute(null, getItems(), connection);
      }
      catch (SQLException e) {
         throw new JobExecutionException(e);
      }
      catch(JobExecutionException e) {
         logger.warn("Error executing warehousing tasks", e);
         throw e;
      }
      finally {
         if (connection != null) {
            try {
               connection.close();
            }
            catch (Exception e) {
               // can't do anything with this.
            }
         }
      }
   }

   /**
    * This method loads the tables and registers the task.
    * 
    * This function is called after the task bean properties have been set.
    * Children are singletons where there bean init function is this method.
    */
   public void init() {
      logger.info("init()");
      Connection connection = null;
      try {
         if (getDataWarehouseManager().isAutoDdl()) {
            InputStream tableDdl = getTableDdl();
            if (tableDdl != null) {
               connection = getDataSource().getConnection();
               connection.setAutoCommit(true);
               DbLoader loader = new DbLoader(connection);
               loader.runLoader(tableDdl);
            }
         }
         getDataWarehouseManager().registerTask(this);
      }
      catch (SQLException e) {
         throw new RuntimeException(e);
      }
      finally {
         if (connection != null) {
            try {
               connection.close();
            }
            catch (Exception e) {
               // can't do anything with this.
            }
         }
      }
   }

   public InputStream getTableDdl() {
      if (getTableDdlResource() != null) {
         return getClass().getResourceAsStream(getTableDdlResource());
      }
      return null;
   }

   protected abstract Collection getItems();

   public DataSource getDataSource() {
      return dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   public ChildWarehouseTask getTask() {
      return task;
   }

   public void setTask(ChildWarehouseTask task) {
      this.task = task;
   }

   public String getTableDdlResource() {
      return tableDdlResource;
   }

   public void setTableDdlResource(String tableDdlResource) {
      this.tableDdlResource = tableDdlResource;
   }

   public DataWarehouseManager getDataWarehouseManager() {
      return dataWarehouseManager;
   }

   public void setDataWarehouseManager(DataWarehouseManager dataWarehouseManager) {
      this.dataWarehouseManager = dataWarehouseManager;
   }
}

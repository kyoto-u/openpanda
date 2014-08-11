package org.sakaiproject.warehouse.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.warehouse.util.db.DbLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.ArrayList;

import org.quartz.JobExecutionException;

import org.sakaiproject.warehouse.service.ChildWarehouseTask;
import org.sakaiproject.warehouse.service.DataWarehouseManager;
import org.sakaiproject.warehouse.service.WarehouseTask;

/**
 * <p>Alternative to the BaseWarehouseTask.  For really large datasets
 * building up a giant list in memory before writing out to the db results
 * in out of memory issues.  Extending from this task will help.</p>
 *
 * <p>The execute() method simply prepares the db (setup up the table, clears it etc).
 * In your derived class you want to call execute(Object) with each entity as you go.
 * This will batch up a smaller list and then send that to the ChildWarehouseTask
 * which will do the real persistence.  You need to call flush at the end of your work
 * to make sure any remaining objects gets flushed out to the db.</p>
 *
 */
public abstract class CallbackWarehouseTask implements WarehouseTask {

   protected final Log logger = LogFactory.getLog(getClass());

   private DataSource dataSource;
   private ChildWarehouseTask task;
   private String tableDdlResource;
   private DataWarehouseManager dataWarehouseManager;
   private ArrayList items = new ArrayList();
   private int batchsize = 1000;

   public void flush() throws Exception {
       execute(true);
       items.clear();
   }

   public void execute() throws JobExecutionException {
       Connection connection = null;
       try {
          connection = getDataSource().getConnection();
          connection.setAutoCommit(true);
          task.prepare(connection);
       }
       catch (SQLException e) {
          throw new JobExecutionException(e);
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
       process();
   }

   public void execute(Object object) throws Exception {
      items.add(object);
      execute(false);
   }

   protected void execute(boolean flush) throws Exception {
      Connection connection = null;

      if (items.size() > getBatchsize() ||
              (flush == true && items.size() > 0 )) {

          try {
             connection = getDataSource().getConnection();
             connection.setAutoCommit(true);
             task.execute(null, items, connection);
          }
          catch (SQLException e) {
             throw new Exception(e);
          }
          catch(JobExecutionException e) {
             logger.warn("Error executing warehousing tasks", e);
             throw new Exception(e);
          }
          finally {
             items.clear();
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
   }

   /**
    * This method loads the tables and registers the task.
    *
    * This function is called after the task bean properties have been set.
    * Children are singletons where there bean init function is this method.
    */
   public void init() {
       items.clear();
      Connection connection = null;
      try {
         InputStream tableDdl = getTableDdl();
         if (tableDdl != null) {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(true);
            DbLoader loader = new DbLoader(connection);
            loader.runLoader(tableDdl);
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

   protected abstract void process();



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

    public int getBatchsize() {
        return batchsize;
    }

    public void setBatchsize(int batchsize) {
        this.batchsize = batchsize;
    }
}

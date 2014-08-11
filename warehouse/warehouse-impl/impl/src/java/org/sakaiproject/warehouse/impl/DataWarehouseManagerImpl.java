/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/impl/DataWarehouseManagerImpl.java $
* $Id:DataWarehouseManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.warehouse.service.DataWarehouseManager;
import org.sakaiproject.warehouse.service.WarehouseTask;
import org.sakaiproject.metaobj.security.impl.AllowAllSecurityAdvisor;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataWarehouseManagerImpl implements DataWarehouseManager {
   protected final Log logger = LogFactory.getLog(DataWarehouseManagerImpl.class);

   private List tasks;
   private SecurityService securityService;
   private boolean autoDdl = true;

   public void registerTask(WarehouseTask task) {
      getTasks().add(task);
   }

   public void execute(JobExecutionContext jobExecutionContext)
         throws JobExecutionException {

      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());
      for (Iterator i=getTasks().iterator();i.hasNext();) {
         WarehouseTask task = (WarehouseTask)i.next();
         try {
           task.execute();
         } catch (Exception e) {
             logger.error("problem running dw warehouse task:" + e.getMessage(), e);
         }
      }
      getSecurityService().popAdvisor();
   }

   public List getTasks() {
      return tasks;
   }

   public void setTasks(List tasks) {
      this.tasks = tasks;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/list/impl/BaseListGenerator.java $
* $Id:BaseListGenerator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.list.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.ColumnConfig;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 10, 2006
 * Time: 3:15:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseListGenerator implements CustomLinkListGenerator {
   private String listGeneratorId;
   private ListService listService;
   private ListGenerator listGenerator;
   private List columnConfig;
   private IdManager idManager;
   
   protected final Log logger = LogFactory.getLog(getClass());


   public void init()
   {
       listService.register(listGeneratorId, listGenerator);  
   }

    public String getListGeneratorId() {
        return listGeneratorId;
    }

    public void setListGeneratorId(String listGeneratorId) {
        this.listGeneratorId = listGeneratorId;
    }

    public ListService getListService() {
        return listService;
    }

    public void setListService(ListService listService) {
        this.listService = listService;
    }

    public ListGenerator getListGenerator() {
        return listGenerator;
    }

    public void setListGenerator(ListGenerator listGenerator) {
        this.listGenerator = listGenerator;
    }
    
    public List getColumns() {
       List columns = new ArrayList();
       for (Iterator i = getColumnConfig().iterator(); i.hasNext();) {
          ColumnConfig config = (ColumnConfig) i.next();
          columns.add(config.getColumnName());
       }
       return columns;
    }
    
    public List getDefaultColumns() {
       List defaultColumns = new ArrayList();
       for (Iterator i = getColumnConfig().iterator(); i.hasNext();) {
          ColumnConfig config = (ColumnConfig) i.next();
          if (config.isDefaultSelected())
             defaultColumns.add(config.getColumnName());
       }
       return defaultColumns;
    }
    
    public List getSortableColumns() {
       List sortableColumns = new ArrayList();
       for (Iterator i = getColumnConfig().iterator(); i.hasNext();) {
          ColumnConfig config = (ColumnConfig) i.next();
          if (config.isSortable())
             sortableColumns.add(config.getColumnName());
       }
       return sortableColumns;
    }
    
    public String getDefaultSortColumn() {
       String retCol = null;
       for (Iterator i = getColumnConfig().iterator(); i.hasNext();) {
          ColumnConfig config = (ColumnConfig) i.next();
          if (config.isSortable() && config.isDefaultSort()) {
             retCol = config.getColumnName();
             break;
          }
       }
       return retCol;
    }
    
    public List getBundleLookupColumns() {
       List lookupColumns = new ArrayList();
       for (Iterator i = getColumnConfig().iterator(); i.hasNext();) {
          ColumnConfig config = (ColumnConfig) i.next();
          if (config.isLookupInBundle()) {
             lookupColumns.add(config.getColumnName());
          }
       }
       return lookupColumns;
    }
    
   /**
    * @return the columnConfig
    */
   public List getColumnConfig() {
      return columnConfig;
   }

   /**
    * @param columnConfig the columnConfig to set
    */
   public void setColumnConfig(List columnConfig) {
      this.columnConfig = columnConfig;
   }

   /**
    * @return the idManager
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager the idManager to set
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

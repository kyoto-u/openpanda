/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/common/tool-lib/src/java/org/theospi/portfolio/list/tool/ListTool.java $
* $Id: ListTool.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.list.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.list.intf.DecoratedListItem;
import org.theospi.portfolio.list.intf.ListItemUtils;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.Column;
import org.theospi.portfolio.list.model.ListConfig;
import org.theospi.portfolio.shared.tool.ToolBase;

public class ListTool extends ToolBase implements ListItemUtils {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ListService listService;
   private DecoratedEntry currentEntry;

   private ListConfig currentConfig;
   private String sortCol;
   private int sortDir = SORT_ASC;
   
   public static final int SORT_ASC = 1;
   public static final int SORT_DESC = -1;
   public static final String SORT_FIELD = "org.theospi.portfolio.list.sortField";
   public static final String SORT_DIR = "org.theospi.portfolio.list.sortDir";
   public static final int TOTAL_COLUMNS = 10;


   public ListTool() {
      logger.debug("ListTool()");
   }

    public String formatMessage(String key, Object[] args) {
        return getMessageFromBundle(key, args);
    }

    public List getEntries() {
      List entries = getListService().getList();
      List returned = new ArrayList();

      int count = 0;
      for (Iterator i=entries.iterator();i.hasNext();) {
          Object listItem = i.next();
          if (listItem instanceof DecoratedListItem)
          {
               ((DecoratedListItem)listItem).setListItemUtils(this);
          }
          returned.add(new DecoratedEntry(listItem, getListService(), this));
           count++;
         if (getCurrentConfig().getRows() > 0 &&
            count == getCurrentConfig().getRows()) {
            //sort(returned);
            break;
            //return returned;
         }
      }

      sort(returned);
      return returned;
   }
    
   protected void sort(List list) {
      ToolSession session = SessionManager.getCurrentToolSession();
      String sortField = getDefaultSortColumn();
      int sortDir = SORT_ASC;
      try {
         if (session.getAttribute(SORT_FIELD) != null)
            sortField = (String)session.getAttribute(SORT_FIELD);
         
         if (session.getAttribute(SORT_DIR) != null)
            sortDir = ((Integer)session.getAttribute(SORT_DIR)).intValue();
      }
      catch(Exception e) {
         logger.debug("Exception getting sorting details. Use the defaults instead.");
      }
      
      if (sortField != null && !sortField.equals("")) {
         Collections.sort(list, new DecoratedEntryComparator(
               sortField, sortDir));
      }
   }
   
   public boolean isCurrentSortField(String field) {
      ToolSession session = SessionManager.getCurrentToolSession();
      String sortField = getDefaultSortColumn();
      if (session.getAttribute(SORT_FIELD) != null)
         sortField = (String)session.getAttribute(SORT_FIELD);
      
      return field.equals(sortField);
   }
   
   public boolean lookUpInBundle(String field) {
      return getBundleLookupColumns().contains(field);
   }
   
   public int getCurrentSortDir() {
      ToolSession session = SessionManager.getCurrentToolSession();
      int sortDir = SORT_ASC;
      try {
       sortDir = ((Integer)session.getAttribute(SORT_DIR)).intValue();
      }
      catch (Exception e) {
         logger.debug("Exception getting sorting details. Use the defaults instead.");
      }
      return sortDir;
   }

   public List getDisplayColumns() {
      List columns = getListService().getCurrentDisplayColumns();
      List decoratedColumns = new ArrayList(columns.size());
      for (Iterator i = columns.iterator(); i.hasNext();) {
         Column column = (Column)i.next();
         decoratedColumns.add(new DecoratedColumn(column, this));
      }
      return decoratedColumns;
   }
   
   public String getServerUrl() {
      return ServerConfigurationService.getServerUrl();
   }
   
   public List getSelectedColumns() {
      Map selected = getCurrentConfig().getSelected();
      List decoratedColumns = new ArrayList(TOTAL_COLUMNS);
      for (int i=0; i< TOTAL_COLUMNS; i++) {
         Column column = (Column)selected.get(i);
         decoratedColumns.add(new DecoratedColumn(column, this));
      }
      return decoratedColumns;
   }
   
   public List getSortableColumns() {
      return getListService().getSortableColumns();
   }
   
   public List getBundleLookupColumns() {
      return getListService().getBundleLookupColumns();
   }
   
   private String getDefaultSortColumn() {
      return getListService().getDefaultSortColumn();
   }

   public ListService getListService() {
      return listService;
   }

   public void setListService(ListService listService) {
      this.listService = listService;
      setCurrentConfig(getListService().getCurrentConfig());
   }

   public String processActionOptions() {
      setCurrentConfig(getListService().getCurrentConfig());

      return "options";
   }
   
   public String processActionSort(DecoratedColumn column, int dir) {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(SORT_FIELD, column.getBase().getName());
      session.setAttribute(SORT_DIR,dir);

      return "main";
   }

   public String processMain() {
      return "main";
   }

   public String processActionOptionsSave() {
      getListService().saveOptions(getCurrentConfig());
      return "main";
   }

   public DecoratedEntry getCurrentEntry() {
      return currentEntry;
   }

   public void setCurrentEntry(DecoratedEntry currentEntry) {
      this.currentEntry = currentEntry;
   }

   public ListConfig getCurrentConfig() {
      return currentConfig;
   }

   public void setCurrentConfig(ListConfig currentConfig) {
      this.currentConfig = currentConfig;
   }

   /**
    * @return the sortCol
    */
   public String getSortCol() {
      return sortCol;
   }

   /**
    * @param sortCol the sortCol to set
    */
   public void setSortCol(String sortCol) {
      this.sortCol = sortCol;
   }

   /**
    * @return the sortDir
    */
   public int getSortDir() {
      return sortDir;
   }

   /**
    * @param sortDir the sortDir to set
    */
   public void setSortDir(int sortDir) {
      this.sortDir = sortDir;
   }

}

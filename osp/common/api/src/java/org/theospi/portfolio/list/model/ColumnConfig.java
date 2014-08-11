/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/common/api/src/java/org/theospi/portfolio/list/model/ColumnConfig.java $
* $Id: ColumnConfig.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

/**
 * 
 */
package org.theospi.portfolio.list.model;

/**
 * @author chrismaurer
 *
 */
public class ColumnConfig {

   private String columnName;
   private boolean defaultSelected;
   private boolean sortable;
   private boolean defaultSort;
   private int defaultSortDirection;
   private boolean lookupInBundle;
   
   /**
    * @return the columnName
    */
   public String getColumnName() {
      return columnName;
   }
   /**
    * @param columnName the columnName to set
    */
   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }
   /**
    * @return the defaultSelected
    */
   public boolean isDefaultSelected() {
      return defaultSelected;
   }
   /**
    * @param defaultSelected the defaultSelected to set
    */
   public void setDefaultSelected(boolean defaultSelected) {
      this.defaultSelected = defaultSelected;
   }
   /**
    * @return the defaultSort
    */
   public boolean isDefaultSort() {
      return defaultSort;
   }
   /**
    * @param defaultSort the defaultSort to set
    */
   public void setDefaultSort(boolean defaultSort) {
      this.defaultSort = defaultSort;
   }
   /**
    * @return the defaultSortDirection
    */
   public int getDefaultSortDirection() {
      return defaultSortDirection;
   }
   /**
    * @param defaultSortDirection the defaultSortDirection to set
    */
   public void setDefaultSortDirection(int defaultSortDirection) {
      this.defaultSortDirection = defaultSortDirection;
   }
   /**
    * @return the sortable
    */
   public boolean isSortable() {
      return sortable;
   }
   /**
    * @param sortable the sortable to set
    */
   public void setSortable(boolean sortable) {
      this.sortable = sortable;
   }
   /**
    * @return the lookupInBundle
    */
   public boolean isLookupInBundle() {
      return lookupInBundle;
   }
   /**
    * @param lookupInBundle the lookupInBundle to set
    */
   public void setLookupInBundle(boolean lookupInBundle) {
      this.lookupInBundle = lookupInBundle;
   }
   
   
}

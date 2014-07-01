/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/common/tool-lib/src/java/org/theospi/portfolio/list/tool/DecoratedColumn.java $
* $Id: DecoratedColumn.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

/**
 * 
 */
package org.theospi.portfolio.list.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.list.model.Column;

/**
 * @author chrismaurer
 *
 */
public class DecoratedColumn {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private ListTool parent;
   private Column base;
   
   public DecoratedColumn() {}
   
   public DecoratedColumn(Column base, ListTool parent) {
      this.base = base;
      this.parent = parent;
   }
   
   public String processActionSortDesc() {
      return parent.processActionSort(this, ListTool.SORT_DESC);
   }

   public String processActionSortAsc() {
      return parent.processActionSort(this, ListTool.SORT_ASC);
   }
   
   public boolean isSortable() {
      return parent.getSortableColumns().contains(base.getName());
   }
   
   public boolean isCurrentSortField() {
      return parent.isCurrentSortField(base.getName());
   }

   
   /**
    * @return the base
    */
   public Column getBase() {
      return base;
   }
   /**
    * @param base the base to set
    */
   public void setBase(Column base) {
      this.base = base;
   }
   /**
    * @return the parent
    */
   public ListTool getParent() {
      return parent;
   }
   /**
    * @param parent the parent to set
    */
   public void setParent(ListTool parent) {
      this.parent = parent;
   }
   
   
}

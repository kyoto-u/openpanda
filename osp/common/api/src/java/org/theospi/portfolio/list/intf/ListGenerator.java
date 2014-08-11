/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/common/api/src/java/org/theospi/portfolio/list/intf/ListGenerator.java $
* $Id: ListGenerator.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.list.intf;

import java.util.List;
import java.util.Map;

import org.sakaiproject.site.api.ToolConfiguration;

public interface ListGenerator {

   /**
    *
    * @return array of coluimn names (should be bean names)
    */
   public List getColumns();

   /**
    *
    * @return array of columns a user has by default
    */
   public List getDefaultColumns();
   
   /**
    * 
    * @return The list of columns that are sortable
    */
   public List getSortableColumns();
   
   /**
    * 
    * @return The column name that is the default sort
    */
   public String getDefaultSortColumn();
   
   /**
    * 
    * @return The list of columns that need to have values looked up in the message bundle
    */
   public List getBundleLookupColumns();
   
   /**
    * 
    * @return The list of ColumnConfigs that were defined
    */
   public List getColumnConfig();

   /**
    *
    * @return the current user's list of objects
    * (whatever that means to the implentation)
    */
   public List getObjects();

   /**
    *
    * @param entry
    * @return map of params
    */
   public Map getToolParams(Object entry);

   public ToolConfiguration getToolInfo(Map request);

   public boolean isNewWindow(Object entry);

}

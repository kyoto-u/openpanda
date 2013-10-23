/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/common/api/src/java/org/theospi/portfolio/list/intf/ListService.java $
* $Id: ListService.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.list.intf;

import java.util.List;

import org.theospi.portfolio.list.model.ListConfig;

public interface ListService {

   public List getList();

   public String getEntryLink(Object entry);

   public List getCurrentDisplayColumns();
   
   public String getDefaultSortColumn();

   public List getSortableColumns();
   
   public List getBundleLookupColumns();
   
   public ListGenerator getListGenerator(String name);

   public ListConfig getCurrentConfig();

   public void saveOptions(ListConfig currentConfig);

   public boolean isNewWindow(Object entry);

   public void register(String id, ListGenerator listGenerator); 
   
   public List getSiteTypeList();
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/ListScrollIndexerImpl.java $
 * $Id: ListScrollIndexerImpl.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.mvc.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;

public class ListScrollIndexerImpl implements ListScrollIndexer {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private int perPage;

   public List indexList(Map request, Map model, List sourceList) {
      return indexList( request, model, sourceList, false );
   }
   
   /** 
    ** Create sublist from given list of items to allow paging (i.e. scrolling) through a list
    **
    ** @param request (input) request parameters
    ** @param model (output) presentation parameters
    ** @param sourceList list of items
    ** @param hideOnePageScroll if true, do not display scroll buttons if not needed
    **/
   public List indexList(Map request, Map model, List sourceList, boolean hideOnePageScroll) {
      int startingIndex = 0;
      int total = sourceList.size();

      String ensureVisible = (String) request.get(ListScroll.ENSURE_VISIBLE_TAG);

      if (ensureVisible != null) {
         int visibleIndex = Integer.parseInt(ensureVisible);
         int startingPage = (visibleIndex / perPage);
         startingIndex = startingPage * perPage;
      }
      else {
         String newStart = (String) request.get(ListScroll.STARTING_INDEX_TAG);

         if (newStart != null) {
            startingIndex = Integer.parseInt(newStart);
            if (startingIndex < 0) {
               startingIndex = 0;
            }
         }
      }

      if (startingIndex > total) {
         int lastPage = (int) Math.ceil(((double) total) / ((double) perPage));
         lastPage--;
         startingIndex = lastPage * perPage;
      }

      int endingIndex = startingIndex + perPage;

      if (endingIndex > sourceList.size()) {
         endingIndex = sourceList.size();
      }

      model.put("listScroll", new ListScroll(perPage, sourceList.size(), startingIndex, hideOnePageScroll));

      return sourceList.subList(startingIndex, endingIndex);
   }

   public int getPerPage() {
      return perPage;
   }

   public void setPerPage(int perPage) {
      this.perPage = perPage;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/common/tool-lib/src/java/org/theospi/portfolio/list/tool/DecoratedEntry.java $
* $Id: DecoratedEntry.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.list.tool;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.shared.model.OspException;

public class DecoratedEntry {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Object entry;
   private ListService service;
   private ListTool parent;

   public DecoratedEntry(Object entry, ListService service, ListTool parent) {
      this.entry = entry;
      this.service = service;
      this.parent = parent;
   }

   public Object getEntry() {
      return entry;
   }

   public void setEntry(Object entry) {
      this.entry = entry;
   }

   public String getEntryLink() {
      return getService().getEntryLink(getEntry());
   }

   public ListService getService() {
      return service;
   }

   public void setService(ListService service) {
      this.service = service;
   }

   public String getRedirectUrl() {
      String link = getService().getEntryLink(getEntry());

      return link;
   }

   public Map getColumnValues() {
      return new ColumnValuesMap();
   }

   public boolean isNewWindow() {
      return getService().isNewWindow(getEntry());
   }

   private class ColumnValuesMap extends HashMap {

      /**
       * Returns the value associated with the given key
       *
       * @return the value associated with the given key
       */
      public Object get(Object key) {
         int index = Integer.parseInt(key.toString());

         List current = parent.getCurrentConfig().getSelectedColumns();

         if (current.size() <= index) {
            return null;
         }

         String name = (String)current.get(index);

         try {
            return PropertyUtils.getNestedProperty(entry, name);
         } catch (IllegalAccessException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (InvocationTargetException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (NoSuchMethodException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      } //-- get
   }
}

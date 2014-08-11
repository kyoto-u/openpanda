/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/integration/api/src/java/org/theospi/portfolio/admin/model/IntegrationList.java $
* $Id: IntegrationList.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.admin.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.shared.model.OspException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IntegrationList {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String title;
   private String description;
   private List options;

   public IntegrationList() {
   }

   public IntegrationList(List options, String title) {
      this.options = options;
      this.title = title;
   }

   public IntegrationList(IntegrationList copy) {
      this.options = createCopy(copy.options);
      this.title = copy.title;
      this.description = copy.description;
   }

   protected List createCopy(List options) {
      List newList = new ArrayList();

      for (Iterator i=options.iterator();i.hasNext();) {
         try {
            newList.add(((IntegrationOption)i.next()).clone());
         } catch (CloneNotSupportedException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }
      return newList;
   }

   public List getOptions() {
      return options;
   }

   public void setOptions(List options) {
      this.options = options;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}

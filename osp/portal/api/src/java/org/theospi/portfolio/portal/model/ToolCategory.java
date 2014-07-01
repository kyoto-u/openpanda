/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/portal/api/src/java/org/theospi/portfolio/portal/model/ToolCategory.java $
* $Id: ToolCategory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.portal.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.Map;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:24:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategory extends IdentifiableObject implements Comparable, Cloneable {

   public static final String UNCATEGORIZED_KEY = "org.theospi.portfolio.portal.model.ToolCategory.uncategorized";

   public static final ToolCategory UNCATEGORIZED = new ToolCategory(UNCATEGORIZED_KEY);

   private String key;
   private String description;
   private int order;
   private String homePagePath;
   private Map pages;
   private Map tools;

   public ToolCategory() {
      pages = new Hashtable();
   }

   public ToolCategory(ToolCategory copy) {
      this.key = copy.key;
      this.order = copy.order;
      this.homePagePath = copy.homePagePath;
      this.tools = copy.tools;
   }

   protected ToolCategory(String key) {
      this.key = key;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public int compareTo(Object o) {
      Integer order = Integer.valueOf(getOrder());
      Integer other = Integer.valueOf(((ToolCategory)o).getOrder());
      if (other.equals(order) && !getKey().equals(((ToolCategory)o).getKey())) {
         return getKey().equals(UNCATEGORIZED_KEY)?1:-1;
      }
      return order.compareTo(other);
   }

   public Map getTools() {
      return tools;
   }

   public void setTools(Map tools) {
      this.tools = tools;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public String getHomePagePath() {
      return homePagePath;
   }

   public void setHomePagePath(String homePagePath) {
      this.homePagePath = homePagePath;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      final ToolCategory that = (ToolCategory) o;

      if (order != that.order) {
         return false;
      }
      if (key != null ? !key.equals(that.key) : that.key != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result;
      result = (key != null ? key.hashCode() : 0);
      result = 29 * result + order;
      return result;
   }

   public Map getPages() {
      return pages;
   }

   public void setPages(Map pages) {
      this.pages = pages;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
   
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/integration/api-impl/src/java/org/theospi/portfolio/admin/service/ToolWrapper.java $
* $Id: ToolWrapper.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

package org.theospi.portfolio.admin.service;

import org.sakaiproject.tool.api.Tool;

import java.util.Properties;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 4:37:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolWrapper implements Tool {

   private String id;

   public ToolWrapper(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public String getTitle() {
      return null;
   }

   public String getDescription() {
      return null;
   }

   public String getHome() {
	   return null;
   }

   public Properties getRegisteredConfig() {
      return null;
   }

   public Properties getMutableConfig() {
      return null;
   }

   public Properties getFinalConfig() {
      return null;
   }

   public Set getKeywords() {
      return null;
   }

   public Set getCategories() {
      return null;
   }

   public AccessSecurity getAccessSecurity() {
      return null;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/osp/style/StyleWarehouseTask.java $
* $Id: StyleWarehouseTask.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.warehouse.osp.style;

import java.util.Collection;

import org.theospi.portfolio.style.mgt.StyleManager;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class StyleWarehouseTask extends BaseWarehouseTask {

   private StyleManager styleManager;

   protected Collection getItems() {
      Collection styles = getStyleManager().getStylesForWarehouse();
      
      return styles;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }

}

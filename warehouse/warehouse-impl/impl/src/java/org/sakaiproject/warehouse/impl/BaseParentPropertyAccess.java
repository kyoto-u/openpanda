/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-2.9.0/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/impl/BaseParentPropertyAccess.java $
* $Id: BaseParentPropertyAccess.java 59691 2009-04-03 23:46:45Z arwhyte@umich.edu $
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
package org.sakaiproject.warehouse.impl;

import org.sakaiproject.warehouse.service.ParentPropertyAccess;
import org.sakaiproject.warehouse.service.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 2, 2005
 * Time: 4:49:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseParentPropertyAccess implements ParentPropertyAccess {

   private PropertyAccess base = null;

   public Object getPropertyValue(Object parent, Object source) throws Exception {
      return base.getPropertyValue(parent);
   }

   public PropertyAccess getBase() {
      return base;
   }

   public void setBase(PropertyAccess base) {
      this.base = base;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-10.1/warehouse-api/api/src/java/org/sakaiproject/warehouse/service/ParentPropertyAccess.java $
* $Id: ParentPropertyAccess.java 105080 2012-02-24 23:10:31Z ottenhoff@longsight.com $
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
package org.sakaiproject.warehouse.service;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 2, 2005
 * Time: 4:48:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ParentPropertyAccess {

   public Object getPropertyValue(Object parent, Object source) throws Exception;

}

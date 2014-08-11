/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-2.9.1/warehouse-api/api/src/java/org/sakaiproject/warehouse/service/ItemIndexInParentPropertyAccess.java $
* $Id: ItemIndexInParentPropertyAccess.java 59691 2009-04-03 23:46:45Z arwhyte@umich.edu $
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
package org.sakaiproject.warehouse.service;

/**
 * a complex child is dealt with as a List.  This allows the ordering of the list to be captured.
 * When looping through the list of a complex field, the index of each element is passed to the function
 * that processes the single item.  This access puts that index as an implicit property into the database
 * 
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 2, 2005
 * Time: 4:48:55 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ItemIndexInParentPropertyAccess {

}

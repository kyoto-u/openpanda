/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/sakai/resource/ResourcePropertyPropertyAccess.java $
* $Id:ResourcePropertyPropertyAccess.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.sakaiproject.warehouse.sakai.resource;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.warehouse.impl.BeanPropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 11:07:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourcePropertyPropertyAccess extends BeanPropertyAccess {

   public Object getPropertyValue(Object source) throws Exception {
        if (source instanceof ContentResource) {
            ContentResource resource = (ContentResource) source;
            return super.getPropertyValue(resource.getProperties());
        } else if (source instanceof ContentCollection) {
            ContentCollection collection = (ContentCollection) source;
            return super.getPropertyValue(collection.getProperties());
        }
        return null;       
   }

}

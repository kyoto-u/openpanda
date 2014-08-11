/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/sakai/resource/DateResourcePropertyAccess.java $
* $Id:DateResourcePropertyAccess.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.time.api.Time;

import java.sql.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 11:12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateResourcePropertyAccess extends ResourcePropertyPropertyAccess {

   public Object getPropertyValue(Object source) throws Exception {
      String propName = (String) super.getPropertyValue(source);
      Time time = null;
     
       try {
            if (source instanceof ContentResource) {
                time = ((ContentResource)source).getProperties().getTimeProperty(propName);

            }
            else if (source instanceof ContentCollection) {
                time = ((ContentCollection)source).getProperties().getTimeProperty(propName);

            }
            if (time != null){
                return new Date(time.getTime());
            }
       }
       catch (EntityPropertyNotDefinedException e){
            //e.printStackTrace();

   }
       return null;
   }
}

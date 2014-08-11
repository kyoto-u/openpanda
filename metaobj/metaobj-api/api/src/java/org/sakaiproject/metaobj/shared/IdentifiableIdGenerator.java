/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/shared/model/IdentifiableIdGenerator.java $
* $Id:IdentifiableIdGenerator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.metaobj.shared;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.sakaiproject.metaobj.shared.IdGenerator;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 18, 2006
 * Time: 5:38:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentifiableIdGenerator extends IdGenerator {

   public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {

      IdentifiableObject object = (IdentifiableObject) arg1;
      if (object.getNewId() != null) {
         return object.getNewId();
      }
      return super.generate(arg0, arg1);
   }

}

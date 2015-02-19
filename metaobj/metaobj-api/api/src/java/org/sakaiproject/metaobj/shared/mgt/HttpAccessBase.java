/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/HttpAccessBase.java $
 * $Id: HttpAccessBase.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 3:14:16 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HttpAccessBase implements HttpAccess {

   public void handleAccess(HttpServletRequest req, HttpServletResponse res,
                            Reference ref, Collection copyrightAcceptedRefs)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      ReferenceParser parser = createParser(ref);
      checkSource(ref, parser);
      ContentEntityWrapper wrapper = (ContentEntityWrapper) ref.getEntity();
      if (wrapper == null || wrapper.getBase() == null) {
         throw new EntityNotDefinedException(ref.getReference());
      }
      else {
         Reference realRef = EntityManager.newReference(wrapper.getBase().getReference());
         EntityProducer producer = realRef.getEntityProducer();
         producer.getHttpAccess().handleAccess(req, res, realRef, copyrightAcceptedRefs);
      }
   }

   protected ReferenceParser createParser(Reference ref) {
      return new ReferenceParser(ref.getReference(), ref.getEntityProducer());
   }

   protected abstract void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException;

}

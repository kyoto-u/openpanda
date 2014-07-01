/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/common/tool-lib/src/java/org/theospi/portfolio/shared/control/ContentResourceUriResolver.java $
* $Id: ContentResourceUriResolver.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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
package org.theospi.portfolio.shared.control;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.ServerOverloadException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ContentResourceUriResolver implements URIResolver {
   
   private EntityManager entityManager;
   private ServerConfigurationService serverConfigurationService;

   protected final Log logger = LogFactory.getLog(getClass());
   
   public Source resolve(String href, String base) throws TransformerException {
      try {
         String accessUrl = getServerConfigurationService().getAccessUrl();
         String url = href.replaceAll(accessUrl, "");

         // We depend on these resolving as content entities (e.g., /content/group/<site> or /content/user/<user>),
         // so provide some assistance and consistency with metaobj resolution.
         
         // Check if this already resolves as a content entity and, if not, try again by prepending /content/.
         Reference ref = getEntityManager().newReference(url);
         Object entity = ref.getEntity();
         if (entity == null || !(entity instanceof ContentResource)) {
            if (!url.startsWith("/content/")) {
               url = "/content" + (url.startsWith("/") ? "" : "/") + url;
               ref = getEntityManager().newReference(url);
               entity = ref.getEntity();
            }
            if (entity == null || !(entity instanceof ContentResource)) {
               String msg = "Could not resolve URI as a content resource: " + href;
               logger.info(msg);
               throw new TransformerException(msg);
            }
         }
         
         ContentResource res = (ContentResource) entity;
         StreamSource strs = new StreamSource(res.streamContent());
         SAXSource ss = new SAXSource(new InputSource(strs.getInputStream()));
         CatalogResolver resolver = new CatalogResolver();
         String appUrl = getServerConfigurationService().getServerUrl();
         try {
        	 resolver.getCatalog().parseCatalog(appUrl + "/osp-common-tool/dtd/catalog.xml");
        	 XMLReader xread = XMLReaderFactory.createXMLReader();
        	 xread.setEntityResolver(resolver);
        	 ss.setXMLReader(xread);
         } catch (MalformedURLException e) {
        	 logger.error(e);
         } catch (IOException e) {
        	 logger.error(e);
         } catch (SAXException e) {
        	 logger.error(e);
         }
         
         return ss;
      } catch (ServerOverloadException e) {
         logger.error("", e);
      }
      return null;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

}

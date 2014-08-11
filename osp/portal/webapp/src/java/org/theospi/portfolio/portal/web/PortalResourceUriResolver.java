/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/portal/webapp/src/java/org/theospi/portfolio/portal/web/PortalResourceUriResolver.java $
* $Id:PortalResourceUriResolver.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.portal.web;

import org.theospi.portfolio.portal.intf.PortalManager;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 22, 2006
 * Time: 10:08:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortalResourceUriResolver implements URIResolver {

   private ServletContext context;
   private PortalManager manager;

   public PortalResourceUriResolver(PortalManager manager, ServletContext context) {
      this.manager = manager;
      this.context = context;
   }

   public Source resolve(String href, String base) throws TransformerException {
      if (manager.isUseDb()) {
         return new StreamSource(new ByteArrayInputStream(manager.getCategoryPage(href)));
      }

      return new StreamSource(context.getResourceAsStream(href));
   }
}

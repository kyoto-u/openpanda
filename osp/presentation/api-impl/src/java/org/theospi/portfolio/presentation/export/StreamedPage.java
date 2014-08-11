/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/export/StreamedPage.java $
* $Id:StreamedPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import websphinx.Access;
import websphinx.Link;
import websphinx.Page;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class StreamedPage extends Page {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Link link;

   public StreamedPage(Link link) {
      super("Streamed");
      this.link = link;
   }

   /**
    * Get the Link that points to this page.
    *
    * @return the Link object that was used to download this page.
    */
   public Link getOrigin() {
      return link;
   }

   public InputStream getStream() throws IOException {
      URLConnection conn =
          Access.getAccess ().openConnection (link);

      // fetch and store final redirected URL and response headers
      InputStream returned = conn.getInputStream ();

      this.setContentEncoding(conn.getContentEncoding());
      this.setContentType(conn.getContentType());
      this.setExpiration(conn.getExpiration());
      this.setLastModified(conn.getLastModified());

      return returned;
   }

   /**
    * Get the URL.
    *
    * @return the URL of the link that was used to download this page
    */
   public URL getURL() {
       try {
       return new URL(PortfolioMirror.escapeUrl(getOrigin().getURL().toString()));
       }
       catch (IOException e)
       {
           return getOrigin().getURL();
       }
   }
}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/servlet/SakaiComponentFilter.java $
 * $Id: SakaiComponentFilter.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.control.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

public class SakaiComponentFilter implements RequestSetupFilter {
   protected final transient Log logger = LogFactory.getLog(getClass());

   /**
    * This request's parsed parameters
    */
   protected final static String ATTR_PARAMS = "sakai.wrapper.params";

   /**
    * This request's return URL root
    */
   protected final static String ATTR_RETURN_URL = "sakai.wrapper.return.url";

   private MultipartResolver multipartResolver = null;

   protected MultipartResolver getMultipartResolver() {
      return multipartResolver;
   }

   public void setMultipartResolver(MultipartResolver multipartResolver) {
      this.multipartResolver = multipartResolver;
   }

   public boolean processRequest(HttpServletRequest request) {
//      if ("Title".equals(request.getParameter("panel"))) {
//         return false;
//      }
      return true;
   }

   public HttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse response) throws Exception {
      if (getMultipartResolver().isMultipart(req)) {
         req = getMultipartResolver().resolveMultipart(req);
      }

//    TODO: fix wrapRequest method  
      logger.warn("Bad things could happen here, as this is unsupported: CurrentService.startThread(\"REQUEST\")");
      //CurrentService.startThread("REQUEST");


      //if (Setup.setup(req, (HttpServletResponse) response /*, false*/)) {
      //   return null;
      //}

      return req;
   }

   public void tearDown(HttpServletRequest req) {
      //CurrentService.clearInThread();
      logger.error("CurrentService.clearInThread is not supported");
      if (req instanceof MultipartHttpServletRequest) {
         getMultipartResolver().cleanupMultipart((MultipartHttpServletRequest) req);
      }
   }


}

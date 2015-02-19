/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/servlet/FileDownloadServlet.java $
 * $Id: FileDownloadServlet.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.sakaiproject.metaobj.shared.control.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.DownloadableManager;

public class FileDownloadServlet extends HttpServlet {
   protected final Log logger = LogFactory.getLog(getClass());

   public final static String REPOSITORY_PREFIX = "repository";
   private static final String MANAGER_NAME = "manager";

   /**
    * Called by the server (via the <code>service</code> method) to
    * allow a servlet to handle a GET request.
    * <p/>
    * <p>Overriding this method to support a GET request also
    * automatically supports an HTTP HEAD request. A HEAD
    * request is a GET request that returns no body in the
    * response, only the request header fields.
    * <p/>
    * <p>When overriding this method, read the request data,
    * write the response headers, get the response's writer or
    * output stream object, and finally, write the response data.
    * It's best to include content type and encoding. When using
    * a <code>PrintWriter</code> object to return the response,
    * set the content type before accessing the
    * <code>PrintWriter</code> object.
    * <p/>
    * <p>The servlet container must write the headers before
    * committing the response, because in HTTP the headers must be sent
    * before the response body.
    * <p/>
    * <p>Where possible, set the Content-Length header (with the
    * {@link javax.servlet.ServletResponse#setContentLength} method),
    * to allow the servlet container to use a persistent connection
    * to return its response to the client, improving performance.
    * The content length is automatically set if the entire response fits
    * inside the response buffer.
    * <p/>
    * <p>When using HTTP 1.1 chunked encoding (which means that the response
    * has a Transfer-Encoding header), do not set the Content-Length header.
    * <p/>
    * <p>The GET method should be safe, that is, without
    * any side effects for which users are held responsible.
    * For example, most form queries have no side effects.
    * If a client request is intended to change stored data,
    * the request should use some other HTTP method.
    * <p/>
    * <p>The GET method should also be idempotent, meaning
    * that it can be safely repeated. Sometimes making a
    * method safe also makes it idempotent. For example,
    * repeating queries is both safe and idempotent, but
    * buying a product online or modifying data is neither
    * safe nor idempotent.
    * <p/>
    * <p>If the request is incorrectly formatted, <code>doGet</code>
    * returns an HTTP "Bad Request" message.
    *
    * @param request  an {@link javax.servlet.http.HttpServletRequest} object that
    *                 contains the request the client has made
    *                 of the servlet
    * @param response an {@link javax.servlet.http.HttpServletResponse} object that
    *                 contains the response the servlet sends
    *                 to the client
    * @throws java.io.IOException            if an input or output error is
    *                                        detected when the servlet handles
    *                                        the GET request
    * @throws javax.servlet.ServletException if the request for the GET
    *                                        could not be handled
    * @see javax.servlet.ServletResponse#setContentType
    */
   protected void doGet(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
      java.util.Enumeration tokenizer = new StringTokenizer(request.getRequestURI(), "/");

      if (!tokenizer.hasMoreElements()) {
         throw new ServletException("Incorrect format url.");
      }
      String base = (String) tokenizer.nextElement(); // burn off the first element of the path

      while (!base.equalsIgnoreCase(REPOSITORY_PREFIX)) {
         if (!tokenizer.hasMoreElements()) {
            throw new ServletException("Incorrect format url.");
         }
         base = (String) tokenizer.nextElement();
      }

      Hashtable params = HttpUtils.parseQueryString(getNextToken(tokenizer));

      DownloadableManager manager = getDownloadableManager(((String[]) params.get(MANAGER_NAME))[0]);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      String filename = manager.packageForDownload(params, bos);
      
      response.setHeader("Content-Type", "application/octet-stream");
      response.setHeader("Content-Disposition", "attachment"
              + ((filename != null && !filename.equals("")) ? ";filename=\"" + filename + "\"" : ""));
      response.setHeader("Content-Length", Integer.toString(bos.size()));
      
      copyStream(new ByteArrayInputStream(bos.toByteArray()), response.getOutputStream());            
      

   }
   private void copyStream(InputStream in, OutputStream out) throws IOException
   {
       byte data[] = new byte[1024*10];
       
       BufferedInputStream origin = new BufferedInputStream(in, data.length);
       
       int count;
       while ((count = origin.read(data, 0, data.length)) != -1) {
           out.write(data, 0, count);
       }
       in.close();
       out.flush();
   }
   
   protected DownloadableManager getDownloadableManager(String name) {
      return (DownloadableManager) ComponentManager.get(name);
   }

   protected String getNextToken(Enumeration tokenizer) throws ServletException {
      if (!tokenizer.hasMoreElements()) {
         throw new ServletException("Incorrect format url.");
      }
      return (String) tokenizer.nextElement();
   }

}

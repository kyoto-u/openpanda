/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/servlet/SakaiComponentDispatchServlet.java $
 * $Id: SakaiComponentDispatchServlet.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdImpl;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.web.servlet.DispatcherServlet;

public class SakaiComponentDispatchServlet extends DispatcherServlet {


   private class SimpleAgent2 implements Agent {

      String uid = "";
      String eid = "";

      SimpleAgent2(String eid, String uid) {
         this.eid = eid;
         this.uid = uid;
      }

      public Id getId() {
         return new IdImpl(uid, null);
      }
      
      public Id getEid() {
         return new IdImpl(eid, null);
      }

      public Artifact getProfile() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Object getProperty(String key) {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getDisplayName() {
         return this.uid;
      }

      public boolean isInRole(String role) {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isInitialized() {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRole() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public List getWorksiteRoles(String worksiteId) {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public List getWorksiteRoles() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRole() {
         return false;
      }

      public String getName() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }
		
      public String getPassword() {
         return null; // not implemented
      }
   }


   protected final transient Log logger = LogFactory.getLog(getClass());
   public static final String TOOL_STATE_VIEW_KEY = "osp.tool.state.view";
   public static final String TOOL_STATE_VIEW_REQUEST_PARAMS_KEY = "osp.tool.state.request.params";

   /**
    * Obtain and use the handler for this method.
    * The handler will be obtained by applying the servlet's HandlerMappings in order.
    * The HandlerAdapter will be obtained by querying the servlet's
    * installed HandlerAdapters to find the first that supports the handler class.
    * Both doGet() and doPost() are handled by this method.
    * It's up to HandlerAdapters to decide which methods are acceptable.
    */
   protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      // This class has been removed from all places where it was used and replaced by the Spring
      // dispatcher from which it inherits. Delegate to super for now in case this ever gets called.
      // There is one place that depends on the tool constants above, in CommentListGenerator.
      // These constants should be relocated and this class purged.
      super.doService(req, resp);
   }


   /**
    * Called by the servlet container to indicate to a servlet that the
    * servlet is being placed into service.  See {@link javax.servlet.Servlet#init}.
    * <p/>
    * <p>This implementation stores the {@link javax.servlet.ServletConfig}
    * object it receives from the servlet container for later use.
    * When overriding this form of the method, call
    * <code>super.init(config)</code>.
    *
    * @param config the <code>ServletConfig</code> object
    *               that contains configutation
    *               information for this servlet
    * @throws javax.servlet.ServletException if an exception occurs that
    *                                        interrupts the servlet's normal
    *                                        operation
    * @see javax.servlet.UnavailableException
    */

   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }

   protected RequestSetupFilter getFilter() {
      return (RequestSetupFilter) ComponentManager.getInstance().get(RequestSetupFilter.class.getName());
   }

}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/servlet/ViewControllerImpl.java $
 * $Id: ViewControllerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.mvc.impl.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.utils.mvc.impl.ControllerFilterManager;
import org.sakaiproject.metaobj.utils.mvc.impl.HttpServletHelper;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 30, 2004
 * Time: 11:14:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class ViewControllerImpl extends AbstractCommandController {

   private Controller controller = null;
   private Map screenMappings = null;
   private String homeName = null;
   private List customTypedEditors = new ArrayList();

   protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                 Object command, BindException errors) throws Exception {

      Map request = HttpServletHelper.getInstance().createRequestMap(httpServletRequest);
      Map session = HttpServletHelper.getInstance().createSessionMap(httpServletRequest);
      Map application = HttpServletHelper.getInstance().createApplicationMap(httpServletRequest);

      if (controller instanceof CustomCommandController) {
         command = ((CustomCommandController) controller).formBackingObject(request, session, application);
      }

      if (command != null) {
         ServletRequestDataBinder binder = createBinder(httpServletRequest, command);
         binder.bind(httpServletRequest);
      }

      if (controller instanceof LoadObjectController) {
         command = ((LoadObjectController) controller).fillBackingObject(command, request, session, application);
      }

      ModelAndView returnedMv = controller.handleRequest(command, request, session, application, errors);

      if (returnedMv.getViewName() != null) {
         // should get from mappings
         String mappedView = (String) screenMappings.get(returnedMv.getViewName());

         if (mappedView == null) {
            mappedView = returnedMv.getViewName();
         }
         /*
          if (controller instanceof ContextAwareController){
             ((ContextAwareController) controller).addContexts(getHelpManager().getActiveContexts(session), returnedMv.getModel(), mappedView);
          } else {
             getHelpManager().addContexts(session, mappedView);
          }
          getControllerFilterManager().processFilters(request, session, application, returnedMv, mappedView);
          */

         returnedMv = new ModelAndView(mappedView, returnedMv.getModel());
      }

      HttpServletHelper.getInstance().reloadApplicationMap(httpServletRequest, application);
      HttpServletHelper.getInstance().reloadSessionMap(httpServletRequest, session);
      HttpServletHelper.getInstance().reloadRequestMap(httpServletRequest, request);

      return returnedMv;
   }

   protected ControllerFilterManager getControllerFilterManager() {
      return (ControllerFilterManager) ComponentManager.getInstance().get("controllerFilterManager");
   }


   protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
      ServletRequestDataBinder binder = null;
      binder = new ServletRequestBeanDataBinder(command, getCommandName());
      initBinder(request, binder);
      return binder;
   }

   /**
    * Set up a custom property editor for the application's date format.
    */
   protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      for (Iterator i = getCustomTypedEditors().iterator(); i.hasNext();) {
         TypedPropertyEditor editor = (TypedPropertyEditor) i.next();
         binder.registerCustomEditor(editor.getType(), editor);
      }
   }


   protected Object getCommand(HttpServletRequest request) throws Exception {
      Object lightObject = null;

      if (controller instanceof CustomCommandController) {
         lightObject = ((CustomCommandController) controller).formBackingObject(HttpServletHelper.getInstance().createRequestMap(request),

               HttpServletHelper.getInstance().createSessionMap(request),
               HttpServletHelper.getInstance().createApplicationMap(request));
         return lightObject;
      }

      if (super.getCommandClass() == null) {
         return new Object();
      }

      return super.getCommand(request);
   }

   public Controller getController() {
      return controller;
   }

   public void setController(Controller controller) {
      this.controller = controller;
   }

   public Map getScreenMappings() {
      return screenMappings;
   }

   public void setScreenMappings(Map screenMappings) {
      this.screenMappings = screenMappings;
   }

   public String getHomeName() {
      return homeName;
   }

   public void setHomeName(String homeName) {
      this.homeName = homeName;
   }

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

}

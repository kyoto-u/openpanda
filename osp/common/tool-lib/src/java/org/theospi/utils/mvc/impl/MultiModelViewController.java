/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/MultiModelViewController.java $
* $Id:MultiModelViewController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.utils.mvc.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class MultiModelViewController implements LoadObjectController, CustomCommandController {
   protected final Log logger = LogFactory.getLog(getClass());

   private List controllers = null;


   public Object formBackingObject(Map request, Map session, Map application) {
      List currentList = new ArrayList();

      for (Iterator i=controllers.iterator();i.hasNext();) {
         Controller controller = (Controller)i.next();
         ControllerWrapper wrapper = new ControllerWrapper();
         wrapper.controller = controller;
         if (controller instanceof CustomCommandController){
            wrapper.currentObject = ((CustomCommandController)controller).formBackingObject(request, session, application);
         }
         currentList.add(wrapper);
      }

      return currentList;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {

      List currentList = (List)incomingModel;

      for (Iterator i=currentList.iterator();i.hasNext();) {
         ControllerWrapper controller = (ControllerWrapper)i.next();

         if (controller instanceof LoadObjectController){
            controller.currentObject = ((LoadObjectController)controller.controller).fillBackingObject(
               controller.currentObject, request, session, application);
         }
      }

      return currentList;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      List currentList = (List)requestModel;
      Hashtable globalMap = new Hashtable();

      for (Iterator i=currentList.iterator();i.hasNext();) {
         ControllerWrapper controller = (ControllerWrapper)i.next();
         ModelAndView controllerMv = controller.controller.handleRequest(
            controller.currentObject, request, session, application, errors);
         globalMap.putAll(controllerMv.getModel());
      }

      return new ModelAndView("success", globalMap);
   }

   private class ControllerWrapper {
      public Controller controller;
      public Object currentObject;
   }

   public List getControllers() {
      return controllers;
   }

   public void setControllers(List controllers) {
      this.controllers = controllers;
   }

}

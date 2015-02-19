/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/servlet/FormControllerImpl.java $
 * $Id: FormControllerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008, 2009 The Sakai Foundation
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.utils.mvc.impl.ControllerFilterManager;
import org.sakaiproject.metaobj.utils.mvc.impl.HttpServletHelper;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 25, 2004
 * Time: 3:45:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormControllerImpl extends SimpleFormController {

   private Controller controller = null;
   private Map screenMappings = null;
   private ServletRequestDataBinder servletRequestMapDataBinder = null;
   private String homeName;
   private List customTypedEditors = new ArrayList();
   private String formMethod;
   private String[] requiredFields = null;
   private Collection filters;

   //Constant for property enabling save attempt/success cookies (SAK-15911)
   protected static final String PROP_SAVE_COOKIES = "metaobj.save.cookies";

   protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                   Object command, BindException errors) throws Exception {

      Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
      Map session = HttpServletHelper.getInstance().createSessionMap(request);
      Map application = HttpServletHelper.getInstance().createApplicationMap(request);

      ModelAndView returnedMv;

      if (controller instanceof CancelableController &&
            ((CancelableController) controller).isCancel(requestMap)) {
         returnedMv = ((CancelableController) controller).processCancel(requestMap, session,
               application, command, errors);
      }
      else {
         returnedMv = controller.handleRequest(command, requestMap, session, application, errors);
      }

      boolean saveCookies = ServerConfigurationService.getBoolean(PROP_SAVE_COOKIES, false);

      if (errors.hasErrors()) {
         logger.debug("Form submission errors: " + errors.getErrorCount());
         HttpServletHelper.getInstance().reloadApplicationMap(request, application);
         HttpServletHelper.getInstance().reloadSessionMap(request, session);
         HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
         if (saveCookies) {
            Cookie cookie = new Cookie(FormHelper.FORM_SAVE_ATTEMPT, "yes");
            cookie.setMaxAge(30);
            cookie.setPath("/");
            response.addCookie(cookie);
         }
         return showForm(request, response, errors);
      }

      if (returnedMv.getViewName() != null) {
         // should get from mappings
         String mappedView = (String) screenMappings.get(returnedMv.getViewName());

         if (mappedView == null) {
            mappedView = returnedMv.getViewName();
         }

         //getControllerFilterManager().processFilters(requestMap, session, application, returnedMv, mappedView);

         returnedMv = new ModelAndView(mappedView, returnedMv.getModel());
      }

      //We have a successful save coming back, so we set/append to a cookie
      String savedForm = (String) session.get(FormHelper.FORM_SAVE_SUCCESS);
      if (savedForm != null && saveCookies) {
         Cookie cookie = null;
         if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
               if (FormHelper.FORM_SAVE_SUCCESS.equals(c.getName())) {
                  String[] forms = c.getValue().split(",");
                  StringBuilder value = new StringBuilder();
                  boolean alreadyIncluded = false;
                  for (String form : forms) {
                     if (form.equals(savedForm)) {
                        alreadyIncluded = true;
                     }
                     value.append(",").append(form);
                  }
                  if (!alreadyIncluded) {
                     value.append(",").append(savedForm);
                  }
                  cookie = new Cookie(FormHelper.FORM_SAVE_SUCCESS, value.substring(1));
               }
            }
         }
         if (cookie == null) {
            cookie = new Cookie(FormHelper.FORM_SAVE_SUCCESS, savedForm);
         }
         cookie.setMaxAge(2000000);
         cookie.setPath("/");
         response.addCookie(cookie);
      }

      HttpServletHelper.getInstance().reloadApplicationMap(request, application);
      HttpServletHelper.getInstance().reloadSessionMap(request, session);
      HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);

      return returnedMv;
   }

   protected Map referenceData(HttpServletRequest request, Object command, Errors errors) {
      if (getController() instanceof FormController) {
         Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
         Map referenceData = ((FormController) getController()).referenceData(requestMap, command, errors);
         HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
         return referenceData;
      }
      return null;
   }

   protected boolean isFormSubmission(HttpServletRequest request) {
      if (getFormMethod() != null &&
            getFormMethod().equalsIgnoreCase("get") &&
            request.getMethod().equalsIgnoreCase("get")) {
         return true;
      }
      if (getFormMethod() != null &&
            getFormMethod().equalsIgnoreCase("post") &&
            request.getMethod().equalsIgnoreCase("post")) {
         return true;
      }
      return super.isFormSubmission(request);
   }

   protected Object formBackingObject(HttpServletRequest request) throws Exception {
      Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
      Map session = HttpServletHelper.getInstance().createSessionMap(request);
      Map application = HttpServletHelper.getInstance().createApplicationMap(request);

      Object lightObject = null;

      if (controller instanceof CustomCommandController) {
         lightObject = ((CustomCommandController) controller).formBackingObject(requestMap, session, application);
      }
      else {
         lightObject = super.formBackingObject(request);
      }

      Object returned = lightObject;

      if (controller instanceof LoadObjectController) {
         // need to bind variables to fill in lightweight object
         // then pass object to real control to fill in
         // this will get the info from the backing store
         ServletRequestDataBinder binder = createBinder(request, lightObject);
         binder.bind(request);

         returned = ((LoadObjectController) controller).fillBackingObject(lightObject, requestMap, session, application);
      }

      /*
   if (controller instanceof ContextAwareController){
      ((ContextAwareController) controller).addContexts(getHelpManager().getActiveContexts(session), requestMap, getFormView());
   } else {
      getHelpManager().addContexts(session, getFormView());
   }
        */
      //getControllerFilterManager().processFilters(requestMap, session, application, null, getFormView());

      HttpServletHelper.getInstance().reloadApplicationMap(request, application);
      HttpServletHelper.getInstance().reloadSessionMap(request, session);
      HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
      return returned;
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

      if (getRequiredFields() != null) {
         binder.setRequiredFields(getRequiredFields());
      }
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

   public ServletRequestDataBinder getServletRequestMapDataBinder() {
      return servletRequestMapDataBinder;
   }

   public void setServletRequestMapDataBinder(ServletRequestDataBinder servletRequestMapDataBinder) {
      this.servletRequestMapDataBinder = servletRequestMapDataBinder;
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

   public String getFormMethod() {
      return formMethod;
   }

   public void setFormMethod(String formMethod) {
      this.formMethod = formMethod;
   }

   public String[] getRequiredFields() {
      return requiredFields;
   }

   public void setRequiredFields(String[] requiredFields) {
      this.requiredFields = requiredFields;
   }

   protected ControllerFilterManager getControllerFilterManager() {
      return (ControllerFilterManager) ComponentManager.getInstance().get("controllerFilterManager");
   }
}

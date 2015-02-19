/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/ControllerFilter.java $
 * $Id: ControllerFilter.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

/**
 * implement this interface if you have work that needs to be done after a controller.
 * In the case of forms this method is called after formBackingObject() is called or after
 * handleRequest is called.  For non forms it is simply called after handleRequest.
 * <p/>
 * Note implementations of this interface are invoked for each request that goes through a controller, so only
 * use this for things that can't be handled directly in the controller.
 */
public interface ControllerFilter {
   /**
    * this method is called after the handleRequest method of the controller is called
    *
    * @param request
    * @param session
    * @param application
    * @param modelAndView
    * @param screenMapping
    */
   public void doFilter(Map request,
                        Map session,
                        Map application,
                        ModelAndView modelAndView,
                        String screenMapping);
}

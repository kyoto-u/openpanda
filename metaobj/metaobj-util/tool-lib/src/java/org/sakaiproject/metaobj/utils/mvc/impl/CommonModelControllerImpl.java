/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/impl/CommonModelControllerImpl.java $
 * $Id: CommonModelControllerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.mvc.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.metaobj.utils.mvc.intf.CommonModelController;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 30, 2004
 * Time: 8:56:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommonModelControllerImpl implements CommonModelController {


   public void fillModel(Map model, HttpServletRequest request, HttpServletResponse response) {
      // add items to map for common components (ie. left menu, etc...)

   }
}

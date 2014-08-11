/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/ValidatorBase.java $
* $Id:ValidatorBase.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Stack;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 24, 2004
 * Time: 3:27:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ValidatorBase implements Validator {

   private Stack nestedPathStack = new Stack();

   protected void pushNestedPath(String newPath, Errors errors) {
      String currentPath = errors.getNestedPath();

      nestedPathStack.push(currentPath);

      errors.setNestedPath(currentPath + newPath);
   }

   protected void popNestedPath(Errors errors) {
      String newPath = (String) nestedPathStack.pop();
      errors.setNestedPath(newPath);
   }
}

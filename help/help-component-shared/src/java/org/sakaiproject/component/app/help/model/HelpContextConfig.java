/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.4/help-component-shared/src/java/org/sakaiproject/component/app/help/model/HelpContextConfig.java $
 * $Id: HelpContextConfig.java 106357 2012-03-28 23:18:54Z matthew.buckett@oucs.ox.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.component.app.help.model;

import java.util.HashMap;
import java.util.Map;

/**
 * help context configuration
 * @version $Id: HelpContextConfig.java 106357 2012-03-28 23:18:54Z matthew.buckett@oucs.ox.ac.uk $
 */
public class HelpContextConfig extends HashMap
{
  private static final long serialVersionUID = 1L;

/**
   * overloaded constructor
   * @param map
   */
  public HelpContextConfig(Map map)
  {
    super(map);
  }
}



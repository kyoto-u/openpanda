/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.2/help-tool/src/java/org/sakaiproject/jsf/help/HelpSetDefaultActionTag.java $
 * $Id: HelpSetDefaultActionTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.jsf.help;

import javax.faces.webapp.UIComponentTag;

/**
 * help set default action tag
 * @version $Id: HelpSetDefaultActionTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 */
public class HelpSetDefaultActionTag extends UIComponentTag
{
  /** 
   * @see javax.faces.webapp.UIComponentTag#getComponentType()
   */
  public String getComponentType()
  {
    return "SetDefaultAction";
  }

  /** 
   * @see javax.faces.webapp.UIComponentTag#getRendererType()
   */
  public String getRendererType()
  {
    return null;
  }

}
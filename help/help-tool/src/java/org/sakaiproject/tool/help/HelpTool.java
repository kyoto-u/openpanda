/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.4/help-tool/src/java/org/sakaiproject/tool/help/HelpTool.java $
 * $Id: HelpTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.tool.help;

import org.sakaiproject.api.app.help.HelpManager;
import org.sakaiproject.api.app.help.Resource;

/**
 * help tool
 * @version $Id: HelpTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 */
public class HelpTool
{
  private HelpManager helpManager;
  private Resource resource;
  private String docId;

  /**
   * get help doc id
   * @return Returns the helpDocId.
   */
  public String getHelpDocId()
  {
    return docId;
  }

  /**
   * set help doc id
   * @param docId The docId to set.
   */
  public void setDocId(String helpDocId)
  {
    this.docId = helpDocId;
  }

  /**
   * set resource
   * @param resource The resource to set.
   */
  public void setResource(Resource resource)
  {
    this.resource = resource;
  }

  /**
   * get help manager
   * @return Returns the helpManager.
   */
  public HelpManager getHelpManager()
  {
    return helpManager;
  }

  /**
   * set help manager
   * @param helpManager The helpManager to set.
   */
  public void setHelpManager(HelpManager helpManager)
  {
    this.helpManager = helpManager;
  }
}
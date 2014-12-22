/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.3/help-tool/src/java/org/sakaiproject/tool/help/TOCDisplayTool.java $
 * $Id: TOCDisplayTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.sakaiproject.api.app.help.TableOfContents;

/**
 * toc display tool
 * @version $Id: TOCDisplayTool.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 */
public class TOCDisplayTool
{
  private TableOfContents tableOfContents;

  /**
   * get table of contents
   * @return Returns the tableOfContents.
   */
  public TableOfContents getTableOfContents()
  {
    if (tableOfContents == null)
    {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      VariableResolver resolver = facesContext.getApplication()
          .getVariableResolver();
      tableOfContents = ((TableOfContentsTool) resolver.resolveVariable(
          facesContext, "TableOfContentsTool")).getTableOfContents();
    }
    return tableOfContents;
  }

}
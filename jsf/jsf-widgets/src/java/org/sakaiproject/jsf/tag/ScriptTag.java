/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/jsf/tags/sakai-10.1/jsf-widgets/src/java/org/sakaiproject/jsf/tag/ScriptTag.java $
* $Id: ScriptTag.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
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


package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import org.sakaiproject.jsf.util.TagUtil;

/**
 * <p> </p>
 * <p>Description:<br />
 * This class is the tag handler that evaluates the <code>script</code>
 * custom tag.</p>
 * <p>Based on example code by Sun Microsystems. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: ScriptTag.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
 */

public class ScriptTag  extends UIComponentTag
{

  private String path = null;
  private String contextBase;

  public void setPath(String path)
  {
    this.path = path;
  }


  public String getComponentType()
  {
    return ("javax.faces.Output");
  }

  public String getRendererType()
  {
    return "org.sakaiproject.Script";
  }

  protected void setProperties(UIComponent component)
  {

    super.setProperties(component);
    TagUtil.setString(component, "path", path);
    TagUtil.setString(component, "contextBase", contextBase);
  }
  public void setContextBase(String contextBase)
  {
    this.contextBase = contextBase;
  }

}

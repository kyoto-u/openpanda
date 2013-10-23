/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-2.9.3/help-component-shared/src/java/org/sakaiproject/component/app/help/model/ContextBean.java $
 * $Id: ContextBean.java 110562 2012-07-19 23:00:20Z ottenhoff@longsight.com $
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

import org.sakaiproject.api.app.help.Context;

/**
 * context bean
 * @version $Id: ContextBean.java 110562 2012-07-19 23:00:20Z ottenhoff@longsight.com $
 */
public class ContextBean implements Context
{
  private Long id;
  private String name;

  /**
   * get id
   * @return Returns the id.
   */
  public Long getId()
  {
    return id;
  }

  /**
   * set id
   * @param id The id to set.
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @see org.sakaiproject.api.app.help.Context#getName()
   */
  public String getName()
  {
    return name;
  }

  /**
   * @see org.sakaiproject.api.app.help.Context#setName(java.lang.String)
   */
  public void setName(String name)
  {
    this.name = name;
  }
}



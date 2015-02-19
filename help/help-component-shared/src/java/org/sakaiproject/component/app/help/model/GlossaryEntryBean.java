/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.4/help-component-shared/src/java/org/sakaiproject/component/app/help/model/GlossaryEntryBean.java $
 * $Id: GlossaryEntryBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.sakaiproject.api.app.help.GlossaryEntry;

/**
 * glossary entry bean
 * @version $Id: GlossaryEntryBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $ 
 */
public class GlossaryEntryBean implements GlossaryEntry
{
  private String term;
  private String description;

  /**
   * overloaded constructor
   * @param term
   * @param description
   */
  public GlossaryEntryBean(String term, String description)
  {
    this.term = term;
    this.description = description;
  }

  /**
   * @see org.sakaiproject.api.app.help.GlossaryEntry#getDescription()
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @see org.sakaiproject.api.app.help.GlossaryEntry#getTerm()
   */
  public String getTerm()
  {
    return term;
  }

  /**
   * @see org.sakaiproject.api.app.help.GlossaryEntry#setDescription(java.lang.String)
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @see org.sakaiproject.api.app.help.GlossaryEntry#setTerm(java.lang.String)
   */
  public void setTerm(String term)
  {
    this.term = term;
  }
}



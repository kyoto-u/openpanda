/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/help/tags/sakai-10.0/help-api/src/java/org/sakaiproject/api/app/help/Glossary.java $
 * $Id: Glossary.java 106357 2012-03-28 23:18:54Z matthew.buckett@oucs.ox.ac.uk $
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

package org.sakaiproject.api.app.help;

import java.util.Collection;

/**
 * @version $Id: Glossary.java 106357 2012-03-28 23:18:54Z matthew.buckett@oucs.ox.ac.uk $ 
 */
public interface Glossary
{
  /**
   * find the keyword in the glossary.
   * return null if not found.
   * @param keyword
   * @return a GlossaryEntry
   */
  public GlossaryEntry find(String keyword);

  /**
   * returns the list of all GlossaryEntries
   * @return a Collection
   */
  public Collection<GlossaryEntry> findAll();

  /**
   * url to glossary web page
   * @return the Url
   */
  public String getUrl();
}



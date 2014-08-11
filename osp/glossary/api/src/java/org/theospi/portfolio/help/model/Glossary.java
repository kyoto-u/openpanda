/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/api/src/java/org/theospi/portfolio/help/model/Glossary.java $
* $Id:Glossary.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.help.model;

import java.util.Collection;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;

public interface Glossary extends DuplicatableToolService {
   public GlossaryEntry load(Id id);
           
   /**
    * find the keyword in the glossary.
    * return null if not found.
    * @param keyword
    * @return
    */
   public GlossaryEntry find(String keyword, String worksite);
   /**
    * returns the list of all GlossaryEntries
    * @return
    */
   public Collection findAll(String keyword, String worksite);

   public Collection findAll(String worksite);

   public Collection findAll();

   public Collection findAllGlobal();

   /**
    * url to glossary web page
    * @return
    */
   public String getUrl();

   public GlossaryEntry addEntry(GlossaryEntry newEntry);

   public void removeEntry(GlossaryEntry entry);

   public void updateEntry(GlossaryEntry entry);

   public boolean isPhraseStart(String phraseFragment, String worksite);

   public Set getSortedWorksiteTerms(String siteId);

   public void checkCache();
}

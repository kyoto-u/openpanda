/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/api/src/java/org/theospi/portfolio/help/model/HelpManager.java $
* $Id:HelpManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.jdom.JDOMException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.shared.model.Node;

/**
 * Responsible for managing help activities.  This includes
 * managing contexts, resources, the glossary, and the
 * table of contents.
 *
 * @see org.theospi.portfolio.help.model.Glossary
 */
public interface HelpManager {

   /**
    * searches the glossary for the keyword.
    * Returns a GlossaryEntry for this keyword if found,
    * return null if no entry is found.
    * @param keyword
    * @return
    */
   public GlossaryEntry searchGlossary(String keyword);

   public boolean isPhraseStart(String phraseFragment);

   public Glossary getGlossary();

   public GlossaryEntry addEntry(GlossaryEntry newEntry);

   public void removeEntry(GlossaryEntry entry);

   public void updateEntry(GlossaryEntry entry);

   public Collection getWorksiteTerms();

   public boolean isMaintainer();

   public boolean isGlobal();
   
   public void removeFromSession(Object obj);

   public Set getSortedWorksiteTerms();
   
   public Node getNode(Id artifactId);
   
   public void importTermsResource(String resourceId, boolean replaceExisting) throws IOException, UnsupportedFileTypeException, JDOMException;
	
   public void importTermsResource(Id worksiteId, String resourceId, boolean replaceExisting) throws IOException, UnsupportedFileTypeException, JDOMException;
}


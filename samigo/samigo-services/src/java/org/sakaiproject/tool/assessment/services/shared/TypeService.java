/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.6/samigo-services/src/java/org/sakaiproject/tool/assessment/services/shared/TypeService.java $
 * $Id: TypeService.java 319083 2015-05-20 22:24:13Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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


package org.sakaiproject.tool.assessment.services.shared;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.services.PersistenceService;


/**
 * The QuestionPoolService calls the service locator to reach the
 * manager on the back end.
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class TypeService
{
  private Log log = LogFactory.getLog(TypeService.class);

  /**
   * Creates a new QuestionPoolService object.
   */
  public TypeService()  {
  }

  public TypeFacade getTypeById(String typeId)
  {
    try{
      return PersistenceService.getInstance().getTypeFacadeQueries().
          getTypeFacadeById(new Long(typeId));
    }
    catch(Exception e)
    {
      log.error(e); throw new RuntimeException(e);
    }
  }

  public List getFacadeListByAuthorityDomain(String authority, String domain)
  {
    try{
      return PersistenceService.getInstance().getTypeFacadeQueries().
          getFacadeListByAuthorityDomain(authority,domain);
    }
    catch(Exception e)
    {
      log.error(e); throw new RuntimeException(e);
    }
  }

  public List getListByAuthorityDomain(String authority, String domain)
  {
    try{
      return PersistenceService.getInstance().getTypeFacadeQueries().
          getListByAuthorityDomain(authority,domain);
    }
    catch(Exception e)
    {
      log.error(e); throw new RuntimeException(e);
    }
  }

  public List getFacadeItemTypes() {
    try{
      return PersistenceService.getInstance().getTypeFacadeQueries().
          getFacadeItemTypes();
    }
    catch(Exception e)
    {
      log.error(e); throw new RuntimeException(e);
    }
  }
}

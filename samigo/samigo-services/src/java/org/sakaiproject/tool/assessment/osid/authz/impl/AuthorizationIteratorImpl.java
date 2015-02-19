/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.4/samigo-services/src/java/org/sakaiproject/tool/assessment/osid/authz/impl/AuthorizationIteratorImpl.java $
 * $Id: AuthorizationIteratorImpl.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.tool.assessment.osid.authz.impl;

import java.util.Iterator;
import java.util.Collection;

import org.osid.authorization.Authorization;
import org.osid.authorization.AuthorizationException;

public class AuthorizationIteratorImpl
 implements org.osid.authorization.AuthorizationIterator
{
  private Iterator authorizationIter;

  public AuthorizationIteratorImpl(Collection authorizations)
  {
    this.authorizationIter = authorizations.iterator();
  }

  public boolean hasNextAuthorization()
    throws AuthorizationException
  {
    try{
      return authorizationIter.hasNext();
    }
    catch(Exception e){
      throw new AuthorizationException(e.getMessage());
    }
  }

  public Authorization nextAuthorization()
    throws AuthorizationException
  {
    try{
      return (Authorization) authorizationIter.next();
    }
    catch(Exception e){
      throw new AuthorizationException(e.getMessage());
    }
  }

  public void remove()
    throws AuthorizationException
  {
    try{
      authorizationIter.remove();
    }
    catch(Exception e){
      throw new AuthorizationException(e.getMessage());
    }
  }

}

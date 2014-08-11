package org.sakaiproject.metaobj.security.impl;

import org.sakaiproject.authz.api.SecurityAdvisor;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 9, 2007
 * Time: 8:21:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class AllowAllSecurityAdvisor implements SecurityAdvisor {

   /**
    * Can the current session user perform the requested function on the referenced Entity?
    *
    * @param userId    The user id.
    * @param function  The lock id string.
    * @param reference The resource reference string.
    * @return ALLOWED or NOT_ALLOWED if the advisor can answer that the user can or cannot, or PASS if the advisor cannot answer.
    */
   public SecurityAdvice isAllowed(String userId, String function, String reference) {
      return SecurityAdvice.ALLOWED;
   }
}

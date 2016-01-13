package org.theospi.portfolio.list.intf;

import java.util.Map;

/*
 * forces tool into certain state
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/intf/ActionableListGenerator.java,v 1.1 2004/11/02 23:47:28 jbush Exp $
 * $Revision: 5901 $
 * $Date: 2006-05-09 05:28:42 +0900 (Tue, 09 May 2006) $
 */

public interface ActionableListGenerator extends ListGenerator {
   /**
    * Store any params in request into tool state.  
    * These will be added to redirect call to load the tool state.
    * @param toolId
    * @param request
    */
   public void setToolState(String toolId, Map request);
}

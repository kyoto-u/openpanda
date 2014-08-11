package org.theospi.portfolio.portal.impl;

import java.util.Comparator;

import org.sakaiproject.site.api.Site;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 17, 2006
 * Time: 11:03:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteTitleComparator implements Comparator {

   public int compare(Object o1, Object o2) {
      Site site1 = (Site) o1;
      Site site2 = (Site) o2;

      return site1.getTitle().compareTo(site2.getTitle());
   }
}

package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.site.api.Site;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Mar 26, 2007
 * Time: 9:57:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteHomeWrapper implements Comparable {

   private Site site;
   private List homes;

   public SiteHomeWrapper(Site site, List homes) {
      this.site = site;
      this.homes = homes;
   }

   public Site getSite() {
      return site;
   }

   public void setSite(Site site) {
      this.site = site;
   }

   public List getHomes() {
      return homes;
   }

   public void setHomes(List homes) {
      this.homes = homes;
   }

   public int compareTo(Object o) {
      return site.getTitle().compareTo(((SiteHomeWrapper)o).getSite().getTitle());
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/glossary/api-impl/src/java/org/theospi/portfolio/help/model/DbGlossary.java $
* $Id: DbGlossary.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class DbGlossary  extends HibernateDaoSupport implements Glossary, Observer {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private Map worksiteGlossary = new Hashtable();
   private IdManager idManager;
   private List dirtyAddUpdate = new ArrayList();
   private List dirtyRemove = new ArrayList();
   private SchedulerManager schedulerManager;
   private int cacheInterval = 1000 * 10;
   private boolean useCache = true;

   private String url;

   private static final String EVENT_UPDATE_ADD = "org.theospi.glossary.updateAdd";
   private static final String EVENT_DELETE = "org.theospi.glossary.delete";

   public GlossaryEntry load(Id id) {
      GlossaryEntry entry = load(id, true);
      getHibernateTemplate().evict(entry);
      return entry;
   }

   protected GlossaryEntry load(Id id, boolean deep) {
      try {
         GlossaryEntry entry = (GlossaryEntry)getSession().load(GlossaryEntry.class, id);
         if (deep) {
            entry.setLongDescriptionObject(loadDescription(id));
         }
         return entry;
      } catch (HibernateException e) {
         logger.warn("", e);
         return null;
      }
   }

   protected GlossaryEntry find(Id id) {
      List entries = getHibernateTemplate().find("from GlossaryEntry where id = ?", id);
      if (entries.size() > 0) {
         return (GlossaryEntry)entries.get(0);
      }
      return null;
   }

   protected GlossaryDescription loadDescription(Id entryId) {
      Collection entries = getHibernateTemplate().findByNamedQuery("loadDescription",
         entryId);

      if (entries.size() > 0) {
         return (GlossaryDescription)entries.iterator().next();
      }
      else {
         return new GlossaryDescription();
      }
   }

   /**
    * find the keyword in the glossary.
    * return null if not found.
    *
    * @param keyword
    * @return
    */
   public GlossaryEntry find(String keyword, String worksite) {
      Collection entries = getHibernateTemplate().findByNamedQuery("findTerms", new Object[]{keyword, worksite});
      if (entries.size() == 0) {
         return null;
      }
      else if (entries.size() == 1) {
         return (GlossaryEntry)entries.iterator().next();
      }
      else {
         for (Iterator i=entries.iterator();i.hasNext();) {
            GlossaryEntry entry = (GlossaryEntry)i.next();
            if (entry.getWorksiteId() != null) {
               return entry;
            }
         }
      }

      return (GlossaryEntry)entries.iterator().next();
   }

   /**
    * returns the list of all GlossaryEntries
    *
    * @return
    */

   public Collection findAll(String keyword, String worksite) {
      return getHibernateTemplate().findByNamedQuery("findTerms", new Object[]{keyword, worksite});
   }

   /**
    * returns the list of all GlossaryEntries
    *
    * @return
    */
   public Collection findAll(String worksite) {
      return getHibernateTemplate().findByNamedQuery("findAllSiteTerms",
         new Object[]{worksite});
   }

   public Collection findAll() {
      return getHibernateTemplate().findByNamedQuery("findAllTerms");
   }

   public Collection findAllGlobal() {
      return getHibernateTemplate().findByNamedQuery("findGlobalTerms");
   }

   public GlossaryEntry addEntry(GlossaryEntry newEntry) {
      getHibernateTemplate().save(newEntry);
      newEntry.getLongDescriptionObject().setEntryId(newEntry.getId());
      getHibernateTemplate().save(newEntry.getLongDescriptionObject());
      updateCache(newEntry, false);
      return newEntry;
   }

   public void removeEntry(GlossaryEntry entry) {
      getHibernateTemplate().delete(entry);
      GlossaryDescription desc = loadDescription(entry.getId());
      getHibernateTemplate().delete(desc);
      updateCache(entry, true);
   }

   public void updateEntry(GlossaryEntry entry) {
      getHibernateTemplate().merge(entry);
      GlossaryDescription desc = loadDescription(entry.getId());
      desc.setLongDescription(entry.getLongDescription());
      getHibernateTemplate().merge(desc);
      updateCache(entry, false);
   }

   protected void updateCache(GlossaryEntry entry, boolean remove) {
      if (useCache) {
         GlossaryTxSync txSync = new GlossaryTxSync(entry, remove);

         if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(txSync);
         }
         else {
            txSync.afterCompletion(GlossaryTxSync.STATUS_COMMITTED);
         }
      }
   }

   public Set getSortedWorksiteTerms(String worksiteId) {
      Set sortedSet = new TreeSet(new TermComparator());

      Map worksiteTerms = getWorksiteGlossary(worksiteId);
      if (worksiteTerms != null) {
         sortedSet.addAll(worksiteTerms.values());
      }

      String globalId = null;
      Map globalTerms = getWorksiteGlossary(globalId + "");

      if (globalTerms != null) {
         for (Iterator i=globalTerms.values().iterator();i.hasNext();) {
            GlossaryEntry entry = (GlossaryEntry)i.next();
            if (!sortedSet.contains(entry)) {
               sortedSet.add(entry);
            }
         }
      }

      return sortedSet;
   }

   protected Map getWorksiteGlossary(String worksiteId) {
      return getWorksiteGlossary(worksiteId, true);
   }
   
   protected Map getWorksiteGlossary(String worksiteId, boolean checkCache) {
      if (!useCache) {
         logger.warn("using glossary without cache, this could slow down osp tool page loads");
         Map terms = new Hashtable();
         Collection<GlossaryEntry> entries;
         if (worksiteId.equals(null + "")) {
            entries = findAllGlobal();
         }
         else {
            entries = findAll(worksiteId);
         }
         for (Iterator<GlossaryEntry> i = entries.iterator();i.hasNext();) {
            GlossaryEntry entry = i.next();
            terms.put(entry.getId(), entry);
         }
         return terms;
      }
      else {
         if (checkCache) {
            checkCache();
         }
         return (Map)worksiteGlossary.get(worksiteId);
      }
   }

   public void checkCache() {

      synchronized(dirtyAddUpdate) {
         for (Iterator<Id> i=dirtyAddUpdate.iterator();i.hasNext();) {
            GlossaryEntry entry = find(i.next());
            if (entry != null) {
               addUpdateTermCache(entry);
            }
         }
         dirtyAddUpdate.clear();
      }

      synchronized(dirtyRemove) {
         for (Iterator<Id> i=dirtyRemove.iterator();i.hasNext();) {
            removeCachedEntry(i.next());
         }
         dirtyRemove.clear();
      }

   }

   public boolean isPhraseStart(String phraseFragment, String worksite) {
      phraseFragment += "%";
      Collection entries = getHibernateTemplate().findByNamedQuery("findByPhrase", new Object[]{phraseFragment, worksite});
      if (entries.size() > 0) {
         return true;
      }

      return false;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void importResources(String fromContext, String toContext, List resourceIds) {
      Collection orig = findAll(fromContext);

      for (Iterator i=orig.iterator();i.hasNext();) {
         GlossaryEntry entry = (GlossaryEntry)i.next();

         entry.setLongDescriptionObject(loadDescription(entry.getId()));

         getHibernateTemplate().evict(entry);
         getHibernateTemplate().evict(entry.getLongDescriptionObject());

         entry.setWorksiteId(toContext);
         entry.setId(null);
         getHibernateTemplate().save(entry);

         entry.getLongDescriptionObject().setEntryId(entry.getId());
         entry.getLongDescriptionObject().setId(null);
         getHibernateTemplate().save(entry.getLongDescriptionObject());
         addUpdateTermCache(entry);
      }
   }

   public void init() {
      if (isUseCache()) {
         logger.info("init()");
         Collection terms = findAll();

         for (Iterator i=terms.iterator();i.hasNext();) {
            GlossaryEntry entry = (GlossaryEntry)i.next();
            addUpdateTermCache(entry);
         }

         EventTrackingService.addObserver(this);

      }
   }

   protected void addUpdateTermCache(GlossaryEntry entry) {
      String worksiteId = entry.getWorksiteId() + "";
      Map worksiteMap = getWorksiteGlossary(worksiteId, false);

      if (worksiteMap == null) {
         worksiteGlossary.put(worksiteId, new Hashtable());
         worksiteMap = getWorksiteGlossary(worksiteId, false);
      }
      worksiteMap.put(entry.getId(), entry);
   }

   protected void removeCachedEntry(Id entryId) {
      for (Iterator i=worksiteGlossary.values().iterator();i.hasNext();) {
         Map map = (Map)i.next();
         if (map.remove(entryId) != null) {
            // found it
            return;
         }
      }
   }

   /**
    * This method is called whenever the observed object is changed. An
    * application calls an <tt>Observable</tt> object's
    * <code>notifyObservers</code> method to have all the object's
    * observers notified of the change.
    * 
    * This operates within its own Thread so normal rules and conditions don't apply
    *
    * @param o   the observable object.
    * @param arg an argument passed to the <code>notifyObservers</code>
    *            method.
    */
   public void update(Observable o, Object arg) {
      if (arg instanceof Event) {
         Event event = (Event)arg;
         if (event.getEvent().equals(EVENT_UPDATE_ADD)) {
            synchronized(dirtyAddUpdate) {
               dirtyAddUpdate.add(getIdManager().getId(event.getResource()));
            }
         }
         else if (event.getEvent().equals(EVENT_DELETE)) {
            synchronized(dirtyRemove) {
               dirtyRemove.add(getIdManager().getId(event.getResource()));
            }
         }
      }
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   private class GlossaryTxSync extends TransactionSynchronizationAdapter {
      private GlossaryEntry entry;
      private boolean remove = false;

      public GlossaryTxSync(GlossaryEntry entry, boolean remove) {
         this.entry = entry;
         this.remove = remove;
      }

      public void afterCompletion(int status) {
         Event event = null;
         if (status == STATUS_COMMITTED && remove) {
            event = EventTrackingService.newEvent(EVENT_DELETE, entry.getId().getValue(), false);
         }
         else if (status == STATUS_COMMITTED) {
            event = EventTrackingService.newEvent(EVENT_UPDATE_ADD, entry.getId().getValue(), false);
         }

         if (event != null) {
            EventTrackingService.post(event);
         }
      }

      public GlossaryEntry getEntry() {
         return entry;
      }

      public void setEntry(GlossaryEntry entry) {
         this.entry = entry;
      }
   }

   public SchedulerManager getSchedulerManager() {
      return schedulerManager;
   }

   public void setSchedulerManager(SchedulerManager schedulerManager) {
      this.schedulerManager = schedulerManager;
   }

   public int getCacheInterval() {
      return cacheInterval;
   }

   public void setCacheInterval(int cacheInterval) {
      this.cacheInterval = cacheInterval;
   }

   public boolean isUseCache() {
      return useCache;
   }

   public void setUseCache(boolean useCache) {
      this.useCache = useCache;
   }
}

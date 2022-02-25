/**
 * Copyright (c) 2003-2016 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.announcement.tool;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.time.Instant;
import java.util.Comparator;

import org.sakaiproject.announcement.api.AnnouncementBrowsingHis;
import org.sakaiproject.announcement.cover.AnnouncementService;
import org.sakaiproject.entity.api.ResourceProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Comparator for announcement browsing history.
 */
@Slf4j
public class AnnouncementBrowsingHisComparator implements Comparator<AnnouncementBrowsingHis> {

    private static RuleBasedCollator collator_ini = (RuleBasedCollator)Collator.getInstance();
    private Collator collator = Collator.getInstance();
    
    // the criteria
    String m_criteria = null;

    {
        try {
            collator = new RuleBasedCollator(collator_ini.getRules().replaceAll("<'\u005f'", "<' '<'\u005f'"));
        } catch (ParseException e) {
            log.error("{} Cannot init RuleBasedCollator. Will use the default Collator instead.", this, e);
        }
    }

    // the criteria - asc
    boolean m_asc = true;

    /**
     * constructor
     *  @param criteria The sort criteria string
     * @param asc      The sort order string. "true" if ascending; "false" otherwise.
     */
    public AnnouncementBrowsingHisComparator(String criteria, boolean asc) {
        m_criteria = criteria;
        m_asc = asc;

    } // constructor

    /**
     * implementing the compare function
     *
     * @param o1 The first object
     * @param o2 The second object
     * @return The compare result. 1 is o1 < o2; -1 otherwise
     */
    public int compare(AnnouncementBrowsingHis o1, AnnouncementBrowsingHis o2) {
        int result = -1;

        switch (m_criteria) {
        case AnnouncementAction.SORT_HISTORY_ID:
            result = collator.compare(o1.getDisplayUserId(), o2.getDisplayUserId());
            break;
        case AnnouncementAction.SORT_HISTORY_NAME:
            result = collator.compare(o1.getDisplayUserName(), o2.getDisplayUserName());
            break;
        case AnnouncementAction.SORT_HISTORY_DATE:

            Instant o1ReadDate = null;
            if (o1.getReadDate() != null) {
            	o1ReadDate = o1.getReadDate().toInstant();
            }
            Instant o2ReadDate = null;
            if (o2.getReadDate() != null) {
            	o2ReadDate = o2.getReadDate().toInstant();
            }
            result = compareInstantsNullSafe(o1ReadDate, o2ReadDate);
            break;
        
        default:
            break;
        }
        // sort ascending or descending
        if (!m_asc) {
            result = -result;
        }
        return result;
    }

    private int compareInstantsNullSafe(Instant date1, Instant o2ModDate) {
        if (date1 == null && o2ModDate == null) {
            return 0;
        } else if (date1 == null) {
            return 1;
        } else if (o2ModDate == null) {
            return -1;
        }
        else {
        	return date1.compareTo(o2ModDate);
        }
    }

} // AnnouncementBrowsingHisComparator

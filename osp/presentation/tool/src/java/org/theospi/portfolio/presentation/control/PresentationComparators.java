/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ListPresentationController.java $
* $Id:ListPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2009 The Sakai Foundation
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

package org.theospi.portfolio.presentation.control;

import java.util.Comparator;
import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;

/**
 * The comparators in this class assume that you never try to compare directly to null (this will
 * result in a NullPointer). Furthermore, should any object-field under comparison be null (e.g.
 * presentation.getName()), it will be considered -1 against a non-null object-field of the opposing
 * object, or 0 if they both have a null-value at that level. This paranoid null-checking is done
 * because the interface of Presentation defines a lot of 'can-be-null' fields, and we just want an
 * ordering for presentation-purposes
 * 
 */
final class PresentationComparators {

    private static class StringComparator implements Comparator<String> {

        public final int compare(String s1, String s2) {
            if (s1 == null) {
                if (s2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (s2 == null) {
                return 1;
            } else {
                return s1.compareToIgnoreCase(s2);
            }
        }
    }

    static class ByNameComparator implements Comparator<Presentation> {

        private final StringComparator stringComparator = new StringComparator();

        public final int compare(Presentation p1, Presentation p2) {
            return stringComparator.compare(p1.getName(), p2.getName());
        }
    }

    static class ByDateModifiedComparator implements Comparator<Presentation> {

        private final ByNameComparator nameComparator = new ByNameComparator();

        public final int compare(Presentation p1, Presentation p2) {
            final Date p1Modified = p1.getModified();
            final Date p2Modified = p2.getModified();

            if (p1Modified == null) {
                if (p2Modified == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (p2Modified == null) {
                return 1;
            } else {
                int result = p1Modified.compareTo(p2Modified);
                if (result == 0) {
                    result = nameComparator.compare(p1, p2);
                }
                return result;
            }
        }
    }

    static class ByOwnerComparator implements Comparator<Presentation> {

        private final StringComparator stringComparator = new StringComparator();
        private final ByNameComparator nameComparator = new ByNameComparator();

        public int compare(Presentation p1, Presentation p2) {
            final Agent p1Owner = p1.getOwner();
            final Agent p2Owner = p2.getOwner();
            if (p1Owner == null) {
                if (p2Owner == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (p2Owner == null) {
                return 1;
            } else {
                // get the sort names
                String name1 = p1Owner.getDisplayName();
                String name2 = p2Owner.getDisplayName();
                try {
                    name1 = UserDirectoryService.getUserByEid(p1Owner.getEid().getValue())
                            .getSortName();
                } catch (Exception e) {
                    // nothing to do
                }
                try {
                    name2 = UserDirectoryService.getUserByEid(p2Owner.getEid().getValue())
                            .getSortName();
                } catch (Exception e) {
                    // nothing to do
                }
                int result = stringComparator.compare(name1, name2);
                if (result == 0) {
                    result = nameComparator.compare(p1, p2);
                }
                return result;
            }
        }
    }

    static class ByReviewedComparator implements Comparator<Presentation> {

        public int compare(Presentation p1, Presentation p2) {
            final Boolean b1 = p1.getIsDefault();
            final Boolean b2 = p2.getIsDefault();
            return b1.compareTo(b2);
        }
    }
   
}

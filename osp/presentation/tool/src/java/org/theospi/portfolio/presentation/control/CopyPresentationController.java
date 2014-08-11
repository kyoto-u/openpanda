/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/HidePresentationController.java $
 * $Id: HidePresentationController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;

/**
 * This triggers the presentation copy function
 * http://jira.sakaiproject.org/browse/SAK-17351
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class CopyPresentationController extends ListPresentationController {
    protected final static Log logger = LogFactory.getLog(CopyPresentationController.class);

    /* (non-Javadoc)
     * @see org.theospi.portfolio.presentation.control.ListPresentationController#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
     */
    public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
        String id = (String) request.get("id");

        // Agent current = getAuthManager().getAgent();
        // getPresentationManager();
        logger.info("Copy activated for presentation: "+id);
        Presentation original = (Presentation) requestModel;
        if (original != null) {
            getPresentationManager().copyPresentation(original.getId());
            /*
            original = getPresentationManager().getPresentation(original.getId());
            if (original != null) {
                // ready to copy
                logger.info("Ready to copy presentation: "+original.getName());
                // TODO actually do the copy, I'm sure this is the easy part... -AZ
                // NOTE: All this will move down into the service eventually -AZ
                Presentation copy = new Presentation();
                copy.setNewObject(true);
                copy.setAllowComments(original.isAllowComments());
                copy.setDescription(original.getDescription());
                copy.setExpiresOn(original.getExpiresOn());
                copy.setIsCollab(original.getIsCollab());
                copy.setIsDefault(original.getIsDefault());
                copy.setIsPublic(original.getIsPublic());
                HashSet<PresentationItem> copiedItems = new HashSet<PresentationItem>(original.getItems().size());
                for (PresentationItem item : (Set<PresentationItem>) original.getItems()) {
                    copiedItems.add(item);
                }
                copy.setItems(copiedItems); // list (ref)
                copy.setLayout(original.getLayout()); // obj (ref)
                copy.setName("Copy of "+original.getName());
                copy.setOwner(original.getOwner()); // should we copy this?
                List<PresentationPage> origPages = getPresentationManager().getPresentationPagesByPresentation(original.getId());
                if (origPages != null && ! origPages.isEmpty()) {
                    ArrayList<PresentationPage> copiedPages = new ArrayList<PresentationPage>(origPages.size());
                    for (PresentationPage page : origPages) {
                        PresentationPage cp = new PresentationPage();
                        cp.setNewObject(true);
                        cp.setDescription(page.getDescription());
                        cp.setKeywords(page.getKeywords());
                        cp.setLayout(page.getLayout());
                        cp.setPresentation(copy); // NOTE: this should be set automatically when null -AZ
                        if (page.getRegions() != null) {
                            HashSet<PresentationPageRegion> copiedRegions = new HashSet<PresentationPageRegion>();
                            for (PresentationPageRegion region : (Set<PresentationPageRegion>) page.getRegions()) {
                                copiedRegions.add(region);
                            }
                            cp.setRegions(copiedRegions);
                        }
                        cp.setSequence(page.getSequence());
                        cp.setStyle(page.getStyle());
                        cp.setTitle(page.getTitle());
                        copiedPages.add(cp);
                    }
                    copy.setPages(copiedPages); // list (ref)
                }
                //copy.set(original.getPresentationItems()); // list
                copy.setPresentationType(original.getPresentationType());
                copy.setProperties(original.getProperties()); // obj (ref)
                copy.setPropertyForm(original.getPropertyForm()); // ref (id)
                copy.setSecretExportKey(original.getSecretExportKey());
                copy.setSiteId(original.getSiteId());
                copy.setStyle(original.getStyle()); // obj (ref)
                copy.setTemplate(original.getTemplate()); // obj (ref)
                copy.setToolId(original.getToolId());
                Presentation savedCopy = getPresentationManager().storePresentation(copy, true, true);
                logger.info("Copied presentation from "+original.getId()+" to "+savedCopy.getId());
            }
            */
        }

        return super.handleRequest(requestModel, request, session, application, errors);
    }

}

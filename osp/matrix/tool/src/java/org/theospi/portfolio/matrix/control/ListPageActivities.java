/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class ListPageActivities extends AbstractMatrixController
{
	protected final Log logger = LogFactory.getLog(getClass());
	private TaggingManager taggingManager;
	private LinkManager linkManager;
	private TaggingProvider matrixTaggingProvider;
	private EntityManager entityManager;

	public ModelAndView handleRequest(Object requestModel, Map request, Map session,
			Map application, Errors errors)
	{
		String criteriaRef = (String) request.get("criteriaRef");
		Reference ref = getEntityManager().newReference(criteriaRef);
		Id pageId = getIdManager().getId(ref.getId());
		String submit = (String) request.get("submit");
		if (submit != null && "Back".equalsIgnoreCase(submit)) {
			return new ModelAndView("goback", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
		}

		Map<String, Object> model = new HashMap<String, Object>();
		Set<WrappedActivity> activities = new HashSet<WrappedActivity>();
		
		model.put("criteriaRef", criteriaRef);
		
		String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();
		model.put("decoWrapper", "ospMatrix_" + siteId + "_" + pageId);
		
		WizardPageDefinition wpd = getMatrixManager().getWizardPageDefinition(pageId);
		model.put("pageTitle", Validator.escapeHtml(wpd.getTitle()));
		
		Map<String, String> siteNames = new HashMap<String, String>();
		
		try
		{
			List<Link> links = getLinkManager().getLinks(criteriaRef, true);
			for (Link link : links) {
				TaggableActivity activity = getTaggingManager().getActivity(link.getActivityRef(), getMatrixTaggingProvider());
				if (activity != null) {
					activities.add(new WrappedActivity(activity, lookupSiteName(activity.getContext(), siteNames)));
				}
				else {
					logger.warn("Link with ref " + link.getActivityRef() + " no longer exists.  Removing link.");
					getLinkManager().removeLink(link);
				}
			}
		}
		catch (PermissionException e)
		{
			logger.warn("unable to get links for criteriaRef " + criteriaRef, e);
		}
		
		List<WrappedActivity> activityList = new ArrayList<WrappedActivity>(activities);
		Collections.sort(activityList, activityComparator);
		model.put("pageActivities", activityList);
		return new ModelAndView("success", model);
	}
	
	/**
	 * Look for the site name in the map, or go to the site service to find it
	 * @param context
	 * @param siteNames
	 * @return
	 */
	private String lookupSiteName(String context, Map<String, String> siteNames) {
		String siteName = siteNames.get(context);
		if (siteName == null) {
			Site site = getWorksiteManager().getSite(context);
			if (site != null) {
				siteName = site.getTitle();
				siteNames.put(context, siteName);
			}
		}
		return siteName;
	}
	
	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public LinkManager getLinkManager()
	{
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager)
	{
		this.linkManager = linkManager;
	}

	protected TaggingProvider getMatrixTaggingProvider() {
		if (matrixTaggingProvider == null) {
			matrixTaggingProvider = getTaggingManager().findProviderById(
					MatrixTaggingProvider.PROVIDER_ID);
		}
		return matrixTaggingProvider;
	}

	public void setMatrixTaggingProvider(TaggingProvider matrixTaggingProvider) {
		this.matrixTaggingProvider = matrixTaggingProvider;
	}



	public EntityManager getEntityManager()
	{
		return entityManager;
	}



	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	public class WrappedActivity {
		private String contextName;
		private TaggableActivity activity;
		
		public WrappedActivity() {}
		
		public WrappedActivity(TaggableActivity activity, String contextName) {
			this.activity = activity;
			this.contextName = contextName;
		}

		public String getContextName()
		{
			return contextName;
		}

		public void setContextName(String contextName)
		{
			this.contextName = contextName;
		}

		public TaggableActivity getActivity()
		{
			return activity;
		}

		public void setActivity(TaggableActivity activity)
		{
			this.activity = activity;
		}
		
		
	}
	
	public static Comparator<WrappedActivity> activityComparator;
	   static {
		   activityComparator = new Comparator<WrappedActivity>() {
	         public int compare(WrappedActivity o1, WrappedActivity o2) {
	                return o1.getActivity().getTitle().toLowerCase().compareTo(
	                      o2.getActivity().getTitle().toLowerCase());
	         }
	        };
	   }

}

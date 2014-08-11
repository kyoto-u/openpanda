package org.theospi.portfolio.tagging.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.siteassociation.api.SiteAssocManager;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.Tag;
import org.sakaiproject.taggable.api.TagColumn;
import org.sakaiproject.taggable.api.TagList;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class MatrixTaggingProviderImpl implements MatrixTaggingProvider {

	private static final Log logger = LogFactory.getLog(MatrixTaggingProviderImpl.class);
	
	private static ResourceLoader messages = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
	protected TaggingManager taggingManager;
	protected LinkManager linkManager;
	protected SiteAssocManager siteAssocManager;
	private IdManager idManager = null;
	private AuthorizationFacade authzManager = null;
	private AgentManager agentManager = null;
	private MatrixManager matrixManager = null;
	private EntityManager entityManager = null;
	
	protected static final String LINK_HELPER = "osp.matrix.link";
	
	
	
	public void init() {
		logger.info("init()");

		// register as a tagging provider
		getTaggingManager().registerProvider(this);
	}
	
	public String getId() {
		return MatrixTaggingProvider.PROVIDER_ID;
	}

	public String getName() {
		return messages.getString("provider_name");
	}
	
	public String getSimpleTextLabel() {
		return messages.getString("provider_text_label");
	}
	
	public String getHelpLabel() {
		return messages.getString("provider_help_label");
	}

	public String getHelpDescription() {
		return messages.getString("provider_help_desc");
	}
	
	public boolean allowViewTags(String context) {
		boolean allow = false;
		List<String> associations = siteAssocManager.getAssociatedFrom(context);
		if (associations != null && associations.size() > 0) {
			allow = true;
		}
		return allow;
	}
	
	public boolean allowGetActivity(String activityRef, String userId, String taggedItem) {
		Agent currentUser = getAgentManager().getAgent(userId);
		Reference ref = getEntityManager().newReference(taggedItem);
		Id pageDefId = getIdManager().getId(ref.getId());
		ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(pageDefId);
		Id scaffoldingId = sCell.getScaffolding().getId();
		Scaffolding scaff = getMatrixManager().getScaffolding(scaffoldingId);
		boolean result = getAuthzManager().isAuthorized(currentUser, MatrixFunctionConstants.REVISE_SCAFFOLDING_ANY, getIdManager().getId(ref.getContext())) ||
		   (scaff.getOwner().equals(currentUser) 
		         && getAuthzManager().isAuthorized(currentUser, MatrixFunctionConstants.REVISE_SCAFFOLDING_OWN, getIdManager().getId(ref.getContext())) ||
			canViewCellContents(activityRef, new String[] {}, userId, taggedItem));
		
		return result;
	}
	
	public boolean allowGetItem(String activityRef, String itemRef, String userId, String taggedItem) {
		String[] itemRefs = {itemRef};
		return allowGetItems(activityRef, itemRefs, userId, taggedItem);
	}
	
	public boolean allowGetItems(String activityRef, String[] itemRefs, String userId, String taggedItem) {
	   return canViewCellContents(activityRef, itemRefs, userId, taggedItem);
	}
	
	private boolean canViewCellContents(String activityRef, String[] itemRefs, String userId, String taggedItem) {
		//make sure item is properly linked and then do perm check

		try {
			Link link = getLinkManager().getLink(activityRef, taggedItem);

			if (link != null) {
				Agent agent = getAgentManager().getAgent(userId);
				Reference ref = getEntityManager().newReference(taggedItem);
				Id pageDefId = getIdManager().getId(ref.getId());
				ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(pageDefId);
				if(UserDirectoryService.getCurrentUser().getId().equals(userId) ||
						(sCell.isDefaultEvaluators() && getAuthzManager().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getScaffolding().getId())) ||
						(!sCell.isDefaultEvaluators() && getAuthzManager().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getId())) ||
						(sCell.isDefaultReviewers() && getAuthzManager().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getScaffolding().getId())) ||
						(!sCell.isDefaultReviewers() && getAuthzManager().isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getId())) ||
						(getAuthzManager().isAuthorized(agent, MatrixFunctionConstants.ACCESS_ALL_CELLS, getIdManager().getId(sCell.getScaffolding().getReference())))){
					//SecurityService.pushAdvisor(new MySecurityAdvisor(userId, Arrays.asList(functions), Arrays.asList(itemRefs)));  
					return Boolean.valueOf(true);
				}
			}
		} catch (PermissionException e) {
			logger.warn("Unable to get the link for activity: " + activityRef + " and tagCriteriaRef: " + taggedItem, e);
		}
		return false;
	}

	/**
	 * If there are any associations, allow it
	 * @param activityContext
	 * @return
	 */
	protected boolean allowTagActivities(String activityContext) {
		boolean allow = false;
		List<String> associations = siteAssocManager.getAssociatedFrom(activityContext);
		if (associations != null && associations.size() > 0) {
			allow = true;
		}
		return allow;
	}

	public TaggingHelperInfo getActivityHelperInfo(String activityRef) {
		TaggingHelperInfo helperInfo = null;
		String context = taggingManager.getContext(activityRef);
		if (allowTagActivities(context)
				&& (taggingManager.getActivity(activityRef, this) != null)) {
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put(ACTIVITY_REF, activityRef);
			String text = messages.getString("act_helper_text");
			String title = messages.getString("act_helper_title");
			helperInfo = taggingManager.createTaggingHelperInfoObject(LINK_HELPER, text, title,
					parameterMap, this);
		}
		return helperInfo;
	}
	
	public Map<String, TaggingHelperInfo> getActivityHelperInfo(String context, List<String> activityRefs) {
		TaggingHelperInfo helperInfo = null;
		Map<String, TaggingHelperInfo> returnMap = new HashMap<String, TaggingHelperInfo>();
		if (allowTagActivities(context)) {
			
			for (String activityRef : activityRefs) {
				TaggableActivity activity = taggingManager.getActivity(activityRef, this);
				if (activity != null && context.equals(activity.getContext())) {
					Map<String, String> parameterMap = new HashMap<String, String>();
					parameterMap.put(ACTIVITY_REF, activityRef);
					String text = messages.getString("act_helper_text");
					String title = messages.getString("act_helper_title");
					helperInfo = taggingManager.createTaggingHelperInfoObject(LINK_HELPER, text, title,
							parameterMap, this);
					returnMap.put(activityRef, helperInfo);
				}
			}
		}
		return returnMap;
	}


	public TaggingHelperInfo getItemHelperInfo(String itemRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public TaggingHelperInfo getItemsHelperInfo(String activityRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public TagList getTags(TaggableActivity activity) {
		List<TagColumn> columns = new ArrayList<TagColumn>();
		columns.add(taggingManager.createTagColumn(TagList.CRITERIA, messages.getString("column_criteria"), messages.getString("column_criteria"), true));
		columns.add(taggingManager.createTagColumn(TagList.PARENT, messages.getString("column_parent"), messages.getString("column_parent_desc"), true));
		columns.add(taggingManager.createTagColumn(TagList.WORKSITE, messages.getString("column_worksite"), messages.getString("column_worksite_desc"), true));
		TagList tagList = taggingManager.createTagList(columns);
		String activityContext = activity.getContext();
		for (String toContext : getSiteAssocManager().getAssociatedFrom(activityContext)) {
			try {
				for (Link link : linkManager.getLinks(activity
						.getReference(), true, toContext)) {
					Tag tag = taggingManager.createTag(link);
					tagList.add(tag);
				}
			} catch (PermissionException pe) {
				logger.error(pe.getMessage(), pe);
			}
		}
		return tagList;
	}

	public void removeTags(TaggableActivity activity)
			throws PermissionException {

		getTaggingManager().removeLinks(activity);
		
	}

	public void removeTags(TaggableItem item) throws PermissionException {
		// TODO Auto-generated method stub
		
	}

	public void transferCopyTags(TaggableActivity fromActivity,
			TaggableActivity toActivity) throws PermissionException {
		// TODO Auto-generated method stub
		
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

	public SiteAssocManager getSiteAssocManager()
	{
		return siteAssocManager;
	}

	public void setSiteAssocManager(SiteAssocManager siteAssocManager)
	{
		this.siteAssocManager = siteAssocManager;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public AgentManager getAgentManager() {
		return agentManager;
	}

	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}

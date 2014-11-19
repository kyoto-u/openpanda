/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedWizard.java $
 * $Id: DecoratedWizard.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.theospi.portfolio.wizard.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.component.cover.ServerConfigurationService;

import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.tool.WizardTool.UserSelectListComparator;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizard implements DecoratedListInterface {
	private Wizard base;
	private WizardTool parent;
	private DecoratedCategory rootCategory = null;
	private DecoratedWizard next;
	private DecoratedWizard prev;
	private boolean newWizard = false;

	private DecoratedCompletedWizard runningWizard;

	private DecoratedCompletedWizard usersWizard;

	private int totalPages;

	protected final Log logger = LogFactory.getLog(getClass());
	private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.wizard.bundle.Messages");
	
	public DecoratedWizard(WizardTool tool, Wizard base) {
		this.base = base;
		this.parent = tool;
		rootCategory = new DecoratedCategory(base.getRootCategory(), tool);
		usersWizard = null;
	}
	public DecoratedWizard(WizardTool tool, Wizard base, boolean newWizard) {
		this.newWizard = newWizard;
		this.base = base;
		this.parent = tool;
		rootCategory = new DecoratedCategory(base.getRootCategory(), tool);
		usersWizard = null;
	}
	public Wizard getBase() {
		return base;
	}

	public void setBase(Wizard base) {
		this.base = base;
	}

	public String getDescription() {
		return getConcatDescription();
	}

	/** 
	 * This returns the concat description string.  This is currently acceptable
	 * because the wizard description is not html 
	 * @return String
	 */
	public String getConcatDescription() {
		String s = getBase().getDescription();
		if (s == null) {
			s = "";
		}

		if (s.length() > 100) {
			s = s.substring(0, 100) + "...";
		}
		return s;
	}

	public boolean getExposeAsTool() {
		if (base.getExposeAsTool() == null) {
			return false;
		} else {
			return base.getExposeAsTool().booleanValue();
		}
	}

	public void setExposeAsTool(boolean exposeAsTool) {
		base.setExposeAsTool(Boolean.valueOf(exposeAsTool));
	}

	public boolean getCanPublish() {
		return parent.getCanPublish(base);
	}

	public boolean getCanDelete() {
		return parent.getCanDelete(base);
	}

	public boolean getCanEdit() {
		return parent.getCanEdit(base);
	}

	public boolean getCanExport() {
		return parent.getCanExport(base);
	}
	
	public boolean getIgnoreReviewerGroupAccess() {
		return ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false);
	}

	public List getGroupListForSelect() {
		List groupSelect = new ArrayList();
		Collection groups = null;
		boolean allowAllGroups = ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false)
					|| base.getReviewerGroupAccess() == WizardMatrixConstants.UNRESTRICTED_GROUP_ACCESS;
		boolean includeSections = ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_INCLUDE_SECTIONS, false);
					
		try {
			Site site = SiteService.getSite(base.getSiteId());
			if (site.hasGroups()) {
				String currentUser = SessionManager.getCurrentSessionUserId();
				if (allowAllGroups) {
					groups = site.getGroups();
				}
				else {
					groups = site.getGroupsWithMember(currentUser);
				}
				for (Iterator it = groups.iterator(); it.hasNext();) {
					Group group = (Group) it.next();
					if ( includeSections || group.getProperties().getProperty(Group.GROUP_PROP_WSETUP_CREATED) != null )
						groupSelect.add(getParent().createSelect(group.getId(), group.getTitle()));
				}
			}
		} 
		catch (IdUnusedException e) {
			logger.error("", e);
		}
		return groupSelect;
	}
	
	public List getUserListForSelect() {
		String currentSiteId = base.getSiteId();
		List theList = getUserList(currentSiteId);

		String user = getParent().getCurrentUserId()!=null ? 
				getParent().getCurrentUserId() : SessionManager.getCurrentSessionUserId();
				getParent().setCurrentUserId(user);

				return theList;
	}

	private List getUserList(String worksiteId) {
		Set<String> userIds = new HashSet<String>();
		List users = new ArrayList();

      boolean allowAllGroups = ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false)
      			|| base.getReviewerGroupAccess() == WizardMatrixConstants.UNRESTRICTED_GROUP_ACCESS;
					
		try {
			Site site = SiteService.getSite(worksiteId);
			if ( site.hasGroups() ) {
				String filterGroupId = parent.getCurrentGroupId();
				if (allowAllGroups && (filterGroupId == null || filterGroupId.equals(""))) {
					userIds.addAll(site.getUsers());
				}
				else if ( filterGroupId != null && !filterGroupId.equals("") ) {
					Group group = site.getGroup(filterGroupId);
					userIds.addAll(group.getUsers());
				}
				else {
					String currentUser = SessionManager.getCurrentSessionUserId();
					Collection groups = site.getGroupsWithMember(currentUser);
					for (Iterator iter = groups.iterator(); iter.hasNext();) {
						Group group = (Group) iter.next();
						userIds.addAll(group.getUsers());
					}
				}
			}
			else {
				userIds.addAll(site.getUsers());
			}

			for (String userId : userIds) {
				User user;
				try {
					user = UserDirectoryService.getUser(userId);
					users.add(getParent().createSelect(user.getId(), user.getSortName()));
				}
				catch (UserNotDefinedException e) {
					getParent().logger.warn(myResources.getString("err_user_not_found") + e.getId());
				}				
			}
			Collections.sort(users, new UserSelectListComparator());
		}
		catch (IdUnusedException e) {
			throw new OspException(e);
		}
		return users;
	}

	public boolean getCanOperateOnWizardInstance() {
		boolean rethrow = false;
		boolean isPublishedOrPreview = getBase().isPublished() || getBase().isPreview();
		Exception exc = null;

		boolean canOperate = false;
		try {
			canOperate = parent.getCanOperate(base);
		} catch(AuthorizationFailedException e) {
			canOperate = false;
			exc = e;
		}

		boolean isOwner = false;

		if (base.getOwner() != null && base.getOwner().getId() != null)
			isOwner = parent.getCurrentUserId().equals(base.getOwner().getId().getValue());

		boolean can = (isPublishedOrPreview) && (canOperate || isOwner);

		if(!can && rethrow && exc != null)
			throw new RuntimeException("couldn't authorize", exc);

		return can;
	}

	public String getCurrentExportLink() {

		try {
			return "repository/" + "manager=org.theospi.portfolio.wizard.mgt.WizardManager&" +
			WizardManager.WIZARD_PARAM_ID + "=" +
			URLEncoder.encode(getBase().getId().getValue(), "UTF-8") + "/" +
			URLEncoder.encode(getBase().getName().replaceAll("[/\\\\]", "_") + ".zip", "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String processActionEdit() {
		return parent.processActionEdit(base);
	}

	public String processActionDelete() {
		return parent.processActionDelete(base);
	}

	public String processActionConfirmDelete() {
		return parent.processActionConfirmDelete(base);
	}

	public String processActionPublish() {
		return parent.processActionPublish(base);
	}

	public String processActionPreview() {
		return parent.processActionPreview(base);
	}

	public String getStyleName() {
		ToolSession session = SessionManager.getCurrentToolSession();
		if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
			Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
			base.setStyle(style);
		}
		else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
			base.setStyle(null);
			session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
			return "";
		}

		if (base.getStyle() != null) {
			return base.getStyle().getName();
		}
		return "";
	}

	public String processActionSelectStyle() {      
		getParent().clearInterface();
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		ToolSession session = SessionManager.getCurrentToolSession();
		session.removeAttribute(StyleHelper.CURRENT_STYLE);
		session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);

		session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");

		Wizard wizard = getBase();

		if (wizard.getStyle() != null) {
			session.setAttribute(StyleHelper.CURRENT_STYLE_ID, wizard.getStyle().getId().getValue());
		}

		try {
			context.redirect("osp.style.helper/listStyle");
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		}
		return null;
	}

	public WizardTool getParent() {
		return parent;
	}

	public void setParent(WizardTool parent) {
		this.parent = parent;
	}

	public DecoratedCategory getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(DecoratedCategory rootCategory) {
		this.rootCategory = rootCategory;
	}

	public boolean isFirst() {
		return getPrev() == null;
	}

	public boolean isLast() {
		return getNext() == null;
	}

	public String moveUp() {
		return switchSeq(getPrev());
	}

	public String moveDown() {
		return switchSeq(getNext());
	}

	protected String switchSeq(DecoratedWizard other) {
		int otherSeq = other.getBase().getSequence();
		int thisSeq = getBase().getSequence();
		other.getBase().setSequence(thisSeq);
		getBase().setSequence(otherSeq);
		getParent().getWizardManager().saveWizard(getBase());
		getParent().getWizardManager().saveWizard(other.getBase());
		return null;
	}

	public DecoratedWizard getNext() {
		return next;
	}

	public void setNext(DecoratedWizard next) {
		this.next = next;
	}

	public DecoratedWizard getPrev() {
		return prev;
	}

	public void setPrev(DecoratedWizard prev) {
		this.prev = prev;
	}

	public boolean isOwner() {

		boolean isOwner = false;

		String userId = SessionManager.getCurrentSessionUserId();

		if (userId != null && base.getOwner() != null && base.getOwner().getId() != null)
			isOwner = userId.equals(getBase().getOwner().getId().getValue());

		return isOwner;
	}

	public String processActionChangeUser(ValueChangeEvent e) {
		getParent().clearInterface();
		getParent().setCurrentUserId(e.getNewValue().toString());
		processActionRunWizardHelper();
		return getParent().LIST_PAGE;
	}

	public String processActionFilterGroup(ValueChangeEvent e) {
		getParent().clearInterface();
		return getParent().LIST_PAGE;
	}

	public ExternalContext processActionRunWizardHelper() {
		getParent().clearInterface();

		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

		setBase(parent.getWizardManager().getWizard(getBase().getId()));
		rootCategory = new DecoratedCategory(base.getRootCategory(), parent);
		getParent().setCurrent(this);
		setRunningWizard(new DecoratedCompletedWizard(getParent(), this,
				parent.getWizardManager().getCompletedWizard(getBase(), getParent().getCurrentUserId())));

		return context;
	}
	public String processActionRunWizard() {

		ExternalContext context = processActionRunWizardHelper();

		try {
			context.redirect("osp.wizard.run.helper/runWizardGuidance");
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		}
		return null;
	}

	public String processActionEditInstructions()
	{
		parent.processActionGuidanceHelper(getBase(), 1);
		return null;
	}

	public String processActionEditRationale()
	{
		parent.processActionGuidanceHelper(getBase(), 2);
		return null;
	}

	public String processActionEditExamples()
	{
		parent.processActionGuidanceHelper(getBase(), 3);
		return null;
	}
	
	public String processActionEditRubric(){
		parent.processActionGuidanceHelper(getBase(), 4);
		return null;
	}
	
	public String processActionEditExpectations(){
		parent.processActionGuidanceHelper(getBase(), 5);
		return null;
	}
	
	
	

	public DecoratedCompletedWizard getRunningWizard() {
		return runningWizard;
	}

	public void setRunningWizard(DecoratedCompletedWizard runningWizard) {
		this.runningWizard = runningWizard;
	}

	public GuidanceItem getInstruction() {
		if (getBase().getGuidance() == null) {
			return null;
		}
		return getBase().getGuidance().getInstruction();
	}

	public GuidanceItem getExample() {
		if (getBase().getGuidance() == null) {
			return null;
		}
		return getBase().getGuidance().getExample();
	}

	public GuidanceItem getRationale() {
		if (getBase().getGuidance() == null) {
			return null;
		}
		return getBase().getGuidance().getRationale();
	}
	
	public GuidanceItem getRubric() {
		if (getBase().getGuidance() == null) {
			return null;
		}
		return getBase().getGuidance().getRubric();
	}
	
	public GuidanceItem getExpectations() {
		if (getBase().getGuidance() == null) {
			return null;
		}
		return getBase().getGuidance().getExpectations();
	}

	public boolean isGuidanceAvailable() {
		return getBase().getGuidance() != null;
	}

	protected String limitString(String s, int max)
	{
		if (s == null) {
			return "";
		}
		if (s.length() > max) {
			s = s.substring(0, max) + "...";
		}
		return s;
	}

	private void assureAttachmentAccess(Guidance guidance) {
		if (getParent().getAuthzManager().isAuthorized(guidance.getSecurityEditFunction(), guidance.getSecurityQualifier()) ||
				getParent().getAuthzManager().isAuthorized(guidance.getSecurityViewFunction(), guidance.getSecurityQualifier()))
		{
			getParent().getGuidanceManager().assureAccess(guidance);
		}
	}

	public String getGuidanceInstructions() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return null;
		}
		GuidanceItem item = guidance.getInstruction();
		if (item == null) {
			return null;
		}
		return limitString(item.getText(), 100);
	}

	public List getGuidanceInstructionsAttachments() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return new ArrayList();
		}
		GuidanceItem item = guidance.getInstruction();
		if (item == null) {
			return new ArrayList();
		}
		assureAttachmentAccess(guidance);
		return item.getAttachments();
	}

	public String getGuidanceRationale() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return "";
		}
		GuidanceItem item = guidance.getRationale();
		if (item == null) {
			return "";
		}
		return limitString(item.getText(), 100);
	}

	public List getGuidanceRationaleAttachments() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return new ArrayList();
		}
		GuidanceItem item = guidance.getRationale();
		if (item == null) {
			return new ArrayList();
		}
		assureAttachmentAccess(guidance);
		return item.getAttachments();
	}

	public String getGuidanceExamples() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return "";
		}
		GuidanceItem item = guidance.getExample();
		if (item == null) {
			return "";
		}
		return limitString(item.getText(), 100);
	}

	public List getGuidanceExamplesAttachments() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return new ArrayList();
		}
		GuidanceItem item = guidance.getExample();
		if (item == null) {
			return new ArrayList();
		}
		assureAttachmentAccess(guidance);
		return item.getAttachments();
	}

	public String getGuidanceRubric() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return "";
		}
		GuidanceItem item = guidance.getRubric();
		if (item == null) {
			return "";
		}
		return limitString(item.getText(), 100);
	}

	public List getGuidanceRubricAttachments() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return new ArrayList();
		}
		GuidanceItem item = guidance.getRubric();
		if (item == null) {
			return new ArrayList();
		}
		assureAttachmentAccess(guidance);
		return item.getAttachments();
	}
	
	public String getGuidanceExpectations() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return "";
		}
		GuidanceItem item = guidance.getExpectations();
		if (item == null) {
			return "";
		}
		return limitString(item.getText(), 100);
	}

	public List getGuidanceExpectationsAttachments() {
		Guidance guidance = getBase().getGuidance();
		if (guidance == null) {
			return new ArrayList();
		}
		GuidanceItem item = guidance.getExpectations();
		if (item == null) {
			return new ArrayList();
		}
		assureAttachmentAccess(guidance);
		return item.getAttachments();
	}
	
	public List getEvaluators() {
		return parent.getEvaluators(getBase());
	}

	public boolean isNewWizard() {
		return newWizard;
	}

	public void setNewWizard(boolean newWizard) {
		this.newWizard = newWizard;
	}

	public DecoratedCategory getCategory()
	{
		return null;
	}

	public String getIndentString() {
		return "";
	}

	public String getTitle() {
		return getBase().getName();
	}

	public boolean isMoveTarget() {
		return false;
	}

	public boolean getHasChildren() {
		return false;
	}
	public boolean isWizard() {
		return true;
	}

	public String getDeleteMessage() {
		return getParent().getMessageFromBundle("delete_wizard_message", new Object[]{
				base.getName()});
	}

	public DecoratedCompletedWizard getUsersWizard() {
		if (usersWizard == null) {
			setUsersWizard(new DecoratedCompletedWizard(getParent(), this,
					parent.getWizardManager().getCompletedWizard(getBase(), getParent().getCurrentUserId(), false)));
		}
		return usersWizard;
	}

	public void setUsersWizard(DecoratedCompletedWizard usersWizard) {
		this.usersWizard = usersWizard;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public boolean getIsWizardUsed()
	{
		// preview mode is never considered 'in use' (edits should not be prohibited)
		if ( base.isPreview() )
			return false;

		String wizardId = base.getId().getValue();
		List completedWizards = parent.getWizardManager().getCompletedWizardsByWizardId(wizardId);
		for (Iterator i = completedWizards.iterator(); i.hasNext();) 
		{
			CompletedWizard cw = (CompletedWizard)i.next();

			List reviews = parent.getReviewManager().getReviewsByParent( cw.getId().getValue() );
			if ( reviews.size() > 0 )
				return true;
		}
		return false;
	}
}

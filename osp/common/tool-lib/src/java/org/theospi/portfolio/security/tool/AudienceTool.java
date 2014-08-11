/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/security/tool/AudienceTool.java $
 * $Id:AudienceTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
package org.theospi.portfolio.security.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.tool.HelperToolBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 2:54:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudienceTool extends HelperToolBase {

    private AuthorizationFacade authzManager;
    private IdManager idManager;
    private SiteService siteService;
    private ToolManager toolManager;
    private AgentManager agentManager;

    private String[] selectedArray;
    private List selectedMembers = null;
    private List originalMembers = null;
	private List selectedRoles = null;
	private String searchEmails;
    private Site site;

    /**
     * ***********************************
     */
    private String[] availableRoleArray;
    private List availableRoleList;

    private String[] selectedRoleArray;
    private List selectedRoleList;

    private String[] availableUserArray;
    private List availableUserList = null;

    private String[] selectedUserArray;
    private List selectedUserList;

    /**
     * **********************************
     */

    private static String TOOL_JSF = "tool";
    private String SELECTED_MEMBERS = "org.theospi.portfolio.security.tool.SELECTED_MEMBERS";
    private String function;
    private Id qualifier;
	 
    private SelectItemComparator selectItemComparator = new SelectItemComparator();
    private static final String UISERVICE = "ui.service";
    
    /** This accepts email addresses */
    private static final Pattern emailPattern = Pattern.compile(
          "^" +
             "(?>" +
                "\\.?[a-zA-Z\\d!#$%&'*+\\-/=?^_`{|}~]+" +
             ")+" + 
          "@" + 
             "(" +
                "(" +
                   "(?!-)[a-zA-Z\\d\\-]+(?<!-)\\." +
                ")+" +
                "[a-zA-Z]{2,}" +
             "|" +
                "(?!\\.)" +
                "(" +
                   "\\.?" +
                   "(" +
                      "25[0-5]" +
                   "|" +
                      "2[0-4]\\d" +
                   "|" +
                      "[01]?\\d?\\d" +
                   ")" +
                "){4}" +
             ")" +
          "$"
          );
    
    private List roleMemberList = null;
    private List groupMemberList = null;
    private String LIST_SEPERATOR = "__________________";
    private boolean publicAudience = false;
    private String message;

    private MemberSort memberSort = new MemberSort();
    private WorksiteManager worksiteManager;
    
    public static final String UNASSIGNED_GROUP = "UNASSIGNED_GROUP";
    
    /*************************************************************************/
    
    public void sortItemList(List<SelectItem> list) {
		Collections.sort(list, new Comparator<SelectItem>() {
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareToIgnoreCase(o2.getLabel());
			}
		});
	}

    protected List fillMemberList() {
        List returned = new ArrayList();
        originalMembers = new ArrayList();

        String id = (String) getAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
        setQualifier(getIdManager().getId(id));
        setFunction((String) getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION));

        List authzs = getAuthzManager().getAuthorizations(null, getFunction(), getQualifier());

        for (Iterator i = authzs.iterator(); i.hasNext();) {
            Authorization authz = (Authorization) i.next();
            returned.add(new DecoratedMember(this, authz.getAgent()));
            originalMembers.add(authz.getAgent());
        }

        return returned;
    }

    private List getSelectedMembers() {
        if (getAttribute(SELECTED_MEMBERS) == null) {
            selectedMembers = fillMemberList();
            setAttribute(SELECTED_MEMBERS, selectedMembers);
        }

        if (selectedMembers == null)
            selectedMembers = new ArrayList();
        return selectedMembers;
    }

    private void setSelectedMembers(List selectedMembers) {
        this.selectedMembers = selectedMembers;
    }
	 
    private Id getQualifier() {
        return qualifier;
    }

    private void setQualifier(Id qualifier) {
        this.qualifier = qualifier;
    }

    private String getFunction() {
        return function;
    }

    private void setFunction(String function) {
        this.function = function;
    }
    
    public String processActionCancel() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getCancelTarget());
        if(this.isInviteFeedbackAudience()){
        	session.setAttribute("feedbackAction", "cancel");
        }
        clearAudienceSelectionVariables();
        selectedArray = null;
        message = null;
        return returnToCaller();
    }

    public String processActionSaveNotify() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getSaveNotifyTarget());
        if(this.isInviteFeedbackAudience()){
        	session.setAttribute("feedbackAction", "save");
        }
        save();
        clearAudienceSelectionVariables();
        return returnToCaller();
    }
    
    public String processActionNotify(){
    	ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getSaveTarget());
        
        if (getSelectedArray().length == 0) {
        	FacesContext.getCurrentInstance().addMessage(null,
                    getFacesMessageFromBundle("please_select", (new Object[]{})));
        	return "";
        }
        
        notifyAudience();
        if(isInviteFeedbackAudience()){
        	if(getMessage() != null){
				String emailMessage = getMessage();
				session.setAttribute("emailMessage", emailMessage);
        	}
        }
    	clearAudienceSelectionVariables();
        //processActionClearFilter();
        selectedArray = null;
        message = null;
        
        return returnToCaller();
    }

    public String processActionBack() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getBackTarget());
        clearAudienceSelectionVariables();
        return returnToCaller();
    }

    public String getCancelTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
    }

    private String getSaveTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
    }

    public String getSaveNotifyTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET);
    }

    public String getBackTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET);
    }
    
    public String getMatrixReviewerObjectId() {
        return (String) getAttribute(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID);
    }
    
    public String getMatrixReviewFunction(){
    	 return (String) getAttribute(AudienceSelectionHelper.MATRIX_REVIEWER_FUNCTION);
    }

    public String getAudienceFunction() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_FUNCTION);
    }
    
    public boolean isGuestUserEnabled() {
        if ( ServerConfigurationService.getBoolean("notifyNewUserEmail",true) )
           return true;
        else
           return false;
    }

    public boolean isPortfolioAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO) )
           return true;
        else
           return false;
    }

    public boolean isWizardAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD) )
           return true;
        else
           return false;
    }

    public boolean isMatrixAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX) )
           return true;
        else
           return false;
    }
    
    public boolean isMatrixAudienceReview() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX_REVIEW) )
           return true;
        else
           return false;
    }
    
    public boolean isInviteFeedbackAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_INVITE_FEEDBACK) )
           return true;
        else
           return false;
    }
    
    public boolean isWorksiteLimited() {
        if ( isPortfolioAudience() )
            return false;
         else
            return true;
     }

    /**
     ** Return current site for this matrix/wizard
     **/   
    public Site getSite() {
        String currentSiteId = (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SITE);
        if ( site == null || ! currentSiteId.equals(site.getId()) ) {
            try {
                site = getSiteService().getSite(currentSiteId);
            }
            catch (IdUnusedException e) {
                throw new RuntimeException(e);
            }
        }
        return site;
    }


    public List getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    /**
     * This checks that the parameter does not contain the 
     * invalidEmailInIdAccountString string from sakai.properties
     * 
     * @param id String email address
     * @return boolean 
     */
    protected boolean isDomainAllowed(String email)
    {
       String invalidEmailInIdAccountString = ServerConfigurationService.getString("invalidEmailInIdAccountString", null);
       
       if(invalidEmailInIdAccountString != null) {
          String[] invalidDomains = invalidEmailInIdAccountString.split(",");
          
          for(int i = 0; i < invalidDomains.length; i++) {
             String domain = invalidDomains[i].trim();
             
             if(email.toLowerCase().indexOf(domain.toLowerCase()) != -1) {
                return false;
             }
          }
       }
       return true;
    }
    
    public String processActionAddEmailUser() {
        boolean worksiteLimited = ! isPortfolioAudience() && ! isInviteFeedbackAudience();
        
        String emailOrUser = getSearchEmails();
        boolean guestUserEnabled = isGuestUserEnabled();
        if(isInviteFeedbackAudience()){
        	guestUserEnabled = false;
        }
        
        if ( ! findByEmailOrUserName(emailOrUser, guestUserEnabled, worksiteLimited) ) {
           if ( guestUserEnabled )
              FacesContext.getCurrentInstance().addMessage(null,
                                                           getFacesMessageFromBundle("email_user_not_found", (new Object[]{emailOrUser})));
           else
              FacesContext.getCurrentInstance().addMessage(null,
                                                           getFacesMessageFromBundle("user_not_found", (new Object[]{emailOrUser})));
        } 
        else {
            setSearchEmails("");
        }
        return "tool";
    }

    public String processActionAddGroup() {
        for (Iterator i = getSelectedRoles().iterator(); i.hasNext();) {
            String roleId = (String) i.next();
            Agent role = getAgentManager().getAgent(getIdManager().getId(roleId));
            addAgent(role, "role_exists");
        }
        getSelectedRoles().clear();
        return "tool";
    }
    
    /**
     * @param displayName - for a guest user, this is the email address
     * 
     */
    protected boolean findByEmailOrUserName(String displayName, boolean allowGuest, boolean worksiteLimited) {
        List userList = getAgentManager().findByProperty(AgentManager.TYPE_EID, displayName);

        // find users by email
        if (validateEmail(displayName)) {
            List<Agent> emailUserList = getAgentManager().findByProperty(AgentManager.TYPE_EMAIL, displayName);
            if (userList == null) {
                userList = emailUserList;
            } else if (emailUserList != null) {
                for (Agent agent : emailUserList) {
                    if (!userList.contains(agent)) {
                        userList.add(agent);
                    }
                }
            }
        }
        
        // if guest users not allowed and user was not found, return false
        if ( ! allowGuest && userList == null) 
           return false;
        
        if (allowGuest && !worksiteLimited && userList == null) {
            if(validateEmail(displayName) && isDomainAllowed(displayName)) {
                Agent agent = getAgentManager().createAgent(displayName, getIdManager().getId(displayName) );
                if (agent != null) {
                    notifyNewUserEmail( agent );
                 }
                //instantiate userList b/c it is null
                userList = new ArrayList();
         	    userList.add(agent);
            }
         }
           
        boolean found = false;

        if(userList != null){
        	for (Iterator i = userList.iterator(); i.hasNext();) {
        		found = true;
        		Agent agent = (Agent) i.next();
        		if (worksiteLimited && !checkWorksiteMember(agent)) {
        			return false;
        		}
        		addAgent(agent, "user_exists");
        	}
        }

        return found;
    }
    
    protected boolean checkWorksiteMember(Agent agent) {
        List roles = agent.getWorksiteRoles(getSite().getId());
        return (roles != null && roles.size() > 0);
    }

    protected void addAgent(Agent agent, String key) {
        DecoratedMember decoratedAgent = new DecoratedMember(this, agent);
        if (!getSelectedMembers().contains(decoratedAgent)) {
            getSelectedMembers().add(decoratedAgent);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    getFacesMessageFromBundle(key, (new Object[]{agent.getDisplayName()})));
        }
    }

    public String processActionSave() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getSaveTarget());
        if(isInviteFeedbackAudience()){
            session.setAttribute("feedbackAction", "save");           
        	//send invitation emails
        	notifyAudience();
        }
        save();     
        clearAudienceSelectionVariables();
        return returnToCaller();
    }
    
    protected void save() {
        List added = new ArrayList();

        for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
            DecoratedMember member = (DecoratedMember) i.next();

            if (originalMembers.contains(member.getBase())) {
                originalMembers.remove(member.getBase());
            } else {
                added.add(member.getBase());
            }
        }
        setSelectedMembers(null);
        addMembers(added);
        removeMembers(originalMembers);
    }

    protected HashMap<String, String> getSelectedUsersEmails(){
    	HashMap<String, String> selectedUserEmails = new HashMap<String, String>();
    	for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
    		DecoratedMember decoratedMember = (DecoratedMember) i.next();
    		if ( ! decoratedMember.getBase().isRole() && decoratedMember.getBase().getId() != null ){ 
    			selectedUserEmails.put(decoratedMember.getBase().getId().toString(), decoratedMember.getEmail());
    		}           
    	}

    	return selectedUserEmails;    	
    }

    protected void notifyAudience(){
    	if(isInviteFeedbackAudience()){
    		//the email notifications will be processed by HibernateMatrixManagerImpl
    		HashMap<String, String> emailList = getSelectedUsersEmails();    
    		HashMap<String, String> selectedEmailList = new HashMap<String, String>();
    		Set<User> groupAwareUsers = getUserList(getSite().getId(), null, false, new ArrayList<Group>(getGroupList(getSite(), false)));
    		String currentUserId = SessionManager.getCurrentSessionUserId();
    		Set members = getSite().getMembers();
    		for(String selectedId : getSelectedArray()){
    			if (selectedId.startsWith("ROLE")) {
    				//The only way a role could have been selected is when the role was selected by the external user (ie. matrix author).  
    				//Make sure that we are group aware so use the list of members from the external members function
    				String role = selectedId.substring(5, selectedId.length());
    				for (Iterator j = members.iterator(); j.hasNext();) {
    					Member member = (Member) j.next();
    					if (!currentUserId.equals(member.getUserId()) && member.getRole().getId().equals(role)  && containsUserId(groupAwareUsers, member.getUserId())) {
    						String email;
    						try {
    							email = UserDirectoryService.getUser(member.getUserId()).getEmail();

    							if(!selectedEmailList.containsKey(member.getUserId())) {
    								selectedEmailList.put(member.getUserId(), email);
    							}
    						} catch (UserNotDefinedException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}    				
					}
				}else{
					String email = emailList.get(selectedId);
					if(email != null && !"".equals(email)){
						selectedEmailList.put(selectedId, email);
					}
				}
    		}
    		ToolSession session = SessionManager.getCurrentToolSession();
    		session.setAttribute("extraEmailAddrs", selectedEmailList);
    	}else{
    		String url;
    		String emailMessage = "";
    		String subject = "";
    		User user = UserDirectoryService.getCurrentUser();

    		subject = getMessageFromBundle("portfolioSubject", null);
    		url = ServerConfigurationService.getServerUrl() +
    		"/osp-presentation-tool/viewPresentation.osp?id=" + this.getQualifier().getValue();
    		url += "&" + Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();

    		emailMessage = getMessage() + getMessageFromBundle("portfolioBody", 
    				new Object[]{user.getDisplayName()}) + " " + getPageContext() +
    				getMessageFromBundle("portfolioLink", new Object[]{url});



    		try {

    			String from = ServerConfigurationService.getString("setup.request", 
    					"postmaster@".concat(ServerConfigurationService.getServerName()));


    			String[] emailList = null;

    			emailList = getSelectedArray();



    			List sentEmailAddrs = new ArrayList();
    			HashMap<String, String> selectedUserEmails = getSelectedUsersEmails();
    			for (int i = 0; i < emailList.length; i++) {
    				String toUser = emailList[i];
    				if (toUser.startsWith("ROLE")) {
    					String role = toUser.substring(5, toUser.length());
    					Set members = getSite().getMembers();
    					for (Iterator j = members.iterator(); j.hasNext();) {
    						Member member = (Member) j.next();
    						if (member.getRole().getId().equals(role)) {
    							String email = UserDirectoryService.getUser(member.getUserId()).getEmail();
    							if (validateEmail(email) && !sentEmailAddrs.contains(email)) {
    								sentEmailAddrs.add(email);
    								EmailService.send(from, email,
    										subject, emailMessage, null, null, null);

    							}
    						}
    					}

    				} else {
    					String userEmail = selectedUserEmails.get(toUser);
    					if (validateEmail(userEmail) && !sentEmailAddrs.contains(userEmail)) {
    						sentEmailAddrs.add(userEmail);
    						EmailService.send(from, userEmail,
    								subject, emailMessage, null, null, null);
    					}
    				}
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }


    
    private void notifyNewUserEmail(Agent guest) {
        String from = ServerConfigurationService.getString("setup.request", null);
        if (from == null) {

            from = "postmaster@".concat(ServerConfigurationService.getServerName());
        }
        String productionSiteName = ServerConfigurationService.getString("ui.service", "");
        String productionSiteUrl = ServerConfigurationService.getPortalUrl();

        String to = guest.getDisplayName();
        String headerTo = to;
        String replyTo = to;
        String message_subject = getMessageFromBundle("email.guestusernoti", new Object[]{productionSiteName});
        String content = "";

        if (from != null && to != null) {
            StringBuilder buf = new StringBuilder();
            buf.setLength(0);

            // email body
            buf.append(to + ":\n\n");
            buf.append(getMessageFromBundle("email.addedto", new Object[]{productionSiteName, productionSiteUrl}) + "\n\n");
            buf.append(getMessageFromBundle("email.simpleby", new Object[]{UserDirectoryService.getCurrentUser().getDisplayName()}) + "\n\n");
            buf.append(getMessageFromBundle("email.userid", new Object[]{to}) + "\n\n");
            buf.append(getMessageFromBundle("email.password", new Object[]{guest.getPassword()}) + "\n\n");

            content = buf.toString();
            EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
        }
    }
    

    protected boolean validateEmail(String displayName) {
       if (!emailPattern.matcher(displayName).matches()) {
          return false;
       }

       return true;
    }
    
    
    protected List getMatrixReviewersList() {
		List returnList = new ArrayList();
		

		List evaluators = getAuthzManager().getAuthorizations(null,
				getMatrixReviewFunction(), getIdManager().getId(getMatrixReviewerObjectId()));

		for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
			Authorization az = (Authorization) iter.next();
			Agent agent = az.getAgent();
			

			returnList.add(new DecoratedMember(this, agent).getEmail());
		}

		return returnList;
	}
    

    protected void addMembers(List added) {
        for (Iterator i = added.iterator(); i.hasNext();) {
            Agent agent = (Agent) i.next();

            getAuthzManager().createAuthorization(agent,
                    getFunction(), getQualifier());
        }
    }

    protected void removeMembers(List added) {
        for (Iterator i = added.iterator(); i.hasNext();) {
            Agent agent = (Agent) i.next();

            getAuthzManager().deleteAuthorization(agent,
                    getFunction(), getQualifier());
        }
    }

   /** Format role name, optionally including site title
    **/
   private String formatRole( Site site, String roleName ) {
   
      if ( site == null || site.equals(getSite()) ) {
         return roleName;
      }
      else {
         StringBuilder buf = new StringBuilder( roleName );
         buf.append(" (");
         buf.append( site.getTitle() );
         buf.append(")");
         return buf.toString();
      }
   
   }
   
    /**
     ** Return list of roles for wizard or matrix
     **/
    public List getRoles() {
        List<SelectItem> returned = new ArrayList<SelectItem>();
        if ( isWorksiteLimited() ) {
	        Site site = getSite();
	        Set roles = site.getRoles();
	           
	        for (Iterator i = roles.iterator(); i.hasNext();) {
	           Role role = (Role) i.next();
	           if ( isWizardAudience() && !role.isAllowed(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD) )
	              continue;
	           Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
	           returned.add(new SelectItem(roleAgent.getId().getValue(), 
	                                       role.getId(), 
	                                       "role"));
	        }
        }
        
        else {
           List siteList = siteService.getSites(SiteService.SelectionType.ACCESS,
                                                null, null, null, 
                                                SiteService.SortType.TITLE_ASC, null);
                                                
           for (Iterator siteIt = siteList.iterator(); siteIt.hasNext();) {
              Site site = (Site)siteIt.next();
              Set roles = site.getRoles();

              for (Iterator roleIt = roles.iterator(); roleIt.hasNext();) {
                 Role role = (Role) roleIt.next();
                 Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
                 returned.add(new SelectItem(roleAgent.getId().getValue(), 
                                             formatRole( site, role.getId() ),
                                             "role"));
              }
           }
        
        }
        
        sortItemList(returned);

        return returned;
    }

    public String[] getAvailableUserArray() {
        return availableUserArray;
    }

    public void setAvailableUserArray(String[] availableUserArray) {
        this.availableUserArray = availableUserArray;
    }

    public List getAvailableUserList() {

        availableUserList = new ArrayList();

        List userMemberList = new ArrayList();
        userMemberList.addAll(getMembersList());

        for (Iterator idx = userMemberList.iterator(); idx.hasNext();) {
            SelectItem availableItem = (SelectItem) idx.next();
            boolean matchFound = false;
            for (Iterator jdx = getSelectedUserList().iterator(); jdx.hasNext();) {
                SelectItem selectedItem = (SelectItem) jdx.next();
                if (selectedItem.getValue().toString().equals(availableItem.getValue().toString())) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound){
                availableUserList.add(availableItem);
            }
        }

        Collections.sort(availableUserList, memberSort);
        return availableUserList;
    }


    public String[] getAvailableRoleArray() {
        return availableRoleArray;
    }

    public void setAvailableRoleArray(String[] availableRoleArray) {
        this.availableRoleArray = availableRoleArray;
    }

    public void setAvailableRoleList(List availableRoleList) {
        this.availableRoleList = availableRoleList;
    }

    public List getAvailableRoleList() {

            availableRoleList = new ArrayList();
            List roleMemberList = new ArrayList();
            roleMemberList.addAll(getRoles());

            for (Iterator idx = roleMemberList.iterator(); idx.hasNext();) {
                SelectItem availableItem = (SelectItem) idx.next();
                boolean matchFound = false;
                for (Iterator jdx = getSelectedRoleList().iterator(); jdx.hasNext();) {
                    SelectItem selectedItem = (SelectItem) jdx.next();
                    if (selectedItem.getValue().toString().equals(availableItem.getValue().toString())) {
                        matchFound = true;
                        break;
                    }

                }
                if (!matchFound){
                    availableRoleList.add(availableItem);
                }
            }

        Collections.sort(availableRoleList, selectItemComparator);
        return availableRoleList;
    }

   /**
    * Get array of selected users 
    */
    public String[] getSelectedUserArray() {
        return selectedUserArray;
    }

   /**
    * Set array of selected users 
    */
    public void setSelectedUserArray(String[] selectedUserArray) {
        this.selectedUserArray = selectedUserArray;
    }

   /**
     * Get list of selected users 
     */
    public List getSelectedUserList() {

            selectedUserList = new ArrayList();
            for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
                DecoratedMember decoratedMember = (DecoratedMember) i.next();
                if (!decoratedMember.getBase().isRole()
                		&& decoratedMember.getBase().getId() != null)
                	try {
                		selectedUserList.add(new SelectItem(decoratedMember
                				.getBase().getId().getValue(), UserDirectoryService
                				.getUser(
                						decoratedMember.getBase().getId()
                						.getValue()).getSortName(),
                		"member"));
                	} catch (UserNotDefinedException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	}
            }
            sortItemList(selectedUserList);
        Collections.sort(selectedUserList, memberSort);
        return selectedUserList;
    }


   /**
    * Get array of selected roles 
    */
    public String[] getSelectedRoleArray() {
        return selectedRoleArray;
    }

   /**
    * Set array of selected roles 
    */
    public void setSelectedRoleArray(String[] selectedRoleArray) {
        this.selectedRoleArray = selectedRoleArray;
    }

   /**
    ** Parse role id and return Site id
    **/
    private Site getSiteFromRoleMember( String roleMember ) {
       Reference ref = EntityManager.newReference( roleMember );
       String siteId = ref.getContainer();
       Site site = null;
       try {
          site = getSiteService().getSite(siteId);
       }
       catch (IdUnusedException e) {
          // tbd - log warning
       }
            
       return site;
    }

   /**
     * Get list of selected roles 
     */
    public List getSelectedRoleList() {

       selectedRoleList = new ArrayList();
       for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
           DecoratedMember decoratedMember = (DecoratedMember) i.next();
           if (decoratedMember.getBase().isRole()) {
              String roleName = decoratedMember.getBase().getDisplayName();
                             
              selectedRoleList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), 
                                                  roleName,
                                                  "role"));
           }
       }
       sortItemList(selectedRoleList);
       Collections.sort(selectedRoleList, selectItemComparator);
       return selectedRoleList;
    }
    
    public List<DecoratedMember> getExternallySelectedGroupAwareMembers(Site site, String function, String objectId){
    	List<DecoratedMember> returnList = new ArrayList<DecoratedMember>();
    	//Make sure that the matrix level selected Users list are only the users who have 
    	//group access to the current user
    	Set<User> usersInGroup = getUserList(site.getId(), null, false, new ArrayList<Group>(getGroupList(site,	false)));
    	
    	 List authzs = getAuthzManager().getAuthorizations(null, function, idManager.getId(objectId));
    	 for (Iterator i = authzs.iterator(); i.hasNext();) {
    		 Authorization authz = (Authorization) i.next();
    		 DecoratedMember dMember = new DecoratedMember(this, authz.getAgent());
    		 if(!returnList.contains(dMember)){
    			 if(dMember.getBase().isRole()){
    				 returnList.add(dMember);
    			 }else if(containsUserId(usersInGroup, dMember.getBase().getId().getValue())){
    				 returnList.add(dMember);
    			 }
    		 }        		 
    	 }
    	 
    	 return returnList;
    }
    
    public List<String> getExternalReviewersForMatrix(){
    	List<DecoratedMember> externalMembers = getExternallySelectedGroupAwareMembers(getSite(), getMatrixReviewFunction(), getMatrixReviewerObjectId());
    	List<String> returnList = new ArrayList<String>();
    	for (DecoratedMember decoratedMember : externalMembers) {
			returnList.add(decoratedMember.getDisplayName());
		}
    	
    	Collections.sort(returnList);
    	return returnList;
    }
    
    public List getSelectedList() {

        List selectedList = new ArrayList();        
        List allSelectedMembers = getSelectedMembers();       
        
        if(isInviteFeedbackAudience()){
        	//add matrix selected feedback audience for user to select and send an email to
        	List<DecoratedMember> externalMembers = getExternallySelectedGroupAwareMembers(getSite(), getMatrixReviewFunction(), getMatrixReviewerObjectId());
        	for (DecoratedMember decoratedMember : externalMembers) {
				if(!allSelectedMembers.contains(decoratedMember)){
					allSelectedMembers.add(decoratedMember);
				}
			}
        }

        for (Iterator i = allSelectedMembers.iterator(); i.hasNext();) {
        	DecoratedMember decoratedMember = (DecoratedMember) i.next();
        	if (decoratedMember.getBase().isRole()) {
        		String roleName = null;

        		if ( isWorksiteLimited() ) {
        			roleName = decoratedMember.getBase().getDisplayName();
        		}
        		else {
        			Site site = getSiteFromRoleMember( decoratedMember.getBase().getId().getValue() );
        			roleName = formatRole( site, decoratedMember.getBase().getDisplayName() ); 
        		}


        		selectedList.add(new SelectItem(decoratedMember.getEmail(), 
        				roleName,
        				"role"));
        	}else if ( ! decoratedMember.getBase().isRole() && decoratedMember.getBase().getId() != null )
        		try {
        			selectedList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), UserDirectoryService
        					.getUser(
        							decoratedMember.getBase().getId()
        							.getValue()).getSortName(), "member"));
        		} catch (UserNotDefinedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        }

        Collections.sort(selectedList, memberSort);
        
        return selectedList;
     }

   /**
     * Set list of selected roles
     */
    public void setSelectedRoleList(List selectedRoleList) {
        this.selectedRoleList = selectedRoleList;
    }

   /** Action to add to list of users
    **/
    public String processActionAddUser() {
        String[] selected = getAvailableUserArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the list to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            SelectItem addItem = removeItems(selected[i], getAvailableUserList());
            addAgent(getAgentManager().getAgent(addItem.getValue().toString()), "user_exists");
            getSelectedUserList().add(addItem);
        }

        return "main";
    }

   /** Action to remove from list of users
    **/
    public String processActionRemoveUser() {
        String[] selected = getSelectedUserArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            for (Iterator idx = getSelectedMembers().iterator(); idx.hasNext();) {
               DecoratedMember member = (DecoratedMember) idx.next();
               if (member.getBase().getId().toString().equals(selected[i])) 
                   idx.remove();
               
               getAvailableUserList().add(removeItems(selected[i], getSelectedUserList()));
           }
        }

        return "main";
    }

   /** Action to add to list of roles
    **/
    public String processActionAddRole() {
        String[] selected = getAvailableRoleArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the list to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            SelectItem addItem = removeItems(selected[i], getAvailableRoleList());
            addAgent(getAgentManager().getAgent(getIdManager().getId(addItem.getValue().toString())), "role_exists");
            getSelectedRoleList().add(addItem);
        }

        return "main";
    }

   /** Action to remove from list of roles
    **/
    public String processActionRemoveRole() {
        String[] selected = getSelectedRoleArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            for (Iterator idx = getSelectedMembers().iterator(); idx.hasNext();) {
               DecoratedMember member = (DecoratedMember) idx.next();
               if (member.getBase().getId().toString().equals(selected[i]))
                   idx.remove();
               
               getAvailableRoleList().add(removeItems(selected[i], getSelectedRoleList()));
           }
        }

        return "main";
    }

    private SelectItem removeItems(String value, List items) {

        SelectItem result = null;
        for (int i = 0; i < items.size(); i++) {
            SelectItem item = (SelectItem) items.get(i);
            if (value.equals(item.getValue())) {
                result = (SelectItem) items.remove(i);
                break;
            }
        }

        return result;
    }

    protected void clearAudienceSelectionVariables() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
        session.removeAttribute(AudienceSelectionHelper.CONTEXT);
        session.removeAttribute(AudienceSelectionHelper.CONTEXT2);
        session.removeAttribute(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID);
        session.removeAttribute(AudienceSelectionHelper.MATRIX_REVIEWER_FUNCTION);
        session.removeAttribute(SELECTED_MEMBERS);
        
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION_INVITE_FEEDBACK);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX_REVIEW);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD);
        
        
    }
    
    protected List getMembersList() {
        Set members = getSite().getMembers();
        List<SelectItem> memberList = new ArrayList<SelectItem>();
        for (Iterator i = members.iterator(); i.hasNext();) {
            Member member = (Member) i.next();

            Agent agent = getAgentManager().getAgent((member.getUserId()));
            //Check for a null agent since the site.getMembers() will return member records for deleted users
            if (agent != null && agent.getId() != null) {
            	DecoratedMember decoratedMember = new DecoratedMember(this, agent);
            	try {
            		memberList.add(new SelectItem(decoratedMember.getBase()
            				.getId().getValue(), UserDirectoryService.getUser(
            						decoratedMember.getBase().getId().getValue())
            						.getSortName(), "member"));
            	} catch (UserNotDefinedException e) {
            		// TODO Auto-generated catch block
            		e.printStackTrace();
            	}
            }
        }

        sortItemList(memberList);
        return memberList;
    }
    
    /**
     * Context (AudienceSelectionHelper.CONTEXT) is used to describe the page/tool
     * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
     * and context 2 is used for the subtitle (ex. matrix cell or wizard page).  If left
     * blank, then nothing displays on the page.  
     * @return
     */
    public String getPageContext(){
    	String context = (String) getAttribute(AudienceSelectionHelper.CONTEXT);
    	return context != null ? context : "";
    }
    
    /**
     * Context2 (AudienceSelectionHelper.CONTEXT2) is used to describe the page/tool
     * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
     * and Context2 is used for the subtitle (ex. matrix cell or wizard page).  If left
     * blank, then nothing displays on the page.  
     * @return
     */
    public String getPageContext2(){
    	String context2 = (String) getAttribute(AudienceSelectionHelper.CONTEXT2);
    	return context2 != null ? context2 : "";
    }
    
    public String getSearchEmails() {
        return searchEmails;
    }

    public void setSearchEmails(String searchEmails) {
        this.searchEmails = searchEmails;
    }
	 
	 
	// Spring Injection methods
    public AuthorizationFacade getAuthzManager() {
        return authzManager;
    }

    public void setAuthzManager(AuthorizationFacade authzManager) {
        this.authzManager = authzManager;
    }

    public IdManager getIdManager() {
        return idManager;
    }

    public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
	 
    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }
	 
   /** 
    ** Comparator for sorting SelectItem objects
    **/
	public class SelectItemComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((SelectItem)o1).getLabel().compareTo( ((SelectItem)o2).getLabel() );
		}
	}
   
   /**
    ** Sort SelectList of member names
    ** (tbd: localize sorting of names)
    **/
   public class MemberSort implements Comparator<SelectItem> {
      
      public int compare(SelectItem o1, SelectItem o2) {
         String n1 = o1.getLabel();
         String n2 = o2.getLabel();
         
         return n1.compareToIgnoreCase(n2);
      }
   }
    
 
	public String[] getSelectedArray() {
		return selectedArray;
	}

	public void setSelectedArray(String[] selectedArray) {
		this.selectedArray = selectedArray;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public WorksiteManager getWorksiteManager() {
		return worksiteManager;
	}

	public void setWorksiteManager(WorksiteManager worksiteManager) {
		this.worksiteManager = worksiteManager;
	}

	
	public Set<User> getUserList(String worksiteId, String filterGroupId, boolean allowAllGroups, List<Group> groups) {
		Set members = new HashSet();
		Set users = new HashSet();

		try {
			Site site = siteService.getSite(worksiteId);
			if (site.hasGroups()) {
				String currentUser = SessionManager.getCurrentSessionUserId();

				if (allowAllGroups && (filterGroupId == null || filterGroupId.equals(""))) {
					members.addAll(site.getMembers());
				}
				else if (filterGroupId != null && UNASSIGNED_GROUP.equals(filterGroupId)) {
					//get all users not in a group
					//TODO Is there a more efficient way to do this?
					Set<Member> siteMembers = site.getMembers();
					for (Member siteMember : siteMembers) {
						Collection memberGroups = site.getGroupsWithMember(siteMember.getUserId());
						if (memberGroups == null || (memberGroups != null && (memberGroups.isEmpty() || memberGroups.size() == 0))) {
							members.add(siteMember);
						}
					}
				}
				else {
					for (Iterator iter = groups.iterator(); iter.hasNext();) {
						Group group = (Group) iter.next();
						// TODO: Determine if Java loop invariants are optimized out
						if (filterGroupId == null || "".equals(filterGroupId)
								|| filterGroupId.equals(group.getId())) {
							members.addAll(group.getMembers());
						}
					}
				}
			} else {
				members.addAll(site.getMembers());
			}

			for (Iterator memb = members.iterator(); memb.hasNext();) {
				try {
					Member member = (Member) memb.next();
					users.add(UserDirectoryService.getUser(member.getUserId()));
				} catch (UserNotDefinedException e) {
			//		logger.warn("Unable to find user: " + e.getId() + " "
			//				+ e.toString());
				}
			}
		} catch (IdUnusedException e) {
		//	logger.error("", e);
		}
		return users;
	}

	public Set getGroupList(Site site, boolean allowAllGroups) {
		Set groups = new HashSet();
		if (site.hasGroups()) {
			String currentUser = SessionManager.getCurrentSessionUserId();
			if (allowAllGroups) {
				groups.addAll(site.getGroups());
			}
			else {
				groups.addAll(site.getGroupsWithMember(currentUser));
			}
		}
		return groups;
	}

	public boolean containsUserId(Set<User> userList, String id){
		if(id == null || userList == null)
			return false;
		
		for (User user : userList) {
			if(user.getId().equals(id))
				return true;
		}
		return false;
	}
}

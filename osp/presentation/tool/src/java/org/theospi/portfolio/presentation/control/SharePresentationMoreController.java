/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2008, 2009 The Sakai Foundation
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;

import org.theospi.portfolio.security.Authorization;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.presentation.support.AgentWrapper;
import org.theospi.portfolio.presentation.support.PresentationService;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.email.api.EmailService; 
import org.sakaiproject.user.api.UserDirectoryService; 

/**
 **/
public class SharePresentationMoreController extends AbstractPresentationController implements Controller {
   protected final Log logger = LogFactory.getLog(getClass());
   private ResourceLoader rl = new ResourceLoader("org.theospi.portfolio.presentation.bundle.Messages");
   private ServerConfigurationService serverConfigurationService;
   private SiteService siteService;
   private EmailService emailService;
   private UserDirectoryService userDirectoryService;
   
   private UserAgentComparator userAgentComparator = new UserAgentComparator();
   private RoleAgentComparator roleAgentComparator = new RoleAgentComparator();
   private GroupComparator     groupComparator     = new GroupComparator();
   
   private final String SHAREBY_KEY    = "shareBy";
   private final String SHAREBY_BROWSE = "share_browse";
   private final String SHAREBY_GROUP  = "share_group";
   private final String SHAREBY_SEARCH = "share_search";
   private final String SHAREBY_EMAIL  = "share_email";
   private final String SHAREBY_ROLE   = "share_role";
   private final String SHAREBY_ALLROLE= "share_allrole";
   
    /** This accepts email addresses */
    protected static final Pattern emailPattern = Pattern.compile(
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
    
 
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      // Get specified portfolio/presentation      
      Map model = new HashMap();
      boolean returnRedirect = false;
      Presentation presentation = (Presentation) requestModel;
      presentation = getPresentationManager().getPresentation(presentation.getId());
      model.put("id", presentation.getId().getValue());
      
      // Check if request to return to previous page 
      if ( request.get("back") != null || request.get("back_add") != null )
         returnRedirect = true;
         
      boolean myWorkspace = getSiteService().isUserSite(presentation.getSiteId());
      model.put("presentation", presentation);
      model.put("hasGroups", getHasGroups(presentation.getSiteId()));
      model.put("myWorkspace", myWorkspace);
      model.put("guestEnabled", getGuestUserEnabled());
      model.put("baseUrl", PresentationService.VIEW_PRESENTATION_URL);
      
      String shareBy = (String)request.get(SHAREBY_KEY);
      if ( shareBy==null || shareBy.equals("") )
         shareBy = myWorkspace ? SHAREBY_SEARCH : SHAREBY_BROWSE;
      model.put(SHAREBY_KEY, shareBy);

      // Update list of Shared-with Users         
      List shareList = getShareList(presentation);
      
      if (shareBy.equals(SHAREBY_EMAIL) || shareBy.equals(SHAREBY_SEARCH) ) 
      {
         String shareUser = (String)request.get("share_user");
         if ( shareUser != null && !shareUser.equals("") ) {
            String errMsg = addUserByEmailOrId(shareBy, shareUser, shareList);
            if ( errMsg != null )
            {
               model.put("errMsg", rl.getFormattedMessage(errMsg, new Object[]{shareUser}) );
               returnRedirect = false;
            }
         }
      }
      else if ( shareBy.equals(SHAREBY_BROWSE) || shareBy.equals(SHAREBY_GROUP) )
      {
         List groupList = null;
         if ( shareBy.equals(SHAREBY_GROUP) ) {
            groupList = getGroupList(presentation.getSiteId(), request);
            if ( ! returnRedirect )
               model.put("groupList", groupList );
         }

         List availList = getAvailableUserList(presentation.getSiteId(), shareList, groupList);
         updateAvailList( shareBy, request, presentation, shareList, availList );
         if ( ! returnRedirect )
            model.put("availList", availList );
      }
      else if ( shareBy.equals(SHAREBY_ROLE) || shareBy.equals(SHAREBY_ALLROLE) )
      {
         List availList = getAvailableRoleList(shareBy, presentation.getSiteId(), shareList);
         updateAvailList( shareBy, request, presentation, shareList, availList );
         if ( ! returnRedirect )
            model.put("availList", availList );
      }
      
      // Check if request to return to previous page 
      // Note: if next view is a redirect, do _not_ include large lists in model
      if ( returnRedirect )
         return new ModelAndView("back", model);
      else
         return new ModelAndView("share", model);
   }

   /**
    ** Add specified user (or guest-user/email-address) to the shareList
    ** 
    ** @param shareBy indicates whether user or email-address is specified
    ** @param shareUser user to share with (userId or email-address)
    ** @param shareList current list of shared users
    ** @return null if successful, otherwise an error message property is returned
    ** 
    **/
   private String addUserByEmailOrId( String shareBy, String shareUser, List shareList ) {
      List userList = getAgentManager().findByProperty(AgentManager.TYPE_EID, shareUser);
      
      // Check if user not found (and not share-by-email or guest user)
      if ( userList==null && shareBy.equals(SHAREBY_SEARCH) ) {
         return "share_err_user";
      }
      
      // Otherwise if user not found and this is share-by-email or guest user (assume SHAREBY_EMAIL)
      else if ( userList == null ) {
         if ( validateEmail(shareUser) ) {
            Agent agent = getAgentManager().createAgent(shareUser, getIdManager().getId(shareUser) );
            if (agent != null) {
               notifyNewUserEmail( agent );
               shareList.add( agent );
            }
            else {
               return "share_err_email";
            }
         }
         else {
            return "share_err_email";
         }
      }
      
      // Check for duplciates
      else if ( isUserShared( (Agent)userList.get(0), shareList) ) {
         return "share_err_dup";
      }
      
      // Otherwise, user is found; add to the shareList
      else {
         shareList.add( userList.get(0) );
      }
       
      return null;
   }
   
   /** Check if given user is already in shareList
    **/
   private boolean isUserShared( Agent agent, List shareList ) {
      for (Iterator it = shareList.iterator(); it.hasNext();) {
         Agent shareUser = (Agent) it.next();
         if ( shareUser.getId().getValue().equals( agent.getId().getValue() ) )
            return true;
      }
      return false;
   }

    /**
     ** Verify syntax of email adddress and verify it does not
     ** contain the invalidEmailInIdAccountString string from sakai.properties
     ** 
     ** @param email email address string
     ** @return boolean true if valid, otherwise false
     **/
    protected boolean validateEmail(String email) {
       if (!emailPattern.matcher(email).matches())
          return false;
          
       String invalidEmailInIdAccountString = getServerConfigurationService().getString("invalidEmailInIdAccountString", null);
       
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

   /**
    ** Notify specified guest user that they have been added as a guest user
    ** TBD: change to use email template service
    **/
    private void notifyNewUserEmail(Agent guest) {
        String from = getServerConfigurationService().getString("setup.request", null);
        if (from == null) {

            from = "postmaster@".concat(getServerConfigurationService().getServerName());
        }
        String productionSiteName = getServerConfigurationService().getString("ui.service", "");
        String productionSiteUrl = getServerConfigurationService().getPortalUrl();

        String to = guest.getDisplayName();
        String headerTo = to;
        String replyTo = to;
        String message_subject = rl.getFormattedMessage("email.guestusernoti", new Object[]{productionSiteName});
        String content = "";

        if (from != null && to != null) {
            StringBuilder buf = new StringBuilder();
            buf.setLength(0);

            // email body
            buf.append(to + ":\n\n");
            buf.append(rl.getFormattedMessage("email.addedto", new Object[]{productionSiteName, productionSiteUrl}) + "\n\n");
            buf.append(rl.getFormattedMessage("email.simpleby", new Object[]{getUserDirectoryService().getCurrentUser().getDisplayName()}) + "\n\n");
            buf.append(rl.getFormattedMessage("email.userid", new Object[]{to}) + "\n\n");
            buf.append(rl.getFormattedMessage("email.password", new Object[]{guest.getPassword()}) + "\n\n");

            content = buf.toString();
            getEmailService().send(from, to, message_subject, content, headerTo, replyTo, null);
        }
    }

   /**
    ** get session-based share list
    **/
   private List getShareList( Presentation presentation ) {
      Session session = SessionManager.getCurrentSession();
      List shareList = (List)session.getAttribute(SharePresentationController.SHARE_LIST_ATTRIBUTE+presentation.getId().getValue());
      return shareList;
   }
   
   /**
    ** set session-based share list
    **/
   private void setShareList( Presentation presentation, List shareList ) {
      Session session = SessionManager.getCurrentSession();
      session.setAttribute(SharePresentationController.SHARE_LIST_ATTRIBUTE+presentation.getId().getValue(), shareList);
   }
   
   /** 
    ** Check if adding user by email is enabled/disabled
    **/   
   private Boolean getGuestUserEnabled() {
      if ( getServerConfigurationService().getBoolean("notifyNewUserEmail",true) )
         return Boolean.valueOf(true);
      else
         return Boolean.valueOf(false);
   }

   /**
    ** Check if presentation's worksite has groups defined and return true/false
    **/
   private Boolean getHasGroups( String siteId ) {
      try {
         Site site = getSiteService().getSite(siteId);
         return Boolean.valueOf( site.hasGroups() );
      }
      catch (Exception e) {
         logger.warn(e.toString());
      }
      return Boolean.valueOf(false);
   }
    
   /**
    ** Check for share list changes from form submission and update shareList and availList if necessary
    **
    ** @return true if update was necessary, otherwise false
    **/
   private boolean updateAvailList( String shareBy, Map request, Presentation presentation, List shareList, List availList ) {
      boolean mods = false;
      ArrayList selectedList = new ArrayList();
      ArrayList newAvailList = new ArrayList();
      
      if (shareBy.equals(SHAREBY_BROWSE) || shareBy.equals(SHAREBY_GROUP))
      {
         for (Iterator it = availList.iterator(); it.hasNext();) {
            Agent availItem = (Agent) it.next();
            if ( request.get(availItem.getId().getValue()) != null )
            {
               mods = true;
               selectedList.add( availItem );
            }
            else {
               newAvailList.add( availItem );
            }
         }
      }
      
      else // (shareBy.equals(SHAREBY_ROLE) || shareBy.equals(SHAREBY_ALLROLE))
      {
         for (Iterator it = availList.iterator(); it.hasNext();) {
            AgentWrapper availItem = (AgentWrapper) it.next();
            if ( request.get(availItem.getId().getValue()) != null )
            {
               mods = true;
               selectedList.add( availItem );
            }
            else {
               newAvailList.add( availItem );
            }
         }
      }
      
      if ( mods ) {
         // Add selected items to shareList and save
         shareList.addAll(selectedList);
         setShareList(presentation, shareList);
         
         // Delete selected items from availList
         availList.clear();
         availList.addAll(newAvailList);
      }
      
      return mods;
   }

   /** Return list of all groups associated with the given site
    **/   
   private List getGroupList( String siteId, Map request ) {
      List groupsList = new ArrayList();
      Site site;
      
      try {
         site = getSiteService().getSite(siteId);
      }
      catch ( Exception e ) {
         logger.warn(e.toString());
         return groupsList;
      }
      
      Collection groups = site.getGroups();
      String selectedGroup = (String)request.get("groups");
      for (Iterator i = groups.iterator(); i.hasNext();) {
         Group group = (Group) i.next();
         boolean checked = false;
         if ( selectedGroup == null )
            selectedGroup = group.getId();
         if ( selectedGroup.equals(group.getId()) )
            checked = true;
         groupsList.add(new GroupWrapper( group, checked ));
      }
      
      Collections.sort(groupsList, groupComparator);
      return groupsList;
   }

   /** Return list of site users (not yet shared) users, optionally filtered by specified group
    **/
   private List getFilteredMembersList( String siteId, List groupList ) {
      Site site;
      Set members = new HashSet();
      List memberList = new ArrayList();
      
      try {
         site = getSiteService().getSite(siteId);
      } 
      catch (Exception e) {
         logger.warn(e.toString());
         return memberList;
      }

      // Find members of selected groups
      if ( groupList != null ) {
         for ( Iterator gIt=groupList.iterator(); gIt.hasNext(); ) {
            GroupWrapper group = (GroupWrapper)gIt.next();
            if ( group.getChecked() )
               members.addAll( site.getGroup( group.getId()).getMembers() );
         }
      }
      
      // If no groups are available or selected
      if ( members.size() == 0 ) 
         members = site.getMembers(); 
      
      for (Iterator it=members.iterator(); it.hasNext(); ) {
         String userId = ((Member)it.next()).getUserId();
         
         // Check for a null agent since the site.getMembers() will return member records for deleted users
         Agent agent = getAgentManager().getAgent(userId);
         if (agent != null && agent.getId() != null) 
            memberList.add(agent);
      }
      
      return memberList;
   }

   /** Return list of available users (i.e. not in shareList) optionally filtered by group
    **/
   private List getAvailableUserList( String siteId, List shareList, List groupList ) {
      ArrayList availableUserList = new ArrayList();

      ArrayList userMemberList = new ArrayList();
      userMemberList.addAll(getFilteredMembersList(siteId, groupList));

      for (Iterator it1 = userMemberList.iterator(); it1.hasNext();) {
         Agent availableItem = (Agent)it1.next();
         boolean matchFound = false;
         
         for (Iterator it2 = shareList.iterator(); it2.hasNext();) {
            Agent selectedItem = (Agent) it2.next();
            if (selectedItem.getId().getValue().equals(availableItem.getId().getValue())) {
               matchFound = true;
               break;
            }
         }
         if (!matchFound){
            availableUserList.add(availableItem);
         }
      }
      
      Collections.sort(availableUserList, userAgentComparator);
      return availableUserList;
   }
   
   /* Return list of available roles
    */
   private List getAvailableRoleList( String shareBy, String siteId, List shareList ) {
      ArrayList availableRoleList = new ArrayList();
      ArrayList roleMemberList = new ArrayList();
      
      if ( shareBy.equals(SHAREBY_ROLE) )
         roleMemberList.addAll(getRoles(siteId));
      else // (shareBy.equals(SHAREBY_ALLROLE)
         roleMemberList.addAll(getRoles(null));
      
      for (Iterator it1 = roleMemberList.iterator(); it1.hasNext();) {
         AgentWrapper availableItem = (AgentWrapper)it1.next();
         boolean matchFound = false;
         
         for (Iterator it2 = shareList.iterator(); it2.hasNext();) {
            Agent selectedItem = (Agent) it2.next();
            if (selectedItem.getId().getValue().equals(availableItem.getId().getValue())) {
               matchFound = true;
               break;
            }
         }
         if (!matchFound){
            availableRoleList.add(availableItem);
         }
      }
      
      Collections.sort(availableRoleList, roleAgentComparator);
      return availableRoleList;
   }
   
    /**
     ** Return list of roles for this or all worksites
     **/
   public List getRoles( String siteId ) {
        List roleList = new ArrayList();
        
        // get roles for specified sites
        if ( siteId != null ) {
           Site site = null;
           Set roles = null;
           
           try {
              site = getSiteService().getSite(siteId);
              roles = site.getRoles();
           }
           catch (Exception e) {
              logger.warn(e.toString());
              return roleList;
           }
           
           for (Iterator i = roles.iterator(); i.hasNext();) {
              Role role = (Role) i.next();
              Agent agent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
              AgentWrapper roleAgent = new AgentWrapper( agent, site.getTitle() );
              roleList.add(roleAgent);
           }
        }
        
        // get all site roles (no site has been specified)
        else {
           List siteList = getSiteService().getSites(SiteService.SelectionType.ACCESS,
                                                     null, null, null, 
                                                     SiteService.SortType.TITLE_ASC, null);
                                                
           for (Iterator siteIt = siteList.iterator(); siteIt.hasNext();) {
              Site site = (Site)siteIt.next();
              Set roles = site.getRoles();

              for (Iterator roleIt = roles.iterator(); roleIt.hasNext();) {
                 Role role = (Role) roleIt.next();
                 Agent agent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
                 AgentWrapper roleAgent = new AgentWrapper( agent, site.getTitle() );
                 roleList.add(roleAgent);
              }
           }
        }

        return roleList;
    }

   /** Spring Injection Methods **/
   
   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService( SiteService siteService) {
      this.siteService = siteService;
   }
   
   public EmailService getEmailService() {
      return emailService;
   }

   public void setEmailService( EmailService emailService ) {
      this.emailService = emailService;
   }
   
   public UserDirectoryService getUserDirectoryService() {
      return userDirectoryService;
   }

   public void setUserDirectoryService( UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }
   
   /** Comparator for sorting role-based AgentWrapper objects
    **/
	public class RoleAgentComparator implements Comparator<AgentWrapper> {
		public int compare(AgentWrapper o1, AgentWrapper o2) {
			return o1.getDisplayName().compareTo( o2.getDisplayName() );
		}
	}
   
   /** Comparator for sorting GroupWrapper objects by title
    **/
	public class GroupComparator implements Comparator<GroupWrapper> {
		public int compare(GroupWrapper o1, GroupWrapper o2) {
			return o1.getTitle().compareTo( o2.getTitle() );
		}
	}
   
   /** Wrap Group class to support getChecked() method
    **/
   public class GroupWrapper {
      private Group group;
      private boolean checked;
      
      public GroupWrapper( Group group, boolean checked ) {
         this.group = group;
         this.checked = checked;
      }
      public void setChecked( boolean checked ) {
         this.checked = checked;
      }
      public boolean getChecked() {
         return checked;
      }
      public String getId() {
         return group.getId();
      }
      public String getTitle() {
         return group.getTitle();
      }
   }
}

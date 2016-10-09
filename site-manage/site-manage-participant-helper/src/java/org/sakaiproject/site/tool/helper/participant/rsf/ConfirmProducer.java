package org.sakaiproject.site.tool.helper.participant.rsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.site.tool.helper.participant.impl.UserRoleEntry;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.*;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Assign same role while adding participant
 * @author
 *
 */
public class ConfirmProducer implements ViewComponentProducer, NavigationCaseReporter, ActionResultInterceptor {

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ConfirmProducer.class);
	
    public SiteAddParticipantHandler handler;
    public static final String VIEW_ID = "Confirm";
    public MessageLocator messageLocator;
    public FrameAdjustingProducer frameAdjustingProducer;
    public SessionManager sessionManager;

    public String getViewID() {
        return VIEW_ID;
    }
    
    private TargettedMessageList targettedMessageList;
	public void setTargettedMessageList(TargettedMessageList targettedMessageList) {
		this.targettedMessageList = targettedMessageList;
	}
	
	public UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}
	
	private DeveloperHelperService developerHelperService;
    public void setDeveloperHelperService(
			DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

    private SiteService siteService = null;
    public void setSiteService(SiteService siteService)
	{
		this.siteService = siteService;
	}


    public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {
    	
    	String locationId = developerHelperService.getCurrentLocationId();
    	String siteTitle = "";
    	try {
			Site s = siteService.getSite(locationId);
			siteTitle = s.getTitle();
		} catch (IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	UIMessage.make(tofill, "addconf.confirming", "addconf.confirming", new Object[]{siteTitle});
    	
    	UIBranchContainer content = UIBranchContainer.make(tofill, "content:");
    	
        String emailChoice = handler.getEmailNotiChoice();
        if (emailChoice != null && emailChoice.equals(Boolean.TRUE.toString()))
        {
        	// email notification will be sent out to added participants
    		UIMessage.make(content, "emailnoti", "addconf.theywill");
        }
        else
        {
        	// email notification will NOT be sent out to added participants
    		UIMessage.make(content, "emailnoti", "addconf.theywillnot");
        }
        
    	UIForm confirmForm = UIForm.make(content, "confirm", "");
    	// csrf token
    	UIInput.make(confirmForm, "csrfToken", "#{siteAddParticipantHandler.csrfToken}", handler.csrfToken);
    	List<UserRoleEntry> userTable = handler.userRoleEntries;
    	// list of users        
        for (UserRoleEntry userRoleEntry:userTable) {
        	String userEId = userRoleEntry.userEId;
        	// default to userEid
        	String userName = userEId;
        	String displayId = userEId;
        	// if there is last name or first name specified, use it
        	if (userRoleEntry.lastName != null && userRoleEntry.lastName.length() > 0 
        			|| userRoleEntry.firstName != null && userRoleEntry.firstName.length() > 0)
        		userName = userRoleEntry.lastName + " " + userRoleEntry.firstName;
        	// get user from directory
        	try
        	{
        		User u = userDirectoryService.getUserByEid(userEId);
        		userName = u.getSortName();
        		displayId = u.getDisplayId();
        	}
        	catch (Exception e)
        	{
        		M_log.debug(this + ":fillComponents: cannot find user with eid=" + userEId);
        	}
            UIBranchContainer userRow = UIBranchContainer.make(confirmForm, "user-row:", userEId);
            UIOutput.make(userRow, "user-name", userName);
            UIOutput.make(userRow, "user-eid", displayId);
			UIOutput.make(userRow, "user-affiliation", userRoleEntry.userAffiliation);
			UIOutput.make(userRow, "user-department", userRoleEntry.userDepartment);
			UIOutput.make(userRow, "user-external-affiliation", userRoleEntry.userExternalAffiliation);
            UIOutput.make(userRow, "user-role", userRoleEntry.role);
            UIOutput.make(userRow, "user-status", handler.statusChoice.equals("active") ? messageLocator.getMessage("sitegen.siteinfolist.active") : messageLocator.getMessage("sitegen.siteinfolist.inactive"));
        }
        
    	UICommand.make(confirmForm, "continue", messageLocator.getMessage("gen.finish"), "#{siteAddParticipantHandler.processConfirmContinue}");
    	UICommand.make(confirmForm, "back", messageLocator.getMessage("gen.back"), "#{siteAddParticipantHandler.processConfirmBack}");
    	UICommand.make(confirmForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");
    	
    	//process any messages
    	targettedMessageList = handler.targettedMessageList;
        if (targettedMessageList != null && targettedMessageList.size() > 0) {
			for (int i = 0; i < targettedMessageList.size(); i++ ) {
				TargettedMessage msg = targettedMessageList.messageAt(i);
				if (msg.severity == TargettedMessage.SEVERITY_ERROR)
				{
					UIBranchContainer errorRow = UIBranchContainer.make(tofill,"error-row:", Integer.valueOf(i).toString());
					
			    	if (msg.args != null ) 
			    	{
			    		UIMessage.make(errorRow,"error", msg.acquireMessageCode(), (Object[]) msg.args);
			    	} 
			    	else 
			    	{
			    		UIMessage.make(errorRow,"error", msg.acquireMessageCode());
			    	}
				}
				else if (msg.severity == TargettedMessage.SEVERITY_INFO)
				{
					UIBranchContainer errorRow = UIBranchContainer.make(tofill,"info-row:", Integer.valueOf(i).toString());
						
			    	if (msg.args != null ) 
			    	{
			    		UIMessage.make(errorRow,"info", msg.acquireMessageCode(), (Object[]) msg.args);
			    	} 
			    	else 
			    	{
			    		UIMessage.make(errorRow,"info", msg.acquireMessageCode());
			    	}
				}
		    	
			}
        }
    }
    
    public ViewParameters getViewParameters() {
    	AddViewParameters params = new AddViewParameters();

        params.id = null;
        return params;
    }
    
    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> togo = new ArrayList<NavigationCase>();
        togo.add(new NavigationCase("back", new SimpleViewParameters(EmailNotiProducer.VIEW_ID)));
        return togo;
    }
    
    public void interceptActionResult(ARIResult result, ViewParameters incoming,
            Object actionReturn) 
    {
        if ("done".equals(actionReturn)) {
          Tool tool = handler.getCurrentTool();
           result.resultingView = new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager));
        }
    }
}

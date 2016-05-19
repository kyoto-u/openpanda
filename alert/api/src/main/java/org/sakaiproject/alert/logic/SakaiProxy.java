package org.sakaiproject.alert.logic;

import org.sakaiproject.message.api.Message;

import java.util.List;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();
	
	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();
	
	/**
	 * Get current user display name
	 * @return
	 */
	public String getCurrentUserDisplayName();
	
	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();
	
	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	public void postEvent(String event,String reference,boolean modify);
	
	/**
	 * Wrapper for ServerConfigurationService.getString("skin.repo")
	 * @return
	 */
	public String getSkinRepoProperty();
	
	/**
	 * Gets the tool skin CSS first by checking the tool, otherwise by using the default property.
	 * @param	the location of the skin repo
	 * @return
	 */
	public String getToolSkinCSS(String skinRepo);

	public List<Message> getMessages(String channelId);

	public boolean isMyWorkspace();

}

package org.sakaiproject.user.api;

import java.util.List;

/**
 *
 */
public interface AlternativeIdUDP {

	public UserEdit findUserByAlternativeId(String alternativeId, UserFactory factory, String contextReference);
	public List<UserEdit> findUsersByAlternativeId(String alternativeId, UserFactory factory, String contextReference);
        public List<UserEdit> findUsersByTitle(String title, String affiliation, UserFactory factory, String contextReference);
}

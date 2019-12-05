package jp.ac.kyoto_u.sakai.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserFactory;
import org.sakaiproject.user.api.AlternativeIdUDP;

import edu.amc.sakai.user.JLDAPDirectoryProvider;
import edu.amc.sakai.user.LdapUserData;
import edu.amc.sakai.user.LdapAttributeMapper;

import com.novell.ldap.LDAPException;

public class PandaUserDirectoryProvider extends JLDAPDirectoryProvider implements AlternativeIdUDP {

	private static Log M_log = LogFactory.getLog(PandaUserDirectoryProvider.class);

	protected String getFindUserByAlternativeIdFilter(String alternativeId, String contextReference) {
		assert contextReference != null;

		LdapAttributeMapper attributeMapper = getLdapAttributeMapper();

		StringBuilder sb = new StringBuilder();

		sb.append("(|");
		if (contextReference.equals("Site Info")) {
			String attribute = attributeMapper.getAttributeMapping("employeeNumber");
			if (attribute == null) {
				M_log.error("Attribute mapping for `employeeNumber' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("title"))  {
			String attribute = attributeMapper.getAttributeMapping("title");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("title1"))  {
			String attribute = attributeMapper.getAttributeMapping("title1");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title1' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("title2"))  {
			String attribute = attributeMapper.getAttributeMapping("title2");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title2' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("title3"))  {
			String attribute = attributeMapper.getAttributeMapping("title3");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title3' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("Xerox Integration")) {
			String attribute = attributeMapper.getAttributeMapping("employeeNumber");
			if (attribute == null) {
				M_log.error("Attribute mapping for `employeeNumber' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else if (contextReference.equals("employeeNumber")) {
			String attribute = attributeMapper.getAttributeMapping("employeeNumber");
			if (attribute == null) {
				M_log.error("Attribute mapping for `employeeNumber' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(alternativeId));
			sb.append(")");
		} else {
			return null;
		}
		sb.append(")");

		return sb.toString();
	}

	public UserEdit findUserByAlternativeId(String alternativeId, UserFactory factory, String contextReference) {
		if (contextReference == null) {
			return null;
		}

		String filter = getFindUserByAlternativeIdFilter(alternativeId, contextReference);
		if (filter == null) {
			return null;
		}

		try {
			LdapUserData ldapUserData = (LdapUserData)searchDirectoryForSingleEntry(filter, null, null, null, null);

			if (ldapUserData == null) {
				return null;
			}

			//create a user object and map the data onto it
			UserEdit user = factory.newUser(ldapUserData.getEid());
			mapUserDataOntoUserEdit(ldapUserData, user);
			return user;

		} catch (LDAPException e) {
			M_log.warn("An error occurred searching for users: " + e.getClass().getName() + ": (" + e.getResultCode() + ") " + e.getMessage());
			return null;
		}
	}

	public List<UserEdit> findUsersByAlternativeId(String alternativeId, UserFactory factory, String contextReference) {
		if (contextReference == null) {
			return null;
		}

		String filter = getFindUserByAlternativeIdFilter(alternativeId, contextReference);
		if (filter == null) {
			return null;
		}

		List<UserEdit> users = new ArrayList<UserEdit>();

		try {

		    //no limit to the number of search results, use the LDAP server's settings.
		    List<LdapUserData> ldapUsers = searchDirectory(filter, null, null, null, null, 5000);

		    if (ldapUsers == null) {
			return null;
		    }

		    for(LdapUserData ldapUserData: ldapUsers) {
                                
			//create a user object and map the data onto it
			//SAK-20625 ensure we have an id-eid mapping at this time
			UserEdit user = factory.newUser(ldapUserData.getEid());
			mapUserDataOntoUserEdit(ldapUserData, user);
			
			users.add(user);
		    }

		} catch (LDAPException e) {
			M_log.warn("An error occurred searching for users: " + e.getClass().getName() + ": (" + e.getResultCode() + ") " + e.getMessage());
			return null;
		}

		return users;

	}

        public List<UserEdit> findUsersByTitle(String title, String affiliation, UserFactory factory, String contextReference) {
		if (title == null) {
			return null;
		}

		String filter = getFindUsersByTitleFilter(title, affiliation, contextReference);

		if (filter == null) {
			return null;
		}

		List<UserEdit> users = new ArrayList<UserEdit>();

		try {

		    //no limit to the number of search results, use the LDAP server's settings.
		    List<LdapUserData> ldapUsers = searchDirectory(filter, null, null, null, null, 5000);

		    if (ldapUsers == null) {
			return null;
		    }

		    for(LdapUserData ldapUserData: ldapUsers) {
                                
			//create a user object and map the data onto it
			//SAK-20625 ensure we have an id-eid mapping at this time
			UserEdit user = factory.newUser(ldapUserData.getEid());
			mapUserDataOntoUserEdit(ldapUserData, user);
			
			users.add(user);
		    }

		} catch (LDAPException e) {
			M_log.warn("An error occurred searching for users: " + e.getClass().getName() + ": (" + e.getResultCode() + ") " + e.getMessage());
			return null;
		}

		return users;

	}

        protected String getFindUsersByTitleFilter(String title, String affiliation, String contextReference) {
		assert contextReference != null;

		LdapAttributeMapper attributeMapper = getLdapAttributeMapper();

		StringBuilder sb = new StringBuilder();

		sb.append("(&");
		if (contextReference.equals("title"))  {
			String attribute = attributeMapper.getAttributeMapping("title");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(title));
			sb.append(")");
		} else if (contextReference.equals("title1"))  {
			String attribute = attributeMapper.getAttributeMapping("title1");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title1' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(title));
			sb.append(")");
		} else if (contextReference.equals("title2"))  {
			String attribute = attributeMapper.getAttributeMapping("title2");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title2' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(title));
			sb.append(")");
		} else if (contextReference.equals("title3"))  {
			String attribute = attributeMapper.getAttributeMapping("title3");
			if (attribute == null) {
				M_log.error("Attribute mapping for `title3' is not configured.");
				return null;
			}

			sb.append("(");
			sb.append(attribute);
			sb.append("=");
			sb.append(attributeMapper.escapeSearchFilterTerm(title));
			sb.append(")");
		} else {
			return null;
		}

		if (affiliation != null)  {

		    String attribute = attributeMapper.getAttributeMapping("affiliation");
		    if (attribute == null) {
			M_log.error("Attribute mapping for `affiliation' is not configured.");
			return null;
		    }

		    sb.append("(");
		    sb.append(attribute);
		    sb.append("=");
		    sb.append(attributeMapper.escapeSearchFilterTerm(affiliation));
		    sb.append(")");
		}

		    String attribute = attributeMapper.getAttributeMapping("enrollment");
		    if (attribute == null) {
			M_log.error("Attribute mapping for `enrollment' is not configured.");
			return null;
		    }

		    sb.append("(");
		    sb.append(attribute);
		    sb.append("=");
		    sb.append(attributeMapper.escapeSearchFilterTerm("true"));
		    sb.append(")");

		sb.append(")");

		return sb.toString();
	}

	/**
	 * Authenticates the specified user login by recursively searching for 
	 * and binding to a DN below the configured base DN. Search results are 
	 * subsequently added to the cache. 
	 *
	 * This is an extention of
	 * JLDAPDirectoryProvier.authenticateUser(). The major
	 * difference is to use a specific LDAP server for
	 * authentication in addition to the default LDAP server.
	 * 
	 */
	public boolean authenticateUser(String userLogin, UserEdit edit, String password)
	{
	    return super.authenticateUser(userLogin, edit, password);
	}

}

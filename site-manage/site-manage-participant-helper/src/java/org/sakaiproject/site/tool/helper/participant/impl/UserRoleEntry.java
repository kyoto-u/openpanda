package org.sakaiproject.site.tool.helper.participant.impl;

/**
 * 
 * @author 
 *
 */
public class UserRoleEntry {
	
	  /** The user eid **/
	  public String userEId;
	  
	  /** The desired role for this user **/
	  public String role;
	  
	  /** The user first name **/
	  public String firstName;
	  
	  /** The user last name **/
	  public String lastName;

	  /** The user affiliation **/
	  public String userAffiliation;

 	  /** The user department **/
	  public String userDepartment;

	  /** The user external affiliation **/
	  public String userExternalAffiliation;

	  /**
	   * constructor with no params
	   */
	  public UserRoleEntry ()
	  {
		  userEId = "";
		  role = "";
		  firstName = "";
		  lastName = "";
	  }
	  
	  /**
	   * constructor with only two params
	   * @param eid
	   * @param r
	   */
	  public UserRoleEntry(String eid, String r)
	  {
		  userEId = eid;
		  role = r;
		  firstName = "";
		  lastName = "";
	  }
	  
	  /**
	   * constructor with four params
	   * @param eid
	   * @param r
	   * @param fName
	   * @param lName
	   */
	  public UserRoleEntry(String eid, String r, String fName, String lName)
	  {
		  userEId = eid;
		  role = r;
		  firstName = fName;
		  lastName = lName;
	  }

  	/**
	 * constructor with four params
	 * @param eid
	 * @param r
	 * @param fName
	 * @param lName
	 * @param affiliation
	 * @param department
	 * @param externalAffiliation
	 */
	public UserRoleEntry(String eid, String r, String fName, String lName, String affiliation, String department, String externalAffiliation)
	{
		userEId = eid;
		role = r;
		firstName = fName;
		lastName = lName;
		userAffiliation = affiliation;
		userDepartment = department;
		userExternalAffiliation = externalAffiliation;
	}

}


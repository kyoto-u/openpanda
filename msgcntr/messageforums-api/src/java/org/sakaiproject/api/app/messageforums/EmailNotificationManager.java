/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/msgcntr/tags/msgcntr-3.0.3/messageforums-api/src/java/org/sakaiproject/api/app/messageforums/EmailNotificationManager.java $
 * $Id: EmailNotificationManager.java 68560 2009-11-04 08:31:19Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
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
package org.sakaiproject.api.app.messageforums;

import java.util.List;
import org.sakaiproject.api.app.messageforums.Topic;



public interface EmailNotificationManager {
      
  /*
   * Get emailnotification settings for a user
   * @param userid
   */
   public EmailNotification getEmailNotification(String userid);
    
    
  /**
   * Save emailnotification for a user
   * @param emailnotification to save
   */
  public void saveEmailNotification(EmailNotification emailoption);

  
  /**
   * get list of users who should be notified of a new posting
   * @param userId : author of the message that the new posting is replying to.
   * @param the topic the message is in - used for permission checks
    */
  public List<String> getUsersToBeNotifiedByLevel(String notificationlevel);

  /**
   * Filter a list of notification participants 
   * @param allusers
   * @param topic
   * @return
   */
  public List<String> filterUsers(List<String> allusers, Topic topic);
  
}
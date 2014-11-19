/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/syllabus/tags/sakai-10.2/syllabus-api/src/java/org/sakaiproject/api/app/syllabus/SyllabusAttachment.java $
 * $Id: SyllabusAttachment.java 105080 2012-02-24 23:10:31Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.api.app.syllabus;

/**
 * @author <a href="mailto:cwen.iupui.edu">Chen Wen</a>
 * @version $Id: SyllabusAttachment.java 105080 2012-02-24 23:10:31Z ottenhoff@longsight.com $
 * 
 */
public interface SyllabusAttachment
{
  public Integer getLockId();
  
  public void setLockId(Integer lockId);
  
  public String getAttachmentId();
  
  public void setAttachmentId(String attachId);
  
  public Long getSyllabusAttachId();
  
  public void setSyllabusAttachId(Long syllabusAttachId);
  
  public String getName();
  
  public void setName(String name);
  
  public String getSize();
  
  public String getType();
  
  public String getCreatedBy();
  
  public String getLastModifiedBy();

  public void setSize(String size);
  
  public void setType(String type);
  
  public void setCreatedBy(String createdBy);
  
  public void setLastModifiedBy(String lastMOdifiedBy);
  
  public void setUrl(String url);
  
  public String getUrl();
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/jsf/example/src/java/migration/RepositoryUpgrader.java $
* $Id: RepositoryUpgrader.java 74079 2010-03-01 11:03:11Z david.horwitz@uct.ac.za $
***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package migration;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;



/**

 * "/files" is the base of the repository.

 * Resources doesn't have a base.

 * 

 * "/files/users/*" => "/user/*"

 * "/files/worksites/*" => "/group/*"

 * respository worksites are ids without dashes and resources worksites have dashes

 *    how is that being converted from 1.5=>2.1 in sakai?

 * 

 * When deleting a folder, it doesn't actually remove the record.  It just adds 2000 years to the modification date.

 * This is how we can retain all the info and add our "processed" tag without changing the database.

 * 

 * @author andersjb

 *

 */

public class RepositoryUpgrader {

   

   private final static Log logger = org.apache.commons.logging.LogFactory.getLog(RepositoryUpgrader.class);



   private ContentHostingService contentHostingService;

   /**

    * This moves the files from repository to resources.  The process is interruptible.

    * This method builds the tree structure

    *

    */

   public void upgrade()

   {

      contentHostingService = (ContentHostingService) ComponentManager.get(ContentHostingService.APPLICATION_ID);

      // move the folder tree over to the resources

      try {

         moveTree();

      } catch(Exception e) {

         logger.fatal(e);

      }

   }



   /**

    * all artifacts have a parent directory

    *

    */

   protected void moveTree() throws SQLException,

   InconsistentException, PermissionException, IdUsedException, IdInvalidException, TypeException 

   {

      moveFolder(null, false);

   }



   

   /**

    * get all the children folders and create them, then recurse those children.

    * @param parentId String

    */

   protected void moveFolder(ReFolder parentFolder, boolean move) throws SQLException,

   InconsistentException, PermissionException, IdUsedException, IdInvalidException 

   {

      List children = getChildren(parentFolder);

      

      upgradeEntityPaths(children);

      

      for(Iterator i = children.iterator(); i.hasNext(); ) {

         RepositoryEntity ent = (RepositoryEntity)i.next();

         

         ent.transition(move);

      }

   }



   

   /**

    * this pulls in the file from the old location and places it in the resources

    * @param parentId String

    */

   protected void moveFile(ReFile file) throws SQLException,

   InconsistentException, PermissionException, IdUsedException, IdInvalidException 

   {

//    set the user information into the current session

      Session sakaiSession = SessionManager.getCurrentSession();



      String uid, eid;



      uid = sakaiSession.getUserId();

      eid = sakaiSession.getUserEid();

      sakaiSession.setUserId(file.getOwnerId());

      sakaiSession.setUserEid(file.getOwnerId());

      

      ResourcePropertiesEdit resourceProperties = contentHostingService.newResourceProperties();



      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getTitle());

      resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, file.getTitle());

      

      resourceProperties.addProperty (ResourceProperties.PROP_CREATION_DATE, 

               dateToTime(file.getCreationDate()).toString());

      resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_DATE, 

               dateToTime(file.getLastModifiedDate()).toString());



      resourceProperties.addProperty (ResourceProperties.PROP_CREATOR, file.getOwnerId());

      resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_BY, file.getOwnerId());



      try {

         //We can't just add a collection the normal way because we want to override the live properties

         ContentResourceEdit cc = contentHostingService.addResource(file.getUri());

         addProperties(cc.getPropertiesEdit(), resourceProperties);

         cc.setContent("".getBytes());

         cc.setContentType("");

         contentHostingService.commitResource(cc);

         

         contentHostingService.setUuid(file.getUri(), file.getArtifactId());



      } catch(ServerOverloadException e) {

         throw new RuntimeException(e);

      } catch(OverQuotaException e) {

         throw new RuntimeException(e);

      }

      

      sakaiSession.setUserId(uid);

      sakaiSession.setUserEid(eid);

   }

   

   /**

    * This should be ordered by uri, always.  the folder always comes first

    * @param parentId String of the parent folder structure id

    * @return List of class Folder

    * @throws SQLException

    */

   protected List getChildren(ReFolder parentFolder) //throws SQLException

   {

      Connection connection = null;
      boolean wasCommit = false;
	try {
		connection = SqlService.borrowConnection();
	

      wasCommit = connection.getAutoCommit();

      connection.setAutoCommit(false);



      String sql = "select row_id, parent,  osp_tree_node.id, osp_tree_node.name, uri, " + 

                     "creation, last_modified, owner_id, worksiteId, typeId " +

                   "from osp_tree_node join osp_node_metadata on osp_tree_node.id=osp_node_metadata.id " + 

                   "where parent ";

                   

      Object[] fields = null;

      if(parentFolder != null) {

         sql += "=? ";

         fields = new Object[1];

         fields[0] = parentFolder.getFolderStructureId();

      } else 

         sql += "is null ";

      sql += "order by uri";

      List children = SqlService.dbRead(connection, sql, fields, new SqlReader() {



         public Object readSqlResultRecord(ResultSet result)

         {

            try

            {

               RepositoryEntity ent = null;

               String type = result.getString(10);

               if(type.equals("folder")) {

                  ent = new ReFolder();

               } else if(type.equals("fileArtifact")) {

                  ent = new ReFile();

               } else {

                  ent = new ReForm(type);

               }

               ent.setFolderStructureId(result.getString(1));

               ent.setParentFolderId(result.getString(2));

               ent.setArtifactId(result.getString(3));

               ent.setTitle(result.getString(4));

               ent.setUri(result.getString(5));

               ent.setCreationDate(result.getDate(6));

               ent.setLastModifiedDate(result.getDate(7));

               ent.setOwnerId(result.getString(8));

               ent.setWorksiteId(result.getString(9));

               

               return ent;

            } catch (SQLException ignore) {

               return null;

            }

         }

      });

      





      for(Iterator i = children.iterator(); i.hasNext(); ) {

         RepositoryEntity ent = (RepositoryEntity)i.next();

         ent.setParentFolder(parentFolder);

      }

      

      return children;
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
	      try {
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	      try {
			connection.setAutoCommit(wasCommit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	      SqlService.returnConnection(connection);
	}
	return null;
   }

   

   protected Time dateToTime(Date in)

   {

      GregorianCalendar gc = new GregorianCalendar();

      gc.setTime(in);

      return TimeService.newTime(gc);

   }

   

   protected String idToGuid(String str) throws java.lang.IllegalArgumentException

   {

      if(str == null) return null;

      

      int length = str.length();

      if(length == 36 || length == 0)

         return str;

      if(length == 32)

         return str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16)

            + "-" + str.substring(16, 20) + "-" + str.substring(20, 32);

      throw new IllegalArgumentException("");

   }

   

   

   /**

    * This method will create a folder in resources if it doesn't exist.

    * @param folder

    * @throws InconsistentException

    * @throws PermissionException

    * @throws IdUsedException

    * @throws IdInvalidException

    * @throws TypeException

    */

   protected void createNewResourceFolder(ReFolder folder)

      throws InconsistentException, PermissionException, IdUsedException, IdInvalidException {



      try {

         contentHostingService.getCollection(folder.getUri());

      } catch (PermissionException e) {

         logger.error(e);

      } catch (TypeException e) {

         logger.error(e);

      } catch (IdUnusedException e) {

         // wasn't found, so we need to create it



//         set the user information into the current session

         Session sakaiSession = SessionManager.getCurrentSession();

         

         String uid, eid;



         uid = sakaiSession.getUserId();

         eid = sakaiSession.getUserEid();

         sakaiSession.setUserId(folder.getOwnerId());

         sakaiSession.setUserEid(folder.getOwnerId());

         

         ResourcePropertiesEdit resourceProperties = contentHostingService.newResourceProperties();



         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, folder.getTitle());

         resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, folder.getTitle());

         

         resourceProperties.addProperty (ResourceProperties.PROP_CREATION_DATE, 

                  dateToTime(folder.getCreationDate()).toString());

         resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_DATE, 

                  dateToTime(folder.getLastModifiedDate()).toString());



         resourceProperties.addProperty (ResourceProperties.PROP_CREATOR, folder.getOwnerId());

         resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_BY, folder.getOwnerId());



         //We can't just add a collection the normal way because we want to override the live properties

         ContentCollectionEdit cc = contentHostingService.addCollection(folder.getUri());

         addProperties(cc.getPropertiesEdit(), resourceProperties);

         contentHostingService.commitCollection(cc);

         

         sakaiSession.setUserId(uid);

         sakaiSession.setUserEid(eid);



      }

   }

   

   /**

    * strips "/files", transitions "/users" => "/user"

    * @param folders

    */

   protected void upgradeEntityPaths(List folders)

   {

      for(Iterator i = folders.iterator(); i.hasNext(); ) {

         RepositoryEntity ent = (RepositoryEntity)i.next();

         String uri = ent.getUri();



         if(uri.startsWith(Entity.SEPARATOR+"files"))

            uri = uri.substring((Entity.SEPARATOR+"files").length());

         if(uri.startsWith(Entity.SEPARATOR+"users")) {

            uri = uri.substring((Entity.SEPARATOR+"users").length());

            if(uri.length() == 0)

               ent.setTitle("user");

               

            uri = Entity.SEPARATOR+"user" + uri;

         

         } else if(uri.startsWith(Entity.SEPARATOR+"worksites")) {

            uri = uri.substring((Entity.SEPARATOR+"worksites").length());

            if(uri.length() == 0)

               ent.setTitle("group");

               

            uri = Entity.SEPARATOR+"group" + uri;

         }

            

         // all folders need to have an end separator

         if(ent.isFolder() && !uri.endsWith(Entity.SEPARATOR))

            uri += Entity.SEPARATOR;

         

         ent.setUri(uri);

         

         ent.setArtifactId(idToGuid(ent.getArtifactId()));

      }

   }

   

   

   /**

    * Add properties for a resource.

    * 

    * @param r

    *        The resource.

    * @param props

    *        The properties.

    */

   protected void addProperties(ResourcePropertiesEdit p, ResourceProperties props)

   {

      if (props == null) return;



      Iterator it = props.getPropertyNames();

      while (it.hasNext())

      {

         String name = (String) it.next();



         p.addProperty(name, props.getProperty(name));

      }



   } // addProperties

   

   

   





   public ContentHostingService getContentHostingService() {

      return contentHostingService;

   }

   public void setContentHostingService(ContentHostingService contentHostingService) {

      this.contentHostingService = contentHostingService;

   }

   

   private abstract class RepositoryEntity {

      protected String folderStructureId, parentFolderId, artifactId, title, uri, ownerId, worksiteId, type;

      protected ReFolder parentFolder;

      protected Date creationDate, lastModifiedDate;



      public String getArtifactId() {return artifactId;}

      public void setArtifactId(String artifactId) {this.artifactId = artifactId;}



      public String getFolderStructureId() {return folderStructureId;}

      public void setFolderStructureId(String folderStructureId) {

         this.folderStructureId = folderStructureId;}



      public String getParentFolderId() {return parentFolderId;}

      public void setParentFolderId(String parentFolderId) {

         this.parentFolderId = parentFolderId;}



      public String getTitle() {return title;}

      public void setTitle(String title) {this.title = title;}

      

      public ReFolder getParentFolder() {return parentFolder;}

      public void setParentFolder(ReFolder parentFolder) {this.parentFolder = parentFolder;}

      

      public String getUri() {return uri;}

      public void setUri(String uri) {this.uri = uri;}

      

      public Date getCreationDate() {return creationDate;}

      public void setCreationDate(Date creationDate) {this.creationDate = creationDate;}

      

      public Date getLastModifiedDate() {return lastModifiedDate;}

      public void setLastModifiedDate(Date lastModifiedDate) {this.lastModifiedDate = lastModifiedDate;}

      

      public String getOwnerId() {return ownerId;}

      public void setOwnerId(String ownerId) {this.ownerId = ownerId;}

      

      public String getWorksiteId() {return worksiteId;}

      public void setWorksiteId(String worksiteId) {this.worksiteId = worksiteId;}

      

      public String getType() {return type;}

      //public void setType(String type) {this.type = type;}

      

      public abstract void transition(boolean move) throws SQLException,

      InconsistentException, PermissionException, IdUsedException, IdInvalidException;



      public boolean isFolder() { return false; }

      public boolean isFile() { return false; }

      public boolean isForm() { return false; }

   }

   private class ReFolder extends RepositoryEntity {

      public ReFolder() {type = "folder";}

      public boolean isFolder() { return true; }



      public void transition(boolean move) throws SQLException,

      InconsistentException, PermissionException, IdUsedException, IdInvalidException 

      {

         // create the folder

         if(move)

            createNewResourceFolder(this);

         

         // move all the children nodes

         moveFolder(this, true);

         

         // delete this folder

 //        markEntityProcessed(this);

      }

   }

   private class ReFile extends RepositoryEntity {

      public ReFile() {type = "fileArtifact";}

      public boolean isFile() { return true; }



      public void transition(boolean move) throws SQLException,

      InconsistentException, PermissionException, IdUsedException, IdInvalidException 

      {

         moveFile(this);

         

         // delete this file

//         markEntityProcessed(this);

      }

   }

   private class ReForm extends RepositoryEntity {

      public ReForm(String type) {this.type = type;}

      public boolean isForm() { return true; }



      public void transition(boolean move) throws SQLException,

      InconsistentException, PermissionException, IdUsedException, IdInvalidException 

      {

 //        moveForm(this);

         

         // delete this file

 //        markEntityProcessed(this);

      }

   }

}


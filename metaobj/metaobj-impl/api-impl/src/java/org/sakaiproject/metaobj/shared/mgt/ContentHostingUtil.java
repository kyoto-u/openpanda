package org.sakaiproject.metaobj.shared.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Feb 10, 2007
 * Time: 11:36:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentHostingUtil {

	private static final Log log = LogFactory.getLog(ContentHostingUtil.class);

   public static Element createRepoNode(ContentResource contentResource) {
      Element repositoryNode;
      repositoryNode = new Element("repositoryNode");

      Date created = getDate(contentResource,
            contentResource.getProperties().getNamePropCreationDate());
      if (created != null) {
         repositoryNode.addContent(createNode("created",
               created.toString()));
      }

      Date modified = getDate(contentResource,
            contentResource.getProperties().getNamePropModifiedDate());
      if (modified != null) {
         repositoryNode.addContent(createNode("modified",
               modified.toString()));
      }
      
		UserDirectoryService uds = (UserDirectoryService) ComponentManager.get(UserDirectoryService.class);
		String createdUser = contentResource.getProperties().getProperty(
				contentResource.getProperties().getNamePropCreator());
		if (createdUser != null) {
			try {
				User owner = uds.getUser(createdUser);
				repositoryNode.addContent(createUserNode("owner", owner));
			}
			catch (UserNotDefinedException e) {
				//we have something created by somebody we don't know -- this is worth logging
				log.warn("Cannot find owner of content resource with reference: " + contentResource.getReference());
			}
		}
      
		String modifiedUser = contentResource.getProperties().getProperty(
				contentResource.getProperties().getNamePropModifiedBy());
		if (modifiedUser != null) {
			try {
				User modifiedBy = uds.getUser(modifiedUser);
				repositoryNode.addContent(createUserNode("modifiedBy", modifiedBy));
			}
			catch (UserNotDefinedException e) {
				//we have something modified by somebody we don't know -- this is worth logging
				log.warn("Cannot find modifier of content resource with reference: " + contentResource.getReference());
			}
		}
      
      return repositoryNode;
   }

	private static Element createUserNode(String elementName, User user) {
		Element userNode = new Element(elementName);
		userNode.addContent(createNode("id", user.getId()));
		userNode.addContent(createNode("eid", user.getEid()));
		userNode.addContent(createNode("displayName", user.getDisplayName()));
		userNode.addContent(createNode("email", user.getEmail()));
		userNode.addContent(createNode("firstName", user.getFirstName()));
		userNode.addContent(createNode("lastName", user.getLastName()));
		return userNode;
	}

   public static Element createNode(String name, String value) {
      Element newNode = new Element(name);
      newNode.addContent(value);
      return newNode;
   }

   public static Date getDate(ContentResource resource, String propName) {
      try {
         Time time = resource.getProperties().getTimeProperty(propName);
         return new Date(time.getTime());
      }
      catch (EntityPropertyNotDefinedException e) {
         return null;
      }
      catch (EntityPropertyTypeException e) {
         throw new RuntimeException(e);
      }
   }


}

/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/EditStructuredArtifactDefinitionController.java $
 * $Id: EditStructuredArtifactDefinitionController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.control;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class EditStructuredArtifactDefinitionController extends AddStructuredArtifactDefinitionController
   implements LoadObjectController {
   private ArtifactFinder artifactFinder;
   private SecurityService securityService;

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(StructuredArtifactDefinitionManager.SAD_SESSION_TAG) != null) {
         return session.remove(StructuredArtifactDefinitionManager.SAD_SESSION_TAG);
      }

      StructuredArtifactDefinitionBean home = (StructuredArtifactDefinitionBean) incomingModel;
      home = getStructuredArtifactDefinitionManager().loadHome(home.getId());
      return home;
   }

   protected void save(StructuredArtifactDefinitionBean sad, Errors errors) {
      //check to see if you have edit permissions
      boolean isAllowed = isAllowed(SharedFunctionConstants.EDIT_ARTIFACT_DEF);
      Agent currentAgent = getAuthManager().getAgent();

      //TODO verify user is system admin, if editting global SAD

      if (isAllowed || currentAgent.getId().getValue().equals(sad.getOwner().getId().getValue())) {
    	  // todo this should all be done on the server
    	  // check only if new xsd has been submitted
    	  if (sad.getSchemaFile() != null) {

    		  String type = sad.getType().getId().getValue();

    		  getSecurityService().pushAdvisor(new SecurityAdvisor() {
    			  public SecurityAdvice isAllowed(String userId, String function, String reference) {
    				  return SecurityAdvice.ALLOWED;
    			  }
    		  });

    		  try {
    			  Collection artifacts = artifactFinder.findByType(type);
    			  StructuredArtifactValidator validator = new StructuredArtifactValidator();

    			  // validate every artifact against new xsd to determine
    			  // whether or not an xsl conversion file is necessary
    			  for (Iterator i = artifacts.iterator(); i.hasNext();) {
    				  Object obj = i.next();
    				  if (obj instanceof StructuredArtifact) {
    					  StructuredArtifact artifact = (StructuredArtifact)obj;
    					  artifact.setHome(getStructuredArtifactDefinitionManager().convertToHome(sad));
    					  Errors artifactErrors = new BindException(artifact, "bean");
    					  validator.validate(artifact, artifactErrors);
    					  if (artifactErrors.getErrorCount() > 0) {
    						  if (sad.getXslConversionFileId() == null ||
    								  sad.getXslConversionFileId().getValue().length() == 0) {

    							  errors.rejectValue("schemaFile",
    									  "invalid_schema_file_edit",
    							  "key missing:  invalid_schema_file_edit");

    							  for (Iterator x=artifactErrors.getAllErrors().iterator();x.hasNext();){
    								  ObjectError error = (ObjectError) x.next();
    								  logger.warn(error.toString());
    							  }

    							  return;

    						  } else {
    							  sad.setRequiresXslFile(true);
    							  break;
    						  }
    					  }
    				  }
    			  }
    		  }
    		  finally {
    			  getSecurityService().popAdvisor();
    		  }
    	  }

    	  try {
    		  getStructuredArtifactDefinitionManager().save(sad);
    	  }
    	  catch (PersistenceException e) {
    		  errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
    				  e.getDefaultMessage());
    	  }
      }
      else {
    	  errors.rejectValue("id", "not_allowed", new Object[] {},
    	  "Not allowed to delete");
      }
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }
}

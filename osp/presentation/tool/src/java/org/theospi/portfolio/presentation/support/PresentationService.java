/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/PresentationManager.java $
* $Id: PresentationManager.java 64567 2009-07-02 20:40:39Z bkirschn@umich.edu $
***********************************************************************************
*
* Copyright (c) 2009 The Sakai Foundation
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
package org.theospi.portfolio.presentation.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdCustomEditor;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.entity.api.Reference;

import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.PresentationTemplateNameComparator;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AudienceSelectionHelper;


public class PresentationService {
	private IdManager idManager;
	private AuthenticationManager authnManager;
	private AgentManager agentManager;
	private AuthorizationFacade authzManager;
	private PresentationManager presentationManager;
	private ToolManager toolManager;
	private IdCustomEditor idCustomEditor;
	private TypedPropertyEditor presentationItemCustomEditor;
	private TypedPropertyEditor presentationViewerCustomEditor;
	private ArtifactFinder artifactFinder;
	
	private SiteService siteService;
	private ContentHostingService contentHostingService;
	private ServerConfigurationService serverConfigurationService;
	
	private static final Log log = LogFactory.getLog(PresentationService.class);
	private static final String WIZARD_ITEM_PLACEHOLDER = "Wizard/Matrix";
	
	private static final ResourceLoader resourceBundle = new ResourceLoader(PresentationManager.PRESENTATION_MESSAGE_BUNDLE);	
	
	public static final String VIEW_PRESENTATION_URL =	 "/osp-presentation-tool/viewPresentation.osp?id=";

	public static final String PROP_FREEFORM_DISABLED = "osp.freeform.disabled";
      
	//TODO: Add signature for more parameterized creation -- not just complete current context (user, site, tool)
	public Presentation createPresentation(String presentationType, String templateId) {
		if (!Presentation.FREEFORM_TYPE.equals(presentationType) && !Presentation.TEMPLATE_TYPE.equals(presentationType)) {
			log.warn("Cannot Create Presentation -- Invalid Presentation Type (" + presentationType + ") -- Must be template or free-form.");
			return null;
		}
		
		Agent agent = authnManager.getAgent();
		PresentationTemplate template = presentationManager.getPresentationTemplate(idManager.getId(templateId));
		if (template == null) {
			log.warn("Cannot Create Presentation -- Invalid Presentation Template ID: " + templateId);
			return null;
		}
		
		Presentation presentation = new Presentation();
		presentation.setNewObject(true);
		//presentation.setId(idManager.createId());
		//The Site ID is coalesced in PresentationManager -- the Tool ID should be too, but is not 
		presentation.setToolId(toolManager.getCurrentPlacement().getId());
		presentation.setPresentationType(presentationType);
		presentation.setTemplate(template);
		presentation.setName(template.getName()+" - "+agent.getDisplayName());
		return presentationManager.storePresentation(presentation);
	}
	
	//NOTE: This method is context-aware, returning available templates for the current user/site/tool
	public List<PresentationTemplate> getAvailableTemplates() {
        Agent agent = authnManager.getAgent();
        TreeSet<PresentationTemplate> availableTemplates = new TreeSet<PresentationTemplate>(new PresentationTemplateNameComparator());
        availableTemplates.addAll(presentationManager.findTemplatesByOwner(agent, toolManager.getCurrentPlacement().getContext()));
        availableTemplates.addAll(presentationManager.findPublishedTemplates(toolManager.getCurrentPlacement().getContext()));
        availableTemplates.addAll(presentationManager.findGlobalTemplates());
        return new ArrayList<PresentationTemplate>(availableTemplates);
	}

	//NOTE: This method is context-aware, checking properties of the current tool
	public boolean isFreeFormEnabled() {

		//Leave free-form on by default if not configured
		boolean disabled = serverConfigurationService.getBoolean(PROP_FREEFORM_DISABLED, false);
		try {
			String siteWide = siteService.findTool(toolManager.getCurrentPlacement().getId())
				.getContainingPage().getContainingSite().getProperties()
				.getProperty(PROP_FREEFORM_DISABLED);

			//We want to allow sites to turn free-form back on if off system-wide, or off if on by default
			//But be specific about the property values
			if ("true".equalsIgnoreCase(siteWide) || "1".equals(siteWide))
				disabled = true;
			else if ("false".equalsIgnoreCase(siteWide) || "0".equals(siteWide))
				disabled = false;
		}
		catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("Error retrieving site properties for tool placement: " + toolManager.getCurrentPlacement());
		}

		//NOTE: This method inverts the logic. To admins, disabling is the natural action because enabled is default.
		//      In the code, enabled is the more natural state.
		return !disabled;
	}
	
	public boolean updatePresentation(String presentationId, String name, String description, Boolean active, Boolean allowComments) {
		Presentation presentation = getPresentation(presentationId);
		
		if (name != null)
			presentation.setName(name);
		
		if (description != null)
			presentation.setDescription(description);
				
		if (Boolean.TRUE.equals(active))
			presentation.setExpiresOn(null);
		else if (Boolean.FALSE.equals(active))
			presentation.setExpiresOn(new GregorianCalendar(1970, 1, 1).getTime());
		
		if (Boolean.TRUE.equals(allowComments))
			presentation.setAllowComments(true);
		else if (Boolean.FALSE.equals(allowComments))
			presentation.setAllowComments(false);
		
		presentation = presentationManager.storePresentation(presentation, false, true);
		return (presentation != null);
	}
	
	public Presentation getPresentation(String id) {
		return getPresentation(id, true);
	}
	
	public Presentation getPresentation(String id, boolean checkAuthz) {
		Presentation presentation = presentationManager.getPresentation( idManager.getId(id), checkAuthz );
		if (presentation == null)
			throw new IllegalArgumentException("Portfolio does not exist with ID: " + id);
		return presentation;
	}
	
	public PresentationItemDefinition getPresentationItemDefinition(String id) {
		PresentationItemDefinition presentationItemDef = presentationManager.getPresentationItemDefinition(idManager.getId(id));
		if (presentationItemDef == null)
			throw new IllegalArgumentException("Portfolio Item Definition does not exist with ID: " + id);
		return presentationItemDef;
	}

	public Presentation savePresentation(Presentation presentation) {
		return presentationManager.storePresentation(presentation);
	}
	
	public Presentation savePresentation(Presentation presentation, boolean checkAuthz, boolean updateDates) {
		return presentationManager.storePresentation(presentation, checkAuthz, updateDates);
	}
	
	public List<PresentationComment> getComments(String presentationId) {
		return presentationManager.getPresentationComments(idManager.getId(presentationId), authnManager.getAgent());
	}
	
	/** Returns true if current user is owner of specified presentation
	 **/
	public boolean isOwner(Presentation presentation) {
		Agent agent = authnManager.getAgent();
		return presentation.getOwner().getId().getValue().equals(agent.getId().getValue());
	}
	
	public Map<String, Object> editOptions(String presentationId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Presentation presentation = getPresentation(presentationId);		
		PresentationTemplate template = presentation.getTemplate();
		
		if (template.getPropertyFormType() == null || Presentation.FREEFORM_TYPE.equals(presentation.getPresentationType()))
			throw new IllegalArgumentException("Portfolio Type does not accept options.");

		//Try to find the attached form
		String formId = null;
		if (presentation.getPropertyForm() != null)
			formId = contentHostingService.resolveUuid(presentation.getPropertyForm().getValue()); 
		
		//No form or invalid attachment results in creation, otherwise edit existing
		if (formId == null) {
			//Create or locate the folder to hold the new form
			String folderPath = getPropertiesFolder();					
            params.put(FormHelper.PARENT_ID_TAG, folderPath);
			params.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, presentation.getName() + " (" + presentationId + ")");
		}
		else {
			params.put(ResourceEditingHelper.ATTACHMENT_ID, formId);
		}
		
		params.put(ResourceEditingHelper.CREATE_TYPE, ResourceEditingHelper.CREATE_TYPE_FORM);
		params.put(ResourceEditingHelper.CREATE_SUB_TYPE, template.getPropertyFormType().getValue());
		return params;
	}
	
	public Map<String, Object> createForm(String presentationId, String formTypeId, String itemDefId) {
		return editForm(presentationId, formTypeId, null, itemDefId, true);
	}

	public Map<String, Object> editForm(String presentationId, String formTypeId, String formId, String itemDefId) {
		return editForm(presentationId, formTypeId, formId, itemDefId, false);
	}

	public Map<String, Object> editForm(String presentationId, String formId, String itemDefId) {
		return editForm(presentationId, getFormType(formId), formId, itemDefId);
	}

	protected Map<String, Object> editForm(String presentationId, String formTypeId, String formId, String itemDefId, boolean newItem) {
		// Sanity check parameters (will throw an exception for any unacceptable conditions)
		if (newItem)
			checkFormCreateParameters(presentationId, formTypeId, itemDefId);
		else
			checkFormEditParameters(presentationId, formTypeId, formId, itemDefId);

		Presentation presentation = getPresentation(presentationId);
		PresentationTemplate template = presentation.getTemplate();
		PresentationItemDefinition itemDef = null;
		for (PresentationItemDefinition def : (Collection<PresentationItemDefinition>) template.getItemDefinitions())
			if (def.getId().getValue().equals(itemDefId))
				itemDef = def;

		String folderPath = getFormsFolder();
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (newItem)
			params.put(FormHelper.PARENT_ID_TAG, folderPath);
		else
			params.put(ResourceEditingHelper.ATTACHMENT_ID, contentHostingService.resolveUuid(formId));
		params.put(ResourceEditingHelper.CREATE_TYPE, ResourceEditingHelper.CREATE_TYPE_FORM);
		params.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
		params.put(FormHelper.PRESENTATION_TEMPLATE_ID, template.getId().getValue());
		params.put(FormHelper.PRESENTATION_ITEM_DEF_ID, itemDefId);
		params.put(FormHelper.PRESENTATION_ITEM_DEF_NAME, itemDef.getName());
		return params;
	}

	/**
	 * Check to see that all form creation parameters are set and valid. Throws IllegalArgumentException on any errors.
	 * 
	 * @param presentationId
	 *            Presentation from which the form is being edited
	 * @param formTypeId
	 *            Form type ID for the form to be edited
	 * @param itemDefId
	 *            Presentation Item Definition ID of the "slot" for the item
	 */
	protected void checkFormCreateParameters(String presentationId, String formTypeId, String itemDefId) {
		Presentation presentation = getPresentation(presentationId);
		if (presentation == null)
			throw new IllegalArgumentException("Cannot edit nonexistent presentation");

		PresentationTemplate template = presentation.getTemplate();
		if (!Presentation.TEMPLATE_TYPE.equals(presentation.getPresentationType()) || template == null)
			throw new IllegalArgumentException("Cannot edit form because presentation [ID: " + presentationId + "] has no template.");

		if (formTypeId == null)
			throw new IllegalArgumentException("Cannot create null-typed forms");
		
		PresentationItemDefinition itemDef = null;
		for (PresentationItemDefinition def : (Collection<PresentationItemDefinition>) presentation.getTemplate().getItemDefinitions())
			if (def.getId().getValue().equals(itemDefId))
				itemDef = def;

		if (itemDef == null)
			throw new IllegalArgumentException("Cannot edit form because presentation [ID: " + presentationId + "] "
					+ "does not contain item definition [ID: " + itemDefId + "].");
		
		if (!formTypeId.equals(itemDef.getType()))
			throw new IllegalArgumentException("Presentation [ID: " + presentationId + "] does not accept forms of type: " + formTypeId);
		
	}
	
	/**
	 * Check to see that all form editing parameters are set and valid. Throws IllegalArgumentException on any errors.
	 * 
	 * @param presentationId
	 *            Presentation from which the form is being edited
	 * @param formTypeId
	 *            Form type ID for the form to be edited
	 * @param formId
	 *            The specific artifact ID to edit
	 * @param itemDefId
	 *            Presentation Item Definition ID of the "slot" for the item
	 */
	protected void checkFormEditParameters(String presentationId, String formTypeId, String formId, String itemDefId) {
		checkFormCreateParameters(presentationId, formTypeId, itemDefId);
		
		if (formId == null)
			throw new IllegalArgumentException("Cannot edit null form");

		if (contentHostingService.resolveUuid(formId) == null)
			throw new IllegalArgumentException("Cannot edit nonexistent form");
		
	}
	
	public String getFormType(String formId) {
		return null;
	}
	
	public void saveOptions(String presentationId, String artifactId) {
		try {
			Presentation presentation = getPresentation(presentationId);
			String formId = contentHostingService.resolveUuid(artifactId);
			contentHostingService.checkResource(formId);

			// Check the form type with the options form type. Multiple tabs can stomp each others' session state,
			// forcing an attempt to save the wrong type as the options form, which is unrecoverable from the UI.
			PresentationTemplate template = presentation.getTemplate();
			StructuredArtifact bean = (StructuredArtifact) artifactFinder.load(idManager.getId(artifactId));
			if (template == null || Presentation.FREEFORM_TYPE.equals(presentation.getPresentationType()))
				throw new IllegalArgumentException("Presentation has no template or is free-form, so options cannot be saved.");
			else if (template.getPropertyFormType() == null)
				throw new IllegalArgumentException("Presentation has no options form type set, so options cannot be saved.");
			else if (bean == null || !template.getPropertyFormType().equals(bean.getHome().getType().getId()))
				throw new IllegalArgumentException(
						"Form type mismatch, so options cannot be saved. Are you attempting to edit multiple forms simultaneously?");

			presentation.setPropertyForm(idManager.getId(artifactId));
			presentationManager.storePresentation(presentation);
		} catch (PermissionException e) {
			log.warn("Cannot attach unreadable options form [] to presentation [].");
		} catch (IdUnusedException e) {
			throw new IllegalArgumentException("Cannot find options form: " + artifactId);
		} catch (TypeException e) {
			throw new RuntimeException("Cannot attach collection [" + artifactId + "] to presentation [" + presentationId + "] as options form.");
		}
	}
	
   
   /**
    ** Parse role id and return Site title
    **/
    private String getSiteFromRoleMember( String roleMember ) {
       Reference ref = EntityManager.newReference( roleMember );
       String siteId = ref.getContainer();
       String siteTitle = "";
       try {
          siteTitle = siteService.getSite(siteId).getTitle();
       }
       catch (IdUnusedException e) {
          log.warn(e.toString());
       }
            
       return siteTitle;
    }
    
   /**
    ** Get authorized share list from the database for this portfolio, return a list of Agent objects
    **/
   public List getShareList( Presentation presentation ) {
      List authzList = authzManager.getAuthorizations(null, 
                                                      AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO, 
                                                      presentation.getId() );
               
      ArrayList shareList = new ArrayList(authzList.size());                                            
      for (Iterator it=authzList.iterator(); it.hasNext(); ) {
         Agent agent = ((Authorization)it.next()).getAgent();
         if ( agent.isRole() ) {
            String worksiteName = getSiteFromRoleMember(agent.getId().getValue());
            agent = new AgentWrapper( agent, worksiteName );
         }
         shareList.add( agent );
      }
      
      return shareList;
   }
   
	public Map<String, Object> getPresentationArtifacts(String presentationId) {
        Presentation presentation = getPresentation(presentationId);
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, Object> artifacts = new HashMap<String, Object>();
        Map<String, Object> artifactCache = new HashMap<String, Object>();
        Map<String, Object> itemHash = new HashMap<String, Object>();
        Agent agent = authnManager.getAgent();
        
        // Create list of item definition types for this template
        PresentationTemplate template = presentationManager.getPresentationTemplate(presentation.getTemplate().getId());
        presentation.setTemplate(template);
        model.put("types", template.getSortedItems());
        
        // Create sorted list of artifacts eligible for inclusion in portfolio
        for (PresentationItemDefinition itemDef : (Set<PresentationItemDefinition>) template.getSortedItems()) {

           if (artifactCache.containsKey(itemDef.getType()) && !itemDef.getHasMimeTypes()){
              artifacts.put(itemDef.getId().getValue(), artifactCache.get(itemDef.getType()));
           } 
           else {
              List itemArtifacts = (List)presentationManager.loadArtifactsForItemDef(itemDef, agent);
                 
              // Update display name of resource content to include abbreviated folder name
              if (itemArtifacts.size() > 0 && itemArtifacts.get(0) instanceof ContentResourceArtifact) {
                 // don't do this for forms, as their display name is OK as is
                 if (! ((ContentResourceArtifact)itemArtifacts.get(0)).getBase().getContentType().equals("application/x-osp"))
                    for (ContentResourceArtifact art: (List<ContentResourceArtifact>)itemArtifacts)
                       art.setDisplayName( abbreviateResourceName(art) );
              }
              // Sort artifacts by display name (wizards also sorted by date)
              if (itemArtifacts.size() > 0 && itemArtifacts.get(0) instanceof CompletedWizard)
                 Collections.sort((List<CompletedWizard>)itemArtifacts, new SortWizards());
              else
                 Collections.sort((List<Artifact>)itemArtifacts, new SortArtifacts());

              // cache only full list
              if (!itemDef.getHasMimeTypes()) {
                 artifactCache.put(itemDef.getType(), itemArtifacts);
              }
              artifacts.put(itemDef.getId().getValue(), itemArtifacts);
           }
        }
        
        // Create map of presentation items currently included in portfolio
        for (Iterator it = presentation.getPresentationItems().iterator(); it.hasNext();) {
           PresentationItem item = (PresentationItem) it.next();
           String itemId = item.getDefinition().getId().getValue() + "." + item.getArtifactId();
           
           if ( ! item.getDefinition().getIsFormType().booleanValue() ) 
           {
              itemHash.put( itemId, WIZARD_ITEM_PLACEHOLDER );
              continue;
           }
              
           List artifactList = (List)artifacts.get( item.getDefinition().getId().getValue() );
           ContentResourceArtifact itemArtifact = null;
           
           // search for artifact corresponding to presentation item
           for (Iterator aIt = artifactList.iterator(); aIt.hasNext();) {
              ContentResourceArtifact artifact = (ContentResourceArtifact)aIt.next();
              if ( artifact.getId().getValue().equals(item.getArtifactId().getValue()) ) {
                 itemArtifact = artifact;
                 break;
              }
           }
           
           // load artifact if not found (e.g. artifact added by collaborative author)
           if ( itemArtifact == null ) {
              itemArtifact = presentationManager.loadArtifactForItem(item);
              if ( itemArtifact != null )
                 artifactList.add(itemArtifact);
           }
              
           // add itemArtifact to map
           itemHash.put( itemId, itemArtifact );
        }

        model.put("itemHash", itemHash);
        model.put("artifacts", artifacts);
        
        return model;		
	}
	
	private int FOLDER_MAX_LEN = 16; // maximum display size for folder name
	private int FOLDER_ABBR_SIZE = 7; // abbreviated folder name prefix/suffix size
	private String FOLDER_ABBR_TOKEN = ".."; // abbreviated folder name token

	/**
	 * Prepend containing folder name to resource name, abbreviating if too long
	 */
	private String abbreviateResourceName(ContentResourceArtifact art) {
		String folder = art.getBase().getContainingCollection().getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME);

		if (folder.length() > FOLDER_MAX_LEN) {
			int len = folder.length();
			StringBuilder newFolder = new StringBuilder(folder.substring(0, FOLDER_ABBR_SIZE));
			newFolder.append(FOLDER_ABBR_TOKEN);
			newFolder.append(folder.substring(len - FOLDER_ABBR_SIZE, len));
			folder = newFolder.toString();
		}
		StringBuilder resourceName = new StringBuilder(folder);
		resourceName.append(Entity.SEPARATOR);
		resourceName.append(art.getDisplayName());
		return resourceName.toString();
	}
	

	/**
	  * Sort artifacts by display name
	  */
	protected class SortArtifacts implements Comparator<Artifact> {
		public int compare(Artifact o1, Artifact o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	}

	/**
	 * Sort Wizards by name and date (in case any share the same name)
	 */
	protected class SortWizards implements Comparator<CompletedWizard> {
		public int compare(CompletedWizard o1, CompletedWizard o2) {
			if (o1.getDisplayName().equals(o2.getDisplayName())) {
				return o1.getCreated().compareTo(o2.getCreated());
			}
			else {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		}
	}
	
	protected String getFormsFolder() {
		String idChunk = PresentationManager.PRESENTATION_FORMS_FOLDER;
		String displayName = resourceBundle.getString(PresentationManager.PRESENTATION_FORMS_FOLDER_DISPNAME);
		String description = resourceBundle.getString(PresentationManager.PRESENTATION_FORMS_FOLDER_DESC);
		return getFolder(idChunk, displayName, description);
	}
	
	protected String getPropertiesFolder() {
		String idChunk = PresentationManager.PRESENTATION_PROPERTIES_FOLDER;
		String displayName = resourceBundle.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DISPNAME);
		String description = resourceBundle.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DESC);
		return getFolder(idChunk, displayName, description);
	}
	
	protected String getFolder(String idChunk, String displayName, String description) {
		try {
			String folderBase = getUserCollection().getId();

			Placement placement = toolManager.getCurrentPlacement();
			String currentSite = placement.getContext();

			String rootDisplayName = resourceBundle.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DISPNAME);
			String rootDescription = resourceBundle.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DESC);

			String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
			folderPath = createFolder(folderPath, currentSite, siteService.getSiteDisplay(currentSite), null);

			folderPath = createFolder(folderPath, idChunk, displayName, description);
			return folderPath;
		} catch (TypeException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		} catch (IdUnusedException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		} catch (PermissionException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		}
	}
	
	protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
		User user = UserDirectoryService.getCurrentUser();
		String userId = user.getId();
		String wsId = siteService.getUserSiteId(userId);
		String wsCollectionId = contentHostingService.getSiteCollection(wsId);
		ContentCollection collection = contentHostingService.getCollection(wsCollectionId);
		return collection;
	}
	
	protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
		String folder = base + append + "/";
		try {
			ContentCollectionEdit propFolder = contentHostingService.addCollection(folder);
			propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, appendDisplay);
			propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, appendDescription);
			contentHostingService.commitCollection(propFolder);
			return propFolder.getId();
		} catch (IdUsedException e) {
			// ignore... it is already there.
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return folder;
	}
	
	
	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
	public void setAuthnManager(AuthenticationManager authnManager) {
		this.authnManager = authnManager;
	}
	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}
	public void setPresentationManager(PresentationManager presentationManager) {
		this.presentationManager = presentationManager;
	}
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	public IdCustomEditor getIdCustomEditor() {
		return idCustomEditor;
	}
	public void setIdCustomEditor(IdCustomEditor idCustomEditor) {
		this.idCustomEditor = idCustomEditor;
	}

	public TypedPropertyEditor getPresentationItemCustomEditor() {
		return presentationItemCustomEditor;
	}

	public void setPresentationItemCustomEditor(TypedPropertyEditor presentationItemCustomEditor) {
		this.presentationItemCustomEditor = presentationItemCustomEditor;
	}

	public TypedPropertyEditor getPresentationViewerCustomEditor() {
		return presentationViewerCustomEditor;
	}

	public void setPresentationViewerCustomEditor(TypedPropertyEditor presentationViewerCustomEditor) {
		this.presentationViewerCustomEditor = presentationViewerCustomEditor;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}

	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public void setArtifactFinder(ArtifactFinder artifactFinder) {
		this.artifactFinder = artifactFinder;
	}

}

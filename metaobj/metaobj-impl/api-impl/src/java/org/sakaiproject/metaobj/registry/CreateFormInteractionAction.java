package org.sakaiproject.metaobj.registry;

import org.sakaiproject.content.util.BaseInteractionAction;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Feb 5, 2007
 * Time: 10:27:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateFormInteractionAction extends BaseInteractionAction {

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public CreateFormInteractionAction(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager,
                                      String id, ActionType actionType, String typeId,
                                      String helperId, List requiredPropertyKeys) {
      super(id, actionType, typeId, helperId, requiredPropertyKeys);
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   /* (non-Javadoc)
     * @see org.sakaiproject.content.api.ResourceToolAction#available(java.lang.String)
     */
   public boolean available(ContentEntity entity) {
      return getStructuredArtifactDefinitionManager().hasHomes();
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
}

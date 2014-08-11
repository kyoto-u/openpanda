package org.sakaiproject.metaobj.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * This job will verify/convert bad Metaobj (form definition) schema hashes.
 * If "metaobj.schemahash.update" is set to true, they will be updated.
 * @author chrismaurer
 *
 */
public class UpdateSchemaHashJob implements Job {

	StructuredArtifactDefinitionManager structuredArtifactDefinitionManager = null;

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Session sakaiSession = SessionManager.getCurrentSession();
		String userId = sakaiSession.getUserId();
		String eId = sakaiSession.getUserEid();

		try {
			sakaiSession.setUserId("admin");
			sakaiSession.setUserEid("admin");

			boolean updateSchemaHashes = ServerConfigurationService.getBoolean("metaobj.schemahash.update", false);

			getStructuredArtifactDefinitionManager().verifySchemaHashes(updateSchemaHashes);


		} finally {
			sakaiSession.setUserEid(userId);
			sakaiSession.setUserId(eId);
		}
	}

	public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
		return structuredArtifactDefinitionManager;
	}

	public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
		this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
	}

}

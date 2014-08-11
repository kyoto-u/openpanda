/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseScaffoldingController.java $
* $Id: BaseScaffoldingController.java 85378 2010-11-23 17:35:53Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.matrix.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;

public class BaseScaffoldingController {
   
   protected final Log logger = LogFactory.getLog(getClass());
	
   private AuthorizationFacade authzManager;
   private MatrixManager matrixManager;
   private IdManager idManager;
   private LockManager lockManager = null;
   private TaggingManager taggingManager;
   private WizardActivityProducer wizardActivityProducer;
   private WorksiteManager worksiteManager = null;
   private AuthenticationManager authManager = null;
   
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      Scaffolding scaffolding;
      if (request.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null &&
            session.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null) {
         
         if (request.get("scaffolding_id") != null && !request.get("scaffolding_id").equals("")) {
            Id id = getIdManager().getId((String)request.get("scaffolding_id"));
            scaffolding = getMatrixManager().getScaffolding(id);
         }
         else {
        	Id worksiteId = worksiteManager.getCurrentWorksiteId();
            scaffolding = getMatrixManager().createDefaultScaffolding();
            scaffolding.setWorksiteId(worksiteId);
            
            scaffolding.setOwner(authManager.getAgent());
            
         }
            EditedScaffoldingStorage sessionBean = new EditedScaffoldingStorage(scaffolding);
            session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
                  sessionBean);
         
      }
      else {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
               EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         scaffolding = sessionBean.getScaffolding();
         
         session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      }
      // Traversing the collections to un-lazily load
      scaffolding.getLevels().size();
      scaffolding.getCriteria().size();
      traverseScaffoldingCells(scaffolding);
      
      return scaffolding;
   }
   
   protected void traverseScaffoldingCells(Scaffolding scaffolding) {
      matrixManager.getScaffoldingCells(scaffolding.getId());
      scaffolding.getScaffoldingCells().size();
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         sCell.getCells().size();
      }
   }
   
   protected boolean isDirtyProgression(Scaffolding scaffolding) {
      int newProgression = scaffolding.getWorkflowOption();
      if (scaffolding.getId() == null)
         return true;
      
      Scaffolding origScaff = matrixManager.getScaffolding(scaffolding.getId());
      int origProgression = origScaff.getWorkflowOption();
      
      return (newProgression != origProgression);
   }

   protected Scaffolding saveScaffolding(Scaffolding scaffolding) {
      boolean isDirty = isDirtyProgression(scaffolding);

      // Check for Hibernate error, which could happen if:
      // (1) admin starts to revises published but unused matrix
      // (2) user 'uses' matrix by adding forms/reflections
      // (3) admin now tries to save changes
      try
      {
    	  // if taggable, remove tags for any page defs that have been removed
    	  if (getTaggingManager().isTaggable() && 
    			  scaffolding.getId() != null && 
    			  wizardActivityProducer != null) 
    	  {
    		  Scaffolding origScaffolding = getMatrixManager()
    		  .getScaffolding(scaffolding.getId());
    		  Set<ScaffoldingCell> storedCells = origScaffolding
    		  .getScaffoldingCells();
    		  for (ScaffoldingCell cell : storedCells) 
    		  {
    			  if (!scaffolding.getScaffoldingCells().contains(cell)) 
    			  {
    				  for (TaggingProvider provider : getTaggingManager()
    						  .getProviders()) 
    				  {
    					  TaggableActivity activity = wizardActivityProducer
    					  .getActivity(cell.getWizardPageDefinition());
    					  provider.removeTags(activity);
    				  }
    			  }
    		  }
    	  }
    	  
    	  //call save instead of store (which uses hibernate merge()) when you need the
    	  //newId to be used as the new saved Id.  (only in case where the scaffolding is new)
    	  //*problem was that merge did not switch newId to id when saved
    	  if(scaffolding.getId() == null && scaffolding.getNewId() != null){
    		  scaffolding = (Scaffolding) getMatrixManager().getScaffolding((Id) getMatrixManager().save(scaffolding));
    	  }else{
    		  scaffolding = getMatrixManager().storeScaffolding(scaffolding);
    	  }
        
      }
      catch ( Exception e )
      {
         logger.error( e.toString() );
         // tbd how to report error back to admin user?
      }
      
      //regen the cells
      regenerateScaffoldingCells(scaffolding, isDirty);
      
      if (isDirty)
         regenerateMatrixCellStatus( scaffolding );
      
      return scaffolding;
   }
   
   /**
    ** Update the status of all matrix cells to match the initial status 
    ** defined for the corresponding scaffolding cell.
    **/
   protected void regenerateMatrixCellStatus( Scaffolding scaffolding )
   {
      List matrices = getMatrixManager().getMatrices(scaffolding.getId());

      for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) 
      {
         Matrix matrix = (Matrix)matrixIt.next();
         Set cells = matrix.getCells();
       
         for (Iterator cellIt=cells.iterator(); cellIt.hasNext();) 
         {
            Cell cell = (Cell)cellIt.next();
            WizardPage page = cell.getWizardPage();
            String status = page.getStatus();
            String initialStatus = cell.getScaffoldingCell().getInitialStatus();
            
            if (!status.equals(initialStatus))
            {
               page.setStatus( initialStatus );
               getMatrixManager().storePage( page );
               
               // If status was locked and is now ready, then unlock resources
               if ( initialStatus.equals(MatrixFunctionConstants.READY_STATUS) &&
                    status.equals(MatrixFunctionConstants.LOCKED_STATUS) )
               {
                  for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) 
                  {
                     Attachment att = (Attachment) iter.next();
                     lockManager.removeLock(att.getArtifactId().getValue(),
                           page.getId().getValue());
                  }
                  for (Iterator iter2 = page.getPageForms().iterator(); iter2.hasNext();) 
                  {
                     WizardPageForm form = (WizardPageForm) iter2.next();
                     lockManager.removeLock(form.getArtifactId().getValue(),
                           page.getId().getValue());
                  }
               }
            }
         }
      }
   }
   
   protected void regenerateScaffoldingCells(Scaffolding scaffolding, boolean dirtyProgression) {
      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();
      Criterion criterion = new Criterion();
      Level level = new Level();
      Set cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());
      boolean firstRow = true;
      boolean firstColumn = true;
      
      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);
            String status = MatrixFunctionConstants.READY_STATUS;
            if ((scaffolding.getWorkflowOption() == Scaffolding.HORIZONTAL_PROGRESSION && !firstColumn) ||
                  (scaffolding.getWorkflowOption() == Scaffolding.VERTICAL_PROGRESSION && !firstRow) ||
                  (scaffolding.getWorkflowOption() == Scaffolding.MANUAL_PROGRESSION)) {
               status = MatrixFunctionConstants.LOCKED_STATUS;
            }
            
            if (scaffoldingCell == null) {
            	boolean defaults = getMatrixManager().isEnableDafaultMatrixOptions();
               scaffoldingCell = new ScaffoldingCell(criterion, level, status, scaffolding, defaults, defaults, defaults, defaults, defaults, defaults, defaults);
               scaffoldingCell.getWizardPageDefinition().setSiteId(scaffolding.getWorksiteId().getValue());
               scaffoldingCell.getWizardPageDefinition().setTitle(getDefaultTitle(scaffolding, criterion, level));
               getMatrixManager().storeScaffoldingCell(scaffoldingCell);
            }
            else{
            	if (dirtyProgression){          
            		scaffoldingCell.setInitialStatus(status);
            	}
            	scaffoldingCell.getWizardPageDefinition().setTitle(getDefaultTitle(scaffolding, criterion, level));
            	getMatrixManager().storeScaffoldingCell(scaffoldingCell);
            }
            firstColumn = false;
         }
         firstRow = false;
         //Need to reset firstColumn when moving to the next row
         firstColumn = true;
      }
   }
   
   private ScaffoldingCell getScaffoldingCell(Set cells, Criterion criterion, Level level) {
      for (Iterator iter=cells.iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         if (scaffoldingCell.getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               scaffoldingCell.getLevel().getId().getValue().equals(level.getId().getValue())) {
            return scaffoldingCell;
         }
      }
      return null;
   }
   
   protected String getDefaultTitle(Scaffolding scaffolding, Criterion criterion, Level level) {
      String title = "";
      if(scaffolding.getRowLabel() != null && !"".equals(scaffolding.getRowLabel()))
    	  title = title + scaffolding.getRowLabel() + ": ";
      title = title + criterion.getDescription() + "; ";
      if(scaffolding.getColumnLabel() != null && !"".equals(scaffolding.getColumnLabel()))
    	  title = title + scaffolding.getColumnLabel() + ": ";
      title = title + level.getDescription();

      return title;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	public WorksiteManager getWorksiteManager()
	{
		return worksiteManager;
	}

	public void setWorksiteManager(WorksiteManager worksiteManager)
	{
		this.worksiteManager = worksiteManager;
	}

	public AuthenticationManager getAuthManager()
	{
		return authManager;
	}

	public void setAuthManager(AuthenticationManager authManager)
	{
		this.authManager = authManager;
	}
}

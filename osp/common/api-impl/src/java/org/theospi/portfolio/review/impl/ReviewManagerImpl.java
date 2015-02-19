/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/common/api-impl/src/java/org/theospi/portfolio/review/impl/ReviewManagerImpl.java $
* $Id: ReviewManagerImpl.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.review.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.event.EventService;
import org.theospi.event.EventConstants;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;

public class ReviewManagerImpl extends HibernateDaoSupport implements ReviewManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private ContentHostingService contentHosting = null;
   private LockManager lockManager;
   private EventService eventService = null;
   private AgentManager agentManager = null;
   
   /**
    * This creates a new review with a generated id.  it also flags the review as being a new object
    * @param String description
    * @param String siteId
    * @return Review
    */
   public Review createNew(String description, String siteId) {
      Review review = new Review(getIdManager().createId(), description, 
            siteId);

      return review;
   }

   public Review getReview(Id reviewId) {
      Review review = (Review)getHibernateTemplate().get(Review.class, reviewId);

      if (review == null) {
         return null;
      }

      return review;
   }
   
   public List getReviewsByParent(String parentId) {
      Object[] params = new Object[]{parentId};
      return getHibernateTemplate().findByNamedQuery("getReviewsByParent", params);
   }
   
   /**
    * {@inheritDoc}
    */
   public List getReviewsByParent(String parentId, String siteId, String producer) {
      Object[] params = new Object[]{parentId};
      return getReviewsByParent("getReviewsByParent", params, parentId, siteId, producer);
   }

  /**
    * {@inheritDoc}
    */
   public List getReviewsByParentAndType(String parentId, int type, String siteId, String producer) {
      Object[] params = new Object[]{parentId, Integer.valueOf(type)};
      return getReviewsByParent("getReviewsByParentAndType", params, parentId, siteId, producer);
   }
    /**
    * the top function for getting the reviews.  This pushes these review content
    * into the security advisor.
    *
    * @param query
    * @param params
    * @param parentId
    * @param siteId
    * @param producer
    * @return List of Review classes
     */
    public List getReviewsByParentAndTypes(String parentId, int[] intTypes, String siteId, String producer) {
        Integer[] types = new Integer[intTypes.length];
        for (int i=0;i<intTypes.length;i++){
            types[i] = Integer.valueOf(intTypes[i]);
        }
        List reviews = this.getSession().createCriteria(Review.class).add(
                Restrictions.eq("parent",parentId)).add(Restrictions.in("type",types)).list();
        populateReviews(parentId, siteId, producer, reviews);
        return reviews;
    }

    protected List getReviewsByParent(String query, Object[] params, String parentId, String siteId, String producer) {
       List reviews = getHibernateTemplate().findByNamedQuery(query, params);
       populateReviews(parentId, siteId, producer, reviews);
       return reviews;
    }

    public List<Review> getReviewsByMatrix(String matrixId) {
        Id id = getIdManager().getId(matrixId);
        List<Review> reviews = (List<Review>) getHibernateTemplate().findByNamedQuery("getReviewsByMatrix",
                new Object[] {id});
        return reviews;
    }

    public List<Review> getReviewsByMatrixAndType(String matrixId, int type) {
        Id id = getIdManager().getId(matrixId);
        List<Review> reviews = (List<Review>) getHibernateTemplate().findByNamedQuery("getReviewsByMatrixAndType",
                new Object[] {id, type});
        return reviews;
    }

    protected void populateReviews(String parentId, String siteId, String producer, List reviews) {
        for (Iterator i = reviews.iterator(); i.hasNext();) {
           Review review = (Review) i.next();
           Node node = getNode(review.getReviewContent(), parentId, siteId, producer);
           review.setReviewContentNode(node);
        }
    }

    public Review saveReview(Review review) {
      if (review.isNewObject()) {
         review.setNewId(review.getId());
         review.setId(null);
         getHibernateTemplate().save(review);
         review.setNewObject(false);
         eventService.postEvent(EventConstants.EVENT_REVIEW_ADD, review.getId().getValue());
      }
      else {
         getHibernateTemplate().saveOrUpdate(review);
         eventService.postEvent(EventConstants.EVENT_REVIEW_REVISE, review.getId().getValue());
      }    

      return review;
   }

   public void deleteReview(Review review) {
      getHibernateTemplate().delete(review);
      eventService.postEvent(EventConstants.EVENT_REVIEW_DELETE, review.getId().getValue());
   }

   public List listReviews(String siteId) {
      return getHibernateTemplate().findByNamedQuery("getReviewsBySite",
            siteId);
   }
   
   public List getReviews() {
      return getHibernateTemplate().findByNamedQuery("getReviews");
   }

   public Review getReview(String id) {
      return getReview(getIdManager().getId(id));
   }
   
   protected Node getNode(Id artifactId, String parentId, String siteId, String producer) {
      Node node = getNode(artifactId);
      
      if (node == null) {
         return null;
      }
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(siteId, parentId, node.getResource(), producer));

      return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner(), node.getIsLocked(), 
    		  buildRefDecorator(siteId, parentId, producer) + 
    		  buildRefDecorator(siteId, artifactId.getValue(), MetaobjEntityManager.METAOBJ_CONTENT_ENTITY_PREFIX));
   }

   /**
    * pushes the artifact into the security advisor.  It then gets the resource, properties, and owner
    * and places these into a Node.
    *
    * @param artifactId Id
    * @return Node
    * @throws RuntimeException on PermissionException, IdUnusedException, and TypeException
    */
   protected Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
               getContentHosting().getReference(id)));

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent((getIdManager().getId(ownerId)));
         boolean locked = getLockManager().isLocked(artifactId.getValue());

         return new Node(artifactId, resource, owner, locked);
      }
      catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }

   protected String buildRef(String siteId, String contextId, ContentResource resource,
         String producer) {
      return ContentEntityUtil.getInstance().buildRef(
         producer, siteId, contextId, resource.getReference());
   }
   
   /**
    * Build a new reference with the given params
    * @param siteId
    * @param contextId
    * @param producer
    * @return A String reference like so: "/&lt;producer&gt;/&lt;siteId&gt;/&lt;contextId&gt;"
    */
   protected String buildRefDecorator(String siteId, String contextId, String producer) {
	   return ContentEntityUtil.getInstance().buildRef(
		         producer, siteId, contextId, "");
   }

   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   /**
    * @return Returns the authorizationFacade.
    */
   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   /**
    * @param authorizationFacade The authorizationFacade to set.
    */
   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return Returns the securityService.
    */
   public SecurityService getSecurityService() {
      return securityService;
   }

   /**
    * @param securityService The securityService to set.
    */
   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   /**
    * @return Returns the contentHosting.
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting The contentHosting to set.
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public EventService getEventService() {
	   return eventService;
   }

   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }

   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

}

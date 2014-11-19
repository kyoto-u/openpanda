/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/api/src/java/org/theospi/portfolio/review/mgt/ReviewManager.java $
* $Id: ReviewManager.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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
package org.theospi.portfolio.review.mgt;

import java.util.List;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;

public interface ReviewManager {

   public final static String CURRENT_REVIEW = "org.theospi.portfolio.review.currentReview";
   public final static String CURRENT_REVIEW_ID = "org.theospi.portfolio.review.currentReviewId";
   public final static String CANCEL_REVIEW = "org.theospi.portfolio.review.cancelReview";

   public Review createNew(String description, String siteId);

   public Review getReview(Id reviewId);

   public Review saveReview(Review review);

   public void deleteReview(Review review);

   public List listReviews(String siteId);

   public Review getReview(String id);
   
   public Node getNode(Reference ref);
   
   public List getReviews();
   public List getReviewsByParent(String parentId);
   
   /**
    * the top function for getting the reviews.  This pushes these review content 
    * into the security advisor.
    * 
    * @param parentId
    * @param siteId
    * @param producer
    * @return List of Review
    */
   public List getReviewsByParent(String parentId, String siteId, String producer);
   
   /**
    * the top function for getting the reviews.  This pushes these review content 
    * into the security advisor.
    * 
    * @param parentId
    * @param type
    * @param siteId
    * @param producer
    * @return List of Review
    */
   public List getReviewsByParentAndType(String parentId, int type, String siteId, String producer);

    /**
     * Returns a list of reviews for the given criteria
     *
     * @param parentId - the parentId of the review
     * @param types - array of types to search for
     * @param siteId - the siteId  of the review
     * @param producer
     * @return List of Review
     */
    public List getReviewsByParentAndTypes(String parentId, int[] types, String siteId, String producer);

    /**
     * Retrieve all reviews for all cells in a user's matrix. This does not push the
     * content to the security advisor since its purpose is bulk list efficiency.
     *
     * @param matrixId - the ID of the user's matrix
     * @return List of Review of all types
     */
    public List<Review> getReviewsByMatrix(String matrixId);

    /**
     * Retrieve all reviews of a given type for all cells in a user's matrix. This does not push the
     * content to the security advisor since its purpose is bulk list efficiency.
     *
     * @param matrixId the ID of the user's matrix
     * @param type the desired type of review
     * @return List of Review of all types
     */
    public List<Review> getReviewsByMatrixAndType(String matrixId, int type);

}

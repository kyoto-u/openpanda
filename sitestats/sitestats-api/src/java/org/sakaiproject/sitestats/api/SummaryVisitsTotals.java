/**
 * $URL: https://source.sakaiproject.org/svn/sitestats/tags/sakai-10.0/sitestats-api/src/java/org/sakaiproject/sitestats/api/SummaryVisitsTotals.java $
 * $Id: SummaryVisitsTotals.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 *
 * Copyright (c) 2006-2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.sitestats.api;

public interface SummaryVisitsTotals {

	public double getLast30DaysVisitsAverage();

	public void setLast30DaysVisitsAverage(double last30DaysVisitsAverage);

	public double getLast365DaysVisitsAverage();

	public void setLast365DaysVisitsAverage(double last365DaysVisitsAverage);

	public double getLast7DaysVisitsAverage();

	public void setLast7DaysVisitsAverage(double last7DaysVisitsAverage);

	public double getPercentageOfUsersThatVisitedSite();

	public void setPercentageOfUsersThatVisitedSite(double percentageOfUsersThatVisitedSite);

	public long getTotalUniqueVisits();

	public void setTotalUniqueVisits(long totalUniqueVisits);

	public int getTotalUsers();

	public void setTotalUsers(int totalUsers);

	public long getTotalVisits();

	public void setTotalVisits(long totalVisits);

}
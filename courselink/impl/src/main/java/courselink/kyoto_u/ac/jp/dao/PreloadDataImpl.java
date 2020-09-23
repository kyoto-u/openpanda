/******************************************************************************
 * PreloadDataImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package courselink.kyoto_u.ac.jp.dao;

import org.apache.log4j.Logger;

public class PreloadDataImpl {

	private static final Logger log = Logger.getLogger(PreloadDataImpl.class);


	private CourselinkDao dao;
	public void setDao(CourselinkDao dao) {
		this.dao = dao;
	}

	public void init() {
		preloadItems();
	}

	/**
	 * Preload some items into the database
	 */
	public void preloadItems() {
	}
}

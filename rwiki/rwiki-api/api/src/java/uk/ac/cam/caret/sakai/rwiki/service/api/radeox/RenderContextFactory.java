/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-10.6/rwiki-api/api/src/java/uk/ac/cam/caret/sakai/rwiki/service/api/radeox/RenderContextFactory.java $
 * $Id: RenderContextFactory.java 9108 2006-05-08 14:30:57Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package uk.ac.cam.caret.sakai.rwiki.service.api.radeox;

import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;

// FIXME: Service

public interface RenderContextFactory
{

	/**
	 * Creates a render context to hold information for the renderer
	 * 
	 * @param rwo
	 *        The RWiki Object to be rendered
	 * @param user
	 *        The User requesting the render
	 * @param renderEngine
	 *        The Render engine performing the render
	 * @return A Render Context suitable fo use with the RenderEngine
	 */
	RenderContext getRenderContext(RWikiObject rwo, RenderEngine renderEngine);

}

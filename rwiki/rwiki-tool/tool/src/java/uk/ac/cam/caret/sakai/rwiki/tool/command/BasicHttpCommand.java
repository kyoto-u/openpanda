/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-10.1/rwiki-tool/tool/src/java/uk/ac/cam/caret/sakai/rwiki/tool/command/BasicHttpCommand.java $
 * $Id: BasicHttpCommand.java 21065 2007-02-07 00:15:30Z ian@caret.cam.ac.uk $
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

package uk.ac.cam.caret.sakai.rwiki.tool.command;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand;

/**
 * @author andrew
 */
public class BasicHttpCommand implements HttpCommand
{
	private static Log log = LogFactory.getLog(BasicHttpCommand.class);
	
	private String servletPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.tool.service.HttpCommand#execute(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void execute(Dispatcher dispatcher, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		this.beforeDispatch(request, response);
		if (!response.isCommitted())
		{
			this.dispatch(dispatcher,request, response);
			this.afterDispatch(request, response);
		}
	}

	public void afterDispatch(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		// EMPTY
	}

	public void beforeDispatch(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		// EMPTY
	}

	public void dispatch(Dispatcher dispatcher, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		
		dispatcher.dispatch(servletPath,request, response);
	}

	public String getServletPath()
	{
		return servletPath;
	}

	public void setServletPath(String servletPath)
	{
		this.servletPath = servletPath;
	}


}

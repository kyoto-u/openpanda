/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/tags/kernel-1.3.1/api/src/main/java/org/sakaiproject/antivirus/api/VirusScanner.java $
 * $Id: VirusScanner.java 79508 2010-07-15 17:38:55Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 Sakai Foundation
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

package org.sakaiproject.antivirus.api;

import java.io.InputStream;


/**
 * provide virus scanning capabilities
 * <br>Creation Date: Mar 23, 2005
 *
 * @author Mike DeSimone, mike.[at].rsmart.com
 * @author John Bush
 * @version $Revision: 79508 $
 */
public interface VirusScanner {

   /**
    * check whether the virus scanner is enabled
    * @return true if virus scanning can be performed
    */
   public boolean getEnabled();

   /**
    * scan byte array for a virus
    * @param bytes
    * @throws VirusFoundException if a virus was found
    * @throws VirusScanIncompleteException if virus scan was not completed due to an error
    */
   public void scan(byte[] bytes) throws VirusFoundException, VirusScanIncompleteException;

   /**
    * Scan the InputStream for viruses
    * @param inputStream content to scan
    * @throws VirusFoundException
    * @throws VirusScanIncompleteException
    */
   public void scan(InputStream inputStream) throws VirusFoundException, VirusScanIncompleteException;
   
   /**
    * Scan a item from content hosting service
    * @param resourceId a resource if for an item in contenthosting e.g /content/a434234sdfghsdf
    * @throws VirusFoundException
    * @throws VirusScanIncompleteException
    */
   public void scanContent(String resourceId) throws VirusFoundException, VirusScanIncompleteException;
}

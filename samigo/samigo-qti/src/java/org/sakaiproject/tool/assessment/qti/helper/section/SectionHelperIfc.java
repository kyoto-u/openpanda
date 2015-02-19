/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.4/samigo-qti/src/java/org/sakaiproject/tool/assessment/qti/helper/section/SectionHelperIfc.java $
 * $Id: SectionHelperIfc.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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



package org.sakaiproject.tool.assessment.qti.helper.section;

import java.io.InputStream;

import org.sakaiproject.tool.assessment.qti.asi.Section;

/**
 * Interface for QTI-versioned section helper implementation.
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: SectionHelperIfc.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
 */

public interface SectionHelperIfc
{
  /**
   * Interface for QTI-versioned section helper implementation.
   * <p>Copyright: Copyright (c) 2005</p>
   * <p>Organization: Sakai Project</p>
   * @author Ed Smiley esmiley@stanford.edu
   * @version $Id: SectionHelperIfc.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
   */

  /**
   * read XML document into Section XML
   *
   * @param inputStream the input stream
   *
   * @return Section XML
   */
  public Section readXMLDocument(InputStream inputStream);


  /**
   * Update section XML XPath with value
   *
   * @param sectionXml XML PENDING
   * @param xpath the XPath
   * @param value the value
   *
   * @return the Section XML
   */
  public Section updateSectionXml(
    Section sectionXml, String xpath,
    String value);

}

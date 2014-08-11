/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/export/PresentationExport.java $
* $Id:PresentationExport.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008, 2009 The Sakai Foundation
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
package org.theospi.portfolio.presentation.export;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import websphinx.Access;
import websphinx.Crawler;
import websphinx.DownloadParameters;
import websphinx.Link;
import websphinx.LinkEvent;
import websphinx.LinkListener;
import websphinx.Page;

public class PresentationExport extends Crawler implements LinkListener {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private PortfolioMirror mirror = null;
   private String hostName = null;
   private String webappName = null;
   private String tempDirectory = null;
   public static final int BUFFER = 1024 * 10;
   private ArrayList errorLinks = new ArrayList();
   private static SessionAccess access = new SessionAccess();

   public PresentationExport(String url, String tempDirectory) throws IOException {
      this.tempDirectory = tempDirectory;

      Access.setAccess(access);

      URL urlObj = new URL(url);
      this.hostName = urlObj.getHost();
      String path = urlObj.getPath();

      StringTokenizer tok = new StringTokenizer(path, "/", false);

      webappName = tok.nextToken();
      if (!tok.hasMoreTokens()) {
         webappName = "";
      }
      else {
         webappName = "/" + webappName;
      }

      mirror = new PortfolioMirror(tempDirectory, webappName);

      this.setRootHrefs(url);
      this.setLinkType(Crawler.ALL_LINKS);
      this.setSynchronous(true);
      this.setDomain(Crawler.WEB);
      this.addLinkListener(this);

      DownloadParameters dp = getDownloadParameters();
      dp = dp.changeMaxThreads(1);
      setDownloadParameters(dp.changeMaxPageSize(2000));
   }

   public void createZip(OutputStream out) throws IOException {
	   File directory = new File(tempDirectory + webappName);

	   CheckedOutputStream checksum = null;
	   ZipOutputStream zos = null;
	   try{
		   checksum =  new CheckedOutputStream(out, new Adler32());
		   zos = new ZipOutputStream(new BufferedOutputStream(checksum));
		   recurseDirectory("", directory, zos);

		   zos.finish();
		   zos.flush();
	   } 
	   finally {
		   if (zos != null) {
			   try {
				zos.close();
			} catch (IOException e) {
			}
		   }
		   if (checksum != null) {
			   try {
				checksum.close();
			} catch (IOException e) {
			}
		   }
	   }
      
   }

   /**
    * places a directory into the zip stream
    * @param parentPath
    * @param directory
    * @param zos
    * @throws IOException
    */
   protected void recurseDirectory(String parentPath, File directory, ZipOutputStream zos) throws IOException {
      // get all files... go through those
      File[] files = directory.listFiles(new DirectoryFileFilter(false));
      
      if(files == null)
         throw new NullPointerException("recursing through a directory which is not a directory: " + parentPath + " ---- " + directory);
      
      addFiles(zos, parentPath, files);

      // get all directories... go through those...
      File[] directories = directory.listFiles(new DirectoryFileFilter(true));
      for (int i=0;i<directories.length;i++) {
         recurseDirectory(parentPath + directories[i].getName() + "/",
            directories[i], zos);
      }

   }

   protected void addFiles(ZipOutputStream out, String parentPrefix,
                      File [] files) throws IOException {

      BufferedInputStream origin = null;

      byte data[] = new byte[BUFFER];
      for (int i=0;i<files.length;i++) {
         String fileName = URLDecoder.decode( parentPrefix + files[i].getName() );
         logger.debug("Adding " + fileName);
         InputStream in = null;
         try {
            in = new FileInputStream(files[i]);

            if (in == null)
               throw new NullPointerException();

            origin = new BufferedInputStream(in, BUFFER);

            if (fileName == null)
               throw new NullPointerException();

            ZipEntry entry = new ZipEntry(fileName);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
               out.write(data, 0, count);
            }
            out.closeEntry();
         } finally {
        	 try {
        		 if (origin != null) {
        			 origin.close();
        		 }
        	 } catch (Exception e) {
        		 logger.warn("Error cleaning up resource: ", e);
        	 }
            try {
               in.close();
            } catch (Exception e) {
               logger.warn("Error cleaning up resource: ", e);
            }
         }
      }
   }

   /**
    * Start crawling.  Returns either when the crawl is done, or
    * when pause() or stop() is called.  Because this method implements the
    * java.lang.Runnable interface, a crawler can be run in the
    * background thread.
    */
   public void run() {
      super.run();

      // process error links
      for (Iterator i=errorLinks.iterator();i.hasNext();) {
         Link link = (Link)i.next();
         visit(link.getPage());
      }
   }


   public synchronized void visit(Page page) {

      try {
         mirror.writePage(page);
         mirror.rewrite();
      } catch (IOException e) {
         logger.info("Error visiting link.  Most likely broken link.", e);
      }

      logger.debug("visiting page");
      super.visit(page);
   }

   public synchronized boolean shouldVisit(Link link) {
      if (link.getMethod() == Link.POST) {
         return false;
      }

      if (!link.getHost().equalsIgnoreCase(hostName)) {
         return false;
      }

      // TODO maybe if (link.getURL().getFile().startsWith(webappName + "/showPublicPortfolio.do")) {
      //   return false;
      //}

      return true;
   }

   public void deleteTemp() {
      File temp = new File(tempDirectory);

      deleteContent(temp);
      temp.delete();
   }

   protected void deleteContent(File directory) {
      File[] files = directory.listFiles(new DirectoryFileFilter(false));

      if (files != null) {
         for (int i=0;i<files.length;i++) {
            files[i].delete();
         }
      }

      // get all directories... go through those...
      File[] directories = directory.listFiles(new DirectoryFileFilter(true));
      if (directories != null) {
         for (int i=0;i<directories.length;i++) {
            deleteContent(directories[i]);
            directories[i].delete();
         }
      }
   }

   /**
    * Notify that an event occured on a link.
    */
   public void crawled(LinkEvent event) {
      if (event.getID() == LinkEvent.ERROR) {
         // switch to stream page link

         if (!(event.getLink().getPage() instanceof StreamedPage)) {
            logger.debug("loading file through streamed page.");
            Link newLink = new Link(event.getLink().getURL());
            newLink.setPage(new StreamedPage(event.getLink()));
            addErrorLink(newLink);
         }
         else {
            logger.error("Link error " + event.getLink().getURL().toExternalForm(),
               event.getException());
         }
      }
      else if (event.getID() == LinkEvent.QUEUED) {
         if (event.getLink().getPage() instanceof StreamedPage) {
            event.getLink().setStatus(LinkEvent.DOWNLOADED);
         }
      }
   }

   protected synchronized void addErrorLink(Link newLink) {
      errorLinks.add(newLink);
   }

   /**
    * Implements the FileFilter.  it accepts the switch of whether to accept files or directories
    *
    */
   private static class DirectoryFileFilter implements FileFilter {
      private boolean directories = false;

      public DirectoryFileFilter(boolean directories) {
         this.directories = directories;
      }

      /**
       * Tests whether or not the specified abstract pathname should be
       * included in a pathname list.
       *
       * @param pathname The abstract pathname to be tested
       * @return <code>true</code> if and only if <code>pathname</code>
       *         should be included
       */
      public boolean accept(File pathname) {
         if (directories) {
            return pathname.isDirectory();
         }
         else {
            return pathname.isFile();
         }
      }

   }


}

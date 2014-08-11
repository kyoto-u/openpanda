/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool-lib/src/java/org/theospi/portfolio/help/control/GlossaryTag.java $
* $Id:GlossaryTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.helper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.jsp.JspWriter;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;

public class HelpTagHelper {
   

   static public void renderHelp(Reader reader, int charCount, Writer writer, GlossaryEntry[] terms, 
         boolean firstOnly, boolean hover, boolean link) throws IOException
   {
      boolean wordState = true;
      boolean inPhrase = false;
      StringBuilder buf = new StringBuilder();
      Collection foundWords = new HashSet();
      
      for (int i = 0; i < charCount; i++) {
         char in = (char) reader.read();

         if (wordState && isWordBoundary(in)) {
            // check if the captured phrase is a part of any of the terms
            boolean currentPhrase = isPhraseStart(buf.toString() + in, terms);

            if (currentPhrase) {
               inPhrase = true;
               buf.append(in);
               continue;
            }

            GlossaryEntry entry = searchGlossary(buf.toString(), terms);
            if (inPhrase && entry == null) {
               outputPhrase(writer, buf, foundWords, terms, firstOnly, hover, link);
               inPhrase = false;
            }
            else if (entry == null ||
               (firstOnly && foundWords.contains(buf.toString()))) {
               writer.write(buf.toString());
            }
            else {
               writer.write(getMarkup(buf.toString(), entry, hover, link));
               foundWords.add(buf.toString());
            }

            // If we are going into an HTML tog then stop
            if (in == '<') {
               wordState = false;
            }
            writer.write(in);

            buf = new StringBuilder();
         } else if (wordState) {
            buf.append(in);
         } else if (in == '>') {
            // If we are going out of an HTML tog then start
            wordState = true;
            writer.write(in);
         } else {
            writer.write(in);
         }
      }

      if (buf != null) {
         handleLast(writer, buf, terms, inPhrase, foundWords, firstOnly, hover, link);
      }
   }

   static protected void handleLast(Writer out, StringBuilder buf, GlossaryEntry[] terms,
                             boolean inPhrase, Collection foundWords, 
                             boolean firstOnly, boolean hover, boolean link) throws IOException {
      GlossaryEntry entry = searchGlossary(buf.toString(), terms);
      if (inPhrase && entry == null) {
         outputPhrase(out, buf, foundWords, terms, firstOnly, hover, link);
         inPhrase = false;
      }
      else if (entry == null ||
         (firstOnly && foundWords.contains(buf.toString()))) {
         out.write(buf.toString());
      }
      else {
         out.write(getMarkup(buf.toString(), entry, hover, link));
         foundWords.add(buf.toString());
      }
   }

   static public HelpManager getHelpManager() {
      return (HelpManager) ComponentManager.getInstance().get("helpManager");
   }


   /**
    * Checks to see if the phrase matches the beginning of any terms
    * @param phrase
    * @param terms
    * @return boolean
    */
   static protected boolean isPhraseStart(String phrase, GlossaryEntry[] terms) {
      if (phrase.length() == 0) {
         return false;
      }

      // go backwards... more efficient
      //each term must be translated into it's html equivalent
      //    the phrase may not have the whole html equivalent yet.
      for (int i=terms.length - 1;i>=0;i--) {
         GlossaryEntry entry = terms[i];
         String term = entry.getTerm();
         term = term.replaceAll("&", "&amp;");
         term = term.replaceAll(">", "&gt;");
         term = term.replaceAll("<", "&lt;");
         if (term.toLowerCase().startsWith(phrase.toLowerCase())) {
            return true;
         }
      }
      return false;
   }

   static protected GlossaryEntry searchGlossary(String phrase, GlossaryEntry[] terms) {
      phrase = phrase.replaceAll("&amp;", "&");
      phrase = phrase.replaceAll("&gt;", ">");
      phrase = phrase.replaceAll("&lt;", "<");
      for (int i=0;i<terms.length;i++) {
         GlossaryEntry entry = terms[i];
         if (entry.getTerm().toLowerCase().equals(phrase.toLowerCase())) {
            return entry;
         }
      }
      return null;
   }


   static protected void outputPhrase(Writer out, StringBuilder buf, Collection foundWords, GlossaryEntry[] terms, 
         boolean firstOnly, boolean hover, boolean link) throws IOException {
      StringBuilder newBuf = new StringBuilder();
      boolean firstWord = false;
      boolean inPhrase = false;

      for (int i=0;i<buf.length();i++) {
         char in = buf.charAt(i);

         if (isWordBoundary(in) && newBuf.length() > 0) {
            boolean currentPhrase = false;
            GlossaryEntry entry = null;

            if (firstWord) {
               currentPhrase = isPhraseStart(newBuf.toString() + in, terms);
            }

            firstWord = true;
            if (!currentPhrase) {
               entry = searchGlossary(newBuf.toString(), terms);
            }
            else {
               inPhrase = true;
               newBuf.append(in);
               continue;
            }

            if (inPhrase && entry == null) {
               outputPhrase(out, newBuf, foundWords, terms, firstOnly, hover, link);
               inPhrase = false;
            }
            else if (entry == null ||
               (firstOnly && foundWords.contains(newBuf.toString()))) {
               out.write(newBuf.toString());
               out.write(in);
            } else {
               out.write(getMarkup(newBuf.toString(), entry, hover, link));
               foundWords.add(newBuf.toString());
               out.write(in);
            }

            newBuf = new StringBuilder();
         }
         else if (isWordBoundary(in)) {
            out.write(in);
         }
         else {
            newBuf.append(in);
         }
      }

      GlossaryEntry entry = searchGlossary(newBuf.toString(), terms);
      if (entry == null ||
         (firstOnly && foundWords.contains(newBuf.toString()))) {
         out.write(newBuf.toString());
      } else {
         out.write(getMarkup(newBuf.toString(), entry, hover, link));
         foundWords.add(newBuf.toString());
      }
   }

   static protected String getMarkup(String originalTerm, GlossaryEntry entry, boolean hover, boolean link) {
      StringBuilder markup = new StringBuilder();
      String url = ServerConfigurationService.getServerUrl();
      String linkName = url + getHelpManager().getGlossary().getUrl() + "?id=" + entry.getId();
      linkName += "&" + Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();

      markup.append("<a href=\"#\" onclick=\"openNewWindow('" + linkName + "');return false;\"");

      if (hover) {
         markup.append(" onMouseover=\"showtip(this,event,'" +
               replaceQuotes(entry.getDescription()) +
               "')\" onMouseOut=\"hidetip()\" ");
      }
      if (!link) {
         markup.append(" onClick=\"return false\" ");
      }
      markup.append(">" + originalTerm);
      markup.append("</a>");

      return markup.toString();

   }

   static protected String replaceQuotes(String description) {
      // replace \ with \\
      description = description.replaceAll("\\\\", "\\\\\\\\");

      // replace ' with \'
      description = description.replaceAll("\\\'", "\\\\'");

      // replace " with &quot;
      description = description.replaceAll("\\\"", "&quot;");

      return description;
   }

   static protected boolean isWordBoundary(char c) {
      //       matching [\s] means white space and \p{Punct} is for any punctuation
      return String.valueOf(c).matches("[\\s\\p{Punct}]");
   }
   
}
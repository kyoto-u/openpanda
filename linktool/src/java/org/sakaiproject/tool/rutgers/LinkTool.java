/**********************************************************************************
 * $URL: $
 * $Id: $
 **********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.tool.rutgers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.Web;

/**
 * <p>
 * Sakai Link Tool.
 * </p>
 * 
 * @author Charles Hedrick, Rutgers University.
 * @version $Revision: $
 */
@SuppressWarnings({ "serial", "deprecation" })
public class LinkTool extends HttpServlet
{
   private static final String UTF8 = "UTF-8";

   private static final String headHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n<head><title>Link Tool</title>";
   
   private static final String headHtml1 = "<script type=\"text/javascript\" language=\"JavaScript\">function setFrameHeight(id) { var frame = parent.document.getElementById(id); if (frame) {                var objToResize = (frame.style) ? frame.style : frame; objToResize.height = \""; 
   
   private static final String headHtml2 = "\";  }} </script></head>\n<body onload=\"";
   
   private static final String headHtml3 = "\" style='margin:0;padding:0;'>";
   
   private static final String tailHtml = "</body></html>";

   private static final String privkeyname = "sakai.rutgers.linktool.privkey";
   private static final String saltname = "sakai.rutgers.linktool.salt";
   
   /** Our log (commons). */
   private static Log M_log = LogFactory.getLog(LinkTool.class);
   
   private SecretKey secretKey = null;
   private SecretKey salt = null;
   private String ourUrl = null;
   
   private Set<String> illegalParams;
   private Pattern legalKeys;
   
   /**
    * Access the Servlet's information display.
    * 
    * @return servlet information.
    */
   public String getServletInfo()
   {
      return "Link Tool";
   }
   
   /**
    * Initialize the servlet.
    * 
    * @param config
    *        The servlet config.
    * @throws ServletException
    */
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);

      String homedir = ServerConfigurationService.getString("linktool.home", ServerConfigurationService.getSakaiHomePath());
      if (homedir == null)
         homedir = "/etc/";
      if (!homedir.endsWith("/"))
      {
         homedir = homedir + "/";
      }
      
      //    System.out.println("canread " + homedir + pubkeyname + (new File(homedir + pubkeyname)).canRead());
      //    System.out.println("canread " + homedir + privkeyname + (new File(homedir + privkeyname)).canRead());
      
      if (!(new File(homedir + privkeyname)).canRead()) {
         genkey(homedir);
      }
      
      //    System.out.println("canread public " + (new File(homedir + pubkeyname)).canRead());
      //    System.out.println("canread private " + (new File(homedir + privkeyname)).canRead());
      
      secretKey = readSecretKey(homedir + privkeyname, "Blowfish");
      //    if (secretKey != null)
      //        System.out.println("got private key");
      
      if (!(new File(homedir + saltname)).canRead()) {
         gensalt(homedir);
      }
      
      salt = readSecretKey(homedir + saltname, "HmacSHA1");
      
      ourUrl = ServerConfigurationService.getString("sakai.rutgers.linktool.serverUrl");
      // System.out.println("linktool url " + ourUrl);
      if (ourUrl == null || "".equals(ourUrl))
         ourUrl = ServerConfigurationService.getServerUrl();
      // System.out.println("linktool url " + ourUrl);
      if (ourUrl == null || "".equals(ourUrl))
         ourUrl = "http://127.0.0.1:8080";
      
      // System.out.println("linktool url " + ourUrl);
      
      illegalParams = new HashSet<String>();
      illegalParams.add("user");
      illegalParams.add("internaluser");
      illegalParams.add("site");
      illegalParams.add("role");
      illegalParams.add("session");
      illegalParams.add("serverurl");
      illegalParams.add("url");
      illegalParams.add("time");
      illegalParams.add("sign");
      illegalParams.add("placement");
      
      legalKeys = Pattern.compile("^[a-zA-Z0-9]+$");
      
      M_log.info("init(): home dir: " + homedir);
   }
   
   /**
    * Shutdown the servlet.
    */
   public void destroy()
   {
      M_log.info("destroy()");
      
      super.destroy();
   }
   
   /**
    * Respond to Get requests:
    *   display main content by redirecting to it and adding
    *     user= euid= site= role= serverurl= time= sign=
    *   for privileged users, add a bar at the top with a link to
    *     the setup screen
    *   ?Setup generates the setup screen
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException.
    * @throws IOException.
    */
   
   @SuppressWarnings("unchecked")
   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      
      // get the Tool
      Placement placement = ToolManager.getCurrentPlacement();
      Properties config = null;
      String placementId = "none";
      
      if (placement != null) {
         config = placement.getConfig();
         placementId = placement.getId();
      }
      
      res.setContentType("text/html; charset=utf-8");
      PrintWriter out = res.getWriter();
      
      if (secretKey == null || salt == null) {
         M_log.error("Linktool missing secret key or salt");
         writeErrorPage(req, out, null, "Linktool is not configured correctly", null);
         return;      
      }
      
      String userid = null;
      String euid = null;
      String siteid = null;
      String sessionid = null;
      String url = null;
      String signature = null;
      String element = null;
      String oururl = req.getRequestURI();
      String query = req.getQueryString();
      
      boolean isAnon = false;
      
      // set frame height
      
      StringBuffer bodyonload = new StringBuffer();
      if (placement != null)
      {
         element = Web.escapeJavascript("Main" + placement.getId( ));
         bodyonload.append("setFrameHeight('" + element + "');");
      }
      
      // prepare the data for the redirect
      
      // we can always get the userid from the session
      Session s = SessionManager.getCurrentSession();
      if (s != null && s.getUserId() != null) {
         M_log.debug("got session " + s.getId());
         userid = s.getUserId();
         euid = s.getUserEid();
         sessionid = s.getId();
      } else {
         // No valid user session
         User anon = UserDirectoryService.getAnonymousUser();
         userid = anon.getId();
         euid = anon.getEid();
         isAnon = true;
      }
      
      if (userid != null && (euid == null || "".equals(euid)))
         euid = userid;
      
      // site is there only for tools, otherwise have to use user's arg
      // this is safe because we verify that the user has a role in site
      if (placement != null)
         siteid = placement.getContext();
      if (siteid == null)
         siteid = req.getParameter("site");
      
      // if user has asked for a url, use it
      url = req.getParameter("url");
      // else take it from the tool config
      if (url == null && config != null)
         url = config.getProperty("url", null);
      
      if (url == null && config != null) {
         String urlProp = config.getProperty("urlProp", null);
         
         if (urlProp != null) {
            url = ServerConfigurationService.getString(urlProp);
         }
      }
      
      boolean trustedService = isTrusted(config);
      
      if (trustedService) {
         String appendChar = "?";
         if (url.contains("?")) {
            appendChar = "&";
         }
         try {
            String signingObject = "currentuser&sign=" + sign("currentuser");
            url += appendChar + "signedobject=" + URLEncoder.encode(signingObject, UTF8);
         } catch (Exception e) {
            
         }
      }
      
      // now get user's role in site; must be defined
      String realmId = null;
      String rolename = null;
      
      if (siteid != null) {
         realmId = SiteService.siteReference(siteid);
      }
      
      if (realmId != null && userid != null && !isAnon) {
    	  rolename = AuthzGroupService.getUserRole(userid, realmId);
      }
      
      // Check for .auth or .anon role
      if (rolename == null)
         rolename = isAnon ? AuthzGroupService.ANON_ROLE : AuthzGroupService.AUTH_ROLE;
      
      sessionid = (sessionid != null) ? encrypt(sessionid) : "";
      
      // generate redirect, as url?user=xxx&site=xxx
      
      if (url != null && userid != null && siteid != null && rolename != null && sessionid != null) {
         
         // command is the thing that will be signed
    	  
    	 StringBuilder command = new StringBuilder();
    	 
         command.append("user=" + URLEncoder.encode(euid, UTF8) + 
            "&internaluser=" + URLEncoder.encode(userid, UTF8) + 
            "&site=" + URLEncoder.encode(siteid, UTF8) + 
            "&role=" + URLEncoder.encode(rolename, UTF8) +
            "&session=" + URLEncoder.encode(sessionid, UTF8) +
            "&serverurl=" + URLEncoder.encode(ourUrl, UTF8) +
            "&time=" + System.currentTimeMillis() +
            "&placement=" + URLEncoder.encode(placementId, UTF8));
      
         // pass on any other arguments from the user.
         // but sanitize them to prevent people from trying to
         // fake out the parameters we pass, or using odd syntax
         // whose effect I can't predict
         
         Map params = req.getParameterMap();
         Set entries = params.entrySet();
         Iterator pIter = entries.iterator();
         while (pIter.hasNext()) {
            Map.Entry entry = (Map.Entry)pIter.next();
            String key = "";
            String value = "";
            try {
               key = (String)entry.getKey();
               value = ((String [])entry.getValue())[0];
            } catch (Exception e) { 
               M_log.debug("Exception getting key/value", e);
            }
            if (!illegalParams.contains(key.toLowerCase()) && legalKeys.matcher(key).matches())
               command.append("&" + key + "=" + URLEncoder.encode(value, UTF8));
         }
         
         // Pass on additional parameters from the tool mode configured url
         // (e.g. http://.../somescript?param=value)
         
         int param = url.indexOf('?'); 
         if (param > 0) {
            String extraparams = url.substring(param+1);
            url = url.substring(0, param);
            
            String[] plist = extraparams.split("&");
            for (int i=0; i< plist.length; i++) {
               String[] pval = plist[i].split("=");
               if (pval.length == 2) {
                  String key = pval[0];
                  String value = pval[1];
                  if (!illegalParams.contains(key.toLowerCase()) && legalKeys.matcher(key).matches())
                     command.append(command + "&" + key + "=" + URLEncoder.encode(value, UTF8));               
               }
            }     
            
         }
         
         try {
            // System.out.println("sign >" + command + "<");
            
            signature = sign(command.toString());
            url = url + "?" + command + "&sign=" + signature;
            bodyonload.append("window.location = '" + Validator.escapeJsQuoted(Validator.escapeHtml(url)) + "';");
         } catch (Exception e) {
            M_log.debug("Exception signing command", e);
         }
         
      } else {
         // Cannot generate a correctly signed URL for some reason, so just use the URL as is
         M_log.debug("Cannot generate signed URL for remote application: url=" + url + " userid=" + userid + " siteid=" + siteid + "rolename=" + rolename + " sessionid=" + sessionid);
         
      }
      
      // now put out a vestigial web page, whose main functional
      // part is actually the <body onload=
      
      int height = 600;
      String heights;
      if (config != null) {
         heights =  safetrim(config.getProperty("height", "600"));
         if (heights.endsWith("px"))
            heights = safetrim(heights.substring(0, heights.length()-2));
         //what may be saved might not be a number
         try {
         height = Integer.parseInt(heights);
         } catch (NumberFormatException e) {
        	 //nothing realy to do
         }
         
      }
      
      // now generate the page
      
      // User asked for setup menu
      //       if (query != null)
      //      System.out.println("query: " + query);
      //       else
      //      System.out.println("no query");
      if (query != null && "Setup".equals(query) && SiteService.allowUpdateSite(siteid)) {
         if (writeSetupPage(req, out, placement, element, config, oururl))
            return;
      }
      
      // If user can update site, add config menu
      // placement and config should be defined in tool mode
      // in non-tool mode, there's no config to update
      if (placement != null && config != null && SiteService.allowUpdateSite(siteid) && !trustedService) {
         if (writeOwnerPage(req, out, height, url, element, oururl))
            return;
      }
      
      // default output - show the requested application
      out.println(headHtml + headHtml1 + height + "px" + headHtml2 + bodyonload + headHtml3);
      out.println(tailHtml);
      
      
      //       res.sendRedirect(res.encodeRedirectURL(config.getProperty("url", "/")));
      
   }
   
   protected boolean isTrusted(Properties config) {
	  if (config == null)
		  return false;
	  
      return Boolean.valueOf(config.getProperty("trustedService", "false")).booleanValue();
   }
   
   /**
    * Called by doGet to display the main contents. Differs from
    *   the default output in that it adds a bar at the top containing
    *   a link to the Setup option.
    * 
    * @param out
    *        printwriter generating web display
    * @param height
    *        height of the window to display
    * @param url
    *        url to redirect to
    * @param element
    *        Javascript window id
    * @param oururl
    *        URL for this application
    */
   
   private boolean writeOwnerPage(HttpServletRequest req, PrintWriter out, int height, String url, String element, String oururl) {
      
      String bodyonload = "";
      
      String sakaiHead = (String) req.getAttribute("sakai.html.head");
      
      if (url == null)
         return false;
      
      if (element != null)
         bodyonload = "setFrameHeight('" + element + "');";
      
      out.println(headHtml + sakaiHead + headHtml1 + (height+50) + "px" + headHtml2 + bodyonload + headHtml3);
      out.println("<div class=\"portletBody\">");
      out.println("<div class=\"navIntraTool\"><a href='" + oururl + "?Setup'>Setup</a></div>");
      out.println("<iframe src=\"" + Validator.escapeHtml(url) + "\" height=\"" + height + "\" " + 
                  "width=\"100%\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"auto\" style=\"padding: 0.15em 0em 0em 0em;\" />");
      out.println("</div>");
      out.println(tailHtml);
      
      return true;
   }
   
   /**
    * Called by doGet to display the main contents. Differs from
    *   the default output in that it adds a bar at the top containing
    *   a link to the Setup option.
    * 
    * @param out
    *        printwriter generating web display
    * @param placement
    *        Sakai Placement struct for this tool
    * @param element
    *        Javascript window id
    * @param config
    *        Properties list for this tool
    * @param oururl
    *        URL for this application
    */
   
   private boolean writeSetupPage(HttpServletRequest req, PrintWriter out, Placement placement, String element, Properties config, String oururl) {
      String bodyonload = "";
      
      String sakaiHead = (String) req.getAttribute("sakai.html.head");
      
      // if not in tool mode, nothing to do
      if (placement == null || config == null)
         return false;
      
      if (element != null)
         bodyonload = "setMainFrameHeight('" + element + "');setFocus(focus_path);";
      
      out.println(headHtml);
      out.println(sakaiHead);
      out.println(headHtml1 + "300px" + headHtml2 + bodyonload + headHtml3);
      //       out.println("<h2>Setup page</h2>");
      out.println("<div class='portletBody'><h2>Setup</h2>");
      out.println("<form method='post' action='" + oururl + "?SetupForm'>");
      // <p class="shorttext"><label for="id">Description</label><textarea id="description_0" name="description_0" rows="5" cols="80" wrap="virtual"></textarea></p>
      out.println("<p class=\"shorttext\"><label for=\"url\">URL</label><input id=\"url\" type=\"text\" name=\"url\" size=\"70\" value=\"" +
                  Validator.escapeHtml(config.getProperty("url")) + "\"/></p>");
      out.println("<p class=\"shorttext\"><label for=\"height\">Height</label><input id=\"height\" type=\"text\" name=\"height\" value=\"" +
                  Validator.escapeHtml(config.getProperty("height")) + "\"/></p>");
      out.println("<p class=\"shorttext\"><label for=\"pagetitle\">Page title</label><input id=\"pagetitle\" type=\"text\" name=\"title\" value=\"" +
		  Validator.escapeHtml(placement.getTitle()) + "\"/></p>");
      out.println("<p class=\"act\"><input type=\"submit\" value=\"Update Configuration\"/></p>");
      out.println("</form>");
      out.println("<span style=\"display: block;\" class=\"instruction\">");
      out.println("Setting the Page title changes the title for the entire page (i.e. what is in the left margin). If there is more than one tool on the page, this may not be what you want to do.");
      out.println("</span>");
      out.println("<h3>Session Access</h3>");
      out.println("<div class=\"instruction\">");
      out.println("<p>This section allows you to request a cryptographically signed object that can be used to request access to a Sakai session ID. Session IDs are needed to access most of the web services.</p>");
      
      // Session s = SessionManager.getCurrentSession();
      //       String userid = null;
      // if (s != null) {
      //   // System.out.println("got session " + s.getId());
      //   userid = s.getUserId();
      //}
      
      boolean isprived = SecurityService.getInstance().isSuperUser();
      //       System.out.println("user " + userid + "prived " + isprived);
      if (!isprived) {
         out.println("<p>You can request an object that will generate a session logged in with your userid. For applications that deal with sites that you own, such an object should be sufficient for most purposes.");
         out.println("<p>For applications that need to create sites or users, or deal with many sites, an administrator can generate objects with more privileges.</p>");
         out.println("</div>");
         out.println("<form method='post' action='" + oururl + "?SignForm'>");
         out.println("<p class=\"act\"><input type=submit value='Generate Signed Object'></p");
         out.println("</form>");
      } else {
         out.println("<p>As a privileged user, you can request an object that will generate a session logged in as any user. For applications that just deal with a single site, and which need site owner privileges, you should ask for an object in the name of the site owner. For applications that need to create site or users, or deal with many sites, you should ask for an object in the name of a user with administrative privileges. If you generate an object in the name of an administrator, please be careful only to put it in sites whose security you trust.</p><p>You can also request a second kind of object. This one will generate a session for the current user. That is, when an end user accesses an application, this will return a session for that end user. Please be careful about what sites you put this in, because it will allow the owner of the site to compromise the privacy of any user using the site.</p>");
         
         out.println("</div>");
         out.println("<form method=\"post\" action=\"" + oururl + "?SignForm\">");
         out.println("<p class=\"shorttext\"><label for=\"user\">Specific user</label><input id=\"user\" type=\"text\" name=\"user\" size=\"30\"/> [an internal Sakai userid, not the Enterprise ID]</p>");
         out.println("<p class=\"shorttext\"><label for=\"currentuser\">The current user</label><input id=\"currentuser\" type=\"checkbox\" name=\"current\" value=\"yes\"/></p>");
         out.println("<p class=\"act\"><input type=\"submit\" value=\"Generate Signed Object\"/></p>");
         out.println("</form>");
      }
      
      //       if (SecurityService.getInstance().isSuperUser())
      
      out.println("<h3>Exit</h3><form action=\"" + oururl + "?panel=Main\" method=\"get\"><p class=\"act\"><input type=\"submit\" value=\"Exit Setup\"/></p></form>");
      out.println("</div>");
      
      out.println(tailHtml);
      return true;
   }
   
   
   /**
    * Output a page with an error message on it
    * 
    * @param out
    *        printwriter generating web display
    * @param element
    *        Javascript window id
    * @param error
    *        the actual error message
    * @param oururl
    *        URL for this application
    */
   
   private boolean writeErrorPage(HttpServletRequest req, PrintWriter out, String element, String error, String oururl) {
      
      String bodyonload = "";
      String sakaiHead = (String) req.getAttribute("sakai.html.head");
      
      if (element != null)
         bodyonload = "setMainFrameHeight('" + element + "');setFocus(focus_path);";
      // "sakai.html.body.onload"
      
      out.println(headHtml);
      out.println(sakaiHead);
      out.println(headHtml1 + "300px" + headHtml2 + bodyonload + headHtml3);
      
      out.println("<div class=\"portletBody\"><h3>Error</h3>");
      
      out.println("<div class=\"alertMessage\">" + error + "</div>");
      
      if (oururl != null)
         out.println("<p><a href=\"" + oururl + "?panel=Main\">Return to tool</a></p>");
      out.println("</div>");
      
      out.println(tailHtml);
      return true;
   }
   
   /**
    * Respond to data posting requests.  Request we support are
    *   ?SetupForm - when Setup form is submitted. Implement the
    *      changes and redisplay the setup form with the updated values
    *   ?SignForm - when user submits a request for us to generate a
    *      signed object. Generate the object and display it
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException.
    * @throws IOException.
    */
   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      String query = req.getQueryString();
      if ("SignForm".equals(query)) {
         doSignForm(req, res);
         return;
      }
      
      Placement placement = ToolManager.getCurrentPlacement();
      Properties config = null;
      
      res.setContentType("text/html; charset=utf-8");
      PrintWriter out = res.getWriter();
      
      String userid = null;
      String siteid = null;
      String element = null;
      String oururl = req.getRequestURI();
      
      // must be in tool mode
      if (placement == null) {
         writeErrorPage(req, out, null, "Unable to find the current tool", oururl);
         return;
      }
      
      // site is there only for tools, otherwise have to use user's arg
      // this is safe because we verify that the user has a role in site
      siteid = placement.getContext();
      if (siteid == null) {
         writeErrorPage(req, out, null, "Unable to find the current site", oururl);
         return;
      }
      
      Session s = SessionManager.getCurrentSession();
      if (s != null) {
         // System.out.println("got session " + s.getId());
         userid = s.getUserId();
      }
      
      if (userid == null) {
         writeErrorPage(req, out, null, "Unable to figure out your userid", oururl);
         return;
      }
      
      if (!SiteService.allowUpdateSite(siteid)) {
         writeErrorPage(req, out, null, "You are not allowed to update this site", oururl);
         return;
      }
      
      ToolConfiguration toolConfig = SiteService.findTool(placement.getId());
      
      placement.getPlacementConfig().setProperty("url", 
                                                 safetrim(req.getParameter("url")));
      
      //is the req actually a string?
      String heights = safetrim(req.getParameter("height"));
      int heightv = 600;
      if (heights != null && !heights.equals("")) {
	  try {
	      heightv = Integer.valueOf(heights);
	      if (heightv < 1) 
		  writeErrorPage(req, out, null, "height (" + StringEscapeUtils.escapeHtml(heights) + ") must be a positive integer", oururl);
	      else
		  placement.getPlacementConfig().setProperty("height", heights );
	  } catch (NumberFormatException e) {
    	  
	      writeErrorPage(req, out, null, StringEscapeUtils.escapeHtml(heights) + " is not a valid frame height", oururl);
	  }
      } // null doesn't change current value
      
      String newtitle = safetrim(req.getParameter("title"));
      if (newtitle != null && "".equals(newtitle))
         newtitle = null;
      
      if (newtitle != null) {
         
         placement.setTitle(safetrim(req.getParameter("title")));
         
         if (toolConfig != null) {
            try {
               Site site = SiteService.getSite(toolConfig.getSiteId());
               SitePage page = site.getPage(toolConfig.getPageId());
               page.setTitle(safetrim(req.getParameter("title")));
               SiteService.save(site);
            } catch (Exception e) {
               M_log.debug("Exception setting page title", e);
            }
         }
         
      }
      
      placement.save();
      
      element = Web.escapeJavascript("Main" + placement.getId());
      
      config = placement.getConfig();
      
      writeSetupPage(req, out, placement, element, config, oururl);
      
   }
   
   /**
    * Respond to data posting requests. Called from doPost for
    *   ?SignForm - when user submits a request for us to generate a
    *      signed object. Generate the object and display it
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException.
    * @throws IOException.
    */
   
   private void doSignForm(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      Placement placement = ToolManager.getCurrentPlacement();
      
      res.setContentType("text/html; charset=utf-8");
      PrintWriter out = res.getWriter();
      
      String sakaiHead = (String) req.getAttribute("sakai.html.head");
      
      String userid = null;
      String element = null;
      String command = null;
      String signature = null;
      String oururl = req.getRequestURI();
      String object = null;
      String bodyonload = null;
      
      if (placement != null)
         element = Web.escapeJavascript("Main" + placement.getId( ));
      else {
         writeErrorPage(req, out, null, "Unable to find the current tool", oururl);
         return;
      }
      
      String siteid = placement.getContext();
      if (siteid == null) {
         writeErrorPage(req, out, element, "Unable to find the current site", oururl);
         return;
      }
      
      if (!SiteService.allowUpdateSite(siteid)) {
         writeErrorPage(req, out, element, "You are not allowed to generate an object", oururl);
         return;
      }
      
      Session s = SessionManager.getCurrentSession();
      if (s != null) {
         // System.out.println("got session " + s.getId());
         userid = s.getUserId();
      }
      
      if (userid == null) {
         writeErrorPage(req, out, element, "Unable to figure out your userid", oururl);
         return;
      }
      
      boolean isprived = SecurityService.getInstance().isSuperUser();
      
      if (isprived) {
         String requser = safetrim(req.getParameter("user"));
         String current = safetrim(req.getParameter("current"));
         
         if (current != null && "yes".equals(current))
            command = "currentuser";
         else if (requser != null && !"".equals(requser))
            command = "user=" + requser;
         else {
            writeErrorPage(req, out, element, "No username supplied", oururl);
            return;
         }
         
      } else {
         command = "user=" + userid;
      }
      
      if (command != null) {
         try {
            signature = sign(command);
            object = command + "&sign=" + signature;
         } catch (Exception e) {
            M_log.debug("Cannot sign object ", e);
         }
      }
      
      if (object == null) {
         writeErrorPage(req, out, element, "Attempt to generate signed object failed", oururl);
         return;
      }
      
      bodyonload = "setMainFrameHeight('" + element + "');setFocus(focus_path);";
      
      out.println(headHtml);
      out.println(sakaiHead);
      out.println(headHtml1 + "300px" + headHtml2 + bodyonload + headHtml3);
      
      out.println("<div class=\"portletBody\"><h2>Your object</h2>");
      out.println("<p>Here is your object. You should copy it and then paste it into a configuration file to be used in your application.</p>");
      out.println("<p>" + Web.escapeHtml(object) + "</p>");
      
      out.println("<p><a href='" + oururl + "?panel=Main'>Return to tool</a></p>");
      out.println("</div>");
      
      out.println(tailHtml);
      
   }
   
   /**
    * Sign a string with our private signing key. Returns a hex string
    * 
    * @param data
    *        The data to sign
    * @throws NoSuchAlgorithmException 
    * @throws InvalidKeyException 
    * @throws Exception.
    */
   
   private String sign(String data) throws NoSuchAlgorithmException, InvalidKeyException {
      Mac sig = Mac.getInstance("HmacSHA1");
      sig.init(salt);
      return byteArray2Hex(sig.doFinal(data.getBytes()));
   }
   
   /**
    * Read our secret key from a file. returns the key
    * 
    * @param filename
    *        Contains the key in proper binary format
    * @return the secret key object OR null if there is a failure
    */
   private static SecretKey readSecretKey(String filename, String alg) {
      SecretKey privkey = null;
      FileInputStream file = null;
      try {
         file = new FileInputStream(filename);
         byte[] bytes = new byte[file.available()];
         file.read(bytes);
         privkey = new SecretKeySpec(bytes, alg);
      } catch (Exception ignore) {
         M_log.error("Unable to read key from " + filename);
      } finally {
         if (file != null) {
            try {
               file.close();
            } catch (IOException e) {
               // tried
            }
         }
      }
      return privkey;
   }
   
   private static char[] hexChars = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
   };
   
   /**
    * Convert byte array to hex string
    * 
    * @param ba
    *        array of bytes
    * @throws Exception.
    */
   
   private static String byteArray2Hex(byte[] ba){
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < ba.length; i++){
         int hbits = (ba[i] & 0x000000f0) >> 4;
         int lbits = ba[i] & 0x0000000f;
         sb.append(hexChars[hbits]);
         sb.append(hexChars[lbits]);
      }
      return sb.toString();
   }
   
   /**
    * Version of trim that won't blow up if fed null
    * 
    * @param a
    *        string
    */
   
   private String safetrim(String s) {
      if (s == null)
         return null;
      return s.trim();
   }
   
   // genkey
   
   // from http://www.cs.ru.nl/~martijno/
   // Martijn Oostdijk. by permission
   
   /**
    * Generate a secret key, and write it to a file
    * 
    * @param dirname
    *        writes to file privkeyname in this 
    *        directory. dirname assumed to end in /
    */
   
   private void genkey(String dirname) {
      
      try {
         /* Generate key. */
         M_log.info("Generating new key in " + dirname + privkeyname);
         SecretKey key = KeyGenerator.getInstance("Blowfish").generateKey();
         
         /* Write private key to file. */
         writeKey(key, dirname + privkeyname);
      } catch (Exception e) {
         M_log.debug("Error generating key", e);
      }
      
   }
   
   /**
    * Writes <code>key</code> to file with name <code>filename</code>
    *
    * @throws IOException if something goes wrong.
    */
   private static void writeKey(Key key, String filename) {
      FileOutputStream file = null;
      try {
         file = new FileOutputStream(filename);
         file.write(key.getEncoded());
      }
      catch (FileNotFoundException e) {
         M_log.error("Unable to write new key to " + filename);
      }
      catch (IOException e) {
         M_log.error("Unable to write new key to " + filename);
      } finally {
         if (file != null) {
            try {
               file.close();
            } catch (IOException e) {
               // tried
            }
         }
      }
   }
   
   // gensalt
   
   /**
    * Generate a random salt, and write it to a file
    * 
    * @param dirname
    *        writes to file saltname in this 
    *        directory. dirname assumed to end in /
    */
   
   private void gensalt(String dirname) {
      try {
         // Generate a key for the HMAC-SHA1 keyed-hashing algorithm
         KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");
         SecretKey key = keyGen.generateKey();
         writeKey(key, dirname + saltname);
      } catch (Exception e) {
         M_log.debug("Error generating salt", e);
      }
   }
   
   public String encrypt(String str) {
      try {
         Cipher ecipher = Cipher.getInstance("Blowfish");
         ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
         
         // Encode the string into bytes using utf-8
         byte[] utf8 = str.getBytes("UTF8");
         
         // Encrypt
         byte[] enc = ecipher.doFinal(utf8);
         
         // Encode bytes to base64 to get a string
         return byteArray2Hex(enc);
      } catch (javax.crypto.BadPaddingException e) {
         M_log.warn("linktool encrypt bad padding");
      } catch (javax.crypto.IllegalBlockSizeException e) {
         M_log.warn("linktool encrypt illegal block size");
      } catch (java.security.NoSuchAlgorithmException e) {
         M_log.warn("linktool encrypt no such algorithm");
      } catch (java.security.InvalidKeyException e) {
         M_log.warn("linktool encrypt invalid key");
      } catch (javax.crypto.NoSuchPaddingException e) {
         M_log.warn("linktool encrypt no such padding");
      } catch (java.io.UnsupportedEncodingException e) {
         M_log.warn("linktool encrypt unsupported encoding");
      } 
      return null;
   }

}

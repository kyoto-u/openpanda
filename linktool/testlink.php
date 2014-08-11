<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<base href="http://sakai.rutgers.edu/" />
<title>Linktool Test</title>
<link href="/library/skin/tool_base.css" type="text/css" rel="stylesheet" media="all" />
<link href="/library/skin/default/tool.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js"></script>
<script type="text/javascript" language="JavaScript">
var _editor_url = "/library/htmlarea/"
</script>
</head><body>

<?
  // in a real application the following two things would be read from
  // a configuration file, located outside the area accessible to the 
  // web server [for security reasons]

  // obviously this should be replaced with your actual contact
  $emailcontact = "root@localhost";
  // this is a session authorization object. It is issued using the "setup"
  // screen. I put the objects into an array indexed by server name, to
  // allow more than one sakai server to use the same PHP script and to make
  // sure we don't present one of these objects to a rogue site. 

  $obj["http://localhost:8080"] = "user=1e307359-c975-4324-80e7-54837b3ad475&sign=73bb0216a339079f734b3e258939dbe2ba4dff03";
  $obj["http://localhost:8080"] = "currentuser&sign=cb9c03f113189f2476eac856419475cee03b6068";

  // to avoid cross-site scripting problems, arguments should be passed
  // through strip_tags unless you're sure you know what you're doing
  $user = strip_tags($_GET['internaluser']);
  $euid = strip_tags($_GET['user']);
  $site = strip_tags($_GET['site']);
  $server = strip_tags($_GET['serverurl']);
  $sessionid = strip_tags($_GET['session']);
  $url = geturl($server);

  // in a real application these should be session variables, to avoid
  // parsing wsdl for each page
  $signingProxy = getproxy($url, "SakaiSigning");
  $siteProxy = getproxy($url, "SakaiSite");
  $infoProxy = getproxy($url, "SakaiRutgersInfo");
  $gbProxy = getproxy($url, "SakaiGradebook");

  // standard code to verify the arguments passed to us. 
  $result=$signingProxy->testsign($_SERVER['QUERY_STRING']);
  if ($result != "true")
    fatal("Unauthorized call");

  $result=$signingProxy->touchsession($sessionid);
  print("<p>touch session: $result");

  // get a session for doing other web services. This also validates
  // the arguments, so you don't need to do testsign if you're doing
  // getsession
  $session=$signingProxy->getsession($_SERVER['QUERY_STRING'], $obj[$server]);
  // if there's an error, it will be an axis error object, which is not string
  if (gettype($result) != "string")
    fatal("Unauthorized permissions object");

  // see what sites the user can get to. Result is an XML object
  $sites = $siteProxy->getSitesDom($session, "", 1, 9999);
  $sites = str_replace("<", "&lt;", $sites);
  print "<p>$euid ($user) can access the following sites:<pre>$sites</pre>";

  // see what courses are associated with the current site
  $courses = $infoProxy->getSiteCourses($session, $site);
  print "<p>Site $site has the following courses: $courses";

  // See whether user can update the site
  $allow = $infoProxy->allowUpdateSite($session, $site);
  print "<p>Can $user update $site? $allow";

  // get basic info on user. result is XML
  $userinfo = $infoProxy->getInternalUserInfo($session, $user);
  $userinfo = str_replace("<", "&lt;", $userinfo);
  print "<p>Info on $user:<pre>$userinfo</pre>";

  // get basic info on user. result is XML
  $userinfo = $infoProxy->getUserInfo($session, $euid);
  $userinfo = str_replace("<", "&lt;", $userinfo);
  print "<p>Info on $euid:<pre>$userinfo</pre>";


  // does user have grading access? clh is probably nonexistent, so
  // this tests whether the user has access to all students
  $allow = $gbProxy->isUserAbleToGradeStudent($session, $site, "clh");
  print "<p>Can $user grade students? $allow";

/////// functions

  function geturl($url) {
    $url = "$url/sakai-axis/";
    return $url;
  }

  function fatal($msg) {
    print "<h1>Error</h1>";
    print "<p>$msg";
    print "</body></html>";
    exit(0);
  }

  function getproxy($url, $name) {
    require_once('SOAP/Client.php');
    $wsdl=new SOAP_WSDL("$url/$name.jws?wsdl", array("timeout" => 360));
    if (!$wsdl)
      fatal("This error should not happen. Please send email to $emailcontact. Unable to open connection to $url/$name.jws?wsdl");

    $myProxy=$wsdl->getProxy();
    if (!$myProxy)
      fatal("This error should not happen. Please send email to $emailcontact. getProxy returned null.");

    return $myProxy;
  }   

?>

<p>
now is the time now is the time now is the time now is the time now is the time 
now is the time now is the time now is the time now is the time 
now is the time now is the time now is the time now is the time 
now is the time now is the time now is the time now is the time 
now is the time now is the time now is the time now is the time 
now is the time now is the time now is the time now is the time 
</body></html>

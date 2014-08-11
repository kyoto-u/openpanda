<?

header('Content-type: text/html; charset=UTF-8') ;

// Script for testing Sakai linktool getsession requests.
// This is https://source.sakaiproject.org/svn/linktool/trunk/samples/getsession.php

// Requires PEAR SOAP module, and php modules curl, openssl, zlib (for installing PEAR modules if required)

// This script is called from the verification script linktool.php

// Adding PEAR to the include_path may be required depending on local php config
// ini_set("include_path","/usr/share/php");

// Show errors
error_reporting(E_ALL^E_NOTICE);

// Get the linktool parameters
  $signedobject = urldecode(strip_tags($_GET['signedobject']));
  $querystring = urldecode(strip_tags($_GET['querystring']));
  $server= urldecode(strip_tags($_GET['server']));

  $url = $server . "/sakai-axis/";

  $signingurl = "SakaiSigning.jws?wsdl";

  $result = "";
  $checksession = "";

// Check for required variables
if ($server != "") {

  // Get the WSDL for verification
  $signingProxy = getproxy($url, "SakaiSigning");

  if (PEAR::isError($signingProxy)) {
	$result = "SOAP Error";
  } else {
  	// verify the arguments passed to us
  	$result=$signingProxy->getSessionToServer($querystring, $signedobject); 
  }

  if (PEAR::isError($result)) {
		$result = "SOAP Error: " . $result->getMessage();
 	 }
  else  {
  list($sessionid, $newserver) = split(",", $result);

  $url2 = $newserver . "/sakai-axis/";

  // Get the current user's display name to verify that the session is ok
  $sakaiscript = getproxy($url2, "SakaiScript");

  if (PEAR::isError($sakaiscript)) {
	$result = "SOAP Error";
  } else {
	$checksession = $sakaiscript->checkSession($sessionid);
  	if (PEAR::isError($checksession)) {
		$result = "SOAP Error: " . $checksession->getMessage();
 	 }
  }
  }

}

##################
#  Functions     #
##################

function fatal($msg) {
    print "<h1>Error</h1>";
    print "<p>$msg</p>";
    exit(0);
  }

function getproxy($url, $name) {
    require_once('SOAP/Client.php');
    $wsdl=new SOAP_WSDL("$url/$name.jws?wsdl", array("timeout" => 20));

    if (!$wsdl)
      fatal("This error should not happen. Unable to open connection to $url/$name.jws?wsdl");

    $myProxy=$wsdl->getProxy();
    if (!$myProxy) {
      fatal("This error should not happen. getProxy returned null.");
    }

    if (!PEAR::isError($myProxy)) {
    	// Disable SSL certificate checks (not a good idea for production systems)
    	$myProxy->setOpt('curl', CURLOPT_SSL_VERIFYPEER, 0);
    	$myProxy->setOpt('curl', CURLOPT_SSL_VERIFYHOST, 0); 
    }

    return $myProxy;
  }   

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Linktool Test</title>
<style type="text/css">

# Borrow some CSS from Sakai default skin

table.listHier{
        font-size: 1em;
        clear: both;
        margin-top: .5em;
}

table.listHier th{
        font-weight: normal;
        text-align: left;
        white-space: nowrap;
        padding: .3em;
}

table.listHier td{
        padding: .2em;
}

table.listHier td img{
        margin-right: .2em;
        vertical-align: text-top;
}

table.listHier td a:hover,
table.listHier th a:hover{
        text-decoration: none;
}

.listHier th{
        background-color: #ddd;
        border: 1px solid #999;
}

.listHier td{
        vertical-align: top;
        padding: .6em;
}

.listHier th h3,.listHier th h4,.listHier th h5,.listHier th h6,.listHier td h3,.listHier td h4,.listHier td h5,.listHier td h6{
        margin: 0;
        padding: 0;
        color:#000;
        font-size: 1em;
        background: none;
        font-weight: normal;
        font-family: 'Trebuchet MS',Verdana,Geneva,Arial,Helvetica,sans-serif !important;
}

.portletBody{
        padding: 1px 1px 0em 1px;
        border-width: 0px;
        border-color: #fff;
        border-style: solid;
}

body{
        font-family: 'Trebuchet MS',Verdana,Geneva,Arial,Helvetica,sans-serif;
        font-size: 80%;
}

</style>
</head>
<body>
<div class="portletBody">

<h2>Linktool Test</h2>
This script is to test the functionality of the Sakai LinkTool from QA and other servers.

<h3>Linktool Parameters</h3>
<table class="listHier" cellspacing="0" border="0" summary="Parameters">
<tbody>
<tr><th>Parameter</th><th>Value</th><th>Description</th></tr>
<tr><td>server</td><td><?=$server?></td><td>Server</td></tr>
<tr><td>signedobject</td><td><?=$signedobject?></td><td>Linktool signed object</td></tr>
<tr><td>querystring<site</td><td><?=$querystring?></td><td>Query string passed to app by Linktool</td></tr>
</tbody>
</table>

<h3>Server callback</h3>

<table class="listHier" cellspacing="0" border="0" summary="Results">
<tbody>
<tr><td>Callback service</td><td><a href="<?=$url . $signingurl?>"><?=$url . $signingurl?></a></td></tr>
<tr><td>Result</td><td><?=$result?></td></tr>
<tr><td>Session id</td><td><?=$sessionid?></td></tr>
<tr><td>Server</td><td><?=$newserver?></td></tr>
<tr><td>Check session</td><td><?=$checksession?></td></tr>
</tbody>
</table>

<? 

$msg = "Unknown error.";

if ($server == "") {

  $msg = "No server was specified - perhaps this script was not called by the LinkTool verification script.";

} else {

	if ($result == "not enabled") {
		$msg = "The callback service is not enabled. Sakai 2.5 and later requires the sakai.properties setting <strong>linktool.enabled=true</strong>";
	}

	if ($result == "SOAP Error") {
		$msg = "Could not connect to the callback web service. Check to see if the Callback service URL is correct.";
	}

	if (!isset($sessionid)) {
		$msg = "Did not get a valid session id from the web service. The signed object may be invalid.";
	}

	if (strlen($signedobject) == 0) {
		$msg = "No signed object was specified. Perhaps this script was not called by the LinkTool verification script.";
	}

	if (!PEAR::isError($checksession) && strlen($checksession) > 0) {
		$msg = "OK";
	} 
}

?>
<h3>Results</h3>
<p><?=$msg?></p>
</div>
</body>
</html>


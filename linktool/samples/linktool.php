<?

// Script for testing Sakai linktool. This is https://source.sakaiproject.org/svn/linktool/trunk/samples/linktool.php

// Requires PEAR SOAP module, and php modules curl, openssl, zlib (for installing PEAR modules if required)

// This script can be invoked from Sakai in a popup window (for example in a Melete unit)
// with a URL such as:
//    https://sakai.domain/sakai-rutgers-linktool?site=SITEID&url=SCRIPTURL&param1=value1&param2=value2
// where SITEID = Sakai Site ID of the site in which the URL is placed, SCRIPTURL = url of this script,
// param1 and param2 are optional application-specific parameters.

// Adding PEAR to the include_path may be required depending on local php config
// ini_set("include_path","/usr/share/php");

header('Content-type: text/html; charset=UTF-8') ;

// Show errors
error_reporting(E_ALL^E_NOTICE);

// Script path
  $signingurl = "SakaiSigning.jws?wsdl";

// Get the linktool parameters
  $user = strip_tags($_GET['internaluser']);
  $euid = strip_tags($_GET['user']);
  $site = strip_tags($_GET['site']);
  $server = strip_tags($_GET['serverurl']);
  $sessionid = strip_tags($_GET['session']);
  $placement = strip_tags($_GET['placement']);
  $role = strip_tags($_GET['role']);
  $sign = strip_tags($_GET['sign']);
  $time = strip_tags($_GET['time']);

  $url = $server . "/sakai-axis/";

  $result = "";

// Check for required variables
if ($server != "") {

  // Get the WSDL for verification
  $signingProxy = getproxy($url, "SakaiSigning");

  if (PEAR::isError($signingProxy)) {
	$result = "SOAP Error";
  } else {
  	// verify the arguments passed to us
  	$result=$signingProxy->testsign($_SERVER['QUERY_STRING']); 
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
    if (!$myProxy)
      fatal("This error should not happen. getProxy returned null.");

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
<tr><td>user</td><td><?=$euid?></td><td>Sakai enterprise id (eid)</td></tr>
<tr><td>internaluser</td><td><?=$user?></td><td>Sakai internal id (userid)</td></tr>
<tr><td>site</td><td><?=$site?></td><td>Sakai site id</td></tr>
<tr><td>role</td><td><?=$role?></td><td>Role in the site</td></tr>
<tr><td>sessionid</td><td><?=$sessionid?></td><td>Encrypted session id</td></tr>
<tr><td>serverurl</td><td><?=$server?></td><td>URL of the calling server</td></tr>
<tr><td>time</td><td><?=$time?></td><td>Time that script was invoked</td></tr>
<tr><td>placement</td><td><?=$placement?></td><td>Sakai tool placement id (2.5 and later)</td></tr>
<tr><td>sign</td><td><?=$sign?></td><td>Linktool parameter signature</td></tr>
</tbody>
</table>

<h3>Application Parameters</h3>

<? 
 unset($_GET['internaluser']);
 unset($_GET['user']);
 unset($_GET['site']);
 unset($_GET['role']);
 unset($_GET['session']);
 unset($_GET['serverurl']);
 unset($_GET['time']);
 unset($_GET['placement']);
 unset($_GET['sign']);

 if (count($_GET) > 0) {

?>
<table class="listHier" cellspacing="0" border="0" summary="Parameters">
<tbody>
<tr><th>Parameter</th><th>Value</th></tr>
<?

 foreach (array_keys($_GET) as $key) {
   print "<tr><td>" . htmlspecialchars($key) . "</td><td>" . htmlspecialchars($_GET[$key]) . "</td></tr>";
 }

 print "</tbody></table>";

} else { 
 print "None.";
}

?>

<h3>Server callback</h3>

<table class="listHier" cellspacing="0" border="0" summary="Results">
<tbody>
<tr><td>Callback service</td><td><a href="<?=$url . $signingurl?>"><?=$url . $signingurl?></a></td></tr>
<tr><td>Response</td><td><?=$result?></td></tr>
</tbody>
</table>

<? 

$msg = "Unknown error";

if ($server == "") {

  $msg = "No server was specified - perhaps this script was not called by LinkTool.";

} else {

	if ($result == "true") {
		$msg = "All correct - the verification request was authorized by the callback web service.";
	}

	if ($result == "false") {
		$msg = "The callback service did not authenticate the request.";
	}

	if ($result == "not enabled") {
		$msg = "The callback service is not enabled. Sakai 2.5 and later requires the sakai.properties setting <strong>linktool.enabled=true</strong>";
	}

	if ($result == "SOAP Error") {
		$msg = "Could not connect to the callback web service. Check to see if the Callback service URL is correct.";
	}

	if ($result == "stale value") {
		$msg = "The verification token has expired - possibly the same request URL has been reused later from another source.";
	}
}

?>
<h3>Results</h3>
<p><?=$msg?></p>
<h3>Get session using signed object</h3>
<form action="getsession.php" method="get">
<label for="signedobject">Enter a signed object from LinkTool (Setup / Generate Signed Object)</label>
<p><input id="signedobject" type="text" name="signedobject" size="80"/></p>
<input id="querystring" type="hidden" name="querystring" value="<?= urlencode($_SERVER['QUERY_STRING']) ?>"/>
<input id="server" name="server" type="hidden" value="<?= urlencode($server) ?>"/>
<p class="act"><input type="submit" value="Request session"/></p>
</form>
</div>
</body>
</html>


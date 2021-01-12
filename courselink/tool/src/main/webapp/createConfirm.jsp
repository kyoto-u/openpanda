<jsp:directive.include file="/templates/includes.jsp"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" media="all" href="css/courselink.css" />
    <link rel="stylesheet" type="text/css" media="all" href="lib/blue/style.css" />
    <link rel="stylesheet" type="text/css" media="all" href="lib/jquery.alerts.css" />

    <link rel="stylesheet" type="text/css" href="lib/jquery-ui-1.12.1/jquery-ui.css" />
    <script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
    <script src="lib/jquery-1.12.4.js"></script>
    <script src="lib/jquery-ui-1.12.1/jquery-ui.js"></script>
    <script type="text/javascript" src="lib/jquery.alerts.js"></script>


    <title>CourselinkTool</title>
<script>

$( function() {
	var date = new Date();
	$( "#dialog" ).dialog({
    	modal: true,
    	title:'<c:out value='${msgs.course_creation_confirm_title}'/>',
    	width:700,
    	height:350,
        buttons: {
        	"OK": function() {
        		if($('input[name=template]:checked').val() === 'experiencedPerson'){
        			location.href="/direct/courselink/_kcd=${param._kcd}:confirmed=true:useTemplate=experiencedPerson?time="+date.getTime();
        		}else if($('input[name=template]:checked').val() === 'beginner'){
        			location.href="/direct/courselink/_kcd=${param._kcd}:confirmed=true:useTemplate=beginner?time="+date.getTime();
        		}
        	},
        	"cancel": function() {
        		location.href="/portal/";
    		}
        }
    });

  } );
</script>
</head>
<body>

<div id="dialog" title="Basic dialog">
  <p><c:out value='${msgs.course_creation_confirm_message}'/></p>
  <fieldset>
      <legend><c:out value='${msgs.item_select_template}'/></legend>
      <input type="radio" name="template" id="experiencedPerson" value="experiencedPerson" checked="checked"></input>
      <label for="experiencedPerson"><c:out value='${msgs.item_experienced_person}'/></label>
      <input type="radio" name="template" id="beginner" value="beginner"></input>
      <label for="beginner"><c:out value='${msgs.item_beginner}'/></label>
  </fieldset>
</div>

<jsp:directive.include file="/templates/footer.jsp"/>


<%@ page import="java.util.Map"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<html>

<head>
  <title><c:out value="${msgs.title_insertImage}"/></title>

<script type="text/javascript" src="popup.js"></script>

<script type="text/javascript">

window.resizeTo(400, 100);

function Init() {
  __dlg_init();
  var param = window.dialogArguments;
  if (param) {
      document.getElementById("f_url").value = param["f_url"];
      document.getElementById("f_alt").value = param["f_alt"];
      document.getElementById("f_border").value = param["f_border"];
      document.getElementById("f_align").value = param["f_align"];
      document.getElementById("f_vert").value = param["f_vert"];
      document.getElementById("f_horiz").value = param["f_horiz"];
      window.ipreview.location.replace(param.f_url);
  }
  document.getElementById("f_url").focus();
};

function onOK() {
  var required = {
    "f_url": "<c:out value="${msgs.instructions_enterTheURL}"/>"
  };
  for (var i in required) {
    var el = document.getElementById(i);
    if (!el.value) {
      alert(required[i]);
      el.focus();
      return false;
    }
  }
  // pass data back to the calling window
  var fields = ["f_url", "f_alt", "f_align", "f_border",
                "f_horiz", "f_vert"];
  var param = new Object();
  for (var i in fields) {
    var id = fields[i];
    var el = document.getElementById(id);
    param[id] = el.value;
  }
  __dlg_close(param);
  return false;
};

function onCancel() {
  __dlg_close(null);
  return false;
};

function onPreview() {
  var f_url = document.getElementById("f_url");
  var url = f_url.value;
  if (!url) {
    alert("<c:out value="${msgs.alert_enterURL}"/>");
    f_url.focus();
    return false;
  }
  window.ipreview.location.replace(url);
  return false;
};
</script>

<style type="text/css">
html, body {
  background: ButtonFace;
  color: ButtonText;
  font: 11px Tahoma,Verdana,sans-serif;
  margin: 0px;
  padding: 0px;
}
body { padding: 5px; }
table {
  font: 11px Tahoma,Verdana,sans-serif;
}
form p {
  margin-top: 5px;
  margin-bottom: 5px;
}
.fl { width: 9em; float: left; padding: 2px 5px; text-align: right; }
.fr { width: 6em; float: left; padding: 2px 5px; text-align: right; }
fieldset { padding: 0px 10px 5px 5px; }
select, input, button { font: 11px Tahoma,Verdana,sans-serif; }
button { width: 70px; }
.space { padding: 2px; }

.title { background: #ddf; color: #000; font-weight: bold; font-size: 120%; padding: 3px 10px; margin-bottom: 10px;
border-bottom: 1px solid black; letter-spacing: 2px;
}
form { padding: 0px; margin: 0px; }
</style>

</head>

<body onload="Init()">

<div class="title"><c:out value="${msgs.instructions_insertImage}"/></div>
<!--- new stuff --->
<form action="" method="get">
 <osp:form/>
<table border="0" width="100%" style="padding: 0px; margin: 0px">
  <tbody>

  <tr>
    <td style="width: 7em; text-align: right"><c:out value="${msgs.table_row_imageURL}"/></td>
    <td>
      <c:if test="${!empty images}">
         <select name="url" id="f_url">
         <c:forEach var="image" items="${images}" >
            <option value="<osp:url value="${image.value}"/>"><c:out value="${image.key}"/>
         </c:forEach>
         </select>
      </c:if>
      <c:if test="${empty images}">
         <input type="text" name="url" id="f_url" style="width:75%"
            title='<c:out value="${msgs.instructions_enterImageURL}"/>' />
      </c:if>
      <button name="preview" onclick="return onPreview();"
      title='<c:out value="${msgs.linktitle_previewImage"/>'><c:out value="${msgs.button_preview}"/></button>
    </td>
  </tr>
  <tr>
    <td style="width: 7em; text-align: right"><c:out value="${msgs.table_row_alternateText}"/></td>
    <td><input type="text" name="alt" id="f_alt" style="width:100%"
      title='<c:out value="${msgs.linktitle_unsupportingBrowsers}"/>' /></td>
  </tr>

  </tbody>
</table>

<p />

<fieldset style="float: left; margin-left: 5px;">
<legend><c:out value="${msgs.legend_layout}"/></legend>

<div class="space"></div>

<div class="fl"><c:out value="${msgs.insertImage_alignment}"/></div>
<select size="1" name="align" id="f_align"
  title='<c:out value="${msgs.linktitle_positioningImage}"/>'>
  <option value=""                             ><c:out value="${msgs.optionInsertImage_alignment_notSet}"/></option>
  <option value="left"                         ><c:out value="${msgs.optionInsertImage_alignment_left}"/></option>
  <option value="right"                        ><c:out value="${msgs.optionInsertImage_alignment_right}"/></option>
  <option value="texttop"                      ><c:out value="${msgs.optionInsertImage_alignment_texttop}"/></option>
  <option value="absmiddle"                    ><c:out value="${msgs.optionInsertImage_alignment_absmiddle}"/></option>
  <option value="baseline" selected="1"        ><c:out value="${msgs.optionInsertImage_alignment_baseline}"/></option>
  <option value="absbottom"                    ><c:out value="${msgs.optionInsertImage_alignment_absbottom}"/></option>
  <option value="bottom"                       ><c:out value="${msgs.optionInsertImage_alignment_bottom}"/></option>
  <option value="middle"                       ><c:out value="${msgs.optionInsertImage_alignment_middle}"/></option>
  <option value="top"                          ><c:out value="${msgs.optionInsertImage_alignment_top}"/></option>
</select>

<p />

<div class="fl"><c:out value="${msgs.insertImage_border}"/></div>
<input type="text" name="border" id="f_border" size="5"
title='<c:out value="${msgs.linktitle_noEmptyBorder}"/>' />

<div class="space"></div>

</fieldset>

<fieldset style="float:right; margin-right: 5px;">
<legend><c:out value="${msgs.legend_spacing}"/></legend>

<div class="space"></div>

<div class="fr"><c:out value="${msgs.insertImage_horizontal}"/></div>
<input type="text" name="horiz" id="f_horiz" size="5"
title='<c:out value="${msgs.linktitle_horizontalPadding}"/>' />

<p />

<div class="fr">Vertical:</div>
<input type="text" name="vert" id="f_vert" size="5"
title='<c:out value="${msgs.linktitle_verticalPadding}"/>' />

<div class="space"></div>

</fieldset>
<br clear="all" />
<table width="100%" style="margin-bottom: 0.2em">
 <tr>
  <td valign="bottom">
    Image Preview:<br />
    <iframe name="ipreview" id="ipreview" frameborder="0" style="border : 1px solid gray;" height="200" width="300" src=""></iframe>
  </td>
  <td valign="bottom" style="text-align: right">
    <button type="button" name="ok" onclick="return onOK();"><c:out value="${msgs.button_OK}"/></button><br>
    <button type="button" name="cancel" onclick="return onCancel();"><c:out value="${msgs.button_cancel}"/></button>
  </td>
 </tr>
</table>
</form>
</body>
</html>

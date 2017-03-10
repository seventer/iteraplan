<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:useAttribute name="id" />
<tiles:useAttribute name="path" />

<c:set var="html_id" value="${itera:replaceNoIdChars(id)}" />

<%-- when the color picker is shown, the overflow property of the enclosing accordion section must be set to visible, otherwise the color picker is cropped off
	 when the color picker is not shown, the overflow property of the enclosing accordion section must be restored to hidden, otherwise the accordion animation doesn't work
	 the second timer is a workaround for a bug in Chrome, which needs a short delay for applying the ColorPicker class
 --%>

<span id="${html_id}_container" class="ColorPicker"
		onmouseover="timer=setTimeout(function(){ $('#${html_id}_container').closest('.collapse').css('overflow', 'visible'); $('#${html_id}_container').attr('class', 'ColorPickerHover');}, 500);"
		onmouseout="$(this).attr('class', 'ColorPicker'); clearTimeout(timer); timer2=setTimeout(function(){ $('#${html_id}_container').closest('.collapse').css('overflow', 'hidden');}, 1);" >
  <form:input id="${html_id}_color" path="${path}"/>
  <span id="${html_id}_picker" onmouseover="clearTimeout(timer2); $('#${html_id}_container').attr('class', 'ColorPickerHover');"></span>
</span>
 	
<script type="text/javascript">
	$(document).ready(function() {
	  var farbtasticObject = $.farbtastic('#${html_id}_picker');
	  
	  farbtasticObject.linkTo(function(color){
		  if(color.length == 7){
		    var colorField = $("#${html_id}_color").val(color.substring(1).toUpperCase());
		    colorField.css('background-color', color);
		  }
	  });
	  
      farbtasticObject.setColor('#' + $("#${html_id}_color").val());
	});
</script>
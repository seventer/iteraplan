<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="description_id" ignore="true" />
<tiles:useAttribute name="buildingBlock_id" ignore="true" />
<c:if test="${empty description_id}">
  <c:set var="description_id" value="description_id" />
</c:if>

<c:if test="${empty buildingBlock_id}">
  <c:set var="buildingBlock_id" value="buildingBlock_id" />
</c:if>

<a id="link_add" href="javascript:toggleLayer('${buildingBlock_id}');"><fmt:message key="global.add_link"/></a><br/>
<div class="hidden row-fluid" id="${buildingBlock_id}">
	<div class="control-group">
		<label class="control-label " for="link_title<c:out value="${buildingBlock_id}" />">
	  		<fmt:message key="global.link_title"/>:
	  	</label>
		<div class="controls">
			<input type="text" id="link_title<c:out value="${buildingBlock_id}" />" size="40" title="<fmt:message key="global.link_title.tooltip"/>"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label " for="link_value_text">
	  		<fmt:message key="global.link_address"/>:
	  	</label>
		<div class="controls">
			<input type="text" id="link_value_text<c:out value="${buildingBlock_id}" />" size="40" title="<fmt:message key="global.link_address.tooltip"/>"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label " for="link_value_file<c:out value="${buildingBlock_id}" />">
	  		<fmt:message key="global.file_link_address"/>:
	  	</label>
		<div class="controls">
			<input type="file" id="link_value_file<c:out value="${buildingBlock_id}" />" size="47" title="<fmt:message key="global.file_link_address.tooltip"/>"/>
		</div>
	</div>
	<input type="button" id="<c:out value="${description_id}"/>_addlink_button" onclick="addLinkToDescription('link_title<c:out value="${buildingBlock_id}" />','link_value_text<c:out value="${buildingBlock_id}" />','link_value_file<c:out value="${buildingBlock_id}" />','<c:out value="${description_id}" />');" value="<fmt:message key="button.add"/>"/>
</div>
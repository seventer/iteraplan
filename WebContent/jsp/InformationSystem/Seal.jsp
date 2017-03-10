<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="path_to_componentModel" />

<c:set var="functionalPermissionSeal" value="${userContext.perms.userHasFuncPermSeal}" scope="request" />
<c:set var="text_field"	value="${path_to_componentModel}.currentAsString" />
<c:set var="available_elements"	value="${path_to_componentModel}.availableEnumKeys" />
<c:set var="label_key" value="${path_to_componentModel}.labelKey" />

<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
<itera:define id="lastSeal" name="memBean" property="${path_to_componentModel}.lastSeal" />
<itera:define id="allSeals" name="memBean" property="${path_to_componentModel}.seals" />
<itera:define id="sealState" name="memBean" property="${text_field}" />
<itera:define id="componentModel" name="memBean" property="${path_to_componentModel}" />
<fmt:message key="DATE_FORMAT_LONG" var="dateFormat"/>

<c:if test="${component_mode == 'READ' and functionalPermissionSeal == true}">
  <li>
	<a data-toggle="modal" href="#SealContainerCreateNew" >
		<i class="icon-ok"></i>
		<fmt:message key="seal.create"/>
	</a>
  </li>
</c:if>

<div id="SealContainer" class="modal hide fade" style="display: none;">
 	<div class="modal-header">
		<a class="close" data-dismiss="modal">×</a>
		<h3>
			<fmt:message key="seal.created.seals"/>
		</h3>
	</div>
	<div class="modal-body">
		<c:if test="${!empty allSeals}">
			<table class="table table-bordered table-striped table-condensed">
				<thead>
					<tr>
						<th><fmt:message key="global.user"/></th>
						<th><fmt:message key="seal.verification.date"/></th>
						<th><fmt:message key="global.comments"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${allSeals}" var="seal" varStatus="countStatus">
						<tr>
							<td>${seal.user}</td>
							<td><fmt:formatDate value="${seal.date}" pattern="${dateFormat}" /></td>
							<td>${seal.comment}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</div> 
</div>

<div id="SealContainerCreateNew" class="modal hide fade" style="display: none;">
 	<div class="modal-header">
		<a class="close" data-dismiss="modal">×</a>
		<h3>
			<fmt:message key="seal.create"/>
		</h3>
	</div>
	<div class="modal-body">
		<fmt:message key="global.comments"/>:<br/> 
		<textarea id="sealComments" name="sealComments" cols="40" rows="5"></textarea>
	</div>
	<div class="modal-footer">
		<!-- set form onsubmit="flowAction..." if not the ie will create two seals -->
		<button class="btn btn-primary" type="submit" name="_eventId_createSeal" id="_eventId_createSeal" onclick="$('#_eventId_createSeal').closest('form').attr('action','${flowExecutionUrl}');$('#_eventId_createSeal').closest('form').attr('onsubmit','flowActionWithID('createSeal',${id});')"><fmt:message key="seal.create"/></button>
		<a href="#" class="btn" data-dismiss="modal"><fmt:message key="button.close" /></a>
	</div>
</div>

<script type="text/javascript">
	// Place the SealContainer on the parent of the unordered list where the SealContainer normally is appended to
	$(document).ready(function() {
		$('#SealContainer').appendTo($('#transactionbar'));
		$('#SealContainerCreateNew').appendTo($('#transactionbar'));
	});
</script>
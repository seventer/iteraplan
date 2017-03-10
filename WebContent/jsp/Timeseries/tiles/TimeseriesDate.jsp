<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="timeseriesEntryCMPath" />

<itera:define id="timeseriesEntryCM" name="memBean" property="${timeseriesEntryCMPath}" />

<fmt:message key="DATE_FORMAT" var="dateformatForJava" />
<c:set var="dateformatForDatepicker"
	value="${fn:toLowerCase(dateformatForJava)}" />

<c:choose>
	<c:when test="${timeseriesEntryCM.componentMode == 'CREATE'}">
		<!-- Date should be editable only in CREATE mode in this case -->
		<div class="control-group">
			<div class="controls">
				<input id="${timeseriesEntryCM.htmlId}_date" class="small datepicker" type="text"
							name="${timeseriesEntryCMPath}.dateAsString" value="<c:out value="${timeseriesEntryCM.dateAsString}" />" />
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<span><c:out value="${timeseriesEntryCM.dateAsString}" /></span>
	</c:otherwise>
</c:choose>

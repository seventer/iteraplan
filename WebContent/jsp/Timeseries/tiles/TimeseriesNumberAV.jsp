<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="valueComponentModelPath" />

<itera:define id="valueModel" name="memBean" property="${valueComponentModelPath}" />

<c:choose>
	<c:when test="${valueModel.componentMode == 'CREATE'}">
		<!-- Value should be editable only in CREATE mode in this case -->
		<tiles:insertTemplate template="TimeseriesAddRemoveNumberAV.jsp" flush="true">
			<tiles:putAttribute name="valueModelPath" value="${valueComponentModelPath}" />
		</tiles:insertTemplate>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/attributes/tiles/ConnectedNumberAV.jsp" flush="true">
			<tiles:putAttribute name="atPartPath" value="${valueComponentModelPath}" />
			<tiles:putAttribute name="extended_html_id" value="${valueModel.htmlId}" />
			<%-- 				<tiles:putAttribute name="valueOnly" value="${valueOnly}" /> --%>
		</tiles:insertTemplate>
	</c:otherwise>
</c:choose>

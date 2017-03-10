<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="timeseriesEntryCMPath" />
<tiles:useAttribute name="rowIndex" />

<itera:define id="timeseriesEntryCM" name="memBean" property="${timeseriesEntryCMPath}" />

<tr>
	<td>
		<c:choose>
			<c:when test="${timeseriesEntryCM.componentMode == 'READ'}">
				<!-- ***** read mode, no add or remove action in this mode ***** -->
			</c:when>
			<c:when test="${timeseriesEntryCM.componentMode == 'EDIT'}">
				<!-- ***** edit mode: entry can be removed but not changed ***** -->
				<a id="<c:out value="${timeseriesEntryCM.htmlId}_remove" />" class="link" href="#" title="<fmt:message key="tooltip.remove"/>"
					onclick="triggerTimeseriesEntryAction(<c:out value="'${rowIndex}', 'remove'" />)"> <i class="icon-remove"></i>
				</a>
			</c:when>
			<c:when test="${timeseriesEntryCM.componentMode == 'CREATE'}">
				<!-- ***** create mode, entry can be edited and added ***** -->
				<a id="<c:out value="${timeseriesEntryCM.htmlId}_add" />" class="link" href="#" title="<fmt:message key="tooltip.add"/>"
					onclick="triggerTimeseriesEntryAction(<c:out value="'${rowIndex}', 'add'" />);"> <i class="icon-plus"></i>
				</a>
			</c:when>
		</c:choose>
	</td>
	<td class="timeseries_date">
		<tiles:insertTemplate template="TimeseriesDate.jsp">
			<tiles:putAttribute name="timeseriesEntryCMPath" value="${timeseriesEntryCMPath}" />
		</tiles:insertTemplate>
	</td>
	<td>
		<c:choose>
			<c:when test="${timeseriesEntryCM.atType == 'ENUM'}">
				<tiles:insertTemplate template="TimeseriesEnumAV.jsp">
					<tiles:putAttribute name="valueComponentModelPath" value="${timeseriesEntryCMPath}.valueModel" />
				</tiles:insertTemplate>
			</c:when>
			<c:when test="${timeseriesEntryCM.atType == 'NUMBER'}">
				<tiles:insertTemplate template="TimeseriesNumberAV.jsp">
					<tiles:putAttribute name="valueComponentModelPath" value="${timeseriesEntryCMPath}.valueModel" />
				</tiles:insertTemplate>
			</c:when>
		</c:choose>
	</td>
</tr>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="lineCount"/>

<tiles:insertTemplate template="/jsp/common/RuntimePeriodComponentView.jsp" flush="false">
	<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
	<tiles:putAttribute name="lineCount" value="${lineCount}"/>
	<tiles:putAttribute name="minimal" value="true" />
</tiles:insertTemplate>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="select_box_style" ignore="true" />
<tiles:useAttribute name="showDivView" ignore="true" />

<tiles:insertTemplate template="/jsp/common/EnumComponentComboboxView.jsp"	flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
	<tiles:putAttribute name="select_box_style" value="${select_box_style}" />
	<tiles:putAttribute name="showDivView" value="${showDivView}" />
</tiles:insertTemplate>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertTemplate
	template="/jsp/InformationSystem/Manage.jsp">
	<tiles:putAttribute name="doCopyAll" value="true" />
</tiles:insertTemplate>
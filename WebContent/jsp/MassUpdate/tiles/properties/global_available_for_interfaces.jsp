<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The name of this JSP 'global_available_for_interfaces.jsp' is generically bound to a property in 
 one ore more *MassUpdateType.java files. When renaming, those properties have to be 
 considered too!! --%>

<tiles:useAttribute name="path_to_componentModel" />

<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
	<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
	<tiles:putAttribute name="minimal" value="true" />
</tiles:insertTemplate>

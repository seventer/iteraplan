<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<%-- The name of this JSP 'massUpdates_name_version.jsp' is generically bound to a property in 
 one ore more *MassUpdateType.java files. When renaming, those properties have to be 
 considered too!! --%>

<tiles:useAttribute name="path_to_componentModel" />

<div class="control-group">
	<label class="control-label" for="massUpdate_name_input">
		<fmt:message key="global.name" />:
	</label>
	<div class="controls">
		<form:input path="${path_to_componentModel}.elementName.current" cssClass="name" id="massUpdate_name_input"/>
	</div>
</div>
<div class="control-group">
	<label class="control-label" for="massUpdate_version_input">
		<fmt:message key="global.release" />:
	</label>
	<div class="controls">
		<form:input path="${path_to_componentModel}.releaseName.current" cssClass="name" id="massUpdate_version_input" />
	</div>
</div>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- The name of this JSP 'global_name.jsp' is generically bound to a property in 
 one ore more *MassUpdateType.java files. When renaming, those properties have to be 
 considered too!! --%>

<tiles:useAttribute name="path_to_componentModel" />

<form:input path="${path_to_componentModel}.current" cssClass="name" id="${path_to_componentModel}.current" />


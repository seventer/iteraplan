<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- The name of this JSP 'global_type_of_status.jsp' is generically bound to a property in 
 one ore more *MassUpdateType.java files. When renaming, those properties have to be 
 considered too!! --%>
 
<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="select_box_style" ignore="true" />

<tiles:insertTemplate template="/jsp/common/PersistentEnumComponentComboboxView.jsp" flush="false">
	<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
	<tiles:putAttribute name="showDivView" value="true" />
</tiles:insertTemplate>

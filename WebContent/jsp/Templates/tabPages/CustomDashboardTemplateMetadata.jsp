<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="html_id" ignore="true" />
<tiles:useAttribute name="dashboard_field_path" ignore="true" />

<div class="well">
	<form:textarea path="${dashboard_field_path}.customDashboardDescription" cssClass="description" id="dashboard_textarea" rows="5" />
</div>

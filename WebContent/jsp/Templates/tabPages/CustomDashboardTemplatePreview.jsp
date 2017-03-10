<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="html_id" ignore="true" />
<tiles:useAttribute name="dashboard_field_path" ignore="true" />

<p class="muted">
	<fmt:message key="customDashboard.template.preview.populate.hint"></fmt:message> 
</p>
<div class="well">
	<itera:dashboard name="dialogMemory" property="${dashboard_field_path}" userAgent="${header['User-Agent']}" breaksAndSpaces="true"/>
</div>

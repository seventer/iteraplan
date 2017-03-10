<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>


<jwr:script src="/bundle/customDashboard.js"></jwr:script>
<jwr:style src="/bundle/customDashboard.css"></jwr:style>

<script type="text/javascript">
	$(document).ready(function() {
		$("#dashboard_textarea").markItUp(mySettings);
	});

	function insertDiagramID(diagramID) {
		$("#dashboard_textarea").insertDiagram(diagramID);
	};

	function rollback() {
		$("#dashboard_textarea").replaceDiagramTag();
	};
</script>

<tiles:useAttribute name="dashboard_field_path" ignore="true" />

<form:textarea path="${dashboard_field_path}.customDashboardContent"
	cssClass="description" id="dashboard_textarea" rows="20" />

<tiles:insertTemplate template="/jsp/Templates/ModalDialog/DiagramModalDialog.jsp"></tiles:insertTemplate>
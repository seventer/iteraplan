<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%-- TODO: convert table into module --%>
<table class="elementComponentView table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th><fmt:message key="global.auditinfo" /></th>
		</tr>
	</thead>
	<tbody>
		<tr>
	        <td><p><fmt:message key="downloadAuditLogfile.text"/></p>
				<p><a href="requestAuditLogFile.do"><fmt:message key="global.logfile"/></a></p>
			</td>
		</tr>
	</tbody>
</table>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%-- Show success message: download started --%>
<c:if test="${not empty triggerDownloadEvent}">
	<div id="downloadStartedMessage" class="alert alert-info">
		<button class="close" data-dismiss="alert" type="button">×</button>
		<fmt:message key="global.download.starting" />
	</div>
</c:if>


<%-- Start the file download, after the page has been loaded. --%>
<c:if test="${not empty triggerDownloadEvent}">
	<script type="text/javascript">
		$(document).ready(function() {
			flowAction('${triggerDownloadEvent}');
		});
	</script>
</c:if>


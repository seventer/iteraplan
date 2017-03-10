<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script>
	$(document).ready(function() {

		$(".combobox").combobox({
			baseUrl : '<c:url value="/" />',
			showInactiveStatus : <c:out value="${userContext.showInactiveStatus}" />
		});
	});
</script>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%-- Display save messages for queries--%>
<c:if test="${not empty SAVE_SUCCESSFUL_TRIGGER}">
	<script type="text/javascript">
		$("html, body").animate({
			scrollTop : 0
		}, "slow");
	</script>

	<div id="querySavedMessage" class="alert alert-info">
		<button class="close" data-dismiss="alert" type="button">×</button>
		<fmt:message key="graphicalReport.information.saveSuccessful" />
	</div>
</c:if>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:if test="${guiContext.timeseriesEnabled}">
	<script>
		var flowExecutionUrl = '${flowExecutionUrl}'; // used for ajax request url
	</script>

	<div id="timeseriesDialog" class="modal hide fade" role="dialog">
		[Will be replaced by actual content when opened.]
	</div>
</c:if>

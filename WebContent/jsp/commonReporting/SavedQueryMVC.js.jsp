<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<tiles:useAttribute name="skipLoadRequestConfirmation" ignore="true" />

<%-- 
submit has to be implemented here explicitly as the normal submitLink(..) method does not work
for diagram generation
--%>
<script type="text/javascript">
/* <![CDATA[ */
	function loadSavedQuery(queryName, queryId, functionName) {
		<c:choose>
			<c:when test="${skipLoadRequestConfirmation}">
				createHiddenField('savedQueryId', queryId);
				createHiddenField('action', 'loadQuery');
				window.document.forms[0].submit();
			</c:when>
			<c:otherwise>
				showConfirmDialog("<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>", 
						"<fmt:message key='graphicalReport.executeSavedQuery'><fmt:param>" + queryName +"</fmt:param></fmt:message>",
						function(){
							createHiddenField('savedQueryId', queryId);
							createHiddenField('action', 'loadQuery');
							window.document.forms[0].submit();
						});
			</c:otherwise>
		</c:choose>
	}

	function deleteSavedQuery(queryName, queryId) {
		showConfirmDialog("<fmt:message key='global.confirmDelete'/>", 
				"<fmt:message key='graphicalReport.executeRemoveQuery'><fmt:param>" + queryName +"</fmt:param></fmt:message>",
				function(){
					createHiddenField('deleteQueryId', queryId);
					createHiddenField('action', "deleteQuery");
					window.document.forms[0].submit();
				});
	}
	
	<c:if test="${massUpdateMode == true}">
	function runSavedQuery(queryId, functionName) {
		if (true) {
			createHiddenField('savedQueryId', queryId);
			createHiddenField('action', 'runQuery');
			window.document.forms[0].submit();
		}

	}
	</c:if>
/* ]]> */
</script>

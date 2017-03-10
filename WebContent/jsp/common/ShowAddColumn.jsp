<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="availableColumnDefinitions" ignore="true" />

<%-- Sort available columns in SELECT field --%> 
<script type="text/javascript">
<!--
	$(document).ready(function(){
		$("#colToAddSelectField").html($("#colToAddSelectField option").sort(function (a, b) {
			var al = a.text.toLowerCase(), bl = b.text.toLowerCase();
	    	return al == bl ? 0 : (al < bl ? -1 : 1);
		}));
	});
//-->
</script>

<div id="addColumnContainer" class="modal hide fade" style="display: none;">
 	<div class="modal-header">
		<a class="close" data-dismiss="modal">×</a>
		<h3>
			<fmt:message key="reports.addColumn" />
		</h3>
	</div>
	<div class="modal-body">
		<select id="colToAddSelectField" name="colToAdd" class="labelAddColumn" size="10" ondblclick="document.forms[0].submit();">
			<c:forEach items="${availableColumnDefinitions}" var="columnDefinition" varStatus="s">
				<option value="${s.count-1}">
					<c:choose>
						<c:when test="${columnDefinition.attributeType ne null}">
							<c:out value="${columnDefinition.tableHeaderKey}" />
						</c:when>
						<c:otherwise>
							<fmt:message key="${columnDefinition.tableHeaderKey}" />
						</c:otherwise>
					</c:choose>
					</option>	
			</c:forEach>
		</select>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal"><fmt:message key="button.close" /></a> <a
			id="addColumnButton" href="#" class="link btn btn-primary"
			onclick="document.forms[0].submit();"><fmt:message key="button.update" /></a>
	</div> 
</div>
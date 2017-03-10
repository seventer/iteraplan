<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@page
	import="de.iteratec.iteraplan.presentation.dialog.XmiDeserialization.XmiDeserializationDialogMemory"%><c:set
	var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermXmiDeserialization}"
	scope="request" />

<div class="alert alert-error">

	<a class="close" data-dismiss="alert">×</a>

	<c:choose>
		<c:when test="${functionalPermissionImport == true}">
			<span class="errorHeader"><fmt:message key="global.xmi.import_failed"/><br/></span>
			<%-- TODO: convert table into module --%>
			<table width="99%">
				<tr>
					<td>
						<span class="errorHeader"><fmt:message key="global.xmi.import_failed_solve"/></span>
					</td>
					<td align="right">
						<input type="submit" value="<fmt:message key="button.xmi.solve_and_import" />"
						onclick="createHiddenField('clickedButton', 'button.xmi.solve_and_import');" />					
					</td>
					<td align="right">
						<input type="submit" value="<fmt:message key="button.import.xmi.hide_conflicts" />"
						onclick="createHiddenField('clickedButton', 'button.import.xmi.hide_conflicts');" />					
					</td>
			</tr>
			</table>
			<%-- TODO: convert table into module --%>
			<table class="searchResultView table table-bordered table-striped table-condensed">
				<colgroup>
					<col class="col-ico" />
					<col class="col-dec" />
				</colgroup>
			<thead>
				<tr>
			 		<th colspan="100%"><fmt:message key="global.xmi.import_conflicts"/></th>
				 </tr>
			 </thead>
			 <tbody>
				<tr class="subHeader">				
					<td align="left"><c:out value="XMI-Instance"/></td>
					<td align="left"><c:out value="DB-Instance"/></td>
					<td align="left"><c:out value="Attribute"/></td>
					<td align="left"><c:out value="Value"/></td>				
				</tr>
			<c:forEach items="${dialogMemory.conflicts}" var="conflict">
				<tr class="${background}">
					<td style="color: red" align="left"><c:out value="${conflict[0]}"/></td>
					<td style="color: red" align="left"><c:out value="${conflict[1]}"/></td>
					<td align="left"><c:out value="${conflict[2]}"/></td>
					<td align="left"><c:out value="${conflict[3]}"/></td>
				</tr>
			</c:forEach>
			</tbody>
			</table>
	
		</c:when>
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />	
		</c:otherwise>
	</c:choose>

</div>
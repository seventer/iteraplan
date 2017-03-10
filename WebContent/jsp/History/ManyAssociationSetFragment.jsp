<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<%-- The message key for displaying the section header --%>
<tiles:useAttribute name="relationLabelKey" />

<%-- The EL expression to retrieve the added elements from bbChangeset --%>
<tiles:useAttribute name="addedElementsPath" />
<itera:define id="addedElements" name="bbChangeset" property="${addedElementsPath}" />

<%-- The EL expression to retrieve the removed elements from bbChangeset --%>
<tiles:useAttribute name="removedElementsPath" />
<itera:define id="removedElements" name="bbChangeset" property="${removedElementsPath}" />

<c:if test="${not empty addedElements or not empty removedElements}">
<%-- TODO: convert table into module --%>
	<table class="elementComponentView table table-bordered table-striped table-condensed">
		<thead>
		  <tr>
			<th>
				<fmt:message key="${relationLabelKey}"/> 
			</th>
		  </tr>
		</thead>
		<tbody>
		  <c:forEach items="${addedElements}" var="curChildAdded">
			  <tr>
				<td>
				<i class="icon-plus"></i> <c:out value="${curChildAdded.nonHierarchicalName}"/>
				</td>
			  </tr>
		  </c:forEach>
	
		  <c:forEach items="${removedElements}" var="curChildRemoved">
			  <tr>
				<td>
				<i class="icon-remove"></i> <c:out value="${curChildRemoved.nonHierarchicalName}"/>
				</td>
			  </tr>
		  </c:forEach>
		</tbody>
	</table>
</c:if>
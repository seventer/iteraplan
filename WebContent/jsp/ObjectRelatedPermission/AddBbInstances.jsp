<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:if test="${componentMode != 'READ'}">
	
	<%-- Not needed here, as it is already defined in StandardQueryForm, 
	     SpringMVC would bind a List, if multiple inputs with same path/id existed! 
	<form:input path="formModification.affectedQueryFormId" cssStyle="display: none;"/>
	<form:input path="formModification.firstLevelIdToExpand" cssStyle="display: none;"/>
	<form:input path="formModification.firstLevelIdToShrink" cssStyle="display: none;"/>
	<form:input path="formModification.secondLevelIdToShrink" cssStyle="display: none;"/>
	--%>

	<fmt:message key="objectRelatedPermissions.selectFurtherInstances"/>:  
	<form:select path="selectedQueryTypeNameDB" onchange="flowAction('changeQueryType');">
		<c:forEach items="${memBean.dto.availableQueryTypes}" var="bbtype">
			<form:option value="${bbtype.typeNameDB}">
				<fmt:message key="${bbtype.typeNamePresentationKey}" />
			</form:option>
		</c:forEach>
	</form:select>
	<br />
	<br />
	<tiles:insertTemplate template="/jsp/commonReporting/StandardQueryForm.jsp" flush="true">
		<tiles:putAttribute name="hideHeader" value="true" />
	</tiles:insertTemplate>
	<br />
	<input name="requestReport" onclick="flowAction('requestReport');" type="button" class="link btn" value="<fmt:message key="button.sendQuery" />"/>
	<input name="resetReport" onclick="flowAction('resetReport');" type="button" class="link btn" value="<fmt:message key="button.reset" />"/>
	
    <c:if test="${not empty memBean.results}">
       &nbsp;
       <input name="addInstancePermissions" onclick="flowAction('addPermissions');" type="button" class="link btn" value="<fmt:message key="objectRelatedPermissions.addPermissions" />"/>
       <br/>
    </c:if>
	<tiles:insertTemplate template="/jsp/commonReporting/resultPages/GeneralResultPage.jsp" flush="true" />

</c:if>

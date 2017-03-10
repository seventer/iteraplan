<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%--
		@UsedFor 	tabularReporting or MassUpdate Form
		@UsedFrom	tabularReporting\Manage.jsp;
		@Note		
 --%>

<c:if test="${not empty memBean.errors}">
	<script type="text/javascript">
		/* <![CDATA[ */
		
		function clearErrors() {
			flowAction('clearErrors');
		}
		
		/* ]]> */
	</script>
	<div class="alert alert-error">
		<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
		<span><fmt:message key="errors.header" /></span>
		<div>
			<c:forEach var="error" items="${memBean.errors}">
				<c:out value="${error}" />
				<br />
			</c:forEach>
		</div>
	</div>
</c:if>

<c:choose>
	<c:when test="${massUpdateMode == true}">
		<c:set var="header_key" value="global.mass_updates" />
    	<c:set var="selectInstances_key" value="massUpdates.selectInstances" />
	</c:when>
	<c:otherwise>
    	<c:set var="header_key" value="global.report.text" />
    	<c:set var="selectInstances_key" value="reports.selectInstances" />
	</c:otherwise>
</c:choose>

<c:if test="${memBean.reportUpdated || (not massUpdateMode && memBean.visibleColumnsUpdated)}">
	<div class="alert alert-error">
		<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
		<span><fmt:message key="message.report.attributeNotFound" /></span>
	</div>
	<br />
</c:if>

<div id="HeaderContainer" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${header_key}" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<fmt:message key="${selectInstances_key}" />&nbsp;	
				<c:choose>
					<c:when test="${massUpdateMode}">
         				<c:set var="availableBbTypesList" value="${memBean.availableBbTypesForMassupdate}"/>
       				</c:when>
       				<c:otherwise>
        				<c:set var="availableBbTypesList" value="${memBean.availableBbTypes}"/>
       				</c:otherwise>
     				</c:choose>
				<form:select path="selectedBuildingBlock" onchange="flowAction('changeQueryType')">
					<c:forEach var="available" items="${availableBbTypesList}">
 						<form:option value="${available}"><fmt:message key="${available}" /></form:option>
					</c:forEach>
				</form:select>
			</div>
		</div>
	</div>
</div>

<c:if test="${not empty availableBbTypesList}">

	<c:if test="${massUpdateMode == true}">
		<tiles:insertTemplate template="/jsp/MassUpdate/configuration/MassUpdateConfigPage.jsp" flush="true" />
	</c:if>
	
	<c:set var="permissionCreateReports">
		<itera:write name="userContext" property="perms.userHasFuncPermTabReportingCreate" escapeXml="false" />
	</c:set>
	
	<c:set var="permissionSaveReports">
		<itera:write name="userContext" property="perms.userHasFuncPermTabReportingFull" escapeXml="false" />
	</c:set>
	
	<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
		<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
		<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
		<tiles:putAttribute name="showBuildingBlockType" value="true" />
	</tiles:insertTemplate>
	
	<tiles:insertTemplate template="/jsp/commonReporting/GeneralQueryForm.jsp" flush="true">
	  <tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
	</tiles:insertTemplate>
	
	<c:if test="${not empty memBean.results && massUpdateMode == true}">
		<div class="ReportingRequestButtons btn-toolbar">
			<div id="massUpdate_moreOptions" class="btn-group" >
			
				<input name="prepareForMassUpdate" class="btn" onclick="flowAction('prepareForMassUpdate')" type="button" value="<fmt:message key="massUpdates.requestMassUpdate" />"/>
				
				<c:if test="${memBean.massUpdateType.typeOfBuildingBlock.value != 'businessMapping.singular'}">
				
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#" >
						<fmt:message key="massUpdates.moreOptions" />
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu dropdown-right">
						<li>
				   			<a id="massDelete" href="#"
				   				onclick="confirmMassenDelete(function(){flowAction('massDelete');})" >
				   				<fmt:message key="massUpdates.requestMassDelete" />
				   			</a>
				   		</li>
				   		<c:if test="${not empty userContext.user.email}">
					   		<li>
					   			<a id="massSubscribe" href="#"
					   				onclick="flowAction('massSubscribe')" >
					   				<fmt:message key="massUpdates.requestMassSubscribe" />
					   			</a>
					   		</li>
					   		<li>
					   			<a id="massUnsubscribe" href="#"
					   				onclick="flowAction('massUnsubscribe')" >
					   				<fmt:message key="massUpdates.requestMassUnsubscribe" />
					   			</a>
					   		</li>
				   		</c:if>
					</ul>
					
				</c:if>
			</div>
		</div>
	</c:if>
	
	<c:set var="permissionSaveReports">
		<itera:write name="userContext" property="perms.userHasFuncPermTabReportingFull" escapeXml="false" />
	</c:set>
	<c:if test="${permissionSaveReports == true}">
		<tiles:insertTemplate template="/jsp/commonReporting/ShowSaveQuery.jsp">
			<tiles:putAttribute name="xmlSaveAsQueryName" value="xmlSaveAsQueryName"/>
			<tiles:putAttribute name="xmlSaveAsQueryDescription" value="xmlSaveAsQueryDescription"/>
			<tiles:putAttribute name="xmlQueryName" value="xmlQueryName" />
			<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
		</tiles:insertTemplate>
	</c:if>
	<br/><br/>
</c:if>
<tiles:insertTemplate template="/jsp/commonReporting/resultPages/GeneralResultPage.jsp" flush="true" />
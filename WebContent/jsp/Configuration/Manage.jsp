<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermConfiguration}" scope="request" />

<c:choose>
	<c:when test="${functionalPermission == true}">
	
		<div id="GlobalConfigurationModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.configuration" />
			</div>
			<div class="row-fluid">
				<div class="module-body">
					<div class="row-fluid">
						<div class="control-group">
							<div class="controls">
								<form:checkbox path="showInactive" id="globalConfiguration_checkbox"/>
							</div>
							<label class="control-label-right" for="globalConfiguration_checkbox">
						  		<fmt:message key="configuration.showInactive" />
						  	</label>
						</div>
						<div class="transactionbar">
							<input id="changeShowInactive" type="button" value="<fmt:message key="button.ok" />" class="btn" onclick="submitForm('saveConfiguration.do');" />				  		
					  	</div>
					</div>
				</div>
			</div>
		</div>
			
		<c:choose>
			<%-- Is the user allowed to change the data source? --%>
			<c:when test="${userContext.perms.userHasFuncPermDatasources}">
			<c:set var="memBean" value="${dialogMemory}" scope="request"/>
			<tiles:insertTemplate template="/jsp/common/RoutingDatasourceComponentComboboxView.jsp" flush="true">
				<tiles:putAttribute name="pathToComponentModel" value="routingDatasourceModel" />
				<tiles:putAttribute name="showSubmitButton" value="true" />
			</tiles:insertTemplate>
			</c:when>
		</c:choose>	
		
		<div id="ManageSearchModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="manageSearch.heading" />
			</div>
			<div class="row-fluid">
				<div class="module-body">
					<div class="row-fluid">
						<div class="control-group">
							<div class="controls">
								<form:checkbox path="purgeIndex" id="manageSearch_checkbox"/>
							</div>
							<label class="control-label-right" for="manageSearch_checkbox">
						  		<fmt:message key="configuration.purgeIndex" />
						  	</label>
						</div>
						<div class="transactionbar">
							<span class="errorInline" style="display: none;" id="refreshIndexInProgress"><fmt:message key="configuration.refreshIndexInProgress" /></span> 
					    	<input id="submitRefreshIndex" type="button" value="<fmt:message key="button.refreshIndex" />" class="btn"
								onclick="toggleButton('submitRefreshIndex'); $('#refreshIndexInProgress').css('display','block'); submitForm('refreshIndex.do');" />				  		
					  	</div>
					  	<div class="helpText">
					  		<fmt:message key="configuration.refreshIndex.hintText" />
					  	</div>
					</div>
				</div>
			</div>
		</div>
		
		<div id="globalCacheModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.cache" />
			</div>
			<div class="row-fluid">
				<div class="module-body">
					<div class="row-fluid">
						<input id="clearCache" type="button" value="<fmt:message key="global.cache.clear" />" onclick="submitForm('clearHibernateCache.do');" class="btn" />
						<div class="helpText">
							<fmt:message key="global.cache.description" />
						</div>
					</div>
				</div>
			</div>
		</div>
	
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>
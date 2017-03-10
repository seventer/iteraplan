<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
<script type="text/javascript">
	addMiscShortcuts();
</script>        

<c:set var="informationsystem" value="informationSystem.singular" />
<itera:define id="isrPermission" name="userContext"
    property="perms.userHasBbTypeFunctionalPermission(${informationsystem})" />
<c:set var="functionalPermissionISR" value="${userContext.perms.userHasFuncPermSuccessorReport && isrPermission}" scope="request" />

<c:set var="technicalcomponent" value="technicalComponent.singular" />
<itera:define id="tcrPermission" name="userContext"
    property="perms.userHasBbTypeFunctionalPermission(${technicalcomponent})" />
<c:set var="functionalPermissionTCR" value="${userContext.perms.userHasFuncPermSuccessorReport && tcrPermission}" scope="request" />


<div class="tabbable tabs-top">

	<c:set var="informationSystemSuccessorsActive" value="active"/>
	<c:set var="technicalComponentSuccessorsActive" value=""/>
	
	<c:if test="${guiContext.selectedTab == 'technicalComponentSuccessors'}">
	<c:set var="informationSystemSuccessorsActive" value=""/>
		<c:set var="technicalComponentSuccessorsActive" value="active"/>
	</c:if>

	<c:if test="${functionalPermissionISR == true && functionalPermissionTCR == true}">
		<ul class="nav nav-tabs">
			<li class="${informationSystemSuccessorsActive}">
				<a href="#tab_informationSystemSuccessors" data-toggle="tab" onclick="GuiService.setSelectedTab('informationSystemSuccessors');">
					<fmt:message key="reports.successors.informationsystems.title" />
				</a>
			</li>
			
			<li class="${technicalComponentSuccessorsActive}">
				<a href="#tab_technicalComponentSuccessors" data-toggle="tab" onclick="GuiService.setSelectedTab('technicalComponentSuccessors');">
					<fmt:message key="reports.successors.technicalcomponents.title" />
				</a>
			</li>
		</ul>
	</c:if>
	
	<div class="tab-content">
	
		<c:if test="${functionalPermissionISR == true}">
			<div class="tab-pane ${informationSystemSuccessorsActive}" id="tab_informationSystemSuccessors">
				<div class="row-fluid">
					<form:select path="selectedIsrId" cssClass="nameforhierarchy" id="selectIsr"> 
	           			<form:option value="-1">
	             				&lt;<fmt:message key="reports.selectAll" />&gt;
	           			</form:option>
	        			<form:options items="${dialogMemory.isrSuccessorDTO.availableReleases}" itemLabel="hierarchicalName" itemValue="id" />
	         		</form:select>
				</div>
				<div class="row-fluid">
				    <form:radiobutton path="isrSuccessorDTO.showSuccessor" value="true" cssStyle="vertical-align:middle;margin: 0px 3px 0px 0px;"/>
				    <fmt:message key="reports.successors.showSuccessors" />&nbsp;&nbsp;&nbsp;&nbsp;
				    <form:radiobutton path="isrSuccessorDTO.showSuccessor" value="false" cssStyle="vertical-align:middle;margin: 0px 3px 0px 0px;"/>
				    <fmt:message key="reports.successors.showPredecessors" />
				</div>
				<div class="row-fluid">
				    <fmt:message key="reports.resultFormat" />&nbsp;
					<form:select path="isrSuccessorDTO.selectedResultFormat">
			        	<c:forEach var="availableResultFormat" items="${dialogMemory.availableResultFormats}">
			          		<form:option value="${availableResultFormat.presentationKey}">
			            		<fmt:message key="${availableResultFormat.presentationKey}" />
			          		</form:option>
			        	</c:forEach>
					</form:select>
					&nbsp;
					<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.sendQuery.tooltip'/>">
						<input type="submit" id="<fmt:message key='button.sendQuery.id'/>" class="link btn btn-primary" value="<fmt:message key="button.sendQuery" />"	
							onclick="createHiddenField('clickedButton', 'button.requestReport.ISR');" />
					</a>
				</div>
			</div>
		</c:if>
	
		<c:if test="${functionalPermissionTCR == true}">
			<div class="tab-pane ${technicalComponentSuccessorsActive}" id="tab_technicalComponentSuccessors">
				<div class="row-fluid">
					<form:select path="selectedTcrId" cssClass="nameforhierarchy" id="selectTcr">
	           			<form:option value="-1">
	             				&lt;<fmt:message key="reports.selectAll" />&gt;
	           			</form:option>
	           			<form:options items="${dialogMemory.tcrSuccessorDTO.availableReleases}" itemLabel="hierarchicalName" itemValue="id" />
	         		</form:select>
				</div>
				<div class="row-fluid">
				    <form:radiobutton path="tcrSuccessorDTO.showSuccessor" value="true" cssStyle="vertical-align:middle;margin: 0px 3px 0px 0px;"/>
		          	<fmt:message key="reports.successors.showSuccessors" />&nbsp;&nbsp;&nbsp;&nbsp;
		          	<form:radiobutton path="tcrSuccessorDTO.showSuccessor" value="false" cssStyle="vertical-align:middle;margin: 0px 3px 0px 0px;"/>
		          	<fmt:message key="reports.successors.showPredecessors" />
				</div>
				<div class="row-fluid">
				    <fmt:message key="reports.resultFormat" />&nbsp;
		          	<form:select path="tcrSuccessorDTO.selectedResultFormat">
		            	<c:forEach var="availableResultFormat" items="${dialogMemory.availableResultFormats}">
		              		<form:option value="${availableResultFormat.presentationKey}">
		                		<fmt:message key="${availableResultFormat.presentationKey}" />
		              		</form:option>
		            	</c:forEach>
		          	</form:select>
		          	&nbsp;
		          	<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.sendQuery2.tooltip'/>">
						<input type="submit" id="<fmt:message key='button.sendQuery2.id'/>" class="link btn btn-primary" value="<fmt:message key="button.sendQuery" />" 
							onclick="createHiddenField('clickedButton', 'button.requestReport.TCR');" />
					</a>
				</div>
			</div>
		</c:if>
		
	</div>

</div>

<c:if test="${not functionalPermissionISR && not functionalPermissionTCR}">
	<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:if>
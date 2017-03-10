<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />

<c:set var="updatePermission" value="true" />
<c:set var="showCloseButton" value="true" />
<c:set var="showDeleteButton" value="true" />
<c:set var="deletePermission" value="true" />

<script type="text/javascript">
   $(document).ready(function() {
     $('#colorpicker').farbtastic('#color');
   });
 </script>

<c:set var="delete_hook_script"	value="confirmDelete(function(){flowAction('delete')});" />

<div id="transactionbar" class="pull-right">
	<div class="btn-group">
		
		<%-- ### View-Mode ### --%>
		<c:if test="${componentMode == 'READ'}">
		
			<%-- EDIT --%>
			<c:if test="${updatePermission}">
				<a id="transactionEdit" class="btn" href="#"
					onclick="flowAction('edit');" >
					<i class="icon-pencil"></i>
					<fmt:message key="button.edit" />
				</a>
			</c:if>
			<%-- DELETE --%>
			<c:if test="${showDeleteButton && deletePermission}">
				<a id="transactionDelete" class="btn" href="#"
					onclick="<c:out value="${delete_hook_script}" />" >
					<i class="icon-trash"></i>
					<fmt:message key="button.delete" />
				</a>
			</c:if>
			<%-- CLOSE --%>
			<c:if test="${showCloseButton}">
				<a id="transactionClose" class="btn" href="#"
					onclick="flowAction('close');" >
					<i class="icon-remove"></i>
					<fmt:message key="button.close" />
				</a>
		  	</c:if>
		</c:if>
		<%-- ### Edit-Mode ### --%>
		<c:if test="${componentMode == 'EDIT' || componentMode == 'CREATE'}">
		
			<%-- SAVE --%>
			<a id="transactionSave" class="btn btn-primary" href="#"
				onclick="flowAction('save');" >
				<fmt:message key="button.save" />
			</a>
			
			<%-- CANCEL --%>
			<a id="transactionCancel" class="btn" href="#"
				onclick="msgOkCancel(function(){flowAction('cancel');});" >
				<fmt:message key="button.cancel" />
			</a>
		</c:if>
	</div>
</div>



<h1><fmt:message key="global.dateinterval" /></h1>



<form class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="name"><fmt:message key="global.name" />:</label>
    <div class="controls">
    	<c:choose>
			<c:when test="${(componentMode != 'READ')}">
<%-- 				<input type="text" id="name" placeholder="Name" value="${memBean.componentModel.nameModel.name}"></input> --%>
				<form:input id="name" path="componentModel.dateInterval.name" maxlength="40" />
			</c:when>
			<c:otherwise>
				<c:out value="${memBean.componentModel.nameModel.name}" />
			</c:otherwise>
		</c:choose>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="startAttributeType"><fmt:message key="global.startfordateinterval" />:</label>
    <div class="controls">
    	<c:choose>
			<c:when test="${(componentMode != 'READ')}">
				
				<form:select path="componentModel.selectedStartDate" id="startAttributeType">
				  <itera:define id="availableDates_array" name="memBean" property="dates" />
				    <form:options items="${availableDates_array}" itemLabel="name" itemValue="id" />
				</form:select>
				
			</c:when>
			<c:otherwise>
				<c:out value="${memBean.componentModel.dateInterval.startDate}" />
			</c:otherwise>
		</c:choose>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="endAttributeType"><fmt:message key="global.endfordateinterval" />:</label>
    <div class="controls">
    	<c:choose>
			<c:when test="${(componentMode != 'READ')}">
				
				<form:select path="componentModel.selectedEndDate" id="endAttributeType">
				  <itera:define id="availableDates_array" name="memBean" property="dates" />
				    <form:options items="${availableDates_array}" itemLabel="name" itemValue="id" />
				</form:select>
				
			</c:when>
			<c:otherwise>
				<c:out value="${memBean.componentModel.dateInterval.endDate}" />
			</c:otherwise>
		</c:choose>
    </div>
  </div>

	<div class="control-group">
    <label class="control-label" for="color"><fmt:message key="reports.color" />:</label>
    <div class="controls">
    	<c:choose>
			<c:when test="${(componentMode != 'READ')}">
<%-- 					<input type="text" id="color" name="color" value="${memBean.componentModel.dateInterval.defaultColorHex}" /> --%>
					<form:input id="color" path="componentModel.dateInterval.defaultColorHex" maxlength="40" />
 				<div id="colorpicker"></div>
			</c:when>
			<c:otherwise>
				<div style="background-color:<c:out value="${memBean.componentModel.dateInterval.defaultColorHex}"/>; width:50px;height:20px; border-style:solid; border-width:1px" />
			</c:otherwise>
		</c:choose>
    </div>
  </div>

</form>
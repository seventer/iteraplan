<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="countFirstLevel" ignore="true" />
<tiles:useAttribute name="countSecondLevel" ignore="true" />
<tiles:useAttribute name="countSecondLevelBlocks" ignore="true" />
<tiles:useAttribute name="affectedQueryFormIdJavaScript" ignore="true" />
<tiles:useAttribute name="affectedSecondLevelQueryFormIdJavaScript" ignore="true" />
<tiles:useAttribute name="pathToQPart" />
<tiles:useAttribute name="idSuffix" />
<tiles:useAttribute name="availableAttributes" />
<tiles:useAttribute name="availableAttributeValuesResolver" />

<itera:define id="qPart" name="memBean" property="${pathToQPart}" />

<tr>
	<td><form:select id="attributeDropDown_${idSuffix}"
			path="${pathToQPart}.chosenAttributeStringId"
			cssClass="attributeDropDown" onchange="flowAction('refreshReport');">

			<c:forEach items="${availableAttributes}" var="availableAttribute">
				<%-- Remove the status and seal attributes --%>
				<c:if
					test="${availableAttribute.id != 0 and availableAttribute.id != -11}">
					<form:option value="${availableAttribute.stringId}">
						<c:choose>
							<c:when test="${availableAttribute.type == 'blank'}">
								<fmt:message key="${availableAttribute.name}" />
							</c:when>
							<c:when test="${availableAttribute.type == 'fixed'}">
								<fmt:message key="${availableAttribute.name}" />
							</c:when>
							<c:when test="${availableAttribute.type == 'fixedDate'}">
								<fmt:message key="${availableAttribute.name}" />
							</c:when>
							<c:when test="${availableAttribute.type == 'fixedSet'}">
								<fmt:message key="${availableAttribute.name}" />
							</c:when>
							<c:when test="${availableAttribute.type == 'fixedEnum'}">
								<fmt:message key="${availableAttribute.name}" />
							</c:when>
							<c:otherwise>
								<c:out value="${availableAttribute.name}" />
							</c:otherwise>
						</c:choose>
					</form:option>
				</c:if>
			</c:forEach>
		</form:select> <c:set var="chosenAttributeId">
			<itera:write name="memBean"
				property="${pathToQPart}.chosenAttributeStringId" escapeXml="false" />
		</c:set> <c:set var="inputFieldsDisabled" value="false" /> <c:if
			test="${chosenAttributeId == null || chosenAttributeId == '' || chosenAttributeId == 'blank_null_-1'}">
			<c:set var="inputFieldsDisabled" value="true" />
		</c:if></td>
	<td><c:set var="nestedProperty2" value="${pathToQPart}.operators" />
		<itera:define id="items" name="memBean" property="${nestedProperty2}" />

		<form:select id="operationDropDown_${idSuffix}"
			path="${pathToQPart}.chosenOperationId" cssClass="operationDropDown"
			disabled="${inputFieldsDisabled}"
			onchange="flowAction('refreshReport');">
			<c:forEach items="${items}" var="operator">
				<c:if
					test="${!(criticalExtension && operator.description == 'notOperation')}">
					<c:choose>
						<c:when
							test="${chosenAttributeId == 'fixed_technicalComponent.availableForInterfaces_-12'}">
							<c:if
								test="${operator.id == 5 || operator.id == 6 || operator.id == 8}">
								<form:option value="${operator.id}">
									<fmt:message key="${operator.name}" />
								</form:option>
							</c:if>
						</c:when>
						<c:otherwise>
							<form:option value="${operator.id}">
								<fmt:message key="${operator.name}" />
							</form:option>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<%-- show empty dummy option if select is disabled/empty, needed for W3C validation --%>
			<c:if test="${inputFieldsDisabled}">
				<form:option value=""></form:option>
			</c:if>
		</form:select></td>

	<%-- Boolean Variable to decide whether the selection fields are disabled or not --%>
	<itera:define id="selectFieldsDisabled" name="memBean"
		property="${pathToQPart}.selectFieldsDisabled" />

	<%-- The following code sets the list of available attribute values into a bean called attributeValueList, which is needed now and later on. --%>
	<c:set var="nestedProperty3"
		value="${availableAttributeValuesResolver}.availableAttributeValues(${qPart.chosenAttributeStringId})" />
	<itera:define id="attributeValueList" name="memBean"
		property="${nestedProperty3}" />

	<td><c:set var="radiobuttonAndFirstFieldDisabled"
			value="${inputFieldsDisabled || (empty attributeValueList) || selectFieldsDisabled}" />
		<form:radiobutton id="attributeValueRadio_${idSuffix}"
			path="${pathToQPart}.freeTextCriteriaSelected" value="false"
			disabled="${radiobuttonAndFirstFieldDisabled}" /></td>
	<td><form:select id="attributeValueDropDown_${idSuffix}"
			path="${pathToQPart}.existingCriteria"
			cssClass="attributeValueDropDown"
			disabled="${radiobuttonAndFirstFieldDisabled}"
			onclick="switchRadioButton('${pathToQPart}.freeTextCriteriaSelected', 0);">
			<c:if test="${not empty attributeValueList}">
				<c:choose>
					<c:when
						test="${chosenAttributeId == 'fixed_technicalComponent.availableForInterfaces_-12'}">
						<c:forEach items="${attributeValueList}" var="attributeValue">
							<c:choose>
								<c:when test="${attributeValue.name eq true}">
									<form:option value="${attributeValue.name}">
										<fmt:message key="global.yes" />
									</form:option>
								</c:when>
								<c:otherwise>
									<form:option value="${attributeValue.name}">
										<fmt:message key="global.no" />
									</form:option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<form:options items="${attributeValueList}"
							itemLabel="description" itemValue="name" />
					</c:otherwise>
				</c:choose>
			</c:if>
			<%-- show empty dummy option if select is disabled/empty, needed for W3C validation --%>
			<c:if test="${inputFieldsDisabled}">
				<form:option value=""></form:option>
			</c:if>
		</form:select></td>
	<td>&nbsp;</td>
	<td>
		<c:if test="${not empty affectedQueryFormIdJavaScript}">
			<a class="link" href="#"
				name="<c:out value="removeButton_${idSuffix}"/>"
				title="<fmt:message key="reports.removeCondition"/>"
				onclick="<c:out value="${affectedQueryFormIdJavaScript}${affectedSecondLevelQueryFormIdJavaScript}" />setHiddenField('formModification.firstLevelIdToShrink','<c:out value="${countFirstLevel}"/>');setHiddenField('formModification.secondLevelIdToShrink','<c:out value="${countSecondLevel}"/>');flowAction('shrinkLevel');">
					<i class="icon-remove"></i>
			</a>
		</c:if>
	</td>
</tr>

<tr>
	<c:choose>
		<c:when test="${countSecondLevel < (countSecondLevelBlocks-1)}">
			<td rowspan="2" colspan="2"><b><fmt:message key="reports.or" /></b></td>
		</c:when>
		<c:otherwise>
			<td colspan="2">&nbsp;</td>
		</c:otherwise>
	</c:choose>

	<td><form:radiobutton
			id="attributeValueInputRadio_${idSuffix}"
			path="${pathToQPart}.freeTextCriteriaSelected"
			value="true" disabled="${radiobuttonAndFirstFieldDisabled}" /></td>
	<td><c:set var="calendarDisabled">
			<itera:write name="memBean"
				property="${pathToQPart}.dateATSelected"
				escapeXml="false" />
		</c:set> <c:set var="isNumberAT">
			<itera:write name="memBean"
				property="${pathToQPart}.numberATSelected"
				escapeXml="false" />
		</c:set> <c:if test="${isNumberAT}">
			<script type="text/javascript">
							                  		$(document).ready(function() {
							                	    	// validateNumber
				                	    				$("#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}").validate({
															<%-- TODO: checkRegEx --%>
															expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
															<%-- TODO: checkErrorMessage --%>
															message: "<fmt:message key="errors.invalidValue"/>"
														});
							                	    	$('#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}').click(function(){ switchRadioButton('${dynamicQueryFormDataField}.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected', 1); });
							                	 	});    
											  	</script>
		</c:if>
		<div class="control-group">
			<div class="controls">
				<c:choose>
					<c:when test="${not calendarDisabled}">
						<form:input
							path="${pathToQPart}.freeTextCriteria"
							cssClass="attributeValueDropDown"
							disabled="${inputFieldsDisabled || selectFieldsDisabled}"
							id="freeTextCriteria_${idSuffix}"
							onClick="switchRadioButton('${pathToQPart}.freeTextCriteriaSelected', 1);" />
					</c:when>
					<c:otherwise>
						<form:input class="small datepicker" type="text"
							path="${pathToQPart}.freeTextCriteria"
							id="freeTextCriteria_${idSuffix}"
							onClick="switchRadioButton('${pathToQPart}.freeTextCriteriaSelected', 1);" />
															&nbsp;
					                  					</c:otherwise>
				</c:choose>
			</div>
		</div></td>
	<c:if test="${calendarDisabled}">
		<td></td>
		<td><a
			id="<c:out value="dateRemoveAnchor3_${idSuffix}"/>"
			class="link" title="<fmt:message key="global.date.remove"/>"
			onclick="$('#<c:out value="freeTextCriteria_${idSuffix}"/>').val('');">
				<i class="icon-remove"></i>
		</a></td>
	</c:if>
</tr>


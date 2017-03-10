<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<itera:define id="queryFirstLevelsArray" name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.queryFirstLevels" />
      
<%-- iterates over all sub is-query and blocks --%>
<c:forEach items="${queryFirstLevelsArray}" var="firstLevel" varStatus="countFirstLevelStatus">
    
	<c:set var="countFirstLevel" value="${countFirstLevelStatus.index}" />
	<c:if test="${countFirstLevel >= 1}">
		<tr>
			<td colspan="3"><b><fmt:message key="reports.and" /></b></td>
		</tr>
	</c:if>
        
	<tr>
		<td>
			<div class="row-fluid inner-module" style="margin-bottom: 0px;">
				<div class="row-fluid">
					<div class="inner-module-body-table">
						<div class="row-fluid">
							<itera:define id="countSecondLevelBlocksHelper" name="firstLevel" property="querySecondLevels" />
							<c:set var="countSecondLevelBlocks" value="${fn:length(countSecondLevelBlocksHelper)}" />
							<c:out value="${countSecondLevelBlocks2}" />
							<table>
				            	<itera:define id="querySecondLevelsArray" name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels" />            
				            	<c:forEach items="${querySecondLevelsArray}" var="qPart" varStatus="countSecondLevelStatus">
				              		<c:set var="countSecondLevel" value="${countSecondLevelStatus.index}" />
				              		<c:set var="pathToQPart" value="${dynamicQueryFormDataField}.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}]" />
				              		<c:set var="idSuffix" value="${currentReportFormId}_${countFirstLevel}_${countSecondLevel}" />
				              		<itera:define id="availableAttributes" name="memBean" property="${dynamicQueryFormDataField}.availableAttributes" />
				              		
				              		
									<tiles:insertTemplate template="/jsp/commonReporting/QPartForm.jsp" flush="true">
										<tiles:putAttribute name="countFirstLevel" value="${countFirstLevel}" />
										<tiles:putAttribute name="countSecondLevel" value="${countSecondLevel}"/>
										<tiles:putAttribute name="countSecondLevelBlocks" value="${countSecondLevelBlocks}"/>
										<tiles:putAttribute name="affectedQueryFormIdJavaScript" value="${affectedQueryFormIdJavaScript}" />
										<tiles:putAttribute name="affectedSecondLevelQueryFormIdJavaScript" value="${affectedSecondLevelQueryFormIdJavaScript}" />
										<tiles:putAttribute name="pathToQPart" value="${pathToQPart}" />
										<tiles:putAttribute name="idSuffix" value="${idSuffix}" />
										<tiles:putAttribute name="availableAttributes" value="${availableAttributes}" />
										<tiles:putAttribute name="availableAttributeValuesResolver" value="${dynamicQueryFormDataField}" />
						      	  	</tiles:insertTemplate>
					    
					              	<c:choose>
										<c:when test="${countSecondLevel < (countSecondLevelBlocks-1)}">
					                  		<tr>
					                    		<td colspan="3">&nbsp;</td>
					                  		</tr>
					                	</c:when>
					                	<c:otherwise>
					                  		<tr>
					                  		<td colspan="5">
					                  			<c:set var="orButtonTooltip">
					                        			<fmt:message key="reports.addAlternative" />
					                      			</c:set>
					                      			<c:set var="imageOrURL">
					                        			<fmt:message key="image.orButton" />
					                      			</c:set>
					                      			<img src="<c:url value="${imageOrURL}" />" 
								                       	onclick="${affectedQueryFormIdJavaScript}${affectedSecondLevelQueryFormIdJavaScript}setHiddenField('formModification.firstLevelIdToExpand','${countFirstLevel}');flowAction('expandSecondLevel');"
								                       	class="link" title="${orButtonTooltip}" 
								                       	alt="${orButtonTooltip}" 
								                       	align="right" name="orButton_${currentReportFormId}_${countFirstLevel}" />
					                    		</td>
					                  		</tr>
					                	</c:otherwise>
					              	</c:choose>
								</c:forEach>
							</table>
						</div>
					</div>
				</div>
			</div>
		</td>
	</tr>
</c:forEach>
<div class="row-fluid">
	<c:set var="andButtonTooltip">
		<fmt:message key="reports.addAnd" />
	</c:set>
	<c:set var="imageAndURL">
		<fmt:message key="image.andButton" />
	</c:set>
	<img src="<c:url value="${imageAndURL}" />"
		onclick="${affectedQueryFormIdJavaScript}${affectedSecondLevelQueryFormIdJavaScript}flowAction('expandFirstLevel');" 
		class="link" alt="${andButtonTooltip}" title="${andButtonTooltip}"
		id="andButton_${currentReportFormId}" />
</div>

  
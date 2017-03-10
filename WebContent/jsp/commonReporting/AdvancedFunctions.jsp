<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	Shows options for the report Post Processing Strategy "(advanced) functions".
		@UsedFrom 	jsp\commonReporting\StandardQueryForm.jsp
		@Note 		used as inline Table!
 --%>

<c:choose>
 <c:when test="${memBean.queryResult.advancedFunctions == true}">
   <c:set var="hideOrShow" value="visibleRows" />
 </c:when>
 <c:otherwise>
   <c:set var="hideOrShow" value="hiddenRows" />
 </c:otherwise>
</c:choose>

<c:if test="${not empty memBean.queryResult.postProcessingStrategies}">
<div class="accordion" id="advancedFunctionsContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#advancedFunctionsContainer" href="#advancedFunctions"
					onclick="toggleIcon('collapseIcon', 'icon-resize-full', 'icon-resize-small');" >
			  <i id="collapseIcon" class="icon-resize-full"></i>
              <fmt:message key="reports.advancedFunctions" />
            </a>
        </div>
    <div id="advancedFunctions" class="accordion-body collapse">
		<div class="accordion-inner-table">
			<table id="TabularReportingAdvancedFunctions" class="table table-striped table-condensed">
				<tbody>
			    	<c:forEach var="ppsEntry" items="${memBean.queryResult.postProcessingStrategies}" varStatus="loopStatus">
						<c:set var="count" value="${loopStatus.index}" />
						<tr>
                   			<td>
                     			<form:checkbox path="queryResult.postProcessingStrategies[${count}].selected"
                            		onclick="enableCheckboxBySuperOption(this, 'queryResult.postProcessingStrategies[${count}].strategy.additionalOptions[0].selected');" />
                            	<%-- Currently there is only one dependent checkbox, so its id could be hard-wired. As sonn, as more checkboxes come into play, you'll have to tweak the JS function for more genericness. --%>
                   				&nbsp;
                     			<fmt:message key="${ppsEntry.strategy.nameKeyForPresentation}" />
                   			</td>
                 		</tr>
               			<c:forEach var="option" items="${ppsEntry.strategy.additionalOptions}" varStatus="optLoopStatus">
                 			<c:set var="optionsCount" value="${optLoopStatus.index}" />
                 			<tr>
                       			<td>
                     				<%-- If the superordinate checkbox is selected, enable the checkbox, otherwise disable it --%>
                     				<c:set var="initialState" value="${not (ppsEntry.selected)}"/>
                     				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<form:checkbox id="queryResult.postProcessingStrategies${count}.strategy.additionalOptions${optionsCount}.selected"
                     			   		path="queryResult.postProcessingStrategies[${count}].strategy.additionalOptions[${optionsCount}].selected" disabled="${initialState}" />
                     			   	&nbsp;
                     				<fmt:message key="${option.key}" />
                   				</td>
                 			</tr>
               			</c:forEach>
               		</c:forEach>
				</tbody>
			</table>
		</div>
     </div>
    </div>
 </div>
 
</c:if>
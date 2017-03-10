<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="extensionDescription">
  <tiles:getAsString name="extensionDescriptionParam" ignore="false" />
</c:set>

<c:set var="currentReportFormId">
  <tiles:getAsString name="currentReportFormIdParam" ignore="false" />
</c:set>

<c:set var="noAssignmentsSelected">
  <itera:write name="memBean" property="queryResult.queryForms[${currentReportFormId}].queryUserInput.noAssignements" escapeXml="false" />
</c:set>

<c:set var="associationType">
  <itera:write name="memBean" property="queryResult.queryForms[${currentReportFormId}].type.associationType" escapeXml="false" />
</c:set>

<c:set var="hideOrShowInput" value="visible" />
<c:if test="${noAssignmentsSelected}">
  <c:set var="hideOrShowInput" value="hidden" />
</c:if>

<div id="ReportExtensionModul" class="row-fluid inner-module">
	<div class="inner-module-heading">
		<a class="link" href="#"
			name="removeExtension_<c:out value="${currentReportFormId}"/>"
			title="<fmt:message key="reports.removeReportExtension"/>" 
			onclick="setHiddenField('reportExtensionToRemove','<c:out value="${currentReportFormId}"/>');flowAction('removeReportExtension');" >
			<i class="icon-remove"></i>
		</a>
		<fmt:message key="${extensionDescription}" />
	</div>
	<div class="row-fluid">
		<div class="inner-module-body">
			<div class="row-fluid">
				<c:choose>
				    <c:when test="${empty memBean.queryResult.queryForms[currentReportFormId].secondLevelQueryForms}">
				    	<tr>
				        	<td>
				          		<tiles:insertTemplate template="/jsp/commonReporting/DynamicForm.jsp" flush="false">
				            		<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />        
				            		<tiles:putAttribute name="dynamicQueryFormDataField" value="queryResult.queryForms[${currentReportFormId}]" />
				          		</tiles:insertTemplate>
				        	</td>
				      	</tr>    
				    </c:when>
				    
				    <%-- The following 'otherwise' tree is currently only reached when adding an extension for business mappings. --%>
				    <c:otherwise>
				      	<tr>
				        	<td>
				        		<c:set var="size" value="${fn:length(memBean.queryResult.queryForms[currentReportFormId].secondLevelQueryForms)}"/>
				                
	                			<script type="text/javascript">
	                  				function toggleAllExtensionLayers() {
	                    				<c:forEach begin="0" end="${size}" varStatus="status">
	                      					toggleLayer('reportExtension_<c:out value="${currentReportFormId}"/>_<c:out value="${status.index-1}_outerDiv"/>');
	                    				</c:forEach>   
	                  				}
	                			</script>     
	                
	                			<c:set var="outerDivClass" value="visibleRows"/>
	                			<c:if test="${memBean.queryResult.queryForms[currentReportFormId].queryUserInput.noAssignements}">              
	                  				<c:set var="outerDivClass" value="hiddenRows"/>
	                			</c:if>
				        		<div class="control-group">
									<div class="controls">
										<form:checkbox id="queryResult.queryForms${currentReportFormId}.queryUserInput.noAssignements"
			                				path="queryResult.queryForms[${currentReportFormId}].queryUserInput.noAssignements"
			                				onclick="toggleAllExtensionLayers();" />
									</div>
									<label class="control-label-right" for="queryResult.queryForms${currentReportFormId}.queryUserInput.noAssignements">
										<fmt:message key="reports.noAssignements" />
									</label>
								</div>
							</td>
						</tr>
						<tr>
				        	<td>
				          		<div class="<c:out value="${outerDivClass}"/>" id="reportExtension_<c:out value="${currentReportFormId}"/>_-1_outerDiv">
				          			<div class="row-fluid inner-module">
										<div class="inner-module-heading">
											<c:set var="associationTypeKey">
			                    				<itera:write name="memBean" property="queryResult.queryForms[${currentReportFormId}].type.typeNamePresentationKey" escapeXml="false" />
			                  				</c:set>
			                  				<fmt:message key="reports.associationTypeAttributes" />&nbsp;<fmt:message key="${associationTypeKey}" />
										</div>
										<div class="row-fluid">
											<div class="inner-module-body">
												<div class="row-fluid">
													<tiles:insertTemplate template="/jsp/commonReporting/DynamicForm.jsp" flush="false">
					                    				<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />        
					                    				<tiles:putAttribute name="dynamicQueryFormDataField" value="queryResult.queryForms[${currentReportFormId}]" />
					                    				<tiles:putAttribute name="hideNoAssociationFormPart" value="true" />
					                  				</tiles:insertTemplate>
												</div>
											</div>
										</div>
									</div>
				          		</div>      
				        	</td>
						</tr>
				      
						<c:forEach items="${memBean.queryResult.queryForms[currentReportFormId].secondLevelQueryForms}" var="form" varStatus="formStatus">
				        	<tr>
				          		<td>
				            		<div class="<c:out value="${outerDivClass}"/>" id="<c:out value="reportExtension_${currentReportFormId}_${formStatus.index}_outerDiv"/>">
				            			<div class="row-fluid inner-module">
											<div class="inner-module-heading">
												<fmt:message key="${form.extension.nameKeyForPresentation}" />
											</div>
											<div class="row-fluid">
												<div class="inner-module-body">
													<div class="row-fluid">
														<tiles:insertTemplate template="/jsp/commonReporting/DynamicForm.jsp" flush="false">
					                      					<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />
					                      					<tiles:putAttribute name="secondLevelReportFormId" value="${formStatus.index}" />
					                      					<tiles:putAttribute name="dynamicQueryFormDataField" value="queryResult.queryForms[${currentReportFormId}].secondLevelQueryForms[${formStatus.index}]" />
					                      					<tiles:putAttribute name="hideNoAssociationFormPart" value="true" />
					                    				</tiles:insertTemplate>
													</div>
												</div>
											</div>
										</div>
				            		</div>
				          		</td>
				        	</tr>
			      		</c:forEach>      
			    	</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>
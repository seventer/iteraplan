<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="isRowRelationSet" value="false"/>
<c:if test="${not empty memBean.graphicalOptions.selectedRowRelation}">
    <c:set var="isRowRelationSet" value="true"/>
</c:if>

<c:set var="isAttributeSet" value="false"/>
<c:if test="${memBean.graphicalOptions.selectedRowAttributeId >= 0}">
    <c:set var="isAttributeSet" value="true"/>
</c:if>

<input type="hidden" name="graphicalOptions.selectedRowOption" value="0" />


<div class="form-horizontal">
	<fieldset>
		<legend>
			<fmt:message key="graphicalExport.landscape.rowAssociation"/>
		</legend>
		
		<c:if test="${memBean.graphicalOptions.dialogStep > 1}">
		
			<%--  Display columnRelations --%>
			
			<c:if test="${!isAttributeSet}">
				<div class="control-group">
					<label class="control-label" style="padding-top: 10px; margin-bottom: 0pt; width:60px">
						<fmt:message key="graphicalExport.landscape.relation"/>:
					</label>
					 
					<div class="controls">
						<form:select path="graphicalOptions.selectedRowRelation" disabled="${isRowRelationSet}"
									 onchange="setHiddenField('graphicalOptions.selectedRowOption', 1);flowAction('selectRowType');" id="rowElementType"> 
							<form:option value="">
								<fmt:message key="reports_export_select_relation" />
							</form:option>
							<c:forEach items="${memBean.graphicalOptions.availableRelations}" var="relation">
								<form:option value="${relation.name}">
									<fmt:message key="${relation.nameKeyForPresentation}" />
								</form:option>
							</c:forEach>
						</form:select>
						
						<c:if test="${isAttributeSet || isRowRelationSet}">
			     			&nbsp;<input type="button" class="link btn" onclick="flowAction('changeRowType')" value="<fmt:message key="button.edit"/>" />
			      		</c:if>
					</div>
				</div>
			</c:if>
			
			<c:if test="${!(isAttributeSet || isRowRelationSet)}">
				<b class="help-block"><fmt:message key="reports.or"/></b>
			</c:if>
		
		
			<c:if test="${!isRowRelationSet}">
				<div class="control-group">
					<label class="control-label" style="padding-top: 10px; margin-bottom: 0pt;  width:60px	">
							<fmt:message key="graphicalExport.landscape.attribute"/>:
					</label>
			  		 
			  		<div class="controls">
						<form:select path="graphicalOptions.selectedRowAttributeId" disabled="${isAttributeSet}"
									 onchange="setHiddenField('graphicalOptions.selectedRowOption', '2');
						                       flowAction('selectRowType');" id="rowAttributeType">
						  <form:option value="-1">
						  	<fmt:message key="graphicalExport.landscape.select.attribute"/>
						  </form:option>
						  <c:forEach items="${memBean.queryResults.contentQuery.queryForms[0].availableAttributes}" var="attribute">
						    <c:if test="${attribute.type == 'userdefEnum'}">
						      <form:option value="${attribute.id}">
						        <c:out value="${attribute.name}"/>
						      </form:option>
						    </c:if>            
						    <c:if test="${attribute.type == 'userdefDate'}">
						      <form:option value="${attribute.id}">
						        <c:out value="${attribute.name}"/>
						      </form:option>
						    </c:if>            
						    <c:if test="${attribute.type == 'userdefResponsibility'}">
						      <form:option value="${attribute.id}">
						        <c:out value="${attribute.name}"/>
						      </form:option>
						    </c:if>
						     <c:if test="${attribute.id == 0}">
						      <form:option value="${attribute.id}">
						        <fmt:message key="${attribute.name}"/>
						      </form:option>
						    </c:if>
						  </c:forEach>                     
						</form:select>
						<c:if test="${isAttributeSet || isRowRelationSet}">
			     			&nbsp;<input type="button" class="link btn" onclick="flowAction('changeRowType')" value="<fmt:message key="button.edit"/>" />
			      		</c:if>
					</div>
				</div>
			</c:if>
      	</c:if>
      	
		<p></p>
		
		<div class="control-group">
			<c:choose>
			  <c:when test="${memBean.graphicalOptions.selectedRowOption == 1 && isRowRelationSet}">
			    <c:set var="elementType" scope="page">                                
			      <fmt:message key="${memBean.graphicalOptions.currentRowRelation.requestedType.typeNamePluralPresentationKey}"/>
			    </c:set>        
			    <tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
			      <tiles:putAttribute name="messageKey" value="graphicalExport.masterplan.showChosenElements" />
			      <tiles:putAttribute name="messageKeyArg" value="${elementType}" />
			      <tiles:putAttribute name="collection" value="queryResults.rowQuery.selectedResults" />
			      <tiles:putAttribute name="field" value="identityString" />
			      <tiles:putAttribute name="presentationId" value="rowElements" />
			    </tiles:insertTemplate>
			  </c:when>
			  <c:when test="${memBean.graphicalOptions.selectedRowOption == 2 && isAttributeSet}">
			    <tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
			      <tiles:putAttribute name="messageKey" value="graphicalExport.landscape.showChosenAttributeValues" />
			      <tiles:putAttribute name="collection" value="graphicalOptions.attrValsOfSelectedRowAttribute" />
			      <tiles:putAttribute name="presentationId" value="rowElements" />
			    </tiles:insertTemplate>
			  </c:when>
			</c:choose>
			
			<c:if test="${isRowRelationSet}">
				<div class="control-group">
					<input type="button" class="link btn" onclick="flowAction('filterRowResults');" value="<fmt:message key="button.filter"/>" />
				</div>
			</c:if>
		</div>
		
		<c:if test="${isRowRelationSet && ( memBean.graphicalOptions.levels.sideAxisTopLevel > 1 || memBean.graphicalOptions.levels.sideAxisBottomLevel > 1 )}">
			<div class="control-group">
				<b class="help-block">
					<c:set var="rowType" scope="page">
						<fmt:message key="${memBean.graphicalOptions.currentRowRelation.requestedType.typeNamePluralPresentationKey}"/>
					</c:set>
					<c:set var="rowAssociationString">
						<fmt:message key="graphicalExport.landscape.rowAssociation"/>
					</c:set>
					<fmt:message key="graphicalExport.landscape.configuration.axis.levels">
						<fmt:param value="${rowType}"/>
						<fmt:param value="${rowAssociationString}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.sideAxisTopLevel}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.sideAxisBottomLevel}"/>
					</fmt:message>
				</b>
				<tiles:insertTemplate template="/jsp/GraphicalReporting/SelectLevels.jsp">
					<tiles:putAttribute name="selectedLevelRangePath" value="graphicalOptions.selectedLevelRangeRowAxis"/>
					<tiles:putAttribute name="selectedLevelRangeField" value="${memBean.graphicalOptions.selectedLevelRangeRowAxis}"/>
					<tiles:putAttribute name="minLevel" value="${memBean.graphicalOptions.levels.sideAxisTopLevel}"/>
					<tiles:putAttribute name="maxLevel" value="${memBean.graphicalOptions.levels.sideAxisBottomLevel}"/>
				</tiles:insertTemplate>
			</div>
		</c:if>
		
		<div class="control-group">
			<label class="checkbox">
		  		<form:checkbox path="graphicalOptions.filterEmptyRows" />
		  		<fmt:message key="graphicalExport.landscape.hideEmptyRows" />
		  	</label>
		</div>
	</fieldset>
</div>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="isColumnRelationSet" value="false"/>
<c:if test="${not empty memBean.graphicalOptions.selectedColumnRelation}">
    <c:set var="isColumnRelationSet" value="true"/>
</c:if>

<c:set var="isAttributeSet" value="false"/>
<c:if test="${memBean.graphicalOptions.selectedColumnAttributeId >= 0}">
    <c:set var="isAttributeSet" value="true"/>
</c:if>

<input type="hidden" name="graphicalOptions.selectedColumnOption" value="0" />

<div class="form-horizontal">
	<fieldset>
		<legend>
			<fmt:message key="graphicalExport.landscape.columnAssociation"/>
		</legend>
		
		<c:if test="${memBean.graphicalOptions.dialogStep > 1}">
			
			<%--  Display columnRelations --%>
			
			<c:if test="${!isAttributeSet}">
				<div class="control-group">
					<label class="control-label" style="padding-top: 10px; margin-bottom: 0pt; width:60px">
						<fmt:message key="graphicalExport.landscape.relation"/>: 
					</label>
					 
					<div class="controls">
						<%-- Select box of available relations. Disabled if the relation has already been choosen --%>
						<form:select path="graphicalOptions.selectedColumnRelation" 
								onchange="setHiddenField('graphicalOptions.selectedColumnOption', 1);flowAction('selectColumnType');" 
								id="columnElementType" disabled="${isColumnRelationSet}">
							<form:option value="">
								<fmt:message key="reports_export_select_relation"/>
							</form:option>
							<c:forEach items="${memBean.graphicalOptions.availableRelations}" var="relation">
								<form:option value="${relation.name}">
									<fmt:message key="${relation.nameKeyForPresentation}"/>
								</form:option>
							</c:forEach>	     
						</form:select>
						<c:if test="${isAttributeSet || isColumnRelationSet}">
							&nbsp;<input type="button" class="link btn" onclick="flowAction('changeColumnType');" value="<fmt:message key="button.edit"/>" />
						</c:if>
					</div>
				</div>
			</c:if>
			
			<c:if test="${!(isAttributeSet || isColumnRelationSet)}">
				<b class="help-block">
					<fmt:message key="reports.or"/>
				</b>
			</c:if>
			
			<c:if test="${!isColumnRelationSet}">
				<div class="control-group">
					<label class="control-label" style="padding-top: 10px; margin-bottom: 0pt; width:60px">
						<fmt:message key="graphicalExport.landscape.attribute"/>: 
					</label>
					<div class="controls">
						<form:select path="graphicalOptions.selectedColumnAttributeId" disabled="${isAttributeSet}" 
								onchange="setHiddenField('graphicalOptions.selectedColumnOption', 2); flowAction('selectColumnType');"
								id="columnAttributeType">
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
						<c:if test="${isAttributeSet || isColumnRelationSet}">
							&nbsp;<input type="button" class="link btn" onclick="flowAction('changeColumnType');" value="<fmt:message key="button.edit"/>" />
						</c:if>
					</div>
				</div>
			</c:if>
		</c:if>
		
		<p></p>
		
		<div class="control-group">
			<c:choose>
				<c:when test="${memBean.graphicalOptions.selectedColumnOption == 1 && isColumnRelationSet}">
					<c:set var="elementType" scope="page">                                
						<fmt:message key="${memBean.graphicalOptions.currentColumnRelation.requestedType.typeNamePluralPresentationKey}"/>
					</c:set>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
						<tiles:putAttribute name="messageKey" value="graphicalExport.masterplan.showChosenElements" />
						<tiles:putAttribute name="messageKeyArg" value="${elementType}" />
						<tiles:putAttribute name="collection" value="queryResults.columnQuery.selectedResults" />
						<tiles:putAttribute name="field" value="identityString" />
						<tiles:putAttribute name="presentationId" value="columnElements" />
					</tiles:insertTemplate>
				</c:when>
				<c:when test="${memBean.graphicalOptions.selectedColumnOption == 2 && isAttributeSet}">
					<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
						<tiles:putAttribute name="messageKey" value="graphicalExport.landscape.showChosenAttributeValues" />
						<tiles:putAttribute name="collection" value="graphicalOptions.attrValsOfSelectedColumnAttribute" />
						<tiles:putAttribute name="presentationId" value="columnElements" />
					</tiles:insertTemplate>
				</c:when>
			</c:choose>
			
			<c:if test="${isColumnRelationSet}">
				<input type="button" class="link btn" onclick="flowAction('filterColumnResults');" value="<fmt:message key="button.filter"/>" />
			</c:if>
		</div>
		
		<c:if test="${isColumnRelationSet && ( memBean.graphicalOptions.levels.topAxisTopLevel > 1 || memBean.graphicalOptions.levels.topAxisBottomLevel > 1 )}">
			<div class="control-group">
				<b class="help-block">
					<c:set var="columnType" scope="page">
						<fmt:message key="${memBean.graphicalOptions.currentColumnRelation.requestedType.typeNamePluralPresentationKey}"/>
					</c:set>
					<c:set var="columnAssociationString">
						<fmt:message key="graphicalExport.landscape.columnAssociation"/>
					</c:set>
					<fmt:message key="graphicalExport.landscape.configuration.axis.levels">
						<fmt:param value="${columnType}"/>
						<fmt:param value="${columnAssociationString}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.topAxisTopLevel}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.topAxisBottomLevel}"/>
					</fmt:message>
				</b>
				<tiles:insertTemplate template="/jsp/GraphicalReporting/SelectLevels.jsp">
					<tiles:putAttribute name="selectedLevelRangePath" value="graphicalOptions.selectedLevelRangeColumnAxis"/>
					<tiles:putAttribute name="selectedLevelRangeField" value="${memBean.graphicalOptions.selectedLevelRangeColumnAxis}"/>
					<tiles:putAttribute name="minLevel" value="${memBean.graphicalOptions.levels.topAxisTopLevel}"/>
					<tiles:putAttribute name="maxLevel" value="${memBean.graphicalOptions.levels.topAxisBottomLevel}"/>
				</tiles:insertTemplate>
			</div>
		</c:if>
		
		<div class="control-group">
			<label class="checkbox">
				<form:checkbox path="graphicalOptions.filterEmptyColumns" />
				<fmt:message key="graphicalExport.landscape.hideEmptyColumns" />
			</label>
		</div>
		
	</fieldset>
</div>

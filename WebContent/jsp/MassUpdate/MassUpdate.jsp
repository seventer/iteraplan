<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<itera:define id="massUpdateType" name="memBean" property="massUpdateType"/>
<c:if test="${massUpdateType.typeNamePresentationKey ==  'businessMapping.singular'}">
	<c:set var="businessMapping" value="true"/>
</c:if>

<fmt:message key="tooltip.checkBoxColumnToolTip" var="checkAllBoxTitle"/>

<table id="massUpdate" class="searchResultView table table-bordered table-striped table-condensed">

	<%-- As only attribute or association standard values can be set, show MassUpdateStandardValues.jsp only if attributes or associations are being updated. --%>
	<c:if test="${not empty memBean.massUpdateAssociationConfig || not empty memBean.massUpdateAttributeConfig}">
		<%-- Business mapping has associations of type toOneAssociation. No standard values implemented for this kind of association. --%>
		<c:if test="${!businessMapping}">
			<tiles:insertTemplate template="/jsp/MassUpdate/MassUpdateStandardValues.jsp" flush="false"></tiles:insertTemplate>
		</c:if>
	</c:if>

	<thead>
		<tr>
	    	<th class="col-ico" nowrap="nowrap">
	      		<form:checkbox path="checkAllBox" id="checkAllBox" value="" onclick="checkUnCheckAllByPattern('lines[', '].selectedForMassUpdate', this);" title="${checkAllBoxTitle}"/>						
	      		<br />
	      		<fmt:message key="reports.selectAll" />
	      	</th>
	      
	      	<c:choose>
		      	<c:when test="${massUpdateType.typeNamePresentationKey ==  'interface.singular'}">
		      	 	<th class="col-name"><fmt:message key="massUpdates.interface" />&nbsp;&nbsp;</th>
			  	</c:when>
		     	<c:otherwise>
		      	 	<th class="col-name"><fmt:message key="massUpdates.name" />&nbsp;&nbsp;</th>
			  	</c:otherwise>
		  	</c:choose>
	      
			<c:choose>
				<%-- if mass updating business mappings, associations not part of the update have to be concatenated in the header 
					The keys for this associations are held in the massUpdatePropertyConfig list
				--%>
				<c:when test="${businessMapping}">
		  			<th>
			  			<c:set var="numberOfProps" value="${fn:length(memBean.massUpdatePropertyConfig)}" />
			  			<c:forEach items="${memBean.massUpdatePropertyConfig}" var="muCfg" varStatus="configIdStatus">
			  				<c:set var="configId" value="${configIdStatus.index}" />
							<itera:define id="prop_header" name="memBean" property="massUpdatePropertyConfig[${configId}].headerKey" />
			    			<fmt:message key="${prop_header}" />
			    			<c:if test="${numberOfProps > configId+1}">
								#
							</c:if>
			  			</c:forEach>
	      			</th>
				</c:when>
		
				<%-- For normal building blocks the localized strings of the properties are displayed--%>
				<c:otherwise>
					<c:forEach items="${memBean.massUpdatePropertyConfig}" var="muCfg" varStatus="configIdStatus">
						<c:set var="configId" value="${configIdStatus.index}" />
						<itera:define id="prop_header" name="memBean" property="massUpdatePropertyConfig[${configId}].headerKey" />
			    		<th><fmt:message key="${prop_header}" /></th>	
					</c:forEach>
				</c:otherwise>
			</c:choose>
      
      		<%-- localized header strings of associations that will be updated--%>
      		<c:forEach items="${memBean.massUpdateAssociationConfig}" var="muAssCfg" varStatus="configIdStatus">
      			<c:set var="configId" value="${configIdStatus.index}" />
				<itera:define id="prop_header" name="memBean" property="massUpdateAssociationConfig[${configId}].headerKey" />
				<%-- Business mapping has associations of type toOneAssociation. No standard values implemented for this kind of association. --%>
				<c:if test="${!muAssCfg.toOneAssociation}">
					<th class="col-ico" nowrap="nowrap">
   						<form:checkbox path="checkAllBox" id="checkAllBusinessMappingBox_${configId}" value="" onclick="checkUnCheckAllByPattern('lines[', '].associations[${configId}]', this);" title="${checkAllBoxTitle}" />						
   						<br />
   						<fmt:message key="reports.selectAll" />	
					</th>
				</c:if>
				<th>
					<fmt:message key="${prop_header}" />
				</th>
      		</c:forEach>
      
      		<%-- attribute names--%>
      		<c:forEach items="${memBean.massUpdateAttributeConfig}" var="muAttrCfg" varStatus="configIdStatus">
	      		<c:set var="configId" value="${configIdStatus.index}" />
	      		<itera:define id="prop_header" name="memBean" property="massUpdateAttributeConfig[${configId}].headerKey" />
	      		<th class="col-ico" nowrap="nowrap">
  					<form:checkbox path="checkAllBox" id="checkAllAttributeBox_${configId}" value="" onclick="checkUnCheckAllByPattern('lines[', '].attributes[${configId}].massUpdateAttributeItem.usesStandardAttributeValues', this);" title="${checkAllBoxTitle}" />						
  					<br />
  					<fmt:message key="reports.selectAll" />
     			</th>
     			<th>
     	  			<c:out value="${prop_header}"/>
				</th>
      		</c:forEach>
    	</tr>
	</thead>
    <tbody>
		<c:choose>
			<c:when test="${businessMapping}">
				<tiles:insertTemplate template="/jsp/MassUpdate/MassUpdateBusinessMappingLines.jsp" flush="false">
					<tiles:putAttribute name="currentReportFormId" value="0" />
				</tiles:insertTemplate>
			</c:when>
			<c:otherwise>
				<tiles:insertTemplate template="/jsp/MassUpdate/MassUpdateLines.jsp" flush="false">
					<tiles:putAttribute name="currentReportFormId" value="0" />
				</tiles:insertTemplate>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<br />

<input name="getNewResultSet" onclick="flowAction('getNewResultSet')" type="button" class="link btn" value="<fmt:message key="massUpdates.newMassUpdateSelection" />"/>

<c:set var="disabled" value="false" />
<c:if test="${attributeValueCount == 0}">
  <c:set var="disabled" value="true" />
</c:if>
<input name="runMassUpdate" onclick="confirmMassUpdate(function(){flowAction('runMassUpdate');})" type="button" class="link btn" value="<fmt:message key="massUpdates.runMassUpdate" />"/>

<br />
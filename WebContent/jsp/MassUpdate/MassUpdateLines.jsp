<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<fmt:message key="massUpdates.standardValueCheckbox.tooltip" var="standardValueSelectedTooltip" />
<fmt:message key="massUpdates.lineCheckbox.tooltip" var="lineSelectedTooltip" />
                                        
<c:forEach items="${memBean.lines}" var="line" varStatus="lineCountStatus">
	<c:set var="lineCount" value="${lineCountStatus.index}" />
	<itera:define id="resultForUpdate" name="memBean" property="lines[${lineCount}].massUpdateResult"/>
  	
  	<c:if test="${resultForUpdate != null}">
    	<c:if test="${resultForUpdate.wasExecuted}">
    		<tr>
      			<td colspan="<c:out value="${(memBean.totalNumberOfColumns * 2) + 2}"/>">
        			<c:choose>
          			<c:when test="${resultForUpdate.exception != null}">
              			<itera:define id="exception" name="resultForUpdate" property="exception.localizedMessage"/>
              			<c:choose>
	                		<c:when test="${resultForUpdate.attributeException && !((empty memBean.massUpdatePropertyConfig) && (empty memBean.massUpdateAssociationConfig))}">
		                  		<span class="label label-important">
		                  			<c:out value="${exception}" escapeXml="false"/>
		                  			<fmt:message key="massUpdates.properties.saved"/>
		                  		</span>
	                		</c:when>
	                		<c:otherwise>
		                		<span class="label label-important">
		                			<c:out value="${exception}" escapeXml="false"/>
		        	         		<fmt:message key="massUpdates.failed"/>
		        	         	</span>
	                		</c:otherwise>
              			</c:choose>
          			</c:when>
					<c:otherwise>
	            		<span class="label label-success">
	            			<fmt:message key="massUpdates.updateWasSucessful" />
	            		</span>
          			</c:otherwise>
        			</c:choose>
      			</td>
    		</tr>
		</c:if>
	</c:if>
	
	<tr>	
    	<%-- Add the name of the building block selected for update. --%>
    	<td class="col-ico">
            <form:checkbox path="lines[${lineCount}].selectedForMassUpdate" onclick="unCheckCheckBox('checkAllBox');" title="${lineSelectedTooltip}" />
        </td>
    	<td><c:out value="${line.buildingBlockToUpdate.identityString}"/></td>
    
	    <%-- Add the properties selected for update. --%>
	    <c:forEach items="${memBean.massUpdatePropertyConfig}" var="cfg" varStatus="configIdStatus">
	    	<c:set var="configId" value="${configIdStatus.index}" />
			<itera:define id="tile" name="memBean" property="massUpdatePropertyConfig[${configId}].guiTile" />
			<itera:define id="componentModel" name="memBean" property="massUpdatePropertyConfig[${configId}].pathToComponentModel" />
			<td>
				<tiles:insertTemplate template="/jsp/MassUpdate/tiles/properties/${tile}.jsp" flush="false">
					<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].componentModel.${componentModel}" />
					<tiles:putAttribute name="lineCount" value="${lineCount}"/>
					<tiles:putAttribute name="buildingBlock_id" value="${line.buildingBlockToUpdate.id}"/>
				</tiles:insertTemplate>				
			</td>
		</c:forEach>
	
	    <%-- Add the associations selected for update. --%>
	    <c:forEach items="${memBean.massUpdateAssociationConfig}" var="cfgAss" varStatus="configIdStatus">
	    	<c:set var="configId" value="${configIdStatus.index}" />
			<itera:define id="componentModel" name="memBean" property="massUpdateAssociationConfig[${configId}].pathToComponentModel" />
			<c:choose>
	        <c:when test="${cfgAss.toManyAssociationSet}">
				<td width="16" class="col-ico">
					<form:checkbox path="lines[${lineCount}].associations[${configId}]" onclick="unCheckCheckBox('checkAllBusinessMappingBox_${configId}');" title="${standardValueSelectedTooltip}"/>
				</td>
				<td valign="top">
	            	<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="false">
	              		<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].componentModel.${componentModel}" />
	              		<tiles:putAttribute name="force_component_mode" value="EDIT" />
	              		<tiles:putAttribute name="available_elements_field_style" value="nameforhierarchy" />
	              		<tiles:putAttribute name="minimal" value="true" />
	              		<tiles:putAttribute name="dynamically_loaded" value="true" />
	            	</tiles:insertTemplate>
            	</td>
			</c:when>
			<%-- used for information system interfaces - transports --%>
			<c:when test="${cfgAss.customAssociation}">
				<td>
					<form:checkbox path="lines[${lineCount}].associations[${configId}]" onclick="unCheckCheckBox('checkAllBusinessMappingBox_${configId}');" title="${standardValueSelectedTooltip}"/>
				</td>
				<itera:define id="managedClass" name="memBean" property="lines[${lineCount}].componentModel.managedClassAsString" />
				<td valign="middle">
					<tiles:insertTemplate template="/jsp/MassUpdate/tiles/Custom${managedClass}_${componentModel}.jsp" flush="false">
						<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].componentModel.${componentModel}" />
					</tiles:insertTemplate>
				</td>
			</c:when>
    	    <%-- used for business mapping but then, MassUpdateBusinessMappingLines.jsp is used! TODO: not tested yet--%>
    	    <c:when test="${cfgAss.toOneAssociation}">
				<td valign="top">
					<tiles:insertTemplate template="/jsp/common/OneAssociationComponentComboboxView.jsp" flush="false">
						<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].componentModel.${componentModel}" />
         		<tiles:putAttribute name="dynamically_loaded" value="true" />
					</tiles:insertTemplate>
				</td>
			</c:when>
			<%-- currently not used! TODO: not tested yet.--%>
	        <c:when test="${cfgAss.toManyAssociationList}">
				<td valign="top">
					<tiles:insertTemplate template="/jsp/common/ManyAssociationListComponentView.jsp" flush="false">
						<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].componentModel.${componentModel}" />
						<tiles:putAttribute name="available_elements_field_style" value="name" />
						<tiles:putListAttribute name="connected_elements_field_styles">
							<tiles:addAttribute value="name" />
							<tiles:addAttribute value="descriptionintable" />
						</tiles:putListAttribute>
		            </tiles:insertTemplate>
				</td>
	        </c:when>
			</c:choose>
		</c:forEach>
		
	    <%-- Add the attributes selected for update. --%>
	    <c:forEach items="${memBean.attributeIds}" var="attribute" varStatus="attributeIdStatus">
	    	<c:set var="attributeId" value="${attributeIdStatus.index}" />
			<itera:define id="type" name="memBean" property="lines[${lineCount}].attributes[${attributeId}].type" />
			<td>
				<form:checkbox path="lines[${lineCount}].attributes[${attributeId}].massUpdateAttributeItem.usesStandardAttributeValues" onclick="unCheckCheckBox('checkAllAttributeBox_${attributeId}');" title="${standardValueSelectedTooltip}"/>
			</td>
			<td valign="top">
				<c:choose>
					<c:when test="${type == 'userdefEnum' || type == 'userdefResponsibility'}">
						<tiles:insertTemplate template="/jsp/MassUpdate/tiles/attributes/EnumOrResponsibilityLine.jsp" flush="false">
							<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].attributes[${attributeId}]" />
						</tiles:insertTemplate>
					</c:when>
					<c:when test="${type == 'userdefNumber' || type == 'userdefText' || type == 'userdefDate'}">
						<tiles:insertTemplate template="/jsp/MassUpdate/tiles/attributes/NumberOrTextOrDateLine.jsp" flush="false">
							<tiles:putAttribute name="path_to_componentModel" value="lines[${lineCount}].attributes[${attributeId}]" />
								<tiles:putAttribute name="type" value="${type}"/>
								<tiles:putAttribute name="line" value="${lineCount}" />
								<tiles:putAttribute name="isAttribute" value="true" />
							</tiles:insertTemplate>
					</c:when>
					<c:otherwise>
						Massupdate for <c:out value="${type}"/> not yet implemented!
					</c:otherwise>
				</c:choose>
			</td>
		</c:forEach>
    </tr>
</c:forEach>




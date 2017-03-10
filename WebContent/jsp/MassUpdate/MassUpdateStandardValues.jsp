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

<c:choose>
	<c:when test="${businessMapping}">
		<c:set var="properties" value="1"/>
	</c:when>
	<c:otherwise>
		<c:set var="properties" value="${fn:length(memBean.massUpdatePropertyConfig)}" />
	</c:otherwise>
</c:choose>

<c:set var="associations" value="${fn:length(memBean.massUpdateAssociationConfig)}" />
<c:set var="attributes" value="${fn:length(memBean.massUpdateAttributeConfig)}" />
<c:set var="lines" value="${fn:length(memBean.lines)}" />

<c:if test="${attributes>0 || associations>0}">
	<thead>
		<tr>
			<th colspan="${2 + properties}">
				<fmt:message key="massUpdates.exisitingAttributeValues" />
			</th>	
			<c:if test="${associations>0}">
				<c:forEach items="${memBean.massUpdateAssociationConfig}" var="muAssCfg" varStatus="configIdStatus">
	      			<c:set var="configId" value="${configIdStatus.index}" />
					<itera:define id="prop_header" name="memBean" property="massUpdateAssociationConfig[${configId}].headerKey" />
					<th colspan="2">
						<fmt:message key="${prop_header}" />
					</th>	
	      		</c:forEach>
			</c:if>
			<c:if test="${attributes>0}">
				<c:forEach items="${memBean.massUpdateAttributeConfig}" var="muAttrCfg">
					<itera:define id="prop_header" name="muAttrCfg" property="headerKey" />
					<th colspan="2">
						<c:out value="${prop_header}"/>
					</th>
				</c:forEach>
			</c:if>	
		</tr>
	</thead>
</c:if>
<tbody>
	<tr>
	  <td colspan="${2 + properties + (associations * 2) + (attributes * 2)}" class="helpText">
	      <fmt:message key="massUpdates.help1" />
	  </td>
	</tr>
	
	<tr>
		<td colspan="${2 + properties}">
			&nbsp;
		</td>
		
		<%-- drop down list with associations for standard values field--%>
		<c:if test="${associations>0}">
			<c:forEach items="${memBean.massUpdateAssociationConfig}" var="muAssCfg" varStatus="configIdStatus">
				<td valign="top" colspan="2">				
					<c:set var="configId" value="${configIdStatus.index}" />
					<itera:define id="componentModel" name="memBean" property="massUpdateAssociationConfig[${configId}].pathToComponentModel" />
					<c:choose>
			        	<c:when test="${muAssCfg.toManyAssociationSet}">
			            	<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="false">
			              		<tiles:putAttribute name="path_to_componentModel" value="standardAssociationCM.${componentModel}" />
			              		<tiles:putAttribute name="force_component_mode" value="EDIT" />
			              		<tiles:putAttribute name="available_elements_field_style" value="nameforhierarchy" />
			              		<tiles:putAttribute name="minimal" value="true" />
			              		<tiles:putAttribute name="dynamically_loaded" value="true" />
			            	</tiles:insertTemplate>
	<%-- 		            	button for taking over the associations  --%>
			            	<input type="button" class="link btn" value="<fmt:message key="massUpdates.takeOverStandardValue"/>"
								onclick="setHiddenField('massUpdateAssociationConfig[<c:out value="${configId}"/>].setStandardValue', 'true');
								flowAction('setStandardAssociationValues');" />
							<input type="hidden" name="massUpdateAssociationConfig[<c:out value="${configId}"/>].setStandardValue" value="false"/>					
						</c:when>				
						<%-- used for information system interfaces - transports --%>
						<c:when test="${muAssCfg.customAssociation}">
							<itera:define id="managedClass" name="memBean" property="standardAssociationCM.managedClassAsString" />					
							<tiles:insertTemplate template="/jsp/MassUpdate/tiles/Custom${managedClass}_${componentModel}.jsp" flush="false">
								<tiles:putAttribute name="path_to_componentModel" value="standardAssociationCM.${componentModel}" />
							</tiles:insertTemplate>
							<%-- button for taking over the associations  --%>
							<input type="button" class="link btn" value="<fmt:message key="massUpdates.takeOverStandardValue"/>"
								onclick="setHiddenField('massUpdateAssociationConfig[<c:out value="${configId}"/>].setStandardValue', 'true');
								flowAction('setStandardAssociationValues');" />
							<input type="hidden" name="massUpdateAssociationConfig[<c:out value="${configId}"/>].setStandardValue" value="false"/>
						</c:when>
					</c:choose>			
				</td>
			</c:forEach>
		</c:if>
		
		<c:if test="${attributes>0}">
			<c:forEach items="${memBean.massUpdateAttributeConfig}" var="muAttrCfg" varStatus="configIdStatus">
				<c:set var="configId" value="${configIdStatus.index}" />
				<itera:define id="type" name="memBean" property="massUpdateAttributeConfig[${configId}].type" />
				<itera:define id="prop_header" name="muAttrCfg" property="headerKey" />
		 		<td valign="top" colspan="2">
					<c:choose>
						<c:when test="${type == 'userdefNumber' || type == 'userdefText' || type == 'userdefDate'}">
		<%-- TEXT, NUMBER STANDARD VALUES --%>
							<c:choose>
								<c:when test="${muAttrCfg.multiline}">
									<form:textarea path="massUpdateAttributeConfig[${configId}].standardNewAttributeValue" cssClass="name" />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${type == 'userdefDate'}">
									    	<form:input class="small datepicker" type="text" path="massUpdateAttributeConfig[${configId}].standardNewAttributeValue" id="massUpdateAttributeConfig_${configId}_standardNewAttributeValue" />
									  	</c:when>
									  	<c:when test="${type == 'userdefNumber'}">
								  			<script type="text/javascript">
								  				$(document).ready(function(){
								  					// validateNumber
								  					$("#massUpdateAttributeConfig_${configId}_standardNewAttributeValue").validate({
								  						<%-- TODO: checkRegEx --%>
								  						expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
								  						<%-- TODO: checkErrorMessage --%>
								  						message: "<fmt:message key="errors.invalidValue"/>"
								  					});
		 							  			});
											</script>
											<div class="control-group">
												<div class="controls">
								    				<form:input path="massUpdateAttributeConfig[${configId}].standardNewAttributeValue" cssClass="date" id="massUpdateAttributeConfig_${configId}_standardNewAttributeValue" />
								    			</div>
								    		</div>
								 	  	</c:when>
									  	<c:otherwise>
									  		<form:input path="massUpdateAttributeConfig[${configId}].standardNewAttributeValue" cssClass="name" id="massUpdateAttributeConfig_${configId}_standardNewAttributeValue" />
									  	</c:otherwise>									  
									</c:choose>
								</c:otherwise>
							</c:choose>
						 </c:when>
		<%-- ENUM, RESPONSIBILITY STANDARD VALUES --%>
						 <c:when test="${type == 'userdefEnum' || type == 'userdefResponsibility'}">
					 		<tiles:insertTemplate template="/jsp/MassUpdate/tiles/attributes/StandardValuesEnumOrResponsibilityAV.jsp" flush="false">
					 			<tiles:putAttribute name="currentMassUpdateAttributeConfig" value="${configId}" />
					 		</tiles:insertTemplate>
						 </c:when>
						 <c:otherwise>
						 	Massupdate for <c:out value="${type}"/> not yet implemented!
						 </c:otherwise>
					</c:choose>
					<br /><br />
					<input type="button" class="link btn" value="<fmt:message key="massUpdates.takeOverStandardValue"/>"
						onclick="setHiddenField('massUpdateAttributeConfig[<c:out value="${configId}"/>].setStandardValue', 'true');
	                    flowAction('setStandardAttributeValues');" />
					<input type="hidden" name="massUpdateAttributeConfig[<c:out value="${configId}"/>].setStandardValue" value="false"/>
		 		</td>	
			</c:forEach>
		</c:if>
	</tr>
</tbody>

<tr>
	<td colspan="${2 + properties + (associations * 2) + (attributes * 2)}" style="border: 1px solid #dedede">
      &nbsp;
	</td>
</tr>
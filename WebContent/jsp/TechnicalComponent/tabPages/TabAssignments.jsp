<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="accordion" id="applicationArchitectureContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#applicationArchitectureContainer" href="#applicationArchitecture"
					onclick="toggleIcon('applicationArchitectureIcon', 'icon-resize-full', 'icon-resize-small');" >
			  	<i id="applicationArchitectureIcon" class="icon-resize-small"></i>
				<fmt:message key="global.applicationArchitecture" />
			</a>
		</div>
		<div id="applicationArchitecture" class="accordion-body in collapse" style="height: auto; ">
			<div class="accordion-inner">
				<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.informationSystemReleaseModel" />
					<tiles:putAttribute name="dynamically_loaded" value="true" />
				</tiles:insertTemplate> 
				<tiles:insertTemplate template="/jsp/TechnicalComponent/tiles/JumpToInterfaceComponentView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.informationSystemInterfaceModel" />
					<c:choose>
						<c:when test="${componentMode == 'CREATE'}">
							<tiles:putAttribute name="showNewConnectionLink" value="false" />
						</c:when>
						<c:otherwise>
							<tiles:putAttribute name="showNewConnectionLink" value="true" />
						</c:otherwise>
					</c:choose>
				</tiles:insertTemplate>
			</div>
		</div>
	</div>
</div>
<div class="accordion" id="technicalArchitectureContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#technicalArchitectureContainer" href="#technicalArchitecture"
					onclick="toggleIcon('technicalArchitectureIcon', 'icon-resize-full', 'icon-resize-small');" >
			  	<i id="technicalArchitectureIcon" class="icon-resize-small"></i>
				<fmt:message key="global.technicalArchitecture" />
			</a>
		</div>
		<div id="technicalArchitecture" class="accordion-body in collapse" style="height: auto; ">
			<div class="accordion-inner">
				<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.architecturalDomainModel" />
					<tiles:putAttribute name="dynamically_loaded" value="true" />
				</tiles:insertTemplate>
				<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.infrastructureElementModel" />
					<tiles:putAttribute name="isAttributable" value="true" />
					<tiles:putAttribute name="dynamically_loaded" value="true" />
				</tiles:insertTemplate>
			</div>
		</div>
	</div>
</div>

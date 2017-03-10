<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.successors" />
	<tiles:putAttribute name="addedElementsPath" value="successorsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="successorsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.predecessors" />
	<tiles:putAttribute name="addedElementsPath" value="predecessorsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="predecessorsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.baseComponents" />
	<tiles:putAttribute name="addedElementsPath" value="baseComponentsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="baseComponentsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.parentComponents" />
	<tiles:putAttribute name="addedElementsPath" value="parentComponentsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="parentComponentsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.informationSystemDomains" />
	<tiles:putAttribute name="addedElementsPath" value="informationSystemDomainsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="informationSystemDomainsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.technicalComponentReleases" />
	<tiles:putAttribute name="addedElementsPath" value="technicalComponentReleasesAdded" />
	<tiles:putAttribute name="removedElementsPath" value="technicalComponentReleasesRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.infrastructureElements" />
	<tiles:putAttribute name="addedElementsPath" value="infrastructureElementsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="infrastructureElementsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.projects" />
	<tiles:putAttribute name="addedElementsPath" value="projectsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="projectsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.businessObjects" />
	<tiles:putAttribute name="addedElementsPath" value="businessObjectsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessObjectsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.businessFunctions" />
	<tiles:putAttribute name="addedElementsPath" value="businessFunctionsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessFunctionsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.businessMappings" />
	<tiles:putAttribute name="addedElementsPath" value="businessMappingsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessMappingsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="informationSystemRelease.to.interfaces" />
	<tiles:putAttribute name="addedElementsPath" value="interfaceAdded" />
	<tiles:putAttribute name="removedElementsPath" value="interfaceRemoved" />
</tiles:insertTemplate>



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.successors" />
	<tiles:putAttribute name="addedElementsPath" value="successorsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="successorsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.predecessors" />
	<tiles:putAttribute name="addedElementsPath" value="predecessorsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="predecessorsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.baseComponents" />
	<tiles:putAttribute name="addedElementsPath" value="baseComponentsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="baseComponentsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.parentComponents" />
	<tiles:putAttribute name="addedElementsPath" value="parentComponentsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="parentComponentsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.to.architecturalDomains" />
	<tiles:putAttribute name="addedElementsPath" value="architecturalDomainsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="architecturalDomainsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.to.infrastructureElements" />
	<tiles:putAttribute name="addedElementsPath" value="infrastructureElementsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="infrastructureElementsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.to.informationSystemReleases" />
	<tiles:putAttribute name="addedElementsPath" value="informationSystemReleasesAdded" />
	<tiles:putAttribute name="removedElementsPath" value="informationSystemReleasesRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="technicalComponentRelease.to.informationSystemInterfaces" />
	<tiles:putAttribute name="addedElementsPath" value="interfacesAdded" />
	<tiles:putAttribute name="removedElementsPath" value="interfacesRemoved" />
</tiles:insertTemplate>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="interface.to.technicalComponentReleases" />
	<tiles:putAttribute name="addedElementsPath" value="technicalComponentReleasesAdded" />
	<tiles:putAttribute name="removedElementsPath" value="technicalComponentReleasesRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="interface.transport.businessObjects" />
	<tiles:putAttribute name="addedElementsPath" value="transportsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="transportsRemoved" />
</tiles:insertTemplate>

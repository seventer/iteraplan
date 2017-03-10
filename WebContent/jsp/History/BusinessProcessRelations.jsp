<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessProcess.to.businessDomains" />
	<tiles:putAttribute name="addedElementsPath" value="businessDomainsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessDomainsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessProcess.to.businessMappings" />
	<tiles:putAttribute name="addedElementsPath" value="businessMappingsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessMappingsRemoved" />
</tiles:insertTemplate>

<%-- Business Mappings --%>
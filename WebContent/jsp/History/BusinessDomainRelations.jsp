<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" /> 

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessDomain.to.businessFunctions" />
	<tiles:putAttribute name="addedElementsPath" value="businessFunctionsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessFunctionsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessDomain.to.businessProcesses" />
	<tiles:putAttribute name="addedElementsPath" value="businessProcessesAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessProcessesRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessDomain.to.businessUnits" />
	<tiles:putAttribute name="addedElementsPath" value="businessUnitsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessUnitsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessDomain.to.businessObjects" />
	<tiles:putAttribute name="addedElementsPath" value="businessObjectsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="businessObjectsRemoved" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/History/ManyAssociationSetFragment.jsp">
	<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
	<tiles:putAttribute name="relationLabelKey" value="businessDomain.to.products" />
	<tiles:putAttribute name="addedElementsPath" value="productsAdded" />
	<tiles:putAttribute name="removedElementsPath" value="productsRemoved" />
</tiles:insertTemplate>

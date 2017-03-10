<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<itera:define id="transports" name="memBean" property="${path_to_componentModel}.availableTransportDirections" />
<form:select path="${path_to_componentModel}.currentlySelectedDirection" 
             id="${path_to_componentModel}.currentlySelectedDirection" 
             items="${transports}" itemLabel="name" itemValue="description" cssStyle="width: 6em;" />
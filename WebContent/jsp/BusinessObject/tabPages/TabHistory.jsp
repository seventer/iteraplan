<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<tiles:insertTemplate template="/jsp/common/TabLocalHistory.jsp">
  
	<!-- path to the id+type of the current building block  -->
	<tiles:putAttribute name="buildingBlockId" value="${memBean.componentModel.elementId}" />
	<tiles:putAttribute name="buildingBlockType" value="BusinessObject" />
     
</tiles:insertTemplate>


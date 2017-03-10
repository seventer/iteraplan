<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel"/>

<c:set var="graphicRepresentation_path" value="${path_to_componentModel}.transportInfo.graphicRepresentation" />
<itera:define id="transportKey" name="memBean" property="${graphicRepresentation_path}"/>

<c:if test="${componentMode == 'READ'}">
     <td style="text-align: center;">
     	<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
			<tiles:putAttribute name="directionKey" value="${transportKey}" />
		</tiles:insertTemplate>
     </td>
</c:if>

<c:if test="${componentMode != 'READ'}">
      <td valign="top" style="text-align: center;">
		  <form:select path="componentModel.transportInfoModel.currentlySelectedDirection" 
		        id="${memBean.componentModel.transportInfoModel.htmlId}_selectDirection" 
		        cssStyle="width: 6em;">
		        <form:options items="${memBean.componentModel.transportInfoModel.availableTransportDirections}"
		              itemLabel="name" itemValue="description" />	
		  </form:select>
      </td>
</c:if>
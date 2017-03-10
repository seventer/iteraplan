<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:if test="${componentMode == 'READ'}">

   <tiles:insertTemplate template="/jsp/common/TabFastExport.jsp">
  
        <%-- path to the id of the current building block  --%>
        <tiles:putAttribute name="buildingBlockId" value="${memBean.componentModel.releaseId}" />
          
        <%--  type of current building block --%>
        <tiles:putAttribute name="buildingBlockType" value="InformationSystem" />
     
   </tiles:insertTemplate>

</c:if>
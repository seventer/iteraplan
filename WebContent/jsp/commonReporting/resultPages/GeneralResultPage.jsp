<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%--
		@UsedFor 	Shows the reprot/query result 
		@UsedFrom	jsp\commonReporting\ManageReportOrMassupdate.jsp;
		@Note		
 --%>

<c:if test="${not empty memBean.results}">
   <tiles:insertTemplate template="/jsp/commonReporting/resultPages/ResultPageTile.jsp" flush="true" />
</c:if>

<%-- Shows message for empty result --%>
<c:if test="${empty memBean.results}">
	<div class="alert alert-info" >
	  <a class="close" data-dismiss="alert">×</a>
	  <c:if test="${memBean.showResults == false}">
	    <fmt:message key="reports.results.resultListIsEmtpy" />
	  </c:if>
	  <c:if test="${memBean.showResults == true}">
	    <fmt:message key="reports.results.queryReturnedNoResults" />
	  </c:if>
	</div>
</c:if>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- The message key for the search page heading. Type: String --%>
<tiles:useAttribute name="entitySearchKey" />

<%-- The message key for the plural form of the current entity type. Type: String --%>
<tiles:useAttribute name="entityNamePluralKey" />

<%-- A list of search criteria, containing names and message keys. Type: List<SearchDialogMemory.Criterion> --%>
<tiles:useAttribute name="searchCriteria" />

<%-- A list that specifies how search results columns shall be rendered. Type: List<ColumnDefinitions> --%>
<tiles:useAttribute name="resultColumnDefinitions" ignore="true" />

<tiles:useAttribute name="subscribable_type" ignore="true" />

<tiles:useAttribute name="bbTypeHtmlId" ignore="true" />

<tiles:useAttribute name="addManageShortcuts" ignore="true" />

<tiles:useAttribute name="showSearchLabel" ignore="true" />


<tiles:insertTemplate template="/jsp/common/TransactionBarOverviewPage.jsp">
  <!-- Pass attribute to child tile, because it's not used here anymore.  -->
  <tiles:putAttribute name="subscribable_type" value="${subscribable_type}" />
</tiles:insertTemplate>



<%--   Just pass the attributes --%>
<tiles:insertTemplate template="/jsp/common/SearchForm.jsp">
  <tiles:putAttribute name="bbTypeHtmlId" value="${bbTypeHtmlId}" />
  <tiles:putAttribute name="entitySearchKey" value="${entitySearchKey}" />
  <tiles:putAttribute name="searchCriteria" value="${searchCriteria}" />
  <tiles:putAttribute name="showSearchLabel" value="${showSearchLabel}" />
  <tiles:putAttribute name="specialCharHintEscaped" value="${specialCharHintEscaped}" />
</tiles:insertTemplate>

<c:choose>
  <c:when test="${dialogMemory.treeView and dialogMemory.hierarchical}">
    <c:set var="resultsTile" value="/jsp/common/ResultsHierarchical.jsp" />
  </c:when>
  <c:otherwise>
    <c:set var="resultsTile" value="/jsp/common/Results.jsp" />
  </c:otherwise>
</c:choose>

<%-- Is tree view? Remember state across multiple requests. --%>
<form:hidden path="treeView"  />

<input type="hidden" name="previousPage" id="previousPage" value="" />
<input type="hidden" name="nextPage" id="nextPage" value="" />

<%--   Just pass the attributes --%>
<tiles:insertTemplate template="${resultsTile}">
  <tiles:putAttribute name="addManageShortcuts" value="${addManageShortcuts}" />
  <tiles:putAttribute name="entityNamePluralKey" value="${entityNamePluralKey}" />
  <tiles:putAttribute name="resultColumnDefinitions" value="${resultColumnDefinitions}" />
</tiles:insertTemplate>


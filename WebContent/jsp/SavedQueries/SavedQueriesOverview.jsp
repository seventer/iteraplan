<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>


<tiles:useAttribute name="selectedSavedQueryId" ignore="true" />

<script type="text/javascript">
  /* <![CDATA[ */

  function deleteSavedQuery(queryName, queryId) {
    showConfirmDialog("<fmt:message key='global.confirmDelete'/>",
        "<fmt:message key='graphicalReport.executeRemoveQuery'><fmt:param>"
            + queryName + "</fmt:param></fmt:message>", function() {
          createHiddenField('deleteQueryId', queryId);
          document.forms[0].submit();
        });
  }

  /* ]]> */
</script>


<%-- Permissions --%>
<c:set var="permissionCreateTabReports" value="${userContext.perms.userHasFuncPermTabReportingCreate}" />
<c:set var="permissionSaveTabReports" value="${userContext.perms.userHasFuncPermTabReportingFull}" />
<c:set var="permissionCreateGraphReports" value="${userContext.perms.userHasFuncPermGraphReportingCreate}" />
<c:set var="permissionSaveGraphReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" />
<c:set var="permissionSaveAnyReports" value="${permissionSaveTabReports or permissionSaveGraphReports}" />
<c:set var="permissionCreateAnyReports" value="${permissionCreateTabReports or permissionCreateGraphReports}" />

<c:set var="filterTooltip">
  <fmt:message key="global.filter.tooltip"/>
</c:set>

<itera:define id="savedQueries" name="dialogMemory" property="savedQueries" />
<c:set var="queriesSize" value="${fn:length(savedQueries)}" />

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermTabReporting || userContext.perms.userHasFuncPermGraphReporting}" />

<c:choose>
  <c:when test="${functionalPermission}">

    <tiles:insertTemplate template="/jsp/ErrorMessagesMVC.jsp" />
  
    <div id="ShowSafedQueryContainer" class="row-fluid module">
      <div id="savedQueryHeading" class="module-heading" style="height:22px">
        <c:out value="${queriesSize} " />
        <fmt:message var="savedQueriesValue" key="graphicalReport.savedQueries" />
        <fmt:message var="found" key="search.found.header" />
        <c:out value=" ${savedQueriesValue} ${fn:toLowerCase(found)}" />
        
        <tiles:insertTemplate template="/jsp/commonReporting/InstantFilter.jsp">
          <tiles:putAttribute name="filterInputId" value="savedQueryFilter" />
          <tiles:putAttribute name="tableToFilterId" value="ShowSavedQueryTable" />
          <tiles:putAttribute name="searchFormIdToUse" value="savedQuerySearchForm" />
        </tiles:insertTemplate>
      </div>
      
      <c:choose>
      <c:when test="${queriesSize > 0}">
        <div class="row-fluid">
          <div class="module-body-table">
            <div id="savedQueriesScrollBox" class="row-fluid">
              <table id="ShowSavedQueryTable" class="table table-striped table-condensed tableInModule">
                  <colgroup>
                    <col class="col-exec" />
                    <col class="col-name" />
                    <col class="col-desc" />
                    <col class="col-desc" />
                    <col class="col-desc" />
                    <col class="col-desc" />
                    <c:if test="${permissionSaveAnyReports}">
                      <col class="col-desc" />
                    </c:if>
                  </colgroup>
                  <thead>
                  <tr>
                    <th class="col-desc"><fmt:message key="global.execute"/></th>
                    <th class="col-name"><fmt:message key="global.name"/></th>
                    <th class="col-name"><fmt:message key="global.description"/></th>
                    <th class="col-desc"><fmt:message key="global.reportType"/></th>
                    <th class="col-desc"><fmt:message key="global.bbtype"/></th>
                    <th class="col-desc"><fmt:message key="global.link"/></th>
                    <c:if test="${permissionSaveAnyReports}">
                      <th class="col-desc"><fmt:message key="button.delete"/></th>
                    </c:if>
                  </tr>
                </thead>
                  <tbody>
                    <c:forEach items="${savedQueries}" var="savedQuery">
                      <c:set var="query_name">${itera:escapeJavaScript(savedQuery.name)}</c:set>
    
                      <c:set var="fastExportURLforBookmark">
                        <c:out value="${iteraplanApplicationUrl}" />
                        <c:out value="/show/fastexport/generateSavedQuery.do?id=${savedQuery.id}&savedQueryType=${savedQuery.type.value}&outputMode=attachment" />
                      </c:set>
                      <c:url var="fastExportURL"  value="/show/fastexport/generateSavedQuery.do">
                        <c:param name="id" value="${savedQuery.id}" />
                        <c:param name="savedQueryType" value="${savedQuery.type.value}" />
                        <c:param name="outputMode" value="attachment" />
                      </c:url>
                      
                      <c:url var="loadQueryUrl"  value="/show/${savedQuery.type.flowMapping}">
                        <c:param name="_eventId" value="loadSavedQuery" />
                        <c:param name="savedQueryId" value="${savedQuery.id}" />
                      </c:url>
    
                      <c:set var="permissionLoadThisQueryIfGraphReport" value="${savedQuery.type.graphicalReport and permissionCreateGraphReports}" />
                      <c:set var="permissionLoadThisQueryIfTabReport" value="${savedQuery.type.tabularReport and permissionCreateTabReports}" />
                      <c:set var="permissionLoadThisQuery" value="${permissionLoadThisQueryIfGraphReport or permissionLoadThisQueryIfTabReport}" />
    
                      <c:set var="executeQueryOnclickValue">
                        changeLocation('<c:out value="${fastExportURL}" />'); return false;
                      </c:set>
    
                      <c:set var="loadQueryOnclickValue">
                        <c:choose>
                          <c:when test="${permissionLoadThisQuery}">
                            changeLocation('${loadQueryUrl}'); return false;
                          </c:when>
                          <c:otherwise>
                            ${executeQueryOnclickValue}
                          </c:otherwise>
                        </c:choose>
                      </c:set>
    
                      <tr style="${rowStyle}">
                      
                        <%-- "Execute/Run" action --%>
                        <td onclick="${executeQueryOnclickValue}">
                          <a href="javascript:<c:out value="${executeQueryOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.executeSavedQuery.tooltip'/>">
                            <i class="icon-play"></i>
                          </a>
                        </td>
                        
                        <%-- Name --%>
                        <td onclick="${loadQueryOnclickValue}">
                          <a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
                            <c:out value="${savedQuery.name}" escapeXml="true" />
                          </a>
                        </td>
                        
                        <%-- Description --%>
                        <td onclick="${loadQueryOnclickValue}">
                          <a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
                            <c:out value="${savedQuery.description}" />
                          </a>
                        </td>
                        
                        <%-- Report Type --%>
                        <td onclick="${loadQueryOnclickValue}">
                          <a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
                            <fmt:message key="${savedQuery.type.titleProperty}" />
                          </a>
                        </td>
                        
                        <%-- Building Block Type --%>
                        <td  onclick="changeLocation('${loadQueryUrl}')">
                          <c:if test="${not empty savedQuery.resultBbType}">
                            <a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
                              <fmt:message key="${savedQuery.resultBbType.typeOfBuildingBlock.value}" />
                            </a>
                          </c:if>
                        </td>
                        
                        <%-- "Link" action --%>
                        <td>
                          <a href="#" onclick="showTipLinkDialog('<fmt:message key="global.bookmark" />:', '<fmt:message key="global.bookmark" />', '${fastExportURLforBookmark}');">
                            <i class="icon-bookmark"></i>
                          </a>
                        </td>
                        
                        <%-- "Delete" action --%>
                        <c:if test="${permissionSaveAnyReports}">
                          <td>
                            <c:if test="${(permissionSaveTabReports and savedQuery.type.tabularReport) or (permissionSaveGraphReports and savedQuery.type.graphicalReport)}">
                              <a href="#" onclick="deleteSavedQuery('<c:out value="${query_name}"/>', '<c:out value="${savedQuery.id}"/>');">
                                <i class="icon-remove"></i>
                              </a>
                            </c:if>
                          </td>
                        </c:if>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
            </div>
          </div>
        </div>
      </c:when>
      <c:otherwise>
      <%-- Show a message if there are no saved queries --%>
        <div class="module-body">
          <fmt:message key="graphicalReport.noSavedQueries" />
        </div>
      </c:otherwise>
      </c:choose>
    </div>
    </c:when>
    <c:otherwise>
      <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
    </c:otherwise>
</c:choose>

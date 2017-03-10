<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="hierarchicalEntityNode" />
<tiles:useAttribute name="resultColumnDefinitions" />
<tiles:useAttribute name="addManageShortcuts" />
<tiles:useAttribute name="hasPermissionForActions" />

<c:set var="resultItem" value="${hierarchicalEntityNode.entity}" />

<%-- The Row structure MUST be the same as in /jsp/common/HierarchicalRow.jsp !! --%>
<tr class="partial-loading-placeholder"
  data-treelevel="${hierarchicalEntityNode.treeLevel}"
  data-nodeid="${hierarchicalEntityNode.id}"
  data-collapsed="<c:out value='${hierarchicalEntityNode.collapsed}'></c:out>"
  style="<c:if test='${not hierarchicalEntityNode.display}'>display:none;</c:if>">

  <td class="firstPMCell"></td>

  <c:if test="${hierarchicalEntityNode.treeLevel gt 1}">
    <td colspan="${hierarchicalEntityNode.treeLevel - 1}"></td>
  </c:if>

  <c:forEach items="${resultColumnDefinitions}" var="columnProps"
    varStatus="s">

    <c:set var="calcColSpanTd">
      <c:choose>
        <c:when test="${s.first}">
          <c:out
            value="${(dialogMemory.treeViewHelper.maxTreeLevel + 2) - hierarchicalEntityNode.treeLevel}" />
        </c:when>
        <c:otherwise>
          <c:out value="1" />
        </c:otherwise>
      </c:choose>
    </c:set>

    <td colspan="${calcColSpanTd}">
        <c:if test="${s.first}">
        <div onclick="createHiddenField('loadAllFrom', '<c:out value='${hierarchicalEntityNode.parent.id}'/>');self.document.forms[0].submit();">
          <i class="${dialogMemory.iconCss}"></i>
          <em style="opacity:0.4;">
            <fmt:message key="button.treeview.placeholderCaption">
              <fmt:param value="${hierarchicalEntityNode.parent.numberOfUnloadedChildren}" />
            </fmt:message>
          </em>
        </div> 
        </c:if>
    </td>
  </c:forEach>
 
  <c:if test="${addManageShortcuts == true}">
    <c:if test="${hasPermissionForActions}">
      <td></td>
    </c:if>
  </c:if>
</tr>
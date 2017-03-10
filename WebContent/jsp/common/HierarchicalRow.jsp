<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="hierarchicalEntityNode" />
<tiles:useAttribute name="resultColumnDefinitions" />
<tiles:useAttribute name="addManageShortcuts" />
<tiles:useAttribute name="hasPermissionForActions" />
<tiles:useAttribute name="createPermissionType" />
<tiles:useAttribute name="updatePermissionType" />
<tiles:useAttribute name="deletePermissionType" />

<c:set var="resultItem" value="${hierarchicalEntityNode.entity}" />

<%-- The Row structure MUST be the same as in /jsp/common/HierarchicalPlaceholder.jsp !! --%>
<tr 
  data-treelevel="${hierarchicalEntityNode.treeLevel}"
  data-nodeid="${hierarchicalEntityNode.id}"
  data-collapsed="<c:out value='${hierarchicalEntityNode.collapsed}'></c:out>"
  style="<c:if test='${not hierarchicalEntityNode.display}'>display:none;</c:if><c:if test='${not hierarchicalEntityNode.validResult}'>opacity: 0.3;</c:if>">

  <td class="firstPMCell"></td>

  <c:if test="${hierarchicalEntityNode.treeLevel gt 1}">
    <td style="padding-right: 0px;" colspan="${hierarchicalEntityNode.treeLevel - 1}"></td>
  </c:if>

  <c:forEach var="columnProps" items="${resultColumnDefinitions}" varStatus="s">
    <c:choose>
      <c:when test="${columnProps.pathToLinkedElement != null && columnProps.pathToLinkedElement != ''}">
      
        <%-- the link points to a property of the element --%>
        <c:set var="linkScript">
          <itera:linkToElement name="resultItem"
            property="${columnProps.pathToLinkedElement}" type="js" />
        </c:set>
        <c:set var="linkStyle" value="link" />
        <c:set var="link">
          <itera:linkToElement name="resultItem"
            property="${columnProps.pathToLinkedElement}" type="html" />
        </c:set>
      </c:when>
      
      <c:when test="${columnProps.pathToLinkedElement != null && columnProps.pathToLinkedElement == ''}">
        <%-- the link points to the element itself --%>
        <c:set var="linkScript">
          <itera:linkToElement name="resultItem" type="js" />
        </c:set>
        <c:set var="linkStyle" value="link" />
        <c:set var="link">
          <itera:linkToElement name="resultItem" type="html" />
        </c:set>
      </c:when>
      <c:otherwise>
        <c:set var="linkScript" value="" />
        <c:set var="linkStyle" value="" />
        <c:set var="link" value="" />
      </c:otherwise>
    </c:choose>
    <%-- End onclick()-Handler creation. --%>

    <c:set var="calcColSpanTd">
      <c:choose>
        <c:when test="${s.first}">
          <c:out value="${(dialogMemory.treeViewHelper.maxTreeLevel + 2) - hierarchicalEntityNode.treeLevel}" />
        </c:when>
        <c:otherwise>
          <c:out value="1" />
        </c:otherwise>
      </c:choose>
    </c:set>

    <td colspan="${calcColSpanTd}" <c:if test="${!dialogMemory.treeViewHelper.reorderingEnabled}">onclick="${linkScript}"</c:if>
      class="<c:if test='${s.first and not hierarchicalEntityNode.leaf}'>showPM </c:if>${linkStyle}"
      ><c:if test="${s.first}">
        <i class="${dialogMemory.iconCss}"></i>
      </c:if> <c:set var="linklength" value="${fn:length(link)}" /> <c:choose>
        <c:when test="${columnProps.attributeType ne null}">
          <c:set var="output">
            <itera:define id="attributeValues" name="resultItem"
              property="${columnProps.modelPath}" />

            <c:if
              test="${attributeValues ne null && fn:length(attributeValues) gt 0}">
              <c:set var="attributeValuesCounter" value="${1}" />
              <c:forEach var="attributeValue" items="${attributeValues}">
                <c:if test="${(attributeValuesCounter > 1)}">
                  <c:out value="; " />
                </c:if>
                <c:choose>
                  <c:when
                    test="${columnProps.attributeType.typeOfAttribute.name eq 'attribute.type.date'}">
                    <fmt:message var="dateFormat"
                      key="calendar.dateFormat" />
                    <fmt:formatDate value="${attributeValue.value}"
                      pattern="${dateFormat}" />
                  </c:when>
                  <c:when
                    test="${columnProps.attributeType.typeOfAttribute.name == 'attribute.type.text'}">
                    <itera:write name="attributeValue" property="value"
                      plainText="true" truncateText="true" escapeXml="false" />
                  </c:when>
                  <c:otherwise>
                    <c:out value="${attributeValue.value}"
                      escapeXml="true" />
                  </c:otherwise>
                </c:choose>
                <c:set var="attributeValuesCounter"
                  value="${attributeValuesCounter + 1}" />
              </c:forEach>
            </c:if>
          </c:set>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${columnProps.modelPath == 'direction'}">
              <tiles:insertTemplate
                template="/jsp/common/DirectionIcon.jsp">
                <tiles:putAttribute name="directionString"
                  value="${resultItem.direction}" />
              </tiles:insertTemplate>
              <c:set var="output" value="" />
            </c:when>
            <c:when test="${columnProps.internationalized}">
              <c:set var="output">
                <fmt:message>
                  <itera:write name="resultItem"
                    property="${columnProps.modelPath}" escapeXml="false" />
                </fmt:message>
              </c:set>
            </c:when>
            <c:when
              test="${columnProps.tableHeaderKey != null && columnProps.tableHeaderKey == 'global.description'}">
              <c:set var="output">
                <itera:write name="resultItem" plainText="true"
                  truncateText="true"
                  property="${columnProps.modelPath}" escapeXml="false" />
              </c:set>
            </c:when>
            <c:otherwise>
              <c:set var="output">
                <itera:write name="resultItem"
                  property="${columnProps.modelPath}" escapeXml="false" />
              </c:set>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose> <span
      class="<c:if test='${columnProps.rightAligned}'>pull-right</c:if>">
        <itera:htmlLinkToElement link="${link}"
          isLinked="${!dialogMemory.treeViewHelper.reorderingEnabled}">
          <c:out value="${output}" />
        </itera:htmlLinkToElement>
    </span>
    </td>
  </c:forEach>
  <c:if test="${addManageShortcuts == true}">
    <itera:checkBbInstancePermission2 name="resultItem"
      property="owningUserEntities" result="writePermissionInstance"
      userContext="userContext" />
    <c:if test="${hasPermissionForActions}">
      <td><c:if
          test="${writePermissionInstance and (resultItem.identityString != '-')}">
          <c:if test="${updatePermissionType}">
            <a
              href="javascript:changeLocation('${link}?_eventId=edit');"
              title="<fmt:message key="button.edit" />"> <i
              class="icon-pencil"></i>
            </a>
          </c:if>
          <c:if test="${createPermissionType}">
            <a
              href="javascript:changeLocation('${link}?_eventId=copyBB');"
              title="<fmt:message key="button.copy" />"> <i
              class="icon-share"></i>
            </a>
          </c:if>
          <c:if test="${deletePermissionType}">
            <a
              href="javascript:confirmDeleteBuildingBlocks(function(){changeLocation('${link}?_eventId=delete')});"
              title="<fmt:message key="button.delete" />"> <i
              class="icon-trash"></i>
            </a>
          </c:if>
        </c:if></td>
    </c:if>
  </c:if>
</tr>
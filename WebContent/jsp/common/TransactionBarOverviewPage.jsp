<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="subscribable_type" ignore="true" />

<c:set var="subscribePermission" value="${userContext.perms.userHasFuncPermSubscription}" scope="request" />
<c:set var="showSubscribersPermission" value="${userContext.perms.userHasFuncPermShowSubscribers}" scope="request" />

<c:set var="isCommonEadataDialog" value="${guiContext.eadataDialogActive && !(guiContext.activeDialogName == 'Overview' || guiContext.activeDialogName == 'Search' || guiContext.activeDialogName == 'BusinessMapping')}" />

<c:if test="${isCommonEadataDialog && (subscribePermission || showSubscribersPermission)}">
    <div id="transactionbar" class="pull-right">
      <div class="btn-group">
        <c:if test="${subscribePermission}">
          <a class="btn" href="javascript:doSubscribe();">
            <i class="icon-envelope"></i>
            <span class="subscribe_button"><fmt:message key="global.subscribe" /></span>
          </a>
        </c:if>
        <c:if test="${showSubscribersPermission}">
          <a class="btn" href="javascript:showSubscribedUsers();">
            <i class="icon-user"></i>
            <fmt:message key="global.subscribed.users.show" /><c:out value=" "/>
            <span class="subscribercount">(<c:out value="${fn:length(subscribed_element.subscribedUsers)}"/>)</span>
          </a>
        </c:if>
      </div>
    </div>
</c:if>

<%-- embed subscription JS functions for this BBT, so that they can be used from menu actions --%>
<c:if test="${subscribable_type != null && (subscribePermission || showSubscribersPermission)}">
  <tiles:insertTemplate template="/javascript/SubscriptionFunctions.js-block.jsp">
    <tiles:putAttribute name="service_class" value="BuildingBlockTypeService" />
    <tiles:putAttribute name="subscribed_element" value="${bbt}" />
    <tiles:putAttribute name="id" value="${bbt.id}" />
  </tiles:insertTemplate>
</c:if>
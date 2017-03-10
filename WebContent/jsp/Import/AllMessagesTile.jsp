<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

		<div class="row-fluid">
			<c:if test="${not empty memBean.resultMessages.errors}">
				<c:set var="errorsHeader">
					<fmt:message key="errors.header" />
				</c:set>
				<tiles:insertTemplate template="SingleMessagesTile.jsp">
					<tiles:putAttribute name="messagesHeader" value="${errorsHeader}" />
					<tiles:putAttribute name="messageList" value="${memBean.resultMessages.errors}" />
					<tiles:putAttribute name="messagesId" value="errors" />
				</tiles:insertTemplate>
			</c:if>
		
			<c:if test="${not empty memBean.resultMessages.warnings}">
				<c:set var="warningsHeader">
					<fmt:message key="warnings.header" />
				</c:set>
				<tiles:insertTemplate template="SingleMessagesTile.jsp">
					<tiles:putAttribute name="messagesHeader" value="${warningsHeader}" />
					<tiles:putAttribute name="messageList" value="${memBean.resultMessages.warnings}" />
					<tiles:putAttribute name="messagesId" value="warnings" />
				</tiles:insertTemplate>
			</c:if>
		
			<c:if test="${not empty memBean.resultMessages.infos}">
				<c:set var="infosHeader">
					<fmt:message key="infos.header" />
				</c:set>
				<tiles:insertTemplate template="SingleMessagesTile.jsp">
					<tiles:putAttribute name="messagesHeader" value="${infosHeader}" />
					<tiles:putAttribute name="messageList" value="${memBean.resultMessages.infos}" />
					<tiles:putAttribute name="messagesId" value="infos" />
				</tiles:insertTemplate>
			</c:if>
		</div>

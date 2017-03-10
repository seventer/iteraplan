<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page isErrorPage="true" %>

<fmt:setLocale value="${userContext.locale}" />


<tiles:insertTemplate template="/jsp/layouts/standard.jsp">
	<tiles:putAttribute name="title" value="errorview.title" />
	<tiles:putAttribute name="header" value="/jsp/Header.jsp" />
	<tiles:putAttribute name="navigation" value="/jsp/Navigation.jsp" />
	<tiles:putAttribute name="breadcrumb" value="/jsp/MainBreadcrumb.jsp" />
	<tiles:putListAttribute name="contents">
		<tiles:addAttribute value="/jsp/ErrorViewContent.jsp" type="page" />
	</tiles:putListAttribute>
	<tiles:putAttribute name="footer" value="/jsp/Footer.jsp" />
</tiles:insertTemplate>

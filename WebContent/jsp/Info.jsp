<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertTemplate template="/jsp/layouts/standard.jsp" flush="true">
    <tiles:putAttribute name="title">
        <fmt:message key="infoview.title" />
    </tiles:putAttribute>
    <tiles:putAttribute name="header" value="/jsp/Header.jsp" />
	<tiles:putAttribute name="navigation" value="/jsp/Navigation.jsp" />
	<tiles:putAttribute name="menu" value="/jsp/Menu.jsp" />
	<tiles:putAttribute name="breadcrumb" value="/jsp/MainBreadcrumb.jsp" />

    <tiles:putListAttribute name="contents">
        <tiles:addAttribute value="/jsp/InfoContent.jsp" type="page" />
    </tiles:putListAttribute>
    <tiles:putAttribute name="footer" value="/jsp/Footer.jsp" />
</tiles:insertTemplate>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<spring:eval var="buildid"	expression="@applicationProperties.getProperty('build.id')" />

<span class="iteratecfooter">
 &copy; <fmt:message key="footer.text" />
</span>
&nbsp;&nbsp;
<fmt:message key="footer.build_id" />: ${buildid}

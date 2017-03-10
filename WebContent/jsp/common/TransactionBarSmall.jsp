<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<li>
	<a id="bookmarkLink" href="#"
		onclick="showTipLinkDialog('<fmt:message key="global.bookmark" />:', '<fmt:message key="global.bookmark" />', '<itera:linkToBookmark  parameter="url"/>');" >
		<i class="icon-bookmark"></i>
		<fmt:message key="global.link" />
	</a>
</li>
<c:if test="${componentMode == 'READ'}">
	<li>
		<a id="printLink" href="#"
			onclick="window.print();return false;" >
			<i class="icon-print"></i>
			<fmt:message key="global.print" />
		</a>
	</li>
</c:if>
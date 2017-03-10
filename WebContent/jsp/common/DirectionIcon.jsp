<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="directionKey" ignore="true"/>
<tiles:useAttribute name="directionString" ignore="true"/>

<c:if test="${not empty directionKey}">
	<c:choose>
		<c:when test="${directionKey == 'noDirection'}">
			<img src="<c:url value="/images/icon-minus.png" />" />
		</c:when>
		<c:when test="${directionKey == 'firstToSecond'}">
			<img src="<c:url value="/images/icon-right.png" />" />
		</c:when>
		<c:when test="${directionKey == 'secondToFirst'}">
			<img src="<c:url value="/images/icon-left.png" />" />
		</c:when>
		<c:when test="${directionKey == 'bothDirections'}">
			<img src="<c:url value="/images/icon-left.png" />" />
			<img src="<c:url value="/images/icon-right.png" />" />
		</c:when>
	</c:choose>
</c:if>
<c:if test="${not empty directionString}">
	<c:choose>
		<c:when test="${directionString == '-'}">
			<img src="<c:url value="/images/icon-minus.png" />" />
		</c:when>
		<c:when test="${directionString == '->'}">
			<img src="<c:url value="/images/icon-right.png" />" />
		</c:when>
		<c:when test="${directionString == '<-'}">
			<img src="<c:url value="/images/icon-left.png" />" />
		</c:when>
		<c:when test="${directionString == '<->'}">
			<img src="<c:url value="/images/icon-left.png" />" />
			<img src="<c:url value="/images/icon-right.png" />" />
		</c:when>
	</c:choose>
</c:if>
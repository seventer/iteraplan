<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<tiles:useAttribute name="contents" ignore="true" />

	<c:catch var="jsp_exception">
	<c:if test="${not empty contents}">
		<c:forEach items="${contents}" var="content">
			<tiles:insertTemplate template="${content}" flush="false" />
		</c:forEach>
	</c:if>
	</c:catch>
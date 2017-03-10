<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<tiles:useAttribute name="content" />
<tiles:insertTemplate template="${content}" flush="false" />

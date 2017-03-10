<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<div class="well" id="iteraplanMenu">
	
	<tiles:insertTemplate template="/jsp/ContextActions.jsp" flush="true"/>
	<tiles:insertTemplate template="/jsp/OpenElements.jsp" flush="true"/>
	<tiles:insertTemplate template="/jsp/common/WatchedElements.jsp" flush="true"/>
	
			
	<%-- TODO bookmarks
	<li class="nav-header"><fmt:message key="contextMenu.bookmarks"/></li>
	<li>
		<i class="icon-star"></i>
			coming soon
	</li>
	--%>
</div>
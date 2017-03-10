<%@page import="de.iteratec.iteraplan.businesslogic.common.URLBuilder"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>


<!-- context sensitive url -->
<script type="text/javascript">		
		function buildCurrentUrl() {
			pathArray = window.location.pathname.split( '/' );
			index = pathArray.length-1;
			url = "";

			var context = '<%= URLBuilder.getApplicationURL(request) %>'.split('/');
			end = context[context.length-1];
			
			while (pathArray[index].indexOf(end) == -1) {
				url = "/"+pathArray[index]+url;
				index--;
			}
			
			url = url.substring(1, url.lenght);
			return url; 
		};
</script>

<c:set var="show" value="collapse" />
<c:set var="icon_to_show" value="icon-chevron-up" /> 
<c:if test="${guiContext.expandedMenuStatus[1]}">
	<c:set var="show" value="in" />
	<c:set var="icon_to_show" value="icon-chevron-down" /> 
</c:if>

<div class="accordion" id="openElementsContainer">
<div class="accordion-group">
     <div class="accordion-heading" onclick="GuiService.setMenuStatus(1);">
       <a class="accordion-toggle" data-toggle="collapse" data-parent="#openElementsContainer" href="#openElementsAccordion"
						onclick="toggleIcon('openElementsIcon', 'icon-chevron-down', 'icon-chevron-up');" >
				   <i class="icon-file"></i>
	               <fmt:message key="contextMenu.openElements" />
	               <i style="float: right;" id="openElementsIcon" class="${icon_to_show}"></i>
	   </a>
       
     </div>
	<div id="openElementsAccordion" class="accordion-body ${show}">
       <div class="accordion-inner">

	<c:set var="firstItem" value="true"/>
	<c:choose>	
		<c:when test="${guiContext.listableOpenElements}">
		<ul class="nav nav-list">
		<c:forEach items="${guiContext.dialogsWithOpenElements}" var="dialog">
			<c:if test="${!(dialog == 'BusinessMapping')}">
				<c:forEach items="${guiContext.flowEntries[dialog]}" var="entry">
				
					<c:if test="${firstItem}">
						<c:set var="firstItem" value="false"/>
					</c:if>
					
					<c:set var="flow_url" value="/show/${fn:toLowerCase(dialog)}" />
					<c:set var="active" value="" />
					<c:set var="editColor" value="" />
					<c:set var="edited" value="" />
					<c:choose>
						<c:when test="${not empty entry.entityId}">
							<c:url var="closeUrl" value="${flow_url}/${entry.entityId}"><c:param name="execution" value="${entry.key}" /><c:param name="_eventId" value="close" /></c:url>
						</c:when>
						<c:otherwise>
							<c:url var="closeUrl" value="${flow_url}"><c:param name="execution" value="${entry.key}" /><c:param name="_eventId" value="close" /></c:url>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
		            <c:when test="${guiContext.activeDialog == entry.key}">
		            	<%-- use standard transaction if element to close is active --%>
		            	<c:set var="onClick">
							changeLocation('<c:out value="${closeUrl}" escapeXml="false"/>'); return false;
						</c:set>
		            </c:when>
		            <c:otherwise>
						<c:set var="onClick">
						<%-- use pseudo-redirect when open element to close is not active --%>
							changeLocation('<c:out value="${closeUrl}" escapeXml="false"/>WithoutRedirect&amp;_currentUrl=' + buildCurrentUrl()); 
							return false;
						</c:set>
		            </c:otherwise>
		            </c:choose>
					
					<c:if test="${entry.edit}">
						 <c:set var="editColor" value="style=\"color:#AC007C;\"" />
						 <c:set var="edited" value="isEdited" />
		            	 <c:set var="onClick">
							confirmChangeLocation(msgOkCancel, '<c:out value="${closeUrl}" escapeXml="false"/>'); return false;
						 </c:set>
					</c:if>
					<c:if test="${guiContext.activeDialog == entry.key}">
						<c:set var="active" value="active" />
					</c:if>

					
					<li id="m_${entry.entityId}_${entry.key}" class="${active}">
							<a ${editColor} href="<c:url value="${flow_url}/${entry.entityId}"><c:param name="execution" value="${entry.key}" /></c:url>"
									id="menu.${this_dialog}.${entry.key}"
			    					<c:if test="${entry.truncated}">
			    						title="<c:out value='${entry.label}'/>"
			    					</c:if>
									>
								<i class="icon-iteraplan-${fn:toLowerCase(dialog)}"></i>
								

							<%-- italics depending on whether the open element has been saved --%>
							<c:choose>
								<c:when test="${not entry.edit}">
									<c:out value="${entry.truncatedLabel}" />
								</c:when>
								<c:otherwise>
									<i><c:out value="${entry.truncatedLabel}" /></i>
								</c:otherwise>
							</c:choose>	
								
								<%-- provide the close button --%>
								<i class="icon-remove" style="float: right;" onclick="${onClick}" title="<fmt:message key="button.close"/>"></i>
							</a>
					</li>
				</c:forEach>
			</c:if>
	</c:forEach>
	</ul>
	</c:when>
	<c:otherwise>
		<fmt:message key="global.contextMenu.openElements.empty" />
	</c:otherwise>
	</c:choose>
		       </div>
	</div>
</div>
</div>
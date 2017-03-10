<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- Set the path explicitly in order to override the default "componentModel.attributeModel" --%>
<tiles:useAttribute name="path_to_componentModel" ignore="true" />

<%-- If overviewMode is set to true, the componentMode is ignored and all attributes are displayed read only. --%>
<tiles:useAttribute name="overviewMode" ignore="true" />

<%-- If true, the header table (incl. header) is shown (default=yes) --%>
<tiles:useAttribute name="showHeaderTable" ignore="true" />

<%-- If true, the header for each AttributeTypeGroup is shown (default=yes) --%>
<tiles:useAttribute name="showATGHeaderTable" ignore="true" />

<%-- If true, the Attributes are displayed in a single column instead of halfpage-width columns --%>
<tiles:useAttribute name="single_col" ignore="true" />


<c:set var="attributeModelPath" value="componentModel.attributeModel"/>
<c:if test="${path_to_componentModel != null}" >
  <c:set var="attributeModelPath" value="${path_to_componentModel}"/>
</c:if>

<itera:define id="attributeModel" name="memBean" property="${attributeModelPath}"/>
<itera:define id="html_id" name="memBean" property="${attributeModelPath}.htmlId" />

<c:if test="${overviewMode == null}">
  <c:set var="overviewMode" value="false" />
</c:if>

<c:if test="${showHeaderTable == null}">
  <c:set var="showHeaderTable" value="true" />
</c:if>

<c:if test="${showATGHeaderTable == null}">
  <c:set var="showATGHeaderTable" value="true" />
</c:if>

<c:if test="${not empty attributeModel.atgParts}" >

	<c:forEach items="${attributeModel.atgParts}" var="atgPart" varStatus="atgIterateStatus">
    	
	    <itera:checkAttrTypeGroupPermission result="readPermissionATG" userContext="userContext" name="atgPart" property="atg" permissionType="read" />
	    <itera:checkAttrTypeGroupPermission result="writePermissionATG" userContext="userContext" name="atgPart" property="atg" permissionType="write" />    
	    
	    <c:set var="atgVisible" value="visibleRows" />
	    <c:set var="atgIcon" value="icon-resize-full" />
	    <c:set var="accordionVisible" value="" />
	    
		<c:set var="html_id_atg" value="${itera:replaceNoIdChars(html_id)}_${atgPart.atg.nameForHtmlId}" />
		<c:set var="html_id_atg_accordion" value="${itera:replaceNoIdChars(html_id)}_${atgPart.atg.nameForHtmlId}Accordion" />
		<c:set var="html_id_atg_icon" value="${itera:replaceNoIdChars(html_id)}_${atgPart.atg.nameForHtmlId}Icon" />
	    
	    <c:set var="showATG" scope="request">
	    	<itera:write name="guiContext" property="openedATG(${html_id_atg})" escapeXml="false" />
	    </c:set>
	    
	    <c:if test="${showATG == true}">
	    	<c:set var="atgVisible" value="visibleRows" />
	    	<c:set var="atgIcon" value="icon-resize-small" />
	    	<c:set var="accordionVisible" value="in" />
	    </c:if>
	    
	    <c:if test="${readPermissionATG}">
	    
<c:if test="${showATGHeaderTable}">
			<div class="accordion" id="${html_id_atg_accordion}">
				<div class="accordion-group">
					<div class="accordion-heading" onclick="GuiService.setAttributeGroupStatus('<c:out value="${html_id_atg}" />');">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#${html_id_atg_accordion}" href="#<c:out value="${html_id_atg}" />"
								onclick="toggleIcon('${html_id_atg_icon}', 'icon-resize-full', 'icon-resize-small');" >
							<i id="${html_id_atg_icon}" class="${atgIcon}"></i>
							<c:out value="${atgPart.atg.name}" />
						</a>
					</div>
					<div id="<c:out value="${html_id_atg}" />" class="accordion-body ${accordionVisible} collapse">
						<div class="accordion-inner">
						
							<div id="MainAttributesModule" class="row-fluid module" style="border:0;">
								<div class="row-fluid">
									<div class="module-body">
</c:if>

									<c:if test="${componentMode != 'READ' && !writePermissionATG}">
										<div class="row-fluid">
											<div class="helpText"><fmt:message key="messages.noAtGroupWritePermission" /></div>
										</div>
									</c:if>
									
									<%-- When the AT should be displayed in one column, just iterate over them and display them one after each other --%>
									<c:choose>
										<c:when test="${single_col}">
								          <c:forEach items="${atgPart.atParts}" var="currentAtPart" varStatus="atIterateStatus">
								            <div class="row-fluid visibleRows">
								                <tiles:insertTemplate template="/jsp/common/attributes/ManageAVA.jsp" flush="true">
								                  <tiles:putAttribute name="atPartPath" value="${attributeModelPath}.atgParts[${atgIterateStatus.index}].atParts[${atIterateStatus.index}]"/>
								                  <tiles:putAttribute name="atPartPathAnchor" value="${atgIterateStatus.index}_${atIterateStatus.index}" />
								                  <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
								                  <tiles:putAttribute name="extended_html_id" value="${html_id}_${atgPart.atg.nameForHtmlId}" />
								                  <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
												  <tiles:putAttribute name="fullPageTable" value="true" />
								                </tiles:insertTemplate>
								            </div>
								          </c:forEach>					
										</c:when>
										
										<%-- When they should be displayed in two columns, it gets a bit more tricky: --%>
										<c:otherwise>
								          <%-- <bean-el:size name="${atgPart.atParts}" id="atPartSize" /> --%>
								          <c:set var="atPartSize" value="${fn:length(atgPart.atParts)}"/>
								          <c:choose>
								            <c:when test="${(atPartSize % 2) == 0}">
								              <c:set var="atPartHalfSize" value="${(atPartSize / 2)-1}" />
								            </c:when>
								            <%-- due to unpredictable behaviour within the fmt:formatNumber-Tag
								                 (1.5 results in 2, 3.5 results in 4, but 2.5 also results in 2,), 
								                 0.5 is deducted from the variable to "floor" the value. 
								            --%>
								            <c:otherwise>
								              <c:set var="atPartHalfSize" value="${(atPartSize / 2)-0.5}" />
								            </c:otherwise>
								          </c:choose>
											
								          <fmt:formatNumber value="${atPartHalfSize}" var="atPartHalfSize" pattern="#" />
									          <c:forEach items="${atgPart.atParts}" var="currentAtPart" end="${atPartHalfSize}" varStatus="atIterateStatus">
									          	  <div class="row-fluid <c:out value="${atgVisible}"/>">
												      <div class="span6">
										                <tiles:insertTemplate template="/jsp/common/attributes/ManageAVA.jsp" flush="true">
										                  <tiles:putAttribute name="atPartPath" value="${attributeModelPath}.atgParts[${atgIterateStatus.index}].atParts[${atIterateStatus.index}]"/>
										                  <tiles:putAttribute name="atPartPathAnchor" value="${atgIterateStatus.index}_${atIterateStatus.index}" />
										                  <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
										                  <tiles:putAttribute name="extended_html_id" value="${html_id_atg}" />
										                  <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
										                </tiles:insertTemplate>
										              </div>
										              <div class="span6">
										                <c:choose>
										                  <c:when test="${(atPartHalfSize+atIterateStatus.count) < atPartSize}">
										                  	<tiles:insertTemplate template="/jsp/common/attributes/ManageAVA.jsp" flush="true">
											                      <tiles:putAttribute name="atPartPath" value="${attributeModelPath}.atgParts[${atgIterateStatus.index}].atParts[${atPartHalfSize+atIterateStatus.count}]"/>
											                      <tiles:putAttribute name="atPartPathAnchor" value="${atgIterateStatus.index}_${atPartHalfSize+atIterateStatus.count}" />
											                      <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
											                      <tiles:putAttribute name="extended_html_id" value="${html_id_atg}" />
											                      <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
											              	</tiles:insertTemplate>
										                  </c:when>
										                  <c:otherwise>
										                      &nbsp;
										                  </c:otherwise>
										                </c:choose>
										              </div>
										              <div style="clear:both;"></div>
									              </div>
									          </c:forEach>
										</c:otherwise>
									</c:choose>
									
<c:if test="${showATGHeaderTable}">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
</c:if>
		
		</c:if>
	
	</c:forEach>
</c:if>
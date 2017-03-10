<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel"/>
<tiles:useAttribute name="virtualElementSelected" ignore="true" />
<tiles:useAttribute name="lineCount" ignore="true"/>
<tiles:useAttribute name="minimal" ignore="true" />
<tiles:useAttribute name="path_to_componentModelName" ignore="true" />

<%-- used for the MassUpdate View --%>
<tiles:useAttribute name="showDivView" ignore="true" />

<itera:define id="localized_label" name="memBean" property="${path_to_componentModel}.localizedLabel"/>
<c:set var="start_date_field" value="${path_to_componentModel}.startAsString"/>
<c:set var="end_date_field" value="${path_to_componentModel}.endAsString"/>
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<itera:define id="start_date_bean" name="memBean" property="${start_date_field}"/>
<itera:define id="end_date_bean" name="memBean" property="${end_date_field}"/>


<c:if test="${not empty path_to_componentModelName && not empty start_date_bean }">
	<div class="vevent">
		<div class="summary"><itera:write name="memBean" property="${path_to_componentModelName}.name" escapeXml="true" /></div>
		<div class="dtstart"><itera:write name="memBean" property="${path_to_componentModel}.startDate" escapeXml="false" /></div>
		<c:if test="${not empty end_date_bean}">
			<div class="dtend"><itera:write name="memBean" property="${path_to_componentModel}.endDate" escapeXml="false" /></div>
		</c:if>
	</div>
</c:if>

<c:if test="${component_mode == 'READ'}">
  <c:set var="tdstyle" value="margin"/>
</c:if>

<c:if test="${virtualElementSelected}">
  <c:set var="disabled" value="true" />
</c:if>

<div class="control-group">
	<c:choose>
		<c:when test="${not showDivView}">	
		  	<c:if test="${empty minimal}">
		  		<label class="control-label" for="${html_id}_date">
		  			<fmt:message key="${localized_label}" />:
		  		</label>
			</c:if>		
			<%-- Do not use ids that could be "ad1" "ad2", 3 or 4 as these are set to 
			 	 hidden by the popular AdBlock-Extension for Firefox!  --%>
			<div class="controls">
			    <c:choose>
					<c:when test="${component_mode != 'READ'}">
					<c:if test="${!(virtualElementSelected)}">
			        	<form:input class="small datepicker" type="text" path="${start_date_field}" id="${html_id}_startDate_${lineCount}" />
			          	&nbsp;&nbsp;<fmt:message key="global.to"/>&nbsp;
			          	<form:input class="small datepicker" type="text" path="${end_date_field}" id="${html_id}_endDate_${lineCount}" />
			          	&nbsp;&nbsp;
			        </c:if>
			      	</c:when>
			      	<c:otherwise>
				        <itera:define id="start_date_bean" name="memBean" property="${start_date_field}"/>
				        <itera:define id="end_date_bean" name="memBean" property="${end_date_field}"/>
			        	<c:choose>
			          		<c:when test="${empty start_date_bean && empty end_date_bean}">
			            		<c:set var="desc1">
			              			&nbsp;
			            		</c:set>
			          		</c:when>
							<c:when test="${empty start_date_bean && not empty end_date_bean}">
					        	<c:set var="desc2">
					            	<fmt:message key="global.ending"/>&nbsp;
					            </c:set>
					        </c:when>
			          		<c:when test="${not empty start_date_bean && empty end_date_bean}">
			            		<c:set var="desc1">
			              			<fmt:message key="global.starting"/>&nbsp;
			            		</c:set>
			          		</c:when>
			          		<c:when test="${not empty start_date_bean && not empty end_date_bean}">
			            		<c:set var="desc2">
			              			&nbsp;<fmt:message key="global.to"/>&nbsp;
			            		</c:set>
			          		</c:when>
			        	</c:choose>
			        	<c:out value="${desc1}" escapeXml="false"/>
				        <c:out value="${start_date_bean}"/>
				        <c:out value="${desc2}" escapeXml="false"/>
				        <c:out value="${end_date_bean}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<%-- used for the MassUpdate View --%>
		<c:otherwise>
			<div class="controls">
				<itera:define id="start_date_bean" name="memBean" property="${start_date_field}"/>
				<itera:define id="end_date_bean" name="memBean" property="${end_date_field}"/>
				<c:choose>
					<c:when test="${empty start_date_bean && empty end_date_bean}">
			        	<c:set var="desc1"></c:set>
			        </c:when>
			        <c:when test="${not empty start_date_bean && empty end_date_bean}">
			        	<c:set var="desc1"><fmt:message key="global.starting"/>&nbsp;</c:set>
			        </c:when>
			        <c:when test="${empty start_date_bean && not empty end_date_bean}">
			        	<c:set var="desc2"><fmt:message key="global.ending"/>&nbsp;</c:set>
			        </c:when>
			        <c:when test="${not empty start_date_bean && not empty end_date_bean}">
			        	<c:set var="desc2"><fmt:message key="global.to"/>:</c:set>
			        </c:when>
				</c:choose>
				<div id="massUpdatePosition">
					<div class="onLeft">
						<div class="runtime">
							<fmt:message key="global.from"/>:
						</div>
						<div class="onRight">
							<c:choose>
					      		<c:when test="${component_mode != 'READ'}">
							        <c:if test="${!(virtualElementSelected)}">
							        	<form:input class="small datepicker" type="text" path="${start_date_field}" id="${html_id}_startDate_${lineCount}" />
							        </c:if>
					      		</c:when>
					      		<c:otherwise>
					        		<c:out value="${start_date_bean}"/>
					        	</c:otherwise>
					        </c:choose>
						</div>
					</div>
					<div class="onLeft">
						<div class="runtime">
							<fmt:message key="global.to"/>:
						</div>
						<div class="onRight">
							<c:choose>
					      		<c:when test="${component_mode != 'READ'}">
							        <c:if test="${!(virtualElementSelected)}">
							        	<form:input class="small datepicker" type="text" path="${end_date_field}" id="${html_id}_endDate_${lineCount}" />
							        </c:if>
					      		</c:when>
					      		<c:otherwise>
					        		<c:out value="${end_date_bean}"/>
					        	</c:otherwise>
					        </c:choose>
						</div>
					</div>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>
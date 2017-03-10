<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="path_to_componentModel" ignore="false" />

<%-- Set the variable for the HTML ID of this component. --%>
<itera:define id="html_id" name="memBean"
  property="${path_to_componentModel}.htmlId" />

<div class="control-group">
	<c:if test="${empty minimal}" >
		<label class="control-label " for="atgSelection">
	  		<fmt:message key="atg.other" />:
	  	</label>
	</c:if>
	<div class="controls" style="width: 60em;">
		<input type="hidden" name="<c:out value="${path_to_componentModel}" />.selectedPosition"/>
		<input type="hidden" name="<c:out value="${path_to_componentModel}" />.action"/>		
		<div class="row">
			<div class="span5" style="float:left;">
				<form:select path="${path_to_componentModel}.currentId"
					size="9" 
					cssClass="nameforSelect" 
					id="atgSelection"
					onchange="flowAction('refresh')">
					<form:options items="${memBean.componentModel.chooseAttributeTypeGroupComponentModel.available}" itemLabel="name" itemValue="id" />
				</form:select>
			</div>
			<div style="float:left;">
				<img src="<c:url value="/images/SortArrowTop.gif" />"
					onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${memBean.componentModel.chooseAttributeTypeGroupComponentModel.position}" />, 'moveTop')"
					class="link" 
					alt="<fmt:message key="tooltip.moveTop" />"
					title="<fmt:message key="tooltip.moveTop" />"
					id="<c:out value="${html_id}_moveTop" />" />
				<br/>
				<img src="<c:url value="/images/SortArrowUp.gif" />"
					onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${memBean.componentModel.chooseAttributeTypeGroupComponentModel.position}" />, 'moveUp')"
					class="link"
					alt="<fmt:message key="tooltip.moveUp" />" 
					title="<fmt:message key="tooltip.moveUp" />"
					id="<c:out value="${html_id}_moveUp" />" />
				<br/>
				<br/>
				<img src="<c:url value="/images/SortArrowDown.gif" />"
					onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${memBean.componentModel.chooseAttributeTypeGroupComponentModel.position}" />, 'moveDown')"
					class="link" 
					alt="<fmt:message key="tooltip.moveDown" />"
					title="<fmt:message key="tooltip.moveDown" />"
					id="<c:out value="${html_id}_moveDown" />" />
				<br/>
				<img src="<c:url value="/images/SortArrowBottom.gif" />"
					onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${memBean.componentModel.chooseAttributeTypeGroupComponentModel.position}" />, 'moveBottom')"
					class="link" 
					alt="<fmt:message key="tooltip.moveBottom" />"
					title="<fmt:message key="tooltip.moveBottom" />"
					id="<c:out value="${html_id}_moveBottom" />" />
			</div>
		</div>
	</div>
</div>

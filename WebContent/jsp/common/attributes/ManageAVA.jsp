<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="atPartPathAnchor" ignore="true"/>
<tiles:useAttribute name="writePermissionATG" />
<tiles:useAttribute name="extended_html_id"/>
<tiles:useAttribute name="overviewMode" ignore="true" />
<tiles:useAttribute name="fullPageTable" ignore="true" />
<tiles:useAttribute name="valueOnly" ignore="true" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>
<c:if test="${overviewMode == null}">
  <c:set var="overviewMode" value="false" />
</c:if>

<c:set var="tableStyle" value="attributeView" />
<c:if test="${fullPageTable}">
  <c:set var="tableStyle" value="attributeViewFullPage" />
</c:if>

	  <c:choose>
	    <c:when test="${atPart.attributeType.typeOfAttribute.name == 'attribute.type.enum'}">
	      <c:choose>
	        <c:when test="${atPart.attributeType.multiassignmenttype == true}">
	          <tiles:insertTemplate template="/jsp/common/attributes/EnumAV-Multi.jsp" flush="true">
	            <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	            <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	            <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	            <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	            <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	          </tiles:insertTemplate>
	        </c:when>
	        <c:otherwise>
	          <tiles:insertTemplate template="/jsp/common/attributes/EnumAV-Single.jsp" flush="true">
	            <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	            <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	            <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	            <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	            <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	          </tiles:insertTemplate>
	        </c:otherwise>
	      </c:choose>
	    </c:when>
	    <c:when test="${atPart.attributeType.typeOfAttribute.name == 'attribute.type.text'}">
	      <tiles:insertTemplate template="/jsp/common/attributes/TextAV.jsp" flush="true">
	        <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	        <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	        <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	        <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	        <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	      </tiles:insertTemplate>
	    </c:when>
	    <c:when test="${atPart.attributeType.typeOfAttribute.name == 'attribute.type.number'}">
	      <tiles:insertTemplate template="/jsp/common/attributes/NumberAV.jsp" flush="true">
	        <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	        <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	        <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	        <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	        <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	      </tiles:insertTemplate>
	    </c:when>
	    <c:when test="${atPart.attributeType.typeOfAttribute.name == 'attribute.type.date'}">
	      <tiles:insertTemplate template="/jsp/common/attributes/DateAV.jsp" flush="true">
	        <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	        <tiles:putAttribute name="atPartPathAnchor" value="${atPartPathAnchor}" />
	        <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	        <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	        <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	        <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	      </tiles:insertTemplate>
	    </c:when>
	    <c:when test="${atPart.attributeType.typeOfAttribute.name == 'attribute.type.responsibility'}">
	      <c:choose>
	        <c:when test="${atPart.attributeType.multiassignmenttype == true}">
	          <tiles:insertTemplate template="/jsp/common/attributes/ResponsibilityAV-Multi.jsp" flush="true">
	            <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	            <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	            <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	            <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	            <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	          </tiles:insertTemplate>
	        </c:when>
	        <c:otherwise>
	          <tiles:insertTemplate template="/jsp/common/attributes/ResponsibilityAV-Single.jsp" flush="true">
	            <tiles:putAttribute name="atPartPath" value="${atPartPath}" />
	            <tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
	            <tiles:putAttribute name="extended_html_id" value="${extended_html_id}_${atPart.attributeType.nameForHtmlId}" />
	            <tiles:putAttribute name="overviewMode" value="${overviewMode}" />
	            <tiles:putAttribute name="valueOnly" value="${valueOnly}" />
	          </tiles:insertTemplate>
	        </c:otherwise>
	      </c:choose>
	    </c:when>
	  </c:choose>
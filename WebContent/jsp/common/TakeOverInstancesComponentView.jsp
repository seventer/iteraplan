<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="master_checkbox_id" ignore="true" />

<itera:define id="source_instance" name="memBean" property="${path_to_componentModel}.sourceInstance" />
<itera:define id="connected_elements" name="memBean" property="${path_to_componentModel}.references" />
<itera:define id="message_key" name="memBean" property="${path_to_componentModel}.labelKey" />
<c:set var="boolean_field" value="${path_to_componentModel}.value" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
<itera:define id="column_header_keys" name="memBean" property="${path_to_componentModel}.columnHeaderKeys"/>
<c:set var="column_header_keys_size" value="${fn:length(column_header_keys)}" />
<itera:define id="connected_elements_fields" name="memBean" property="${path_to_componentModel}.connectedElementsFields"/>

<c:if test="${empty connected_elements}">
  <c:set var="emptyStyle" value="empty" />
</c:if>

<c:if test="${resultPostSelection == false}">
	<c:set var="resultPostSelection" value="true"/>
</c:if>

<%-- TODO: convert table into module --%>
<table class="elementComponentView table table-bordered table-striped table-condensed" id="<c:out value="${html_id}" />">
  <thead>
	  <tr>
	    <th colspan="<c:out value="${column_header_keys_size+1}"/>" 
	        class="<c:out value="${emptyStyle}"/>">
	       <fmt:message>
	      		<itera:write name="message_key" escapeXml="false" />
	      </fmt:message>
	    </th>
	  </tr>
  </thead>
  <tbody>
  <c:choose>
    <c:when test="${not empty connected_elements}">
      <tr class="subHeader">
        <c:if test="${resultPostSelection}">
		  <td class="col-ico">             
             <input type="checkbox" id="checkAllBox_${master_checkbox_id}" name="checkAllBox" value="" checked="checked"  onclick="
            	 checkUnCheckAll(document.getElementsByName('<c:out value="${path_to_componentModel}.selectedReferences"/>'), this);" />
             <%--  <fmt:message key="reports.selectAll" /> --%>
          </td>
		</c:if>
        <c:forEach items="${column_header_keys}" var="column_header_key" >
        	<td><fmt:message key="${column_header_key}" /></td>
        </c:forEach>      
      </tr>
	  <itera:define id="ref_array" name="memBean" property="${path_to_componentModel}.references"/>  
      <c:forEach items="${ref_array}" var="connected_element" varStatus="countStatus">
        <tr>
          <td class="col-ico">
            <form:checkbox value="${connected_element.id}" path="${path_to_componentModel}.selectedReferences"
            	id="${html_id}_checkbox_${countStatus.index}" onclick="unCheckCheckBox('checkAllBox_${master_checkbox_id}');" />
          </td>  
            <c:forEach items="${connected_elements_fields}" var="connected_elements_field">  
              <td>
                <c:choose>
                  <%-- This is a special implementation for the takeover of interfaces.--%>
                  <c:when test="${connected_elements_field == 'connectionToRelease'}">
                    <c:choose>
                      <c:when test="${connected_element.informationSystemReleaseB.id == source_instance.id}">
                        <itera:write name="connected_element" property="informationSystemReleaseA.releaseName" escapeXml="true" />
                      </c:when>
                      <c:otherwise>
                        <itera:write name="connected_element" property="informationSystemReleaseB.releaseName" escapeXml="true" />
                      </c:otherwise>
                    </c:choose>      
                  </c:when>
                  <%-- This is a special implementation for the takeover of interfaces. --%>
                   <c:when test="${connected_elements_field == 'name'}">
                   	  <itera:write name="connected_element" property="name" escapeXml="true" />
                   </c:when>
                   <c:when test="${connected_elements_field == 'direction'}">
                       <itera:write name="connected_element" property="direction" escapeXml="true" />
                   </c:when>
                  <c:when test="${connected_elements_field == 'transportedBusinessObjects'}">
                	<itera:define id="transports" name="connected_element" property="transportInformation" />   
                    <c:set var="size" value="${fn:length(transports)}" />
                    <c:forEach items="${transports}" var="transport" varStatus="countSt">  
                      <img  alt=""
                      	 	src="<c:url value="/images/${transport.misc['transportkey']}.gif"/>"  /> 
        				<itera:write name="transport" property="name" escapeXml="true" />
                      <c:if test="${countSt.index < (size-1)}">&nbsp;<br/></c:if>
                    </c:forEach>
                  </c:when>
                  <%-- This is a special implementation for the takeover of interfaces. --%>
                  <c:when test="${connected_elements_field == 'technicalRealisation'}"> 
                    <itera:define id="technicalComponentReleases" name="connected_element" property="technicalComponentReleasesSorted" />            
                    <c:set var="size" value="${fn:length(technicalComponentReleases)}" />
                    <c:forEach items="${technicalComponentReleases}" var="release" varStatus="cntStatus">  
                      <itera:write name="release" property="releaseName" escapeXml="true" />
                      <c:if test="${cntStatus.index < (size-1)}">&nbsp;<br /></c:if>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <itera:write name="connected_element" property="${connected_elements_field}" escapeXml="true" />
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
        </tr>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <tr>
        <td colspan="<c:out value="${column_header_keys_size + 1}"/>">&nbsp;</td>
      </tr>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>
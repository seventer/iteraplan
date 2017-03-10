<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<c:set var="html_id" value="${path_to_componentModel}.${html_id}"/>

<itera:define id="connectedElements" name="memBean" property="${path_to_componentModel}.connectedElements" />
<c:set var="remove_id_field" value="${path_to_componentModel}.elementIdToRemove"/>

<table class="coloredframe">
  <c:if test="${not empty connectedElements}">
    <input type="hidden" name="<c:out value="${remove_id_field}"/>" />
    
    <c:forEach items="${connectedElements}" var="transport">
      <c:set var="linkScript">
        <itera:linkToElement name="transport" type="js"/>
      </c:set>
      <c:set var="htmlLink">
        <itera:linkToElement name="transport" type="html" />
      </c:set>  
     
      <tr class="link">
        
        <td class="top" style="width: 15px;" align="left">
          <a class="link" href="#"
            title="<fmt:message key="tooltip.remove"/>" 
            onclick="setHiddenField('<c:out value="${remove_id_field}"/>','<c:out value="${transport.id}"/>');flowAction('update');" >
           <i class="icon-remove"></i>
          </a>
        </td>
		
        <td class="smallTableData top" align="center" onclick="<c:out value="${linkScript}" />">
		  <img src="<c:url value="/images/${transport.transportKey}.gif" />" />
		</td>
		
        <td class="nameintable top" onclick="<c:out value="${linkScript}" />">
          <itera:htmlLinkToElement isLinked="true" link="${htmlLink}">
			<c:out value="${transport.businessObject.name}" />
          </itera:htmlLinkToElement>
        </td>
        
        <td onclick="<c:out value="${linkScript}" />">&nbsp;</td>
        
      </tr>    
    </c:forEach>
  </c:if>
</table>

<table class="table table-bordered table-striped table-condensed">
  <tr>
    
    <td align="left" style="width: 15px;">
      <a class="link" href="#"
        title="<fmt:message key="tooltip.add"/>"
        onclick="flowAction('update');" >
       <i class="icon-plus"></i>
	  </a>
    </td>
    
    <td align="center" class="smallTableData">
      <itera:define id="transports" name="memBean" property="${path_to_componentModel}.availableTransportDirections" />
      <form:select path="${path_to_componentModel}.transportDirectionToAdd" id="${html_id}_selectDirection" 
       items="${transports}" itemLabel="name" itemValue="description" cssStyle="width: 6em;" />
    </td>
    
    <td class="dontwrap">
      <itera:define id="elementsForPres" name="memBean" property="${path_to_componentModel}.availableElementsPresentation" />
      <form:select path="${path_to_componentModel}.elementIdToAdd" cssClass="name" id="${html_id}_select"
        items="${elementsForPres}" itemLabel="businessObject.hierarchicalName" itemValue="id" />
      &nbsp;<fmt:message key="global.filter" />:&nbsp;
      <input 
        type="text"
        value=""
        class="filter"
        id="<c:out value="${htmlId}_filterField" />"
        onkeyup="catchTabKeyAndFilterList(this, event, this.value, '<c:out value="${html_id}"/>_select')" 
        onchange="filterList(this.value, '<c:out value="${html_id}"/>_select')" />
    </td>
    
  </tr>
</table>
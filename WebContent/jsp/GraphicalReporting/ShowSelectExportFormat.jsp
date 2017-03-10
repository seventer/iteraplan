<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="availableGraphicFormats" />
<tiles:useAttribute name="exportFormatPath" />

<div class="accordion" id="outputFormatContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#outputFormatContainer" href="#outputFormat"
					onclick="toggleIcon('outputFormatIcon', 'icon-resize-full', 'icon-resize-small');" >
			  	<i id="outputFormatIcon" class="icon-resize-full"></i>
                <fmt:message key="graphicalExport.resultFormat" />
            </a>
        </div>
    <div id="outputFormat" class="accordion-body collapse">
		<div class="accordion-inner">
		  <fmt:message key="graphicalExport.helpResultFormat" />
		  <br/>
	      <form:select path="${exportFormatPath}" cssStyle="width:200px;" onchange="${refreshReportJavaScript}">
					<c:forEach var="format" items="${availableGraphicFormats}">
					  <c:if test="${format.visible}">
					    <form:option value="${format.presentationKey}">
					      <fmt:message key="${format.presentationKey}" />
					    </form:option>
					  </c:if>
					</c:forEach>
	      </form:select>
		</div>
     </div>
    </div>
 </div>
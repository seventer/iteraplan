<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<tiles:useAttribute name="overviewList" />

<div class="row-fluid">
	<div id="OverViewItemContainer" class="row-fluid module">
		<div class="module-heading">
		    <a href="<c:url value="/${overviewList.htmlId}/init.do"/>">
				<span class="headerIcon icon_${overviewList.htmlId}"> </span>
				<fmt:message key="${overviewList.headerKey}" /> 
				<c:if test="${overviewList.htmlId eq 'informationsystem'}">
					<sup> [1]</sup>
				</c:if>
				<c:if test="${overviewList.htmlId eq 'technicalcomponent'}">
					<sup> [2]</sup>
				</c:if>
			</a>
			<div style="float: right; margin-right: 10px; font-weight: normal;">
				<fmt:message key="global.total" />
				<c:out value=": ${overviewList.totalNumberOfElements}"/>
			</div>
		</div>
		<div class="row-fluid">
			<div class="module-body-table overviewBody" id="overviewList_${overviewList.headerKey}">
				<div class="row-fluid">
					<table width="100%" class="table-striped table-condensed tableInModule">
						<tbody>
							<c:forEach items="${overviewList.elements}" var="element" varStatus="elCount">
								<c:set var="linkScript">
									<itera:linkToElement name="element" type="js" />
								</c:set>
								<c:set var="link">
									<itera:linkToElement name="element" type="html" />
								</c:set>
				
								<tr class="link <c:out value="${color}"/>">
									<td onclick="<c:out value="${linkScript}" />"><itera:htmlLinkToElement
											link="${link}" isLinked="true">
											<c:out value="${element.name}" />
										</itera:htmlLinkToElement>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
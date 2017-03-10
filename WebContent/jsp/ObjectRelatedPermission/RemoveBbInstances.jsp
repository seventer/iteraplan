<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- If set to true, the table rows are linked to the corresponding element's 
management page. Defaults to true. --%>
<tiles:useAttribute name="isLinked" ignore="true"/>

<%-- Determine, if table rows should be linked. --%>
<c:set var="linkShow" value="true" />
<c:set var="linkStyle" value="link" />
<c:if test="${not empty isLinked}">
  <c:if test="${!isLinked}">
    <c:set var="linkShow" value="false" />
    <c:set var="linkStyle" value="" />
  </c:if>
</c:if>

<input type="hidden" name="selectedBuildingBlockIdToRemove" value="" />

<div id="RemoveBbInstancesModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="objectRelatedPermissions.hasExplicitPermissions"/>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<thead>
						<c:if test="${not empty memBean.dto.associatedBuildingBlocks}">
							<tr>
								<c:if test="${componentMode == 'EDIT'}">
									<th>&nbsp;</th>
								</c:if>
								<th>
									<fmt:message key="global.bbtype"/>
								</th>
								<th>
									<fmt:message key="global.name"/>
								</th>
								<td>&nbsp;</td>
							</tr>
						</c:if>
						<c:if test="${empty memBean.dto.associatedBuildingBlocks}">
							<tr>
								<th colspan="4">&nbsp;</th>
							</tr>
						</c:if>
					</thead>
					<tbody>
						<c:forEach items="${memBean.dto.associatedBuildingBlocks}" var="bb">
							<%-- Store the onClick()-Handler if the elements should be linked --%>
							<c:set var="linkScript" value="" />
							<c:if test="${linkShow}">
								<c:set var="linkScript"><itera:linkToElement name="bb" type="js"/></c:set>
								<c:set var="link"><itera:linkToElement name="bb" type="html" /></c:set>
							</c:if>
							
							<tr>
								<c:if test="${componentMode == 'EDIT'}">
									<td align="left" class="buttonintable top">
										<a class="link" href="#" title="<fmt:message key="tooltip.remove"/>" onclick="setHiddenField('selectedBuildingBlockIdToRemove','<c:out value="${bb.id}"/>');flowAction('removeBBP');" >
											<i class="icon-remove"></i>
										</a>
									</td>
								</c:if>
								<td class="<c:out value="${linkStyle}" /> completereleaseintablelong top" onclick="<c:out value="${linkScript}" />" nowrap="nowrap">
									<itera:htmlLinkToElement isLinked="${linkShow}" link="${link}">
										<fmt:message key="${bb.buildingBlockType.typeOfBuildingBlock.value}" />
									</itera:htmlLinkToElement>
								</td>
								<td class="<c:out value="${linkStyle}" /> completereleaseintablelong top" onclick="<c:out value="${linkScript}" />" nowrap="nowrap">
									<itera:htmlLinkToElement isLinked="${linkShow}" link="${link}">
										<c:out value="${bb.identityString}" />
									</itera:htmlLinkToElement>
								</td>
								<td class="<c:out value="${linkStyle} ${linkScript}" />" >&nbsp;</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
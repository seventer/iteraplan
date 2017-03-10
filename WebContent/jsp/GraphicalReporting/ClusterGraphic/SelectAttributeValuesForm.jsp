<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="flowAction" />

<p class="aligned"><fmt:message	key="graphicalExport.cluster.helpContentSelectAttribute" /></p>

<form:select path="graphicalOptions.selectedAttributeType"
	onchange="flowAction('selectClusterType');"
	items="${memBean.graphicalOptions.availableAttributeTypes}"
	itemLabel="name" itemValue="id" />
<input type="button"
	value="<fmt:message key="global.confirmSelection" />"
	id="buttonConfirmSelection" class="link btn"
	onclick="resetScrollCoordinates(); flowAction('<c:out value="${flowAction}"></c:out>');" />

<br />
<br />
<br />

<div id="SelectAttributeValuesTableModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="global.attributevalues" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-ico" />
					</colgroup>
					<thead>
						<tr>
							<th>
								<form:checkbox path="checkAllBox" id="checkAllBox" value="" onclick="checkUnCheckAll(document.getElementsByName('graphicalOptions.selectedAttributeValues'), this);" />
							</th>
							<th><fmt:message key="reports.selectAll" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${memBean.graphicalOptions.colorOptionsBean.attributeValues}" var="value" varStatus="status">
							<tr>
								<td class="checkboxintable" nowrap="nowrap" align="center" valign="top" id="SelectAv_cell_<c:out value='${status.index}' />">
									<form:checkbox path="graphicalOptions.selectedAttributeValues" value="${value}" onclick="updateCheckAllBox(document.getElementsByName('graphicalOptions.selectedAttributeValues'), document.getElementById('checkAllBox'));" />
								</td>
								<td>
									<c:choose>
						      			<c:when test="${value == 'graphicalReport.unspecified'}">
						        			<fmt:message key="graphicalReport.unspecified" />
						      			</c:when>
							      		<c:otherwise>
											<c:out value="${value}" />
							      		</c:otherwise>
						    		</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script type="text/javascript">
	function isPartialExport() {
		if (document.getElementById('partialCheckbox').checked == false) {
			document.getElementById('partialBuildingBlockTypes').style.visibility = "hidden";
			document.getElementById('downloadTemplateExcel2003')
					.removeAttribute("disabled");
			document.getElementById('downloadTemplateExcel2007')
					.removeAttribute("disabled");
			document.getElementById('downloadEcore')
					.removeAttribute("disabled");
			document.getElementById('downloadFullModelExcel2003')
					.removeAttribute("disabled");
			document.getElementById('downloadBundle').removeAttribute(
					"disabled");
			document.getElementById('downloadTemplateExcel2003')
					.removeAttribute("onclick");
			document.getElementById('downloadTemplateExcel2007')
					.removeAttribute("onclick");
			document.getElementById('downloadEcore').removeAttribute("onclick");
			document.getElementById('downloadFullModelExcel2003')
					.removeAttribute("onclick");
			document.getElementById('downloadBundle')
					.removeAttribute("onclick");
		} else {
			document.getElementById('partialBuildingBlockTypes').style.visibility = "visible";
			document.getElementById('downloadTemplateExcel2003').setAttribute(
					"disabled", "disabled");
			document.getElementById('downloadTemplateExcel2007').setAttribute(
					"disabled", "disabled");
			document.getElementById('downloadEcore').setAttribute("disabled",
					"disabled");
			document.getElementById('downloadFullModelExcel2003').setAttribute(
					"disabled", "disabled");
			document.getElementById('downloadBundle').setAttribute("disabled",
					"disabled");
			document.getElementById('downloadTemplateExcel2003').setAttribute(
					"onclick", "return false;");
			document.getElementById('downloadTemplateExcel2007').setAttribute(
					"onclick", "return false;");
			document.getElementById('downloadEcore').setAttribute("onclick",
					"return false;");
			document.getElementById('downloadFullModelExcel2003').setAttribute(
					"onclick", "return false;");
			document.getElementById('downloadBundle').setAttribute("onclick",
					"return false;");
		}
	}
</script>

<c:set var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermExcelImport}" scope="request" />

<c:choose>
	<c:when test="${functionalPermissionImport == true}">
		<c:if test="${not empty memBean.resultMessages.errors}">
			<c:set var="errorsHeader">
				<fmt:message key="errors.header" />
			</c:set>
			<tiles:insertTemplate template="SingleMessagesTile.jsp">
				<tiles:putAttribute name="messagesHeader" value="${errorsHeader}" />
				<tiles:putAttribute name="messageList"
					value="${memBean.resultMessages.errors}" />
				<tiles:putAttribute name="messagesId" value="errors" />
			</tiles:insertTemplate>
		</c:if>

		<div id="uploadRunningMessage" class="alert alert-info hide">
			<button class="close" data-dismiss="alert" type="button">×</button>
			<fmt:message key="global.upload.running" />
		</div>

		<div id="excelDownloadModule" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.export.button.excel_data" />
			</div>
			<div class="module-body">
				<table>
					<thead>
						<tr>
							<th></th>
							<th><fmt:message key="global.export.excel.2007" /></th>
							<th><fmt:message key="global.export.excel.2003" /></th>
							<th><fmt:message key="global.export.xmi" /></th>
						</tr>
					</thead>

					<tbody>
						<tr>
							<th><fmt:message key="global.export.metamodel" /></th>
							<td>
								<div class="module-body">
									<a id="downloadTemplateExcel2007"
										href="javascript:flowAction('requestTemplateExcel2007');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.excel_template" />
									</a>
								</div>

							</td>
							<td>
								<div class="module-body">
									<a id="downloadTemplateExcel2003"
										href="javascript:flowAction('requestTemplateExcel2003');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.excel_template" />
									</a>
								</div>

							</td>
							<td>
								<div class="module-body">
									<a id="downloadEcore"
										href="javascript:flowAction('requestEcore');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.ecore" />
									</a>
								</div>
							</td>
						</tr>
						<tr>
							<th><fmt:message key="global.export.model" /></th>
							<td>
								<div class="module-body">
									<a id="downloadFullModelExcel2007"
										href="javascript:flowAction('requestFullModelExcel2007');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.excel_data" />
									</a>
								</div>
							</td>
							<td>
								<div class="module-body">
									<a id="downloadFullModelExcel2003"
										href="javascript:flowAction('requestFullModelExcel2003');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.excel_data" />
									</a>
								</div>
							</td>
							<td>
								<div class="module-body">
									<a id="downloadBundle"
										href="javascript:flowAction('requestBundle');"
										class="link btn" style="width: 212px"> <i
										class="icon-download-alt"></i> <fmt:message
											key="global.export.button.zip" />
									</a>
								</div>
							</td>
						</tr>
						<tr>

							<!-- Message Format hier -->
							<th><fmt:message key="global.excel.partial" />:</th>
							<th colspan="3"><form:checkbox path="partialExport"
									id="partialCheckbox" onchange="isPartialExport()"
									cssStyle="margin-right 10px" /> <span
								id="partialBuildingBlockTypes" style="visibility: hidden;">
									<fmt:message key="global.excel.partial.filter" />: <form:select
										path="filteredTypeExport">
										<c:forEach items="${memBean.typeOfBuildingBlocksForPartial}"
											var="buildingBlock">
											<form:option value="${buildingBlock}">
												<fmt:message key="${buildingBlock.value}" />
											</form:option>
										</c:forEach>
									</form:select> &#91;<form:input style="width: 480px"
										path="extendedFilterExport" type="text" />&#93;;
							</span></th>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(isPartialExport());
		</script>

		<%-- Start the file download, after the page has been loaded. --%>
		<c:if test="${not empty triggerDownloadEvent}">
			<script type="text/javascript">
				$(document).ready(function() {
					flowAction('${triggerDownloadEvent}');
				});
			</script>
		</c:if>

		<div id="excelUploadModule" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.import.heading" />
			</div>
			<div class="module-body">
				<table>
					<tr>
						<td>
							<div style="padding: 9px 0px">
								<select name="importStrategy" style="width: 88%">
									<c:forEach var="strategy" items="${memBean.importStrategies}"
										varStatus="count">
										<c:choose>
											<c:when test="${count.index==0}">
												<option value="${strategy}" selected="selected">${strategy.text}</option>
											</c:when>
											<c:otherwise>
												<option value="${strategy}">${strategy.text}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
								<tiles:insertTemplate template="ImportStrategyDescription.jsp" />
							</div>
						</td>
						<td>
							<div style="padding: 9px 0px">
								<label class="checkbox" for="importMetamodel"><fmt:message
										key="global.import.label.includeMMChanges" /> <form:checkbox
										id="importMetamodel" name="metamodelImport"
										path="importMetamodel" /></label>
							</div>
						</td>
					</tr>
					<tr>
						<td valign="bottom">
							<div style="padding: 9px 0px">
								<input type="file" id="fileUpload" name="fileToUpload"
									size="100%" />
							</div>
						</td>
						<td valign="bottom" style="height: 20px">
							<div style="padding: 9px 0px">
								<a
									href="javascript:$('#uploadRunningMessage').toggleClass('hide');$('#downloadStartedMessage').toggleClass('hide');flowAction('upload');"
									class="link btn" style="width: 212px"> <i
									class="icon-upload-alt"></i> <fmt:message key="button.upload" />
								</a>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<c:if test="${guiContext.timeseriesEnabled == true}">
			<div id="timeseriesDownloadModule" class="row-fluid module">
				<div class="module-heading">
					<fmt:message key="excel.export.timeseries.header" />
				</div>
				<div class="module-body">
					<table>
						<thead>
							<tr>
								<th></th>
								<th><fmt:message key="global.export.excel.2007" /></th>
								<th><fmt:message key="global.export.excel.2003" /></th>
							</tr>
						</thead>

						<tbody>
							<tr>
								<th><fmt:message key="global.export.metamodel" /></th>
								<td>
									<div class="module-body">
										<a id="downloadTimeseriesTemplateExcel2007"
											href="javascript:flowAction('requestTimeseriesTemplateExcel2007');"
											class="link btn" style="width: 212px"> <i
											class="icon-download-alt"></i> <fmt:message
												key="global.export.button.excel_template" />
										</a>
									</div>

								</td>
								<td>
									<div class="module-body">
										<a id="downloadTimeseriesTemplateExcel2003"
											href="javascript:flowAction('requestTimeseriesTemplateExcel2003');"
											class="link btn" style="width: 212px"> <i
											class="icon-download-alt"></i> <fmt:message
												key="global.export.button.excel_template" />
										</a>
									</div>

								</td>
							</tr>
							<tr>
								<th><fmt:message key="global.export.model" /></th>
								<td>
									<div class="module-body">
										<a id="downloadTimeseriesDataExcel2007"
											href="javascript:flowAction('requestTimeseriesDataExcel2007');"
											class="link btn" style="width: 212px"> <i
											class="icon-download-alt"></i> <fmt:message
												key="global.export.button.excel_data" />
										</a>
									</div>
								</td>
								<td>
									<div class="module-body">
										<a id="downloadTimeseriesDataExcel2003"
											href="javascript:flowAction('requestTimeseriesDataExcel2003');"
											class="link btn" style="width: 212px"> <i
											class="icon-download-alt"></i> <fmt:message
												key="global.export.button.excel_data" />
										</a>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>

			<div id="excelTimeseriesUploadModule" class="row-fluid module">
				<div class="module-heading">
					<fmt:message key="excel.import.timeseries.header" />
				</div>
				<div class="module-body">
					<input type="file" id="timeseriesFileUpload"
						name="timeseriesFileToUpload" size="100%" /> <a
						id="timeseriesUploadButton"
						href="javascript:flowAction('uploadTimeseries');" class="link btn"
						style="width: 212px"> <i class="icon-upload-alt"></i> <fmt:message
							key="button.upload" />
					</a>
				</div>
			</div>
		</c:if>

	</c:when>

	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>

</c:choose>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermExcelImport}" scope="request" />

<c:choose>
	<c:when test="${functionalPermissionImport == true}">
		<div class="alert">
			<a class="close" data-dismiss="alert">×</a>
			<fmt:message key="global.excel.known_issues" />
			<ul>
				<li><fmt:message key="global.excel.known_issues.templates_customization" /></li>
				<li><fmt:message key="global.excel.known_issues.cycles_stop_import" /></li>
				<li><fmt:message key="global.excel.limited_excel_macros" /></li>
			</ul>
		</div>
		
		<div id="ExcelDataImportModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.excel_data_import" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<colgroup>
								<col class="col-ico" />
								<col class="col-dec" />
							</colgroup>
							<tbody>
								<tr>
									<td style="width: 18em"></td>
									<c:choose>
										<c:when test="${(dialogMemory.wrongFileType == true)}">
											<td style="color: red"><fmt:message key="global.excel.data_import_description" /></td>							
										</c:when>
										<c:otherwise>
											<td><fmt:message key="global.excel.data_import_description" /></td>
										</c:otherwise>
									</c:choose>	
								</tr>
								<tr>
									<td>
										<input type="submit" style="width: 18em" class="btn"
											value="<fmt:message key="button.excel.data_import"/>"
											onclick="createHiddenField('clickedButton', '<c:out value="button.excel.data_import"/>');" />
									</td>
									<td><input name="excelFile" type="file" size="100%" /></td>
								</tr>
								<tr>
									<td>
										<a  href="javascript:createHiddenField('clickedButton', '<c:out value="button.excel.download_data_temp"/>');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.excel.download_data_temp"/>
										</a> 
									</td>
									<td><fmt:message key="global.excel.excel_data_temp_download_descr" /></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<%-- Feature not yet implemented; deactivate the UI for the moment! --%>
		<div id="ExcelAttributeImportModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.excel_attribute_import" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<colgroup>
								<col class="col-ico" />
								<col class="col-dec" />
							</colgroup>
							<tbody>
								<tr>
									<td style="width: 18em"></td>
									<c:choose>
										<c:when test="${(dialogMemory.wrongEnumFileType == true)}">
											<td style="color: red"><fmt:message key="global.excel.enum_import_description" /></td>							
										</c:when>
										<c:otherwise>
											<td><fmt:message key="global.excel.enum_import_description" /></td>
										</c:otherwise>
									</c:choose>	
								</tr>
								<tr>
									<td>
										<input type="submit" style="width: 18em" class="btn"
											value="<fmt:message key="button.excel.enum_import"/>"
											onclick="createHiddenField('clickedButton', '<c:out value="button.excel.enum_import"/>');" />
									</td>
									<td><input name="excelEnumFile" type="file" size="100%" /></td>
								</tr>
								<tr>
									<td>
										<a  href="javascript:createHiddenField('clickedButton', '<c:out value="button.excel.download_enum_temp"/>');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.excel.download_enum_temp"/>
										</a> 
									</td>
									<td><fmt:message key="global.excel.excel_enum_temp_download_descr" /></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

        <div id="ExcelObjrelpermImportModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="global.excel_objrelperm_import" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<colgroup>
								<col class="col-ico" />
								<col class="col-dec" />
							</colgroup>
							<tbody>
								<tr>
									<td style="width: 18em"></td>
									<c:choose>
										<c:when test="${(dialogMemory.wrongUserFileType == true)}">
											<td style="color: red"><fmt:message key="global.excel.objrelperm_import_description" /></td>							
										</c:when>
										<c:otherwise>
											<td><fmt:message key="global.excel.objrelperm_import_description" /></td>
										</c:otherwise>
									</c:choose>	
								</tr>
								<tr>
									<td>
										<input type="submit" style="width: 18em" class="btn"
											value="<fmt:message key="button.excel.objrelperm_import"/>"
											onclick="createHiddenField('clickedButton', '<c:out value="button.excel.objrelperm_import"/>');" />
									</td>
									<td><input name="excelUserFile" type="file" size="100%" /></td>
								</tr>
								<tr>
									<td>
										<a  href="javascript:createHiddenField('clickedButton', '<c:out value="button.excel.download_objrelperm_temp"/>');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.excel.download_objrelperm_temp"/>
										</a> 
									</td>
									<td><fmt:message key="global.excel.excel_objrelperm_temp_download_descr" /></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
        
   		<c:if test="${not empty dialogMemory.importLog}">
          <pre style="font-family:Fixedsys,Courier,monospace; padding:10px; border-color: black">
<c:out value="${dialogMemory.importLog}" />
          </pre>
   		</c:if>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>

</c:choose>
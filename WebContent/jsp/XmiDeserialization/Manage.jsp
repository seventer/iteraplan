<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="functionalPermissionExport"
	value="${userContext.perms.userHasFuncPermXmiSerialization}" scope="request" />
<c:set var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermXmiDeserialization}" scope="request" />

<c:choose>
	<c:when test="${functionalPermissionExport == true}">
		<div id="XMIExportModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="xmi.serialization" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<tbody>
								<tr>
									<td class="col-ico">
										<a  id="export_xmi_zip"
											href="javascript:createHiddenField('clickedButton', 'button.export.xmi.zip');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.export.xmi.zip" />
										</a> 
									</td>
									<td><fmt:message key="xmi.zip.description" /></td>
								</tr>
								<tr>
									<td>
										<a  id="export_xmi"
											href="javascript:createHiddenField('clickedButton', 'button.export.xmi');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.export.xmi" />
										</a> 
									</td>
									<td><fmt:message key="xmi.serialization.description" /></td>
								</tr>	
								<tr>
									<td>
										<a  id="export_xmi_extended"
											href="javascript:createHiddenField('clickedButton', 'button.export.xmi.extended');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.export.xmi.extended" />
										</a> 
									</td>
									<td><fmt:message key="xmi.serialization.extended.description" /></td>
								</tr>
								<tr>
									<td>
										<a  id="export_ecore"
											href="javascript:createHiddenField('clickedButton', 'button.export.ecore');document.forms[0].submit();"
											class="link btn"
											style="width: 212px" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.export.ecore" />
										</a> 
									</td>				
									<td><fmt:message key="xmi.ecore.description" /></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

		<div id="XMIExportSpreadsheetModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="xmi.exportForTabularReporting" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<tbody>
								<tr>
									<td class="col-ico">
										<a  id="export_ecore_modified"
											href="javascript:createHiddenField('clickedButton', 'button.export.ecore.modified');document.forms[0].submit();"
											class="link btn"
											style="width: 234px; padding-left: 0px; padding-right: 0px;" >
											<i class="icon-download-alt"></i>
				 							<fmt:message key="button.export.ecore.modified" />
										</a> 
									</td>
									<td><fmt:message key="xmi.ecore.modified.description" /></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

		<c:choose>
			<c:when test="${functionalPermissionImport == true}">
				<div id="XMIImortModul" class="row-fluid module">
					<div class="module-heading">
						<fmt:message key="global.xmi_import" />
					</div>
					<div class="row-fluid">
						<div class="module-body-table">
							<div class="row-fluid">
								<table class="table table-striped table-condensed tableInModule">
									<tbody>
										<tr>
											<td class="col-ico"></td>
											<c:choose>
												<c:when test="${(dialogMemory.xmiFileNull == true)}">
													<td>
														<div class="alert">
															<a class="close" data-dismiss="alert">×</a>
															<fmt:message key="global.xmi.import_description" />
														</div>
													</td>							
												</c:when>
												<c:when test="${(dialogMemory.wrongFileType == true)}">
													<td>
														<div class="alert">
															<a class="close" data-dismiss="alert">×</a>
															<fmt:message key="global.xmi.wrong_filetype" />
														</div>
													</td>							
												</c:when>								
												<c:otherwise>
													<td><fmt:message key="global.xmi.import_description" /></td>
												</c:otherwise>
											</c:choose>
										</tr>
										<tr>
											<td>
												<input type="submit" style="width: 18em" class="btn"
													value="<fmt:message key="button.import.xmi"/>"
													onclick="$('#conflicts').css('visibility', 'hidden');createHiddenField('clickedButton', 'button.import.xmi');" />
											</td>
											<td><input name="xmiFile" type="file" size="100%" /></td>
										</tr>
										<%-- XMI restore is still work in progress -- hide it from normal users
				                        <tr>
											<td>
												<input type="submit" style="width: 18em"
													value="<fmt:message key="button.import.xmi_restore"/>"
													onclick="createHiddenField('clickedButton', '<c:out value="button.import.xmi_restore"/>');" />
											</td>
											<td><fmt:message key="global.xmi.restore_description" /></td>
										</tr>  --%>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
        		<c:choose>
        			<c:when test="${!(dialogMemory.conflicts == null)}">
        				<tiles:insertTemplate template="/jsp/XmiDeserialization/Conflicts.jsp"></tiles:insertTemplate>
        			</c:when>
        			<c:otherwise>
        				<div id="conflicts"></div>
        			</c:otherwise>
        		</c:choose>
        		<c:if test="${dialogMemory.importSuccessful}">
        			<div class="alert alert-success">
	        			<a class="close" data-dismiss="alert">×</a>
	        			<fmt:message key="xmi.import.successful" />
        			</div>
        		</c:if>
			</c:when>
			<c:otherwise>
				<div class="alert alert-error">
				<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
				<span><fmt:message key="errors.noXmiDeserializationPermission" /></span></div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>
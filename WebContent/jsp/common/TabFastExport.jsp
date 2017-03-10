<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="buildingBlockId" />
<tiles:useAttribute name="buildingBlockType" />

<c:set var="fastExportOutput" value="componentModel.fastExportBean.outputMode" />

<fmt:setLocale value="${userContext.locale.language}" scope="request" />

<c:set var="userAgent" value="${header['User-Agent']}" scope="session" />

<c:set var="displayId" value="displayGraphicFrame" />
<c:set var="displayLoadingId" value="displayGraphicFrameLoading" />

<%-- default values for browsers which support SVG natively --%>
<c:set var="resultFormat" value="Svg" />
<%-- url is written into attribute 'data' of object --%>
<c:set var="urlAttribute" value="data" />

<%-- || fn:contains(userAgent, 'Chrome') || fn:contains(userAgent, 'Safari') --%>
<c:if test="${fn:contains(userAgent, 'MSIE') || fn:contains(userAgent, 'Opera') }">
	<%-- MSIE-specific values - fall back to image since there is no SVG support yet --%>
	<c:set var="resultFormat" value="Png" />
	<%-- url is written into attribute 'src' of image --%>
	<c:set var="urlAttribute" value="src" />
</c:if>

<c:set var="selectText"><fmt:message key='global.fastExport.select' /></c:set>

<c:set var="downloadText"><fmt:message key='global.download' /></c:set>

<fmt:message var="previewLabel" key="global.preview"/>

<script type="text/javascript">
	// <![CDATA[
   			var params = {
					baseUrl: "<c:url value='/'/>",
					buildingBlockType: '${buildingBlockType}',
					displayId: "${displayId}",
					displayLoadingId: "${displayLoadingId}",
					buildingBlockType: "${buildingBlockType}",
					urlAttribute: "${urlAttribute}",
					buildingBlockId: "${buildingBlockId}",
					resultFormat: "${resultFormat}",
					selectText: "${selectText}",
					previewHeaderElement: 'previewHeader',
					previewLabel: "${previewLabel} - ",
					noContentMessage: "<fmt:message key="global.fastExport.noContent" />"
			};
			window.variables = new variables(params);
    // ]]>
</script>

<div id="tabFastExportModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="global.fastExport" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<div class="control-group">
					<label class="control-label " for="${html_id}_checkbox">
				  		<fmt:message key="global.selection" />: 
				  	</label>
					<div class="controls visibleRows">
						<div class="btn-group" style="margin-bottom: 7px; margin-top: -7px;">
							<a id="graphSelectionBtn" class="btn dropdown-toggle" data-toggle="dropdown" href="#">
								<fmt:message key='global.content' />:
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu">
								<fmt:message var="lds_IS_BU_lbl" key="landscapeManagementDomain.IS_BusinessUnit" />
								<fmt:message var="masterplanDiagram_lbl" key='graphicalExport.masterplanDiagram'/>
								<fmt:message var="neighborhoodDiagram_lbl" key='graphicalExport.neighborhoodDiagram'/>
								<fmt:message var="informationFlowDiagram_lbl" key='graphicalExport.informationFlowDiagram'/>
								<fmt:message var="lds_IS_BP_lbl" key='landscapeManagementDomain.IS_BusinessProcess'/>
								<fmt:message var="lds_IS_Prod_lbl" key='landscapeManagementDomain.IS_Product'/>
								<fmt:message var="lds_IS_Proj_lbl" key='landscapeManagementDomain.IS_Project'/>
								<fmt:message var="lds_IS_BF_lbl" key='landscapeManagementDomain.IS_BusinessFunction'/>
								<fmt:message var="lds_TBB_IS_AD_lbl" key='landscapeManagementDomain.TBB_IS_AD'/>
								<c:set var="masterplan_hierarchy"><fmt:message key='global.predecessors'/>&nbsp;<fmt:message key='global.and'/>&nbsp;<fmt:message key='global.successors'/></c:set>
								<fmt:message var="masterplan_proj" key='global.projects'/>
								<fmt:message var="masterplan_TC" key='technicalComponent.plural'/>
								
								<c:choose>
									<c:when test="${buildingBlockType == 'Project'}">
										<li class="dropdown">	
											<a href="javascript:clickMasterplanProjects('${itera:escapeJavaScript(masterplanDiagram_lbl)}');" class="navLink" >
												${masterplanDiagram_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickInformationFlow('${itera:escapeJavaScript(informationFlowDiagram_lbl)}');" class="navLink" >
												${informationFlowDiagram_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessUnits('${itera:escapeJavaScript(lds_IS_BU_lbl)}');" class="navLink" >
												${lds_IS_BU_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'Product'}">
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessProcesses('${itera:escapeJavaScript(lds_IS_BP_lbl)}');" class="navLink" >
												${lds_IS_BP_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessUnits('${itera:escapeJavaScript(lds_IS_BU_lbl)}');" class="navLink" >
												${lds_IS_BU_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'BusinessUnit'}">
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessProcesses('${itera:escapeJavaScript(lds_IS_BP_lbl)}');" class="navLink" >
												${lds_IS_BP_lbl}
											</a>
										</li>
										<li class="dropdown">
											
											<a href="javascript:clickBusinessLandscapeByProducts('${itera:escapeJavaScript(lds_IS_Prod_lbl)}');" class="navLink" >
												${lds_IS_Prod_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByProjects('${itera:escapeJavaScript(lds_IS_Proj_lbl)}');" class="navLink" >
												${lds_IS_Proj_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'BusinessProcess'}">
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessUnits('${itera:escapeJavaScript(lds_IS_BU_lbl)}');" class="navLink" >
												${lds_IS_BU_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickBusinessLandscapeByBusinessFunctions('${itera:escapeJavaScript(lds_IS_BF_lbl)}');" class="navLink" >
												${lds_IS_BF_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'TechnicalComponent'}">
										<li class="dropdown">
											<a href="javascript:clickMasterplanTechnicalComponents('${itera:escapeJavaScript(masterplanDiagram_lbl)}');" class="navLink" >
												${masterplanDiagram_lbl}
											</a>
										</li>
										<li class="dropdown">
											
											<a href="javascript:clickTechnicalLandscape('${itera:escapeJavaScript(lds_TBB_IS_AD_lbl)}');" class="navLink" >
												${lds_TBB_IS_AD_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'InformationSystem'}">
										<li class="dropdown">
											<a href="javascript:clickInformationFlow('${itera:escapeJavaScript(informationFlowDiagram_lbl)}');" class="navLink" >
												${informationFlowDiagram_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="javascript:clickNeighborhood('${itera:escapeJavaScript(neighborhoodDiagram_lbl)}');" class="navLink" >
												${neighborhoodDiagram_lbl}
											</a>
										</li>
										<li class="dropdown">
											<a href="#" class="navLink" >
												<fmt:message key='graphicalExport.landscapeDiagram'/>
											</a>
											<ul class="dropdown-menu" id="is_landscape">
												<li><a href="javascript:clickBusinessLandscapeByBusinessFunctions('${itera:escapeJavaScript(lds_IS_BF_lbl)}');">${lds_IS_BF_lbl}</a></li>
												<li><a href="javascript:clickBusinessLandscapeByProducts('${itera:escapeJavaScript(lds_IS_Prod_lbl)}');">${lds_IS_Prod_lbl}</a></li>
												<li><a href="javascript:clickBusinessLandscapeByBusinessUnits('${itera:escapeJavaScript(lds_IS_BU_lbl)}');">${lds_IS_BU_lbl}</a></li>
												<li><a href="javascript:clickBusinessLandscapeByProjects('${itera:escapeJavaScript(lds_IS_Proj_lbl)}');">${lds_IS_Proj_lbl}</a></li>
												<li><a href="javascript:clickTechnicalLandscape('${itera:escapeJavaScript(lds_TBB_IS_AD_lbl)}');">${lds_TBB_IS_AD_lbl}</a></li>
											</ul>
										</li>
										<li class="dropdown">
											<a href="#" class="navLink" >
												<fmt:message key='graphicalExport.masterplanDiagram'/>
											</a>
											<ul class="dropdown-menu" id="is_masterplan">
												<li><a href="javascript:clickMasterplanHierarchy('${itera:escapeJavaScript(masterplan_hierarchy)}');">${masterplan_hierarchy}</a></li>
												<li><a href="javascript:clickMasterplanProjects('${itera:escapeJavaScript(masterplan_proj)}');">${masterplan_proj}</a></li>
												<li><a href="javascript:clickMasterplanTechnicalComponents('${itera:escapeJavaScript(masterplan_TC)}');">${masterplan_TC}</a></li>
											</ul>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'InformationSystemDomain'}">
										<li class="dropdown">
											<a href="javascript:clickInformationFlow('${itera:escapeJavaScript(informationFlowDiagram_lbl)}');" class="navLink" >
												${informationFlowDiagram_lbl}
											</a>
										</li>
									</c:when>
									<c:when test="${buildingBlockType == 'InfrastructureElement'}">
										<li class="dropdown">
											<a href="javascript:clickTechnicalLandscape('${itera:escapeJavaScript(lds_TBB_IS_AD_lbl)}');" class="navLink" >
												${lds_TBB_IS_AD_lbl}
											</a>
										</li>
									</c:when>
									<c:otherwise>
										<li class="dropdown">	
											Error: No FastExport Menu for <c:out value="${buildingBlockType}"/>
										</li>
									</c:otherwise>
								</c:choose>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div id="PrevieModule" class="row-fluid inner-module">
				<div id="previewHeader" class="inner-module-heading">
					<fmt:message key='global.preview' />
				</div>
				<div class="row-fluid">
					<div class="inner-module-body-table">
						<div class="row-fluid">
							<table class="table table-striped table-condensed tableInModule">
								<tr>
									<td>
										<span id="resultFormatSpanDownload" style="visibility: hidden;">
											Download Format: 
											<select id="resultFormatDropdown">
												<option value="Visio"><fmt:message key='reports_export_Visio' /></option>
												<option value="Svg" selected="selected"><fmt:message key='reports_export_SVG' /></option>
												<option value="Jpeg"><fmt:message key='reports_export_JPEG' /></option>
												<option value="Png"><fmt:message key='reports_export_PNG' /></option>
												<option value="Pdf"><fmt:message key='reports_export_PDF' /></option>
											</select> 
											&gt;  &gt;	            
											<a id="downloadFastExport" class="btn" style="visibility: hidden;"
												onclick="redirectToUrlFromDisplayedGraphic();" >
												<i class="icon-download-alt"></i>
												<fmt:message key='global.download' />
											</a>
										</span>
									
									
										<div id="fastExportButtonBookmark" style="visibility: hidden;">
											<span>
												<a type="button" class="btn"
													onclick="showTipLinkDialog('<fmt:message key="global.bookmark" />:', '<fmt:message key="global.bookmark" />', retrieveUrlFromDisplayedGraphic() );" >
													<i class="icon-bookmark"></i>
													<fmt:message key="global.linkToGraphic" />
												</a>
											</span>
										</div>
										<br/>
										<a id="jumpToGraphicalReportStep2" class="btn" style="visibility: hidden;"
											onclick="changeLocation(constructUrlForConfigure());">
											<i class="icon-cog"></i>
											<fmt:message key='global.configure'/>
										</a>
									</td>
								</tr>
								<tr class="visibleRows">
								
									<%-- note the use of the CSS class 'notLoaded' while image has not been loaded yet, for excluding it from print view --%>
									<td id="fastExportContentHolder" class="fastExportContentNotLoaded">
										<span id="${displayLoadingId}" class="fastExportLoading"></span>
										<c:choose>
											<c:when test="${resultFormat == 'Svg'}">
												<c:if test="${not fn:contains(userAgent, 'Firefox') }">
													<a href="#" rel="tooltip" data-original-title="<fmt:message key="fastExport.tooltip"/>" data-placement="top">
												</c:if>
												<div style="overflow: auto; max-height: 750px;">
													<object id="${displayId}" title="Visualisation"
															type="image/svg+xml" style="visibility: hidden;"
															onload="onGraphicLoaded()"
															class="embedFastExportSvg" border="1">
														Loading...
													</object>
												</div>
												<c:if test="${not fn:contains(userAgent, 'Firefox') }">
													</a>
												</c:if>
											</c:when>
											
											<c:when test="${resultFormat == 'Png'}">
<!-- 											<div style="max-width: 100%;  overflow: auto;">	 -->
												<img id="${displayId}" alt="Visualisation"  src="#"
													onload="onGraphicLoaded()"
													style="visibility: hidden;" class="embedFastExportImg" border="1" />
<!-- 											</div> -->
											</c:when>
										</c:choose>
									</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
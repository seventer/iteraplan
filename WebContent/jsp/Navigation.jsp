<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera" %>

<c:set var="rest_mapping" value="/show/"/>

<c:set var="link">
	<itera:linkToElement name="userContext" property="user" type="html" />
</c:set>
<c:set var="linkScript">
	<itera:linkToElement name="userContext" property="user" type="js" />
</c:set>

<div class="navbar-inner" id="iteraplanNavigation">
	<div class="container-fluid" style="padding: 0 10px;">
		<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
		</a>
		<div class="nav-collapse collapse">
			<ul class="nav">
				
				<c:if test="${userContext.perms.userHasPermissionsForMenuEadata}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.eadataDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					<li class="dropdown ${active}" id="repository">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#repository">
							<fmt:message key="menu.header.eadata" />
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- Overview --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Overview" />
								<tiles:putAttribute name="title_key" value="global.overview" />	
								<tiles:putAttribute name="url" value="/overview/init.do" />
							</tiles:insertTemplate>
							
							<%-- Search --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Search" />
								<tiles:putAttribute name="title_key" value="manageSearch.title" />	
								<tiles:putAttribute name="url" value="/search/init.do" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Business Domain --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessDomain" />
								<tiles:putAttribute name="title_key" value="businessDomain.plural" />	
								<tiles:putAttribute name="url" value="/businessdomain/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessdomain" />
							</tiles:insertTemplate>
							
							<%-- Business Process --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessProcess" />
								<tiles:putAttribute name="title_key" value="businessProcess.plural" />	
								<tiles:putAttribute name="url" value="/businessprocess/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessprocess" />
							</tiles:insertTemplate>
							
							<%-- Business Unit --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessUnit" />
								<tiles:putAttribute name="title_key" value="businessUnit.plural" />	
								<tiles:putAttribute name="url" value="/businessunit/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessunit" />
							</tiles:insertTemplate>
							
							<%-- Product --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Product" />
								<tiles:putAttribute name="title_key" value="global.products" />	
								<tiles:putAttribute name="url" value="/product/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}product" />
							</tiles:insertTemplate>
							
							<%-- Business Function --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessFunction" />
								<tiles:putAttribute name="title_key" value="global.business_functions" />	
								<tiles:putAttribute name="url" value="/businessfunction/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessfunction" />
							</tiles:insertTemplate>
							
							<%-- Business Object --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessObject" />
								<tiles:putAttribute name="title_key" value="businessObject.plural" />	
								<tiles:putAttribute name="url" value="/businessobject/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessobject" />
							</tiles:insertTemplate>
							
							<%-- Business Mapping --%>
							<tiles:insertTemplate template="/jsp/SingleFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="BusinessMapping" />
								<tiles:putAttribute name="title_key" value="global.business_mappings" />	
								<tiles:putAttribute name="flow_url" value="${rest_mapping}businessmapping" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Information System Domain --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="InformationSystemDomain" />
								<tiles:putAttribute name="title_key" value="informationSystemDomain.plural" />	
								<tiles:putAttribute name="url" value="/informationsystemdomain/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}informationsystemdomain" />
							</tiles:insertTemplate>
							
							<%-- Information System --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="InformationSystem" />
								<tiles:putAttribute name="title_key" value="informationSystemRelease.plural" />
								<tiles:putAttribute name="url" value="/informationsystem/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}informationsystem" />
							</tiles:insertTemplate>
							
							<%-- Information System Interface --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Interface" />
								<tiles:putAttribute name="title_key" value="interface.plural" />
								<tiles:putAttribute name="url" value="/interface/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}interface" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Architectural Domain --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="ArchitecturalDomain" />
								<tiles:putAttribute name="title_key" value="architecturalDomain.plural" />
								<tiles:putAttribute name="url" value="/architecturaldomain/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}architecturaldomain" />
							</tiles:insertTemplate>
							
							<%-- Technical Component --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="TechnicalComponent" />
								<tiles:putAttribute name="title_key" value="technicalComponentRelease.plural" />
								<tiles:putAttribute name="url" value="/technicalcomponent/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}technicalcomponent" />
							</tiles:insertTemplate>
							
							<%-- Infrastructure Element --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="InfrastructureElement" />
								<tiles:putAttribute name="title_key" value="infrastructureElement.plural" />	
								<tiles:putAttribute name="url" value="/infrastructureelement/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}infrastructureelement" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Project --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Project" />
								<tiles:putAttribute name="title_key" value="project.plural" />
								<tiles:putAttribute name="url" value="/project/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}project" />
							</tiles:insertTemplate>	
						</ul>
					</li>
				</c:if>
				<c:if test="${userContext.perms.userHasPermissionsForMenuReports}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.reportDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					<li class="dropdown ${active}" id="reporting">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#reporting">
							<fmt:message key="menu.header.reports" />
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
						
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- Tabular Reporting --%>
							<tiles:insertTemplate template="/jsp/SingleFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="TabularReporting" />
								<tiles:putAttribute name="title_key" value="global.report.text" />	
								<tiles:putAttribute name="flow_url" value="${rest_mapping}tabularreporting" />
							</tiles:insertTemplate>
							
							<%-- IteraQl Console --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="IteraQl" />
								<tiles:putAttribute name="title_key" value="global.iteraql" />
								<tiles:putAttribute name="url" value="/iteraql/init.do" />
							</tiles:insertTemplate>
							
							<%-- Successor Reports --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="SuccessorReport" />
								<tiles:putAttribute name="title_key" value="global.successorreport" />	
								<tiles:putAttribute name="url" value="/successorreport/init.do" />
							</tiles:insertTemplate>
							
							<%-- Consistency Check --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="ConsistencyCheck" />
								<tiles:putAttribute name="title_key" value="manageConsistencyReport.title" />	
								<tiles:putAttribute name="url" value="/consistencycheck/init.do" />
								<tiles:putAttribute name="showNoActive" value="true" />
							</tiles:insertTemplate>
							
						</ul>
					</li>
				</c:if>
				<c:if test="${userContext.perms.userHasPermissionsForMenuVisual}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.visualDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					
					<li class="dropdown ${active}" id="diagram">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#diagram">
							<fmt:message key="menu.header.visualisation" />
							<b class="caret"></b>
						</a>
						
						<ul class="dropdown-menu">
						
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- Graphical Reporting --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="global.report.graphical" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="url" value="/graphicalreporting/init.do" />
								<tiles:putAttribute name="showNoActiveIfSubflow" value="true" />
							</tiles:insertTemplate>
              
                            <%-- Saved Queries overview --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="SavedQueries" />
								<tiles:putAttribute name="title_key" value="global.report.graphical.savedqueries" />	
								<tiles:putAttribute name="url" value="/savedqueries/init.do" />
                            </tiles:insertTemplate>
							
							<%-- Custom Dashboard Instances --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="CustomDashboardInstancesOverview" />
								<tiles:putAttribute name="title_key" value="global.customDashboardInstances" />	
								<tiles:putAttribute name="url" value="/customdashboards/init.do" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Landscape Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.landscapeDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/landscapediagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Landscape" />
							</tiles:insertTemplate>
							
							<%-- Cluster Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.clusterDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/clusterdiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Cluster" />
							</tiles:insertTemplate>
							
							<%-- Nesting Cluster Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.vbbClusterDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/vbbclusterdiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_VbbCluster" />
							</tiles:insertTemplate>
							
							<%-- Information Flow Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.informationFlowDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/informationflowdiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_InformationFlow" />
							</tiles:insertTemplate>
							
							<%-- Portfolio Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.portfolioDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/portfoliodiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Portfolio" />
							</tiles:insertTemplate>
							
							<%-- Masterplan Diagram --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.masterplanDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/masterplandiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Masterplan" />
							</tiles:insertTemplate>
							
							<%-- Dashboard --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Dashboard" />
								<tiles:putAttribute name="title_key" value="global.dashboard" />	
								<tiles:putAttribute name="url" value="/dashboard/init.do" />
							</tiles:insertTemplate>
							
							<%-- Bar or Pie Chart --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.pieBarDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/piebardiagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Pie" />
								<tiles:putAttribute name="queryType2" value="reports_export_graphical_Bar" />
							</tiles:insertTemplate>
							
							<%-- Composite Bar and Pie Chart --%>
							<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="GraphicalReporting" />
								<tiles:putAttribute name="title_key" value="graphicalExport.compositeDiagram" />	
								<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
								<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/compositediagram" />
								<tiles:putAttribute name="queryType" value="reports_export_graphical_Composite" />
							</tiles:insertTemplate>
							
 							<%-- Line Chart --%>
							<c:if test="${guiContext.timeseriesEnabled}">
 								<tiles:insertTemplate template="/jsp/DiagramFlowLinkPerm.jsp" flush="true"> 
 									<tiles:putAttribute name="this_dialog" value="GraphicalReporting" /> 
 									<tiles:putAttribute name="title_key" value="graphicalExport.lineDiagram" />	
 									<tiles:putAttribute name="rest_mapping" value="${rest_mapping}" />	
 									<tiles:putAttribute name="subflow_base_id" value="graphicalreporting/linediagram" /> 
 									<tiles:putAttribute name="queryType" value="reports_export_graphical_Line" />
 								</tiles:insertTemplate> 
 							</c:if>
						</ul>
					</li>
				</c:if>
				<c:if test="${userContext.perms.userHasPermissionsForMenuMass}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.massDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					<li class="dropdown ${active}" id="bulkedit">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#bulkedit">
							<fmt:message key="menu.header.massdata" />
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- Mass Update --%>
							<tiles:insertTemplate template="/jsp/SingleFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="MassUpdate" />
								<tiles:putAttribute name="title_key" value="global.mass_updates" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}massupdate" />
							</tiles:insertTemplate>
							
							<%-- Export/ Import --%>
							<tiles:insertTemplate template="/jsp/SingleFlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Import" />
								<tiles:putAttribute name="title_key" value="global.elasticExportImport_menu" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}import" />
							</tiles:insertTemplate>
							
						</ul>
					</li>
				</c:if>
				<c:if test="${userContext.perms.userHasPermissionsForMenuGovernance}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.governanceDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					<li class="dropdown ${active}" id="users">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#users">
							<fmt:message key="menu.header.governance" />
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
						
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- User --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="User" />
								<tiles:putAttribute name="title_key" value="global.user_management" />	
								<tiles:putAttribute name="url" value="/user/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}user" />
							</tiles:insertTemplate>
							
							<%-- User Group --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="UserGroup" />
								<tiles:putAttribute name="title_key" value="global.usergroup_management" />	
								<tiles:putAttribute name="url" value="/usergroup/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}usergroup" />
							</tiles:insertTemplate>
							
							<%-- Role --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Role" />
								<tiles:putAttribute name="title_key" value="global.role_management" />	
								<tiles:putAttribute name="url" value="/role/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}role" />
							</tiles:insertTemplate>
							
							<%-- Object Related Permission --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="ObjectRelatedPermission" />
								<tiles:putAttribute name="title_key" value="global.instance_permission_management" />
								<tiles:putAttribute name="url" value="/objectrelatedpermission/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}objectrelatedpermission" />
							</tiles:insertTemplate>
							
							<c:if test="${sectionHasEntries}">
								<c:set var="requireDevider" value="true" scope="request" />
								<c:set var="sectionHasEntries" value="false" scope="request" />
							</c:if>
							
							<%-- Supporting Query --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="SupportingQuery" />
								<tiles:putAttribute name="title_key" value="global.permission_queries" />	
								<tiles:putAttribute name="url" value="/supportingquery/init.do" />
							</tiles:insertTemplate>
							
							<%-- Consistency Check --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="ConsistencyCheck" />
								<tiles:putAttribute name="title_key" value="manageConsistencyReport.title" />	
								<tiles:putAttribute name="url" value="/consistencycheck/init.do" />
							</tiles:insertTemplate>
						</ul>
					</li>
				</c:if>
				<c:if test="${userContext.perms.userHasPermissionsForMenuAdmin}">
					<c:set var="active" value=""/>
					<c:if test="${guiContext.adminDialogActive}">
						<c:set var="active" value="active"/>
					</c:if>
					<li class="dropdown ${active}" id="administration">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#administration">
							<fmt:message key="menu.header.administration" />
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							
							<c:set var="sectionHasEntries" value="false" scope="request" />
							<c:set var="requireDevider" value="false" scope="request" />
							
							<%-- Configuration --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Configuration" />
								<tiles:putAttribute name="title_key" value="global.configuration" />	
								<tiles:putAttribute name="url" value="/configuration/init.do" />
							</tiles:insertTemplate>
							
							<%-- Attribute Type Group --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="AttributeTypeGroup" />
								<tiles:putAttribute name="title_key" value="global.attributegroups" />	
								<tiles:putAttribute name="url" value="/attributetypegroup/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}attributetypegroup" />
							</tiles:insertTemplate>
							
							<%-- Attribute Type --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="AttributeType" />
								<tiles:putAttribute name="title_key" value="global.attributes" />
								<tiles:putAttribute name="url" value="/attributetype/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}attributetype" />
							</tiles:insertTemplate>

							<%-- Date Interval --%>
							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="DateInterval" />
								<tiles:putAttribute name="overridePermissionCheckWith" value="${userContext.perms.userHasFuncPermAttributes}" />
								<tiles:putAttribute name="title_key" value="global.dateintervals" />
								<tiles:putAttribute name="url" value="/dateinterval/init.do" />
								<tiles:putAttribute name="flow_url" value="${rest_mapping}dateinterval"/>
							</tiles:insertTemplate>
							
							<%-- Templates --%>
 							<tiles:insertTemplate template="/jsp/FlowLinkPerm.jsp" flush="true">
								<tiles:putAttribute name="this_dialog" value="Templates" />
								<tiles:putAttribute name="title_key" value="global.templates" />
								<tiles:putAttribute name="url" value="/templates/init.do" />
							</tiles:insertTemplate>
						</ul>
					</li>
				</c:if>
			</ul> 
			<ul class="nav pull-right">
				<li class="dropdown" id="language">
					<a class="dropdown-toggle" data-toggle="dropdown" href="#language">
						<fmt:message key="language.select" />
						<b class="caret"></b>
					</a>
					<ul class="dropdown-menu">
						<c:forEach items="${userContext.availableLocales}" var="currentLocale" >
							<li><a href="javascript:changeLanguage('${currentLocale}'); "><fmt:message key="language.${currentLocale}" /></a></li>
						</c:forEach>
					</ul>
				</li>
				<li class="dropdown" id="about">
					<a class="dropdown-toggle" data-toggle="dropdown" href="#about">
						<fmt:message key="menu.header.about" />
						<b class="caret"></b>
					</a>
					<ul class="dropdown-menu">
						<li><a target="_blank" href="<c:url value="/manuals/UserGuide.pdf" />"><fmt:message key="global.userguide" /></a></li>
						<li><a href="<c:url value="/help/show.do" />"><fmt:message key="menu.header.definitions" /></a></li>
						<li><a target="_blank" href="http://www.iteraplan.de/"><fmt:message key="menu.header.more" /></a></li>
					</ul>
				</li>
				<li class="dropdown" id="user">
					<a class="dropdown-toggle" data-toggle="dropdown" href="#user">
						<i class="icon-user"></i><c:out value=" ${userContext.loginName}" />
						<b class="caret"></b>
					</a>
					<ul class="dropdown-menu">
						<c:set var="link">
							<itera:linkToElement name="userContext" property="user" type="html" />
						</c:set>
						<li><a href="<c:out value="${link}" />"><fmt:message key="menu.header.profile" /></a></li>
						<li><a id="menu.Restart" href="#" onclick="javascript:confirmEdited();"><fmt:message key="navigation.reset" /></a></li>
						<li><a id="menu.Logout" href="#" onclick="confirmLogout('<c:url value="/j_iteraplan_logout" />');"><i class="icon-off"></i> 
						<fmt:message key="menu.logout" />
						</a></li>
					</ul>
				</li>
			</ul>
		</div><!--/.nav-collapse -->
	</div>
</div>

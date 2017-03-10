<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="functionalPermission">
	<c:set var="GraphicalReporting" value="GraphicalReporting" />
	<itera:write name="userContext" property="perms.userHasDialogPermission(${GraphicalReporting})" escapeXml="false" />
</c:set>

<c:set var="this_dialog" value="${guiContext.activeDialog}" />

<c:set var="clusterDiagram" value="graphicalreporting/clusterdiagram/start" />
<c:set var="informationFlowDiagram" value="graphicalreporting/informationflowdiagram/start" />
<c:set var="masterPlanDiagram" value="graphicalreporting/masterplandiagram/start" />
<c:set var="landscapeDiagram" value="graphicalreporting/landscapediagram/start" />
<c:set var="portfolioDiagram" value="graphicalreporting/portfoliodiagram/start" />
<c:set var="compositeDiagram" value="graphicalreporting/compositediagram/start" />
<c:set var="pieBarDiagram" value="graphicalreporting/piebardiagram/start" />
<c:set var="vbbClusterDiagram" value="graphicalreporting/vbbclusterdiagram/start" />
<c:set var="timelineDiagram" value="graphicalreporting/timelinediagram/start" />
<c:set var="lineDiagram" value="graphicalreporting/linediagram/start" />
<c:set var="matrixDiagram" value="graphicalreporting/matrixdiagram/start" />
<c:set var="dashboard" value="dashboard/init.do" />

<c:set var="linkLandscapeDiagram" value="../show/graphicalreporting/landscapediagram"/>
<c:set var="linkClusterDiagram" value="../show/graphicalreporting/clusterdiagram"/>
<c:set var="linkInformationFlowDiagram" value="../show/graphicalreporting/informationflowdiagram"/>
<c:set var="linkMasterPlanDiagram" value="../show/graphicalreporting/masterplandiagram"/>
<c:set var="linkPortfolioDiagram" value="../show/graphicalreporting/portfoliodiagram"/>
<c:set var="linkCompositeDiagram" value="../show/graphicalreporting/compositediagram"/>
<c:set var="linkPieBarDiagram" value="../show/graphicalreporting/piebardiagram"/>
<c:set var="linkVbbClusterDiagram" value="../show/graphicalreporting/vbbclusterdiagram"/>
<c:set var="linkTimelineDiagram" value="../show/graphicalreporting/timelinediagram"/>
<c:set var="linkLineDiagram" value="../show/graphicalreporting/linediagram"/>
<c:set var="linkMatrixDiagram" value="../show/graphicalreporting/matrixdiagram"/>
<c:set var="linkDashboard" value="../dashboard/init.do"/>

<c:if test="${guiContext.flowEntries[this_dialog] != null}" >
	<c:forEach items="${guiContext.flowEntries[this_dialog]}" var="entry">
		<c:choose>
			<c:when test="${entry.flowId == landscapeDiagram}">
				<c:set var="linkLandscapeDiagram" value="../show/graphicalreporting/landscapediagram?execution=${entry.key}" />
			</c:when>	
			<c:when test="${entry.flowId == clusterDiagram}">
				<c:set var="linkClusterDiagram" value="../show/graphicalreporting/clusterdiagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == informationFlowDiagram}">
		 		<c:set var="linkInformationFlowDiagram" value="../show/graphicalreporting/informationflowdiagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == masterPlanDiagram}">
				<c:set var="linkMasterPlanDiagram" value="../show/graphicalreporting/masterplandiagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == portfolioDiagram}">
				<c:set var="linkPortfolioDiagram" value="../show/graphicalreporting/portfoliodiagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == compositeDiagram}">
				<c:set var="linkCompositeDiagram" value="../show/graphicalreporting/compositediagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == pieBarDiagram}">
				<c:set var="linkPieBarDiagram" value="../show/graphicalreporting/piebardiagram?execution=${entry.key}" />
			</c:when>
			<c:when test="${entry.flowId == vbbClusterDiagram}">
				<c:set var="linkVbbClusterDiagram" value="../show/graphicalreporting/vbbclusterdiagram?execution=${entry.key}" />
			</c:when>
<%-- 			<c:when test="${entry.flowId == timelineDiagram}"> --%>
<%-- 				<c:set var="linkTimelineDiagram" value="../show/graphicalreporting/timelinediagram?execution=${entry.key}" /> --%>
<%-- 			</c:when> --%>
			<c:when test="${entry.flowId == lineDiagram}">
				<c:if test="${guiContext.timeseriesEnabled}">
					<c:set var="linkLineDiagram" value="../show/graphicalreporting/linediagram?execution=${entry.key}" />
				</c:if>
			</c:when>
			<c:when test="${entry.flowId == matrixDiagram}">
				<c:set var="linkMatrixDiagram" value="../show/graphicalreporting/matrixdiagram?execution=${entry.key}" />
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</c:if>

<c:choose>
	<c:when test="${functionalPermission == true}">
			
		<div class="btn-toolbar">

			<div class="btn-group">	
				<ul class="dropdown-menu">
				
					<%-- Landscape Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" >
						<tiles:putAttribute name="title_key" value="graphicalExport.landscapeDiagram" />
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Landscape" />
					</tiles:insertTemplate>
					
					<%-- Cluster Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.clusterDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Cluster" />
					</tiles:insertTemplate>
					
					<%-- Nesting Cluster Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.vbbClusterDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_VbbCluster" />
					</tiles:insertTemplate>
					
					<%-- Information Flow Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.informationFlowDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_InformationFlow" />
					</tiles:insertTemplate>
					
					<%-- Portfolio Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.portfolioDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Portfolio" />
					</tiles:insertTemplate>
					
					<%-- Masterplan Diagram --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.masterplanDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Masterplan" />
					</tiles:insertTemplate>
					
					<%-- Pie Chart --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.pieDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Pie" />
					</tiles:insertTemplate>
					
					<%-- Bar Chart --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.barDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Bar" />
					</tiles:insertTemplate>
					
					<%-- Composite Bar and Pie Chart --%>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.compositeDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Composite" />
					</tiles:insertTemplate>
					
					<%-- Timeline Diagram (Beta), 
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
						<tiles:putAttribute name="title_key" value="graphicalExport.timelineDiagram" />	
						<tiles:putAttribute name="queryType" value="reports_export_graphical_Timeline" />
					</tiles:insertTemplate> --%>
					
					<%-- Line Chart (Beta), --%>
					<c:if test="${guiContext.timeseriesEnabled}">
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DropdownWithQueries.jsp" flush="true">
							<tiles:putAttribute name="title_key" value="graphicalExport.lineDiagram" />	
							<tiles:putAttribute name="queryType" value="reports_export_graphical_Line" />
						</tiles:insertTemplate>
					</c:if>

				</ul>
			</div>
		</div>
		
		<br />
		
		<%-- Display pictogram for each diagram type --%>
		<div class="row-fluid">
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkLandscapeDiagram}" />" ><img src="<c:url value="/images/landscapeDiagram.gif" />" alt="landscapeDiagram" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkLandscapeDiagram}" />" ><fmt:message key="graphicalExport.landscapeDiagram" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkClusterDiagram}" />" ><img src="<c:url value="/images/clusterDiagram.gif" />" alt="clusterDiagram" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkClusterDiagram}" />" ><fmt:message key="graphicalExport.clusterDiagram" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkVbbClusterDiagram}" />" ><img src="<c:url value="/images/nestingCluster.gif" />" alt="vbbClusterDiagram" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkVbbClusterDiagram}" />" ><fmt:message key="graphicalExport.vbbClusterDiagram" /></a> </p>
			</div>
		</div>
		<br/>
		<br/>
		<div class="row-fluid">
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkInformationFlowDiagram}" />" ><img src="<c:url value="/images/informationFlow.gif" />" alt="informationFlow" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkInformationFlowDiagram}" />" ><fmt:message key="graphicalExport.informationFlowDiagram" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkPortfolioDiagram}" />" ><img src="<c:url value="/images/portfolioDiagram.gif" />" alt="portfolioDiagram" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkPortfolioDiagram}" />" ><fmt:message key="graphicalExport.portfolioDiagram" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkMasterPlanDiagram}" />" ><img src="<c:url value="/images/masterplanDiagram.gif" />" alt="masterplanDiagram" class="diagramPictureLayout" /></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkMasterPlanDiagram}" />" ><fmt:message key="graphicalExport.masterplanDiagram" /></a> </p>
			</div>
		</div>
		<br/>
		<br/>
		<div class="row-fluid">
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkDashboard}" />" ><img src="<c:url value="/images/dashboard.png" />" alt="dashboard" class="diagramPictureLayout"/></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkDashboard}" />" ><fmt:message key="global.dashboard" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkPieBarDiagram}" />" ><img src="<c:url value="/images/pieBarDiagram.gif" />" alt="pieBarDiagram" class="diagramPictureLayout"/></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkPieBarDiagram}" />" ><fmt:message key="graphicalExport.pieBarDiagram" /></a> </p>
			</div>
			<div class="span4" style="text-align:center;">
				<a href="<c:url value="${linkCompositeDiagram}" />" ><img src="<c:url value="/images/compositeDiagram.gif" />" alt="compositeDiagram" class="diagramPictureLayout"/></a>
				<p class="diagramLabel"> <a href="<c:url value="${linkCompositeDiagram}" />" ><fmt:message key="graphicalExport.compositeDiagram" /></a> </p>
			</div>
		</div>
		<c:if test="${guiContext.timeseriesEnabled}">
			<br/>
			<br/>
			<div class="row-fluid">
				<div class="span4" style="text-align:center;">
					<a href="<c:url value="${linkLineDiagram}" />" ><img src="<c:url value="/images/lineChart.gif" />" alt="lineDiagram" class="diagramPictureLayout"/></a>
					<p class="diagramLabel"> <a href="<c:url value="${linkLineDiagram}" />" ><fmt:message key="graphicalExport.lineDiagram" /></a> </p>
				</div>
			</div>
		</c:if>
		
		<div class="module-body">
			<br></br>
			<p>			
				<a  
					href="<c:url value="/visio/vbaCert.cer" />"
					class="link btn btn-primary" >
					<i class="icon-download-alt icon-white"></i>
 					<fmt:message key="graphicalExport.visioCertificate" />
				</a> 
			</p>
		</div>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>
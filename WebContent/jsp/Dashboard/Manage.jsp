<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<%-- Must to be the first definition @see http://docs.dojocampus.org/djConfig --%>	 
<script type="text/javascript">
    var djConfig = {parseOnLoad: true, locale: '<fmt:message key="calendar.lang"/>', baseUrl: '<c:out value="${pageContext.request.contextPath}/resources/dojo/" />'};
</script>

<jwr:style media="all" src="/bundles/dojodashboard.css" />
<jwr:script src="/bundles/dojodashboard.js" />

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermDashboard}" scope="request" />
	
<c:set var="functionalPermissionBD" value="${userContext.perms.userHasFuncPermBD}" scope="request" />	
<c:set var="functionalPermissionBP" value="${userContext.perms.userHasFuncPermBP}" scope="request" />	
<c:set var="functionalPermissionBF" value="${userContext.perms.userHasFuncPermBF}" scope="request" />	
<c:set var="functionalPermissionPROD" value="${userContext.perms.userHasFuncPermPROD}" scope="request" />	
<c:set var="functionalPermissionBU" value="${userContext.perms.userHasFuncPermBU}" scope="request" />	
<c:set var="functionalPermissionBO" value="${userContext.perms.userHasFuncPermBO}" scope="request" />	
<c:set var="functionalPermissionISD" value="${userContext.perms.userHasFuncPermISD}" scope="request" />	
<c:set var="functionalPermissionIS" value="${userContext.perms.userHasFuncPermIS}" scope="request" />	
<c:set var="functionalPermissionINT" value="${userContext.perms.userHasFuncPermINT}" scope="request" />	
<c:set var="functionalPermissionAD" value="${userContext.perms.userHasFuncPermAD}" scope="request" />
<c:set var="functionalPermissionTC" value="${userContext.perms.userHasFuncPermTC}" scope="request" />
<c:set var="functionalPermissionIE" value="${userContext.perms.userHasFuncPermIE}" scope="request" />		
<c:set var="functionalPermissionPROJ" value="${userContext.perms.userHasFuncPermPROJ}" scope="request" />	
<c:set var="header_key" value="global.dashboard" />

<div id="DashboardModule" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${header_key}" />
	</div>
<c:choose>

	<c:when test="${functionalPermission == true}">
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<div class="span6" style="min-width: 530px">
					<div class="row-fluid">
						<div class="row-fluid">
							<div class="span12 pager">
								<h5><fmt:message key="manageDashboard.numberOfBB" /></h5>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span12 pager">
								<div id="chartNumberOfBB" class="dashboardLargeChart"></div>
							</div>
						</div>
					</div>		
					<div class="row-fluid" style="margin-bottom: 5px; max-width: 750px;">
						<div class="span5">
						    <input type="hidden" name="selectedBBOption" value="0" />
							<select name="bbMap" id="bbMap" onchange="change('isrStatusMap','bbMap')">
								
								<option>
										<fmt:message key="global.dashboard.buildingBlocks" />
								</option>
								<c:forEach items="${dialogMemory.bbMap}" var="at">
								
									<c:if test="${(functionalPermissionBD == true) && (at.key == 'businessDomain.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionBP == true) && (at.key == 'businessProcess.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionBF == true) && (at.key == 'businessFunction.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionPROD == true) && (at.key == 'product.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionBU == true) && (at.key == 'businessUnit.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionBO == true) && (at.key == 'businessObject.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionISD == true) && (at.key == 'informationSystemDomain.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionIS == true) && (at.key == 'informationSystemRelease.')}"> 
										<option value="${at.key}">
			                            	<fmt:message key="${at.key }plural" /> 
										</option>		
									</c:if>
								
									<c:if test="${(functionalPermissionINT == true) && (at.key == 'interface.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionAD == true) && (at.key == 'architecturalDomain.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionTC == true) && (at.key == 'technicalComponentRelease.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionIE == true) && (at.key == 'infrastructureElement.')}"> 
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
									<c:if test="${(functionalPermissionPROJ == true) && (at.key == 'project.')}">
										<option value="${at.key}"> 
											<fmt:message key="${at.key }plural" /> 
										</option>
									</c:if>
								
								</c:forEach>
							</select>
						</div>
						<div class="span5">
							<form:select path="isrStatusMap" onchange="createPie('isrStatusMap','bbMap')"  multiple="false">
								<form:option value="attributes" selected="true">
									<fmt:message key="reports.selectAttribute"/>
								</form:option>
							</form:select>
						</div>
					</div>
					
					<div class="row-fluid">
						<div class="span12 pager">
							<div id="pieChart" class="pieLargeChart"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:if test="${functionalPermissionIS == true}"> 
						<div class="row-fluid" style="margin-top: 18px;">
							<c:set var="topIsrToDisplay" value="5" />
		
							<%-- Simple table of ISR ordered by number of interfaces --%>
							<!-- <div id="topUsedISR" class="container-fluid"> -->
								<div id="TopIsrModule" class="row-fluid module">
									<div class="module-heading">
										<fmt:message key="manageDashboard.topUsedISR" />
									</div>
									<div class="row-fluid">
										<div class="module-body-table">
											<div class="row-fluid">
												<table class="table table-striped table-condensed tableInModule">
													<tbody>
														<c:forEach items="${dialogMemory.topUsedIsrMap}" var="isrEntry" varStatus="statusIsr">
															<c:set var="isr" value="${isrEntry.key}" />
															<c:if test="${statusIsr.index < topIsrToDisplay}">
																<c:set var="linkScriptIsr">
																	<itera:linkToElement name="isr" type="js" />
																</c:set>
																<c:set var="linkIsr">
																	<itera:linkToElement name="isr" type="html" />
																</c:set>
						
																<tr>
																	<td class="link" onclick="<c:out value="${linkScriptIsr}" />">
																		<itera:htmlLinkToElement link="${linkIsr}" isLinked="true">
																			<c:out value="${isr.name}" />
																		</itera:htmlLinkToElement>
																	</td>
																	<td class="link" onclick="<c:out value="${linkScriptIsr}" />">
																		<itera:htmlLinkToElement link="${linkIsr}" isLinked="true">
																			<c:out value="${isrEntry.value}" />
																		</itera:htmlLinkToElement>
																	</td>
																</tr>
															</c:if>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							<!-- </div> -->
		
						</div>
					</c:if>
		
			        <c:if test="${functionalPermissionTC == true}"> 
						<!-- <div class="dashboardRow">
							<div class="dashboardTopList"> -->
						<div class="row-fluid">
							<c:set var="topTcrToDisplay" value="5" />
		
							<%-- Simple table of TCR ordered by number of uses --%>
							<div id="TopUsedTCRModule" class="row-fluid module">
								<div class="module-heading" onclick="$('#TopUsedTCRModule').toggle();$('#TopUsedTCRByADModule').toggle();">
									<i id="collapseIcon" class="icon-resize-full"></i>
                               		&nbsp;<fmt:message key="manageDashboard.topUsedTCR" />
								</div>
								<div class="row-fluid">
									<div class="module-body-table">
										<div class="row-fluid">
											<table class="table table-striped table-condensed tableInModule">
												<tbody>
													<c:forEach items="${dialogMemory.topUsedTcrMap}" var="tcrEntry" varStatus="status">
														<c:set var="tcr" value="${tcrEntry.key}" />
														<c:if test="${status.index < topTcrToDisplay}">
															<c:set var="linkScript">
																<itera:linkToElement name="tcr" type="js" />
															</c:set>
															<c:set var="link">
																<itera:linkToElement name="tcr" type="html" />
															</c:set>
					
															<tr>
																<td class="link" onclick="<c:out value="${linkScript}" />">
																	<itera:htmlLinkToElement link="${link}" isLinked="true">
																		<c:out value="${tcr.name}" />
																	</itera:htmlLinkToElement>
																</td>
																<td class="link" onclick="<c:out value="${linkScript}" />">
																	<itera:htmlLinkToElement link="${link}" isLinked="true">
																		<c:out value="${tcrEntry.value}" />
																	</itera:htmlLinkToElement>
																</td>
															</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
			
							<%-- Table of TCR sorted by uses, clustered by AD --%>
							<div id="TopUsedTCRByADModule" class="row-fluid module" style="display: none;">
								<div class="module-heading" onclick="$('#TopUsedTCRModule').toggle();$('#TopUsedTCRByADModule').toggle();">
									<i id="collapseIcon" class="icon-resize-small"></i>
									&nbsp;<fmt:message key="manageDashboard.topUsedTCRByAD" />
								</div>
								<div class="row-fluid">
									<div class="module-body-table">
										<div class="row-fluid">
											<table class="table table-striped table-condensed tableInModule">
												<tbody>
													<c:forEach items="${dialogMemory.topUsedTcrByAdMap}" var="tcrByAdEntry" varStatus="headerStatus">
														<c:choose>
															<%-- The first element will always be the unassigned TCRs --%>
															<c:when test="${headerStatus.index == 0}">
																<tr>
																	<th colspan="2">
																		<fmt:message key="manageDashboard.noADAssigned" />
																	</th>
																</tr>
															</c:when>
															<c:otherwise>
																<c:set var="ad" value="${tcrByAdEntry.key}" />
																<c:set var="linkScript">
																	<itera:linkToElement name="ad" type="js" />
																</c:set>
																<c:set var="link">
																	<itera:linkToElement name="ad" type="html" />
																</c:set>
																<tr>
																	<th class="link" colspan="2" onclick="<c:out value="${linkScript}" />">
																		<c:out value="${ad.name}" />
																	</th>
																</tr>
															</c:otherwise>
														</c:choose>
														<c:forEach items="${tcrByAdEntry.value}" var="tcrEntry" varStatus="status">
															<c:set var="tcr" value="${tcrEntry.key}" />
															<c:if test="${status.index < topTcrToDisplay}">
																<c:set var="linkScript">
																	<itera:linkToElement name="tcr" type="js" />
																</c:set>
																<c:set var="link">
																	<itera:linkToElement name="tcr" type="html" />
																</c:set>
					
																<tr>
																	<td class="link" onclick="<c:out value="${linkScript}" />">
																		<itera:htmlLinkToElement link="${link}" isLinked="true">
																			<c:out value="${tcr.name}" />
																		</itera:htmlLinkToElement>
																	</td>
																	<td class="link" onclick="<c:out value="${linkScript}" />">
																		<itera:htmlLinkToElement link="${link}" isLinked="true">
																			<c:out value="${tcrEntry.value}" />
																		</itera:htmlLinkToElement>
																	</td>
																</tr>
															</c:if>
														</c:forEach>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
		<script type="text/javascript">

	function createInititalPieChart() {
		
		var bboptions1 = document.getElementById("bbMap");
		var attroptions1 = document.getElementById("isrStatusMap");
		var bbindex = 0;
		var attrindex = 0;
		var bbtype = bboptions1.options[0].value;
		var i = 0;
		var j = 0;
		
		for (i=0;i<bboptions1.options.length;i++) {
			option = bboptions1.options[i];
			if (option.value == "informationSystemRelease.") {
			option.selected = true;
			}
		}
		
		change('isrStatusMap','bbMap');
	}
		
	function processEvent(evt) {
		if(evt.type == 'onclick') {
			var links = new Array();
			<c:forEach items="${dialogMemory.bbMap}" var="entry">
				// bbName at first is something like informationSystemRelease.abbr
				var bbName = '<c:out value="${entry.key}" />';
				// extract original bbName (without .abbr, no camelcase)
				bbName = bbName.toLowerCase().substring(0, bbName.indexOf("."));
				links.push("../" + bbName + "/init.do");
			</c:forEach>

			changeLocation(links[evt.index]);
		}
	}
	
	function processForwardToSpreadsheet(evt,attrId,bbId,data) {
		if(evt.type == 'onclick') {
			var attroptions = document.getElementById(attrId);
			var bboptions = document.getElementById(bbId);
			var bbindex = bboptions.selectedIndex;
			var bbtype = bboptions.options[bbindex].value;
			var atindex = attroptions.selectedIndex;
			var selectedDestination = 'global.report.text';
			var links = new Array();
			
			<c:forEach items="${dialogMemory.bbMap}" var="entry">
				var opt = '<c:out value="${entry.key}"/>';
				if (bbtype == 'informationSystemRelease.' || bbtype == 'technicalComponentRelease.') {
					if (bbtype == opt) {
						<itera:define id="bbname" name="${entry.key}"/>
						<c:forEach items="${bbname}" var="attr">
							if('<c:out value="${attr.key}"/>' == 'global.type_of_status'){
								<c:forEach items="${attr.value}" var="attrvalue">
									for(var i = 0; i < data.length; i++) {
										if('<fmt:message key="${attrvalue.key}"/>' == data[i].text) {
											links.push('interchange.do?bbt=' + bbtype + "singular" + '&at=' + 'global.type_of_status' + '&selectedDestination=' + selectedDestination + "&av=" + '<c:out value="${attrvalue.key}"/>');					
										}
									}
								</c:forEach>
							} 
							else if('<c:out value="${attr.key}"/>' == 'seal'){
								<c:forEach items="${attr.value}" var="attrvalue">
									for(var i = 0; i < data.length; i++) {
										if('<fmt:message key="${attrvalue.key}"/>' == data[i].text) {
											links.push('interchange.do?bbt=' + bbtype + "singular" + '&at=' + 'seal' + '&selectedDestination=' + selectedDestination + "&av=" +'<c:out value="${attrvalue.key}"/>');					
										}
									}
								</c:forEach>
							}
							else {
								for(var i = 0; i < data.length; i++) {
									links.push('interchange.do?bbt=' + bbtype + "singular" + '&at=' + attroptions.options[atindex].value + '&selectedDestination=' + selectedDestination + "&av=" + data[i].text);					
								}
							}
						</c:forEach>
					}
				}
				else {
					for(var i = 0;i < data.length; i++){
						links.push('interchange.do?bbt=' + bbtype + "singular" + '&at=' + attroptions.options[atindex].value + '&selectedDestination=' + selectedDestination + "&av=" + data[i].text);					
					}
				}
				
				changeLocation(links[evt.index]);
			</c:forEach>
		}
		}
	
	function change(attrId,bbId) {
		var attroptions = document.getElementById(attrId);
		var bboptions = document.getElementById(bbId);
		var bbindex = bboptions.selectedIndex;
		var bbtype = bboptions.options[bbindex].value;

		var attrindex = 0;
		attroptions.length = 0;
		<c:forEach items="${dialogMemory.bbMap}" var="entry">
			var opt = '<c:out value="${entry.key}"/>';
			if (bbtype == opt) {
				<itera:define id="bbname" name="${entry.key}"/>
				if ('<c:out value="${fn:length(bbname)}"/>' != '0') {
					<c:forEach items="${bbname}" var="attr">
						if('<c:out value="${attr.key}"/>' == 'global.type_of_status') {
							newOption = new Option('<fmt:message key="${attr.key}"/>');
							newOption.selected = true;
						} else if ('<c:out value="${attr.key}"/>'== 'seal') {
							newOption = new Option('<fmt:message key="${attr.key}"/>');
						} else {
							newOption = new Option('<c:out value="${attr.key}"/>');
						}
						attroptions[attrindex] = newOption;
						attrindex = attrindex+1;
					</c:forEach>
					attroptions.disabled = false;
					attroptions.style.width = '';
				}
				else {
					attroptions.disabled = true;
					attroptions.style.width = '100px';
				}
			} 
		</c:forEach>
		resetPieChartCell();
		createPie('isrStatusMap','bbMap');

	}
	
	function removeChildrenFromNode(e) {
			if(!e) {
	              return false;
	          }
	          if(typeof(e)=='string') {
	              e = xGetElementById(e);
	          }
	         while (e.hasChildNodes()) {
	              e.removeChild(e.firstChild);
	          }
	          return true;
	      }
	
	function resetPieChartCell(){
		var pieChartElement = document.getElementById('pieChart');
		removeChildrenFromNode(pieChartElement);
		pieChartElement.className = pieChartElement.className.replace(/\bpieLargeChart\b/g,'');
		pieChartElement.removeAttribute('style');
	}
	      
	function createPie(attrId,bbId){
		resetPieChartCell();
		var attroption = document.getElementById(attrId);
		var bboption = document.getElementById(bbId);
		var data = [];
		var attrindex=attroption.selectedIndex;
		var attrvalue = attroption.options[attrindex].text;
		var bbindex =bboption.selectedIndex;
		var bbvalue = bboption.options[bbindex].value;
	<c:forEach items="${dialogMemory.bbMap}" var="entry">
		var bbtempval = '<c:out value="${entry.key}"/>';
		if (bbvalue==bbtempval) {
			<itera:define id="bbname" name="${entry.key}"/>
		<c:forEach items="${bbname}" var="attr">
			var attrtempval;
			
			if('<c:out value="${attr.key}"/>'=="global.type_of_status"){
				attrtempval="<fmt:message key='${attr.key}'/>";
			}
			else if('<c:out value="${attr.key}"/>'=="seal"){
				attrtempval="<fmt:message key='${attr.key}'/>";
			}
			else {
				attrtempval = '<c:out value="${attr.key}"/>';
			}
			
			var opt2 = '<c:out value="${attr.value}"/>';
			if(attrvalue==attrtempval){
				var count;
				if('<c:out value="${attr.key}"/>'=="global.type_of_status"){
					<c:forEach items="${attr.value}" var="attrvalue">
						data.push({y:<c:out value="${fn:length(attrvalue.value)}"/>, text:'<fmt:message key="${attrvalue.key}"/>', tooltip:<c:out value="${fn:length(attrvalue.value)}" />});
					</c:forEach>
				}
				else if('<c:out value="${attr.key}"/>'=="seal"){
					<c:forEach items="${attr.value}" var="attrvalue">
						data.push({y:<c:out value="${fn:length(attrvalue.value)}"/>, text:'<fmt:message key="${attrvalue.key}"/>', tooltip:<c:out value="${fn:length(attrvalue.value)}"/>});
					</c:forEach>
				}else{
					<c:forEach items="${attr.value}" var="attrvalue">
						data.push({y:<c:out value="${fn:length(attrvalue.value)}"/>, text:'<c:out value="${attrvalue.key}" escapeXml="false"/>', tooltip:<c:out value="${fn:length(attrvalue.value)}"/>});
					</c:forEach>
				}
				
				if(data.length>0) {
					$('#pieChart').addClass('pieLargeChart');
					drawPieChart("Series A",data);
				}else{
					document.getElementById('pieChart').appendChild(document.createTextNode('<fmt:message key="global.dashboard.attributemessage" />'));
				}
		
				return;
			}
	</c:forEach>
		}
	</c:forEach>
	}
	
	 function drawPieChart(series,data){
		 $(document).ready(function() {
    	    	var chart = new dojox.charting.Chart2D('pieChart');
    	    	chart.removeSeries(series);
    	        chart.setTheme(dojox.charting.themes.PlotKit.purple);
    	        chart.removePlot('default');
    	        chart.addPlot('default', {
    	                            type: 'Pie',
    	                            font: 'normal normal 11pt Tahoma',
    	                            fontColor: 'black',
    	                            labelOffset: -30,
    	                            radius: 200
    	                        });
    	                        chart.addSeries(series, data);
    	        
    	        var anim4b = new dojox.charting.action2d.Tooltip(chart, 'default');
    	        var anim4c = new dojox.charting.action2d.Shake(chart,'default');
    	        
    	        chart.connectToPlot('default', function(evt){processForwardToSpreadsheet(evt,'isrStatusMap','bbMap', data);});
    	        
    	        chart.render();
    	});
    }

    var chartData = [];
    var nrBB = 0;
    <c:forEach items="${dialogMemory.bbMap}" var="entry">  
    	<c:if test="${(functionalPermissionBD == true) && (entry.key == 'businessDomain.')}">
    	 		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
    	 		nrBB += 1;
    	</c:if>
   		<c:if test="${(functionalPermissionBP == true) && (entry.key == 'businessProcess.')}">
   				chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
   				nrBB += 1;
    	</c:if>
    	<c:if test="${(functionalPermissionBF == true) && (entry.key == 'businessFunction.')}">
    			chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
    			nrBB += 1;
    	</c:if>
        <c:if test="${(functionalPermissionPROD == true) && (entry.key == 'product.')}">
       			 chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
       			nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionBU == true) && (entry.key == 'businessUnit.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionBO == true) && (entry.key == 'businessObject.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionISD == true) && (entry.key == 'informationSystemDomain.')}">
       			chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
       			nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionIS == true) && (entry.key == 'informationSystemRelease.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>                   	         
        <c:if test="${(functionalPermissionINT == true) && (entry.key == 'interface.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionAD == true) && (entry.key == 'architecturalDomain.')}">
       			chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
       			nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionTC == true) && (entry.key == 'technicalComponentRelease.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionIE == true) && (entry.key == 'infrastructureElement.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
        <c:if test="${(functionalPermissionPROJ == true) && (entry.key == 'project.')}">
        		chartData.push({ x: '<fmt:message key="${entry.key}singular" />', y: <c:out value="${entry.value}" />, tooltip: '<c:out value="${entry.value}" />&nbsp;<fmt:message key="${entry.key}plural" />' });
        		nrBB += 1;
        </c:if>
    </c:forEach>
    
    nrBB += 1;
    $(document).ready(function() {
    	
    	// ensure proper styling
    	$('body').addClass('tundra');

        var chartNumberOfBB = new dojox.charting.Chart2D('chartNumberOfBB').
                        setTheme(dojox.charting.themes.PlotKit.purple).
                        addAxis('x', { includeZero: false, min:0, max: nrBB, 
                        	labels: [
									{value: 0, text: '' }
									 <c:set var="axisElementCounter" value="0" />
                        	         <c:forEach items="${dialogMemory.bbMap}" var="entry" varStatus="status">
                        	         		<c:if test="${(functionalPermissionBD == true) && (entry.key == 'businessDomain.')}">
                        	         		    <c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                        	         			, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'}
                        	         		</c:if>
                                	        <c:if test="${(functionalPermissionBP == true) && (entry.key == 'businessProcess.')}">
                                	       		 <c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionBF == true) && (entry.key == 'businessFunction.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                            	        		, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionPROD == true) && (entry.key == 'product.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	       		 , {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	       	</c:if>
                                	        <c:if test="${(functionalPermissionBU == true) && (entry.key == 'businessUnit.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionBO == true) && (entry.key == 'businessObject.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                            	        		, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionISD == true) && (entry.key == 'informationSystemDomain.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionIS == true) && (entry.key == 'informationSystemRelease.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                            </c:if>                   	         
                                	        <c:if test="${(functionalPermissionINT == true) && (entry.key == 'interface.')}">
                                	       		<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'}
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionAD == true) && (entry.key == 'architecturalDomain.')}">
                                	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
                                	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'}
                                	        </c:if>
                                	        <c:if test="${(functionalPermissionTC == true) && (entry.key == 'technicalComponentRelease.')}">
	                            	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
	                            	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                            	        	</c:if>
                                        	<c:if test="${(functionalPermissionIE == true) && (entry.key == 'infrastructureElement.')}">
	                                        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
	                                        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'} 
                                       		</c:if>
                            	        	<c:if test="${(functionalPermissionPROJ == true) && (entry.key == 'project.')}">
	                            	        	<c:set var="axisElementCounter" value="${axisElementCounter + 1}" />
	                            	        	, {value:  <c:out value="${axisElementCounter}" />, text: '<fmt:message key="${entry.key}abbr" />'}
                            	        	</c:if>

                        	 		</c:forEach>
                        	 		, {value: nrBB, text: '' }
                        	] 
                        }).
                        addAxis('y', { vertical: true, fixLower: 'major', fixUpper: 'major', min: 0 }).
                        addPlot('default', { type: 'Columns', gap: 2 }).
                        addSeries('DataBB', chartData, {});
        var anim4b = new dojox.charting.action2d.Tooltip(chartNumberOfBB, 'default');
        var anim4c = new dojox.charting.action2d.Shake(chartNumberOfBB,'default');
        // connect own handler that will open bb-page onClick of a bar 
        chartNumberOfBB.connectToPlot('default', function(evt){processEvent(evt);});
                
        chartNumberOfBB.render();
        var legendBB = new dojox.charting.widget.Legend({ chart: chartNumberOfBB }, 'legendBB');     

    });
      
    $(document).ready(
	   createInititalPieChart
   );
    
</script>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>
</div>
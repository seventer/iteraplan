<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="componentModel" />
<tiles:useAttribute name="path_to_componentModel" />

<c:set var="bbForColumn" value="${componentModel.bbPageForColumn}"/>
<c:set var="bbForRow" value="${componentModel.bbPageForRow}"/>
<c:set var="rowColumnUpdateEnabled" value="${componentModel.rowColumnUpdateEnabled}"/>

<script type="text/javascript">
<!--
	function switchPage(id, increment) {
		var newValue = parseInt($('#' + id).val()) + increment;
		$('#' + id).val(newValue);
		flowAction('refresh');
	}

	function switchToPage(id, page) {
		$('#' + id).val(page);
		flowAction('refresh');
	}
	
	$(document).ready(addSearchShortcuts);
//-->
</script>

<c:if test="${componentMode == 'READ'}">
	<div id="businessMapping_rowColumnSettings" class="well">
		<%-- TODO: change table color --%>
    	<table class="tableInWell">
    		<tbody>
	    		<%-- Settings for Column Page --%>
				<tr>
	      			<td>
						<fmt:message key="businessMapping.columns"/>:
					</td>
					<td width="5%"/>
		    		<td>
	      				<c:if test="${componentModel.actualColumnPage > 0}">
				    		<a href="#" id="arrow_first_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.firstPage.tooltip'/>" class="link navigateSearchResults" onclick="switchToPage('actualColumnPage', 0);">
				    			<img alt="First Page" src="<c:url value="/images/pfeil_ganzlinks_schwarz.gif" />" title="" />
			        		</a>
						</c:if>
		    		</td>
					<td>
	      				<c:if test="${componentModel.actualColumnPage > 0}">
				    		<a href="#" id="arrow_prev_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.prevPage.tooltip'/>" class="link navigateSearchResults" onclick="switchPage('actualColumnPage', -1);">
				    			<img alt="Previous Page" src="<c:url value="/images/pfeil_einenlinks_schwarz.gif" />" title="" />
			        		</a>
						</c:if>
					</td>
					<td style="text-align: center">
						<form:hidden id="actualColumnPage" path="${path_to_componentModel}.actualColumnPage"/>
						<fmt:message key="search.page"/><c:out value=" "/><fmt:formatNumber value="${componentModel.actualColumnPage + 1}" maxFractionDigits="0"/>/<fmt:formatNumber value="${componentModel.numberOfColumnPages + 1}" maxFractionDigits="0"/>
	      			</td>
		  			<td>
						<c:if test="${componentModel.actualColumnPage < componentModel.numberOfColumnPages}">
							<a href="#" id="arrow_next_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.nextPage.tooltip'/>" class="link navigateSearchResults" onclick="switchPage('actualColumnPage', 1);">
				    			<img alt="Next Page" src="<c:url value="/images/pfeil_einenrechts_schwarz.gif" />" title="" />
			        		</a>
						</c:if>
		  			</td>
		    		<td>
						<c:if test="${componentModel.actualColumnPage < componentModel.numberOfColumnPages}">
			  				<a href="#" id="arrow_last_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.lastPage.tooltip'/>" class="link navigateSearchResults" onclick="switchToPage('actualColumnPage', ${componentModel.numberOfColumnPages});">
				    			<img alt="Last Page" src="<c:url value="/images/pfeil_ganzrechts_schwarz.gif" />" title="" />
			        		</a>
						</c:if>
		    		</td>
		    		<td width="5%"/>
				    <td style="text-align: right">
						<fmt:message key="businessMapping.numberOfColumns"/>:
					</td>
					<td>
						<form:input id="columnsPerPage" path="${path_to_componentModel}.columnsPerPage" size="5"/>
					</td>
					<td width="5%"/>
			      	<td width="20%" rowspan="2">
			      		<input type="button" class="btn" value="<fmt:message key="businessMapping.apply"/>" onclick="flowAction('refresh');" />
			      	</td>
				</tr>
	
				<%-- Settings for Row Page --%>
	      		<tr>
	      			<td>
						<fmt:message key="businessMapping.rows"/>:
					</td>
					<td/>
				    <td>
			      		<c:if test="${componentModel.actualRowPage > 0}">
						    <a href="#" id="arrow_first_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.firstPage.tooltip'/>" class="link navigateSearchResults" onclick="switchToPage('actualRowPage', 0);">
						    	<img alt="First Page" src="<c:url value="/images/pfeil_ganzlinks_schwarz.gif" />" title="" />
					        </a>
						</c:if>
				    </td>
					<td>
			     	 	<c:if test="${componentModel.actualRowPage > 0}">
						    <a href="#" id="arrow_prev_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.prevPage.tooltip'/>" class="link navigateSearchResults" onclick="switchPage('actualRowPage', -1);">
						    	<img alt="Previous Page" src="<c:url value="/images/pfeil_einenlinks_schwarz.gif" />" title="" />
					        </a>
						</c:if>
					</td>
					<td style="text-align: center">
				      <form:hidden id="actualRowPage" path="${path_to_componentModel}.actualRowPage"/>
				      <fmt:message key="search.page"/><c:out value=" "/><fmt:formatNumber value="${componentModel.actualRowPage + 1}" maxFractionDigits="0"/>/<fmt:formatNumber value="${componentModel.numberOfRowPages + 1}" maxFractionDigits="0"/>
			      	</td>
				  	<td>
						<c:if test="${componentModel.actualRowPage < componentModel.numberOfRowPages}">
							<a href="#" id="arrow_next_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.nextPage.tooltip'/>" class="link navigateSearchResults" onclick="switchPage('actualRowPage', 1);">
						    	<img alt="Next Page" src="<c:url value="/images/pfeil_einenrechts_schwarz.gif" />" title="" />
					        </a>
						</c:if>
				  	</td>
				    <td>
						<c:if test="${componentModel.actualRowPage < componentModel.numberOfRowPages}">
					  		<a href="#" id="arrow_last_page" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.lastPage.tooltip'/>" class="link navigateSearchResults" onclick="switchToPage('actualRowPage', ${componentModel.numberOfRowPages});">
						    	<img alt="Last Page" src="<c:url value="/images/pfeil_ganzrechts_schwarz.gif" />" title="" />
					        </a>
						</c:if>
				    </td>
				    <td/>
				    <td style="text-align: right">
						<fmt:message key="businessMapping.numberOfRows"/>:
					</td>
					<td>
						<form:input id="rowsPerPage" path="${path_to_componentModel}.rowsPerPage" size="5"/>
					</td>
					<td width="5%"></td>
	      		</tr>
	      	</tbody>
		</table>
	</div>
</c:if>

<c:if test="${componentMode == 'EDIT'}">
	<label class="checkbox">
		<form:checkbox path="${path_to_componentModel}.rowColumnUpdateEnabled" id="checkRowColumnUpdateBox" value="" onclick="flowAction('update');" />
		<fmt:message key="businessMapping.enableRowColumnUpdates"/>
	</label>
</c:if>

<%-- Table with Business Mappings --%>
<table id="businessMapping_table" class="table table-bordered table-striped table-condensed">
	<thead>
		<%-- Insert column updater ( standard values ) --%>
		<c:if test="${componentMode == 'EDIT' && rowColumnUpdateEnabled}">
			<tr>
				<td class="businessMapping_table_header tableInTable">
			  		<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" >
						<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}.allUpdateComponent"/>
						<tiles:putAttribute name="minimal" value="true"/>
						<tiles:putAttribute name="dynamically_loaded" value="true" />
					</tiles:insertTemplate>
					<input id="takeOver_all" type="button" class="btn" value="<fmt:message key="businessMapping.takeOverAllValues"/>" onclick="flowAction('rowColumnUpdate');" />
				</td>
				<td class="businessMapping_table_header" />
			  	<c:forEach var="bbColumn" items="${bbForColumn}" varStatus="columnIndex">
			  		<td class="businessMapping_table_header tableInTable">
				  		<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" >
							<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}.columnUpdateComponents[${columnIndex.index}]"/>
							<tiles:putAttribute name="minimal" value="true"/>
							<tiles:putAttribute name="dynamically_loaded" value="true" />
						</tiles:insertTemplate>
						<input id="takeOver_column_${columnIndex.index}" type="button" class="btn" value="<fmt:message key="businessMapping.takeOverColumnValues"/>" onclick="flowAction('rowColumnUpdate');" />
					</td>
			  	</c:forEach>
			</tr>
		</c:if>
		<tr>
		  	<c:if test="${componentMode == 'EDIT' && rowColumnUpdateEnabled}">
			  	<th class="businessMapping_table_header" />
			</c:if>
		  	<th class="businessMapping_table_header" />
		  	<c:forEach var="bbColumn" items="${bbForColumn}" varStatus="columnIndex">
		  		<th class="businessMapping_table_header" scope="col"><c:out value="${bbColumn.nonHierarchicalName}"/></th>
		  	</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="bbRow" items="${bbForRow}" varStatus="rowIndex">
			<tr>
				<%-- Insert column updater ( standard values ) --%>
	  			<c:if test="${componentMode == 'EDIT' && rowColumnUpdateEnabled}">
					<td class="businessMapping_table_header">
				  		<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" >
							<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}.rowUpdateComponents[${rowIndex.index}]"/>
							<tiles:putAttribute name="minimal" value="true"/>
							<tiles:putAttribute name="dynamically_loaded" value="true" />
						</tiles:insertTemplate>
						<input id="takeOver_row_${rowIndex.index}" type="button" class="btn" value="<fmt:message key="businessMapping.takeOverRowValues"/>" onclick="flowAction('rowColumnUpdate');" />
					</td>
				</c:if>
				<th class="businessMapping_table_header" >
					<c:out value="${bbRow.nonHierarchicalName}" />
				</th>
				<c:forEach var="bbColumn" items="${bbForColumn}" varStatus="columnIndex">
					<td class="businessMapping_table_cell">
						<%-- Insert a list for all cells in the table --%>
						<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" >
							<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}.cellComponentsRows[${rowIndex.index}].cellComponents[${columnIndex.index}]"/>
							<tiles:putAttribute name="minimal" value="true"/>
							<tiles:putAttribute name="dynamically_loaded" value="true" />
						</tiles:insertTemplate>
					</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</tbody>
</table>
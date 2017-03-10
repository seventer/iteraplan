<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="pathToComponentModel" />
<tiles:useAttribute name="loggedInUserSelected" ignore="true" />
<%-- if true, show a additional submit button for changing the dataSource onClick (used for Configuration/manage.jsp), 
	 otherwise show no Button --%>
<tiles:useAttribute name="showSubmitButton" ignore="true" />

<itera:define id="componentMode" name="memBean" property="${pathToComponentModel}.componentMode" />
<%-- here we want to compose the string only, not dereference to an object--%>
<c:set var="selected" value="${pathToComponentModel}.selectedDataSource" />

<div id="RoutingDatasourceComponentComboboxViewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="routing.datasource" />
      	<c:if test="${componentMode != 'READ' && loggedInUserSelected}">
        	&nbsp;2)
		</c:if>
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<c:choose>
	    			<c:when test="${componentMode != 'READ' && loggedInUserSelected}">
	         			<itera:write name="memBean" property="${selected}" escapeXml="true" />
	    			</c:when>
	    			<c:when test="${componentMode != 'READ'}">
	        			<itera:define id="available" name="memBean" property="${pathToComponentModel}.availableDataSources" />
	        			<form:select path="${selected}" id="selectedDatasource">
	        				<form:options items="${available}" itemLabel="key" itemValue="key"/>
	        			</form:select>
	        			<form:errors path="${selected}" cssClass="errorMsg" htmlEscape="false"/>
	    			</c:when>
	    			<c:otherwise>
	          			<itera:write name="memBean" property="${selected}" escapeXml="true" />
	    			</c:otherwise>
	  			</c:choose>
	  			<c:if test="${showSubmitButton}">
	    			<input type="hidden" name="doReset" value="false" />
	    
			    	<script type="text/javascript">    
			    	<!--
			    		function confirmDatasourceChange() {
			      
			      			<%-- check if the datasource has been changed. --%>
			      			var dropdownValue = $('#selectedDatasource').val();
			      
		          			setHiddenField('doReset','false');
		          
			      			if ('<c:out value="${dialogMemory.routingDatasourceModel.selectedDataSource}"/>' != dropdownValue) {
			        
			        			showConfirmDialog('<fmt:message key="routing.confirmDataSourceChange" />', '<fmt:message key="routing.confirmDataSourceChange" />', function(){
			        				setHiddenField('doReset', 'true');
				        			submitForm('saveDataSource.do');
			        			});
			        
			      			} else {
				    			// submit unconditionally
			        			submitForm('saveDataSource.do');
			      			}
			    		}   
			    	//-->    
			    	</script>
			    	<br/>  
					<input type="button" value="<fmt:message key="button.ok" />" onclick="confirmDatasourceChange();" class="btn" />
	    		</c:if>
			</div>
		</div>
	</div>
</div>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<script type="text/javascript">
	<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
	addMiscShortcuts();
	
	<%-- adds the shortcut keys to quickly perform then actions such as 'search' and 'new', and to quickly navigate through search results  --%>
	addSearchShortcuts();
</script>         

<c:set var="searchHintEscaped">
	<fmt:message key="manageSearch.hint" var="searchHint"/>
	<c:out value="${searchHint}"/>
</c:set>
<c:set var="specialCharHintEscaped">
	<fmt:message key="manageSearch.warning.specialcharacters" var="specialCharHint"/>
	<c:out value="${specialCharHint}"/>
</c:set>

<%-- Displays the search field and the buttons of the search --%>
<div id="searchForm">
	<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermSearch}" scope="request" />
	<c:choose>
	<c:when test="${functionalPermission == true}">
		<fieldset>
			<legend class="searchheading"><fmt:message key="manageSearch.heading"/></legend>	
		</fieldset>
		<div id="ReportRequestModul" class="row-fluid module">
			<div class="row-fluid">
				<div class="module-body">
					<div class="row-fluid">
						<label for="searchQuery"><fmt:message key="search.helptext"/></label>            	
						<div class="input">
							<i class="icon-search"></i>
							<form:input id="searchQuery" path="searchField" maxlength="40" size="100" />				
							<form:errors path="searchField" cssClass="error" />				
							<script type="text/javascript">
								$('#searchQuery').popover({
									trigger : 'focus',
									placement : 'right',
									content : '${searchHintEscaped}'
								});
								
								$('#searchQuery').tooltip({
									trigger : 'manual',
									placement : 'right',
									title : '${specialCharHintEscaped}'
								});
								
								$('#searchQuery').keyup(function() {
									if ($(this).val().match(/[^a-zA-Z0-9\\s*?À-ÿ]+/)) {
										$(this).tooltip('show');
									} else {
										$(this).tooltip('hide');
									}
								});
				
								// Add additional support: Enter-Key submits the form by clicking the Search-Button
								$('#searchQuery').keypress(function(evt){ onEnterClickButton('sendSearchquery', evt); });
							</script>	
						</div>
						<br />
						<div class="btn-toolbar">
							<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="search.sendQuery.tooltip"/>">
					   			<input type="submit" id="sendSearchquery" value="<fmt:message key="button.sendSearchquery" />" onclick="createHiddenField('requestType', 'search');" class="btn btn-primary" />
					   		</a>
					   		<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.resetReport.tooltip"/>">
					    		<input type="submit" id="buttonResetReport" value="<fmt:message key="button.reset" />" onclick="createHiddenField('requestType', 'reset');" class="btn"/>
					    	</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
	  <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
	</c:choose>
</div>
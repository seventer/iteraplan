<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="de.iteratec.iteraplan.presentation.tags.tab.TabgroupTag" %>

<%@ page import="de.iteratec.iteraplan.common.UserContext"%>
<% 
  String oldEtag = request.getHeader("If-None-Match");
  String etag = (UserContext.getCurrentLocale() != null ? UserContext.getCurrentLocale().toString() : "");
  if (etag != null && oldEtag != null && etag.equals(oldEtag)) {
    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    return;
  }
  response.setHeader("ETag", etag);
  // allow caching for 30 min (=1800s)
  // response.addHeader("Cache-Control", "public, max-age=1800");
%>

<fmt:setLocale value="${userContext.locale.language}"
	scope="request" />
<spring:eval var="auditLogging"
	expression="@applicationProperties.getProperty('audit.logging.enabled')" />
<spring:eval var="lastmodificationLogging"
	expression="@applicationProperties.getProperty('lastmodification.logging.enabled')" />

function confirmDeleteBuildingBlocks(callback) {
  showConfirmDialog("<fmt:message key='global.confirmDelete' />",
	  				"<fmt:message key='global.confirmDeleteBuildingBlocks' />",
	                callback)
}

function confirmDelete(callback) {
  showConfirmDialog("<fmt:message key='global.confirmDelete' />",
					"<fmt:message key='global.confirmDelete' />",
					callback);
}

function confirmMassUpdate(callback) {
  showConfirmDialog("<fmt:message key='massUpdates.runMassUpdate' />",
					"<fmt:message key='global.confirmMassUpdate' />",
					callback);
}

function confirmMassenDelete(callback) {
  showConfirmDialog("<fmt:message key='massUpdates.requestMassDelete' />",
					"<fmt:message key='global.confirmMassenDelete' />",
					callback);
}

function confirmLogout(callback) {
  showConfirmDialog("<fmt:message key='menu.logout' />",
					"<fmt:message key='global.confirmLogout' />",
					callback);
}

function confirmEdited() {
  showConfirmDialog('<fmt:message key='navigation.reset' />',
					'<fmt:message key='global.confirmResetWithOpenTransactions' />',
					'<c:url value="/show/restart" />');
}

function msgOkCancel(callback) {
  showConfirmDialog("<fmt:message key='button.cancel' />",
					"<fmt:message key='global.confirm' />",
					callback);
}

<%-- embed two constants from Java class into the JS code --%>
function newSwitchTab(tabgroup, tabNumber){
	<%-- get all the tabs and change them to unselected --%>
	var node = getElementByIdSafe('<%= TabgroupTag.TABPREFIX %>'+tabgroup);
	var tabsToHide = getElementsByClass(node, 'tab', 'li');
	
	var selectedTabFound = false;
	
	for(var i=0; i < tabsToHide.length; i++){
	    var cssClass = tabsToHide[i].className;
	    var numberIndex = cssClass.indexOf(''+tabNumber);
		
		if (numberIndex != -1) 
		{  selectedTabFound = true;
		   break; 
		}
	}

	if (selectedTabFound == true) 
	{ 
  		
		for(var i=0; i < tabsToHide.length; i++)
		{
			var Classes = tabsToHide[i].className;
			var inactiveIndex = Classes.indexOf("active");
			var isActive = (inactiveIndex >= 0);
			
			if (isActive)
			{
			  var incr = i + 1;
				tabsToHide[i].setAttribute('class', 'tab tab' + incr + ' link');
				tabsToHide[i].setAttribute('className', 'tab tab' + incr + ' link');
			}
		}
		
		<%-- get the clicked tab and change it to selected --%>
		var allTabsToShow = getElementsByClass(node, '' + tabNumber, 'li');
		
		var tabToShow = allTabsToShow[0];
		
		var newClass = "tab " + tabNumber + " active link";
		
		tabToShow.setAttribute('class', newClass);
		tabToShow.setAttribute('className', newClass);
		
		<%-- get all the tabcontents and hide them --%>
		var node = getElementByIdSafe('<%= TabgroupTag.CONTPREFIX %>'+tabgroup);
		var tabcontentsToHide = getElementsByClass(node, 'tabcontent', 'div');
		for(var i=0; i < tabcontentsToHide.length; i++){
			tabcontentsToHide[i].setAttribute('class', 'tabcontent hidden');
			tabcontentsToHide[i].setAttribute('className', 'tabcontent hidden');
		}
		<%-- get the tabcontent of the clicked tab and show it --%>
		var showId = tabToShow.id.replace("tab_", "");
		
		var tabcontentToShow = getElementByIdSafe(showId);
		tabcontentToShow.setAttribute('class', 'tabcontent visible');
		tabcontentToShow.setAttribute('className', 'tabcontent visible');
		
		<%-- Make Ajax remote call (DWR) to save selected tab info in GuiContext --%>	
		GuiService.setSelectedTab(showId);
	}		
} 

<%-- switches to the next tab in the specified direction (either 1 or -1) --%>
function rotateTabs(tabgroup, direction)
{
   var node = getElementByIdSafe('<%= TabgroupTag.TABPREFIX %>'+tabgroup);
   var allTabs = getElementsByClass(node, 'tab', 'li');
   
   	for(var i=0; i < allTabs.length; i++){
   	
	    var isActiveIndex = allTabs[i].className.indexOf("tabColorSelected");
        
        if (isActiveIndex >= 0)
        {
           var newTab = ((i + 1) + direction) % allTabs.length;
           
           if (newTab == 0) { newTab = allTabs.length; }
           
           newSwitchTab(tabgroup, newTab);
           
           break;
        }
    }
}

function changeLanguage(language) {
  		var nextUrl = document.location.pathname;
		
		var reqParameters = document.location.search.slice(1); <%-- remove leading ? from parameters --%>
		if (reqParameters.length > 0) {
			if (reqParameters.indexOf('locale=') == -1) {
				nextUrl += "?" + reqParameters + "&locale=" + language;
			} else {
				reqParameters = reqParameters.replace(/locale=[a-z]{2}/i, 'locale=' + language);
				nextUrl += "?" + reqParameters;
			}
		} else {
			nextUrl += "?locale=" + language;
		}
		document.location.href = nextUrl; 
	return true;
}

function catchTabKeyAndFilterList(id , e, pattern, list) {
	 var keycode;
	 if (window.event) {
	    keycode = window.event.keyCode;
	 } else if (e) { 
	    keycode = e.which;
	 } else {
	    return true;
	 }

     if (keycode != 9) { // if isn't the tab key
	    filterList(pattern, list);
		return false;
	 }
}

<%-- Shortcut Functions --%>

function addShortcutForClick(shortcutKey,elementId)
{
	shortcut.add(shortcutKey, function() {
	   var clickable = $('#' + elementId);
	   
	   if (clickable !== null) {  clickable.click(); }
	}, { 'disable_in_input':true } );
}

 
function addTransactionBarShortcuts()
{
    <c:set var="transactions">edit, delete, newRelease, copyBuildingBlock, copyAttribute, cancel, save, refresh, close</c:set>
    	
    <c:forTokens items="${transactions}" delims=", " var="transaction">
       addShortcutForClick("<fmt:message key='transaction.${transaction}.shortcut'/>","<fmt:message key='transaction.${transaction}.id'/>");
	</c:forTokens>
}


function addTabShortcuts()
{
   <c:set var="tabNumbers">first, second, third, fourth, fifth</c:set>
    	
    <c:forTokens items="${tabNumbers}" delims=", " var="tabNumber">
	   shortcut.add("<fmt:message key="tab.${tabNumber}.shortcut"/>", function() {
	      newSwitchTab("<fmt:message key='tabgroup.tag'/>","<fmt:message key='tab.${tabNumber}.class'/>");
       }, { 'disable_in_input':true } );
    </c:forTokens>

    shortcut.add("Shift+Left", function() {
	   rotateTabs("<fmt:message key='tabgroup.tag'/>", -1);
    }, { 'disable_in_input':true } );    
    
    shortcut.add("Shift+Right", function() {
	   rotateTabs("<fmt:message key='tabgroup.tag'/>", 1);
    }, { 'disable_in_input':true } );    
    
}

function addJumpToInterfaceShortcut()
{
   addShortcutForClick("<fmt:message key='jumpToInterface.shortcut'/>","<fmt:message key='jumpToInterface.id'/>");
}

function addSearchShortcuts()
{
     <c:set var="searchActions">sendQuery, newElement, nextPage, prevPage, lastPage, firstPage</c:set>
     
     <c:forTokens items="${searchActions}" delims=", " var="searchAction">
        addShortcutForClick("<fmt:message key="search.${searchAction}.shortcut"/>","<fmt:message key='search.${searchAction}.id'/>");
     </c:forTokens>
}

function addMiscShortcuts()
{
   <c:set var="miscButtons">back, generate, sendQuery, saveQuery, confirmSelection, resetReport, changeContent, filterContent</c:set>
   
   <c:forTokens items="${miscButtons}" delims=", " var="miscButton">
      addShortcutForClick("<fmt:message key="button.${miscButton}.shortcut"/>","<fmt:message key='button.${miscButton}.id'/>");
   </c:forTokens>
}

function preLoadSavedQuery(queryName, queryId, url, action) {
		showConfirmDialog("<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>", 
				"<fmt:message key='graphicalReport.executeSavedQuery'><fmt:param>" + queryName +"</fmt:param></fmt:message>",
				function() {
					changeLocation(url + "?_eventId=" + action + "&savedQueryId=" + queryId);
				});
}

function loadSavedQueries(reportType, destinationId, flow_url) {

	var liLoading = $('<li><fmt:message key="global.loading"/></li>').appendTo('#' + destinationId);
	
	$.ajax({
		url : '<c:url value="/graphicalreporting/loadSavedQueries.do" />',
		data : {
			'type': reportType,
		},
		dataType : "json",
		success :  function(data){
			
			var dropdownList = $('#' + destinationId);
			if(dropdownList != null){
				liLoading.remove();
				
				for (var i = 0; i < data.length; i++) {
					var liElement = $("<li/>");
					var params = "'" + escapeJavaScript(data[i].name) + "', '" + data[i].id + "', '" + flow_url + "', 'loadSavedQuery'";
					liElement.append($('<a href=\"javascript:preLoadSavedQuery(' + params + ');\" >' + data[i].name + '</a>'));
	    			dropdownList.append(liElement);
	    		}
	    		
	    		if(data.length <= 0) {
	    			dropdownList.remove();
	    		}
	    	}
	   },
	   error : function(error) {
			
			var dropdownList = $('#' + destinationId);
			if(dropdownList != null){
				var liError = $("<li/>");
				liError.text(error);
				liError.appendTo(dropdownList);
	    	}
	    }
	});
}

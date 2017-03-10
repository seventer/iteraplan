<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<tiles:useAttribute name="searchFormIdToUse" />
<tiles:useAttribute name="filterInputId" />
<tiles:useAttribute name="tableToFilterId" />

<div id="${searchFormIdToUse}" class="pull-right">
	<script>
		(function ($) {
		  jQuery.expr[':'].Contains = function(a,i,m){
			  return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase())>=0;
		  };
	
		  var TYPE_DELAY_MS = 400;
		  
		  function listFilter(searchForm, ShowSavedQueryTable) {
			var input = $("#${filterInputId}");
	
			var timeoutId;
			$(input).bind("propertychange input", function () {
				var filter = $(this).val();
				
		    	window.clearTimeout(timeoutId);
		        timeoutId = window.setTimeout(function() {
		        	
					if(filter) {
					  $(ShowSavedQueryTable).find("a:not(:Contains(" + filter + "))").closest("tr").hide();
					  $(ShowSavedQueryTable).find("a:Contains(" + filter + ")").closest("tr").show();
					} else {
					  $(ShowSavedQueryTable).find("tr").show();
					}
					
				    return false;
				}, TYPE_DELAY_MS);
				
		    });
		  }
	
		  $(function () {
			listFilter($("#${searchFormIdToUse}"), $("#${tableToFilterId}"));
		  });
		}(jQuery));
		
	</script> 
	<i class="icon-search" ></i>
	<input id="${filterInputId}" class="filterinput" type="text" align="top" style="height:14px"/>
	<script>
		$('#${tableToFilterId}').tooltip({
			trigger : 'hover',
			placement : 'bottom',
			title : '${filterTooltip}'
		});
	</script>
	<a id="removeFilter" class="link" title="<fmt:message key="global.filter.clear"/>" 
		onclick="$('#${filterInputId}').val('');  $('#${filterInputId}').trigger('propertychange');" >
		<i class="icon-remove"></i>
	</a>
</div>	
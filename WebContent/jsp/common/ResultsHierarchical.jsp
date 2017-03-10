<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>


<tiles:useAttribute name="addManageShortcuts" ignore="true" />

<tiles:useAttribute name="entityNamePluralKey" />

<tiles:useAttribute name="resultColumnDefinitions" ignore="true" />


<script type="text/javascript">
  
(function ($) {
  		
	String.prototype.repeat = function( num ) {
		return new Array( num + 1 ).join( this );
	};
  		
	jQuery.fn.extend({
	
	toggleReorderingEnabled: function () {
		var reorderButton = $(this);
		var containingTable = $("#resultTable");
		createHiddenField("enableReorder", !reorderButton.hasClass("active"));
		self.document.forms[0].submit();
	},
  			
	toggleNode: function () {
  		var pmIconContainer = $(this).toggleClass("collapsed-node");
  		var isCollapsed = pmIconContainer.hasClass("collapsed-node");
  		var outerRow = pmIconContainer.closest("tr");
  		outerRow.data("collapsed", isCollapsed);
  		var treeLevel = outerRow.data("treelevel");
  		var nextRow = outerRow.next("tr");
  		while (nextRow.data("treelevel") > treeLevel) {
			if (isCollapsed) {
				nextRow.hide();
	  		} else {
	  			nextRow.show();
	  		}
	  				
			if (nextRow.data("collapsed")) {
				var subtreeNodeLevel = nextRow.data("treelevel");
				nextRow = nextRow.next();
				while (nextRow.data("treelevel") > subtreeNodeLevel) {
					nextRow = nextRow.next();
				}
			} else {
				nextRow = nextRow.next();
			}
		}
  				
		var elemId = outerRow.data("nodeid");
		if (isCollapsed) {
			$.get('tree.do', {collapseId : elemId});
		} else {
			$.get('tree.do', {expandId : elemId});
		}
  		return this;
	},
  		
  	expandAllNodes: function () {
  		$(this).find("tbody > tr[data-treelevel]").each(function() {
			$(this).data("collapsed", false).show();
  		});  				
		$(this).find(".tree-icon").removeClass("collapsed-node");
  		return this;
  	},
  			
  	collapseAllNodes: function () {
  		$(this).find("tbody > tr[data-treelevel]").not("[data-treelevel='1']").each(function() {
			$(this).data("collapsed", true).hide();
		});
  		$(this).find(".tree-icon").addClass("collapsed-node");
  		return this;
  	},
  	
  	cancel: function() {
		$(this).data("cancel", true);
		$(this).sortable("cancel");
		$(this).find("tr").each(function(){
			$(this).removeClass("movedChildNode").removeClass("sorting").removeClass("dropNotAllowed");
			});
  	}
  			
  	});
  		
	$(function () {
  			
  		<%-- Return a helper which preserves width and doesn't collapse when being dragged --%>
  	  	var fixHelper = function(e, ui) {
  	  		ui.children().each(function(index) {
  	  			var currentCell = $(this);
  	  			currentCell.width($(this).width());
  	  		});
  	  		return ui;
  	  	};
  			
   		$('#resultTable').sortable({
			items: "tbody > tr:visible",
			axis: 'y',
			cancel: '.partial-loading-placeholder',
			delay: 150,
			helper: fixHelper,
			opacity: 0.9,
			forcePlaceholderSize: true,
			disabled: true, 
			cursor: "move",
			tolerance: "pointer",
			start: function(event, ui) {
				var treelevel = ui.item.data("treelevel");
		  		var nextrow = ui.item.next("tr").next("tr"); <%-- skip the placeholder --%>
		  		<%-- the sortable plugin only skips if the absolute index (ignoring hidden nodes) does not change on drop --%>
		  		<%-- we need to skip even if the index changed but if the resulting position stays the same --%>
		  		<%-- therefore we save the current position here and check against it in the update method --%>
		  		ui.item.data("previd", ui.item.prevAll("tr:visible").first().data("nodeid"));
		  		ui.item.data("nextid", nextrow.data("nodeid"));
				if (!ui.item.data("collapsed")){
			  		while (nextrow.data("treelevel") > treelevel) {
						nextrow.addClass("movedChildNode");
						nextrow = nextrow.next();
					}
				}
				ui.placeholder.colDefs = $(this).find('colgroup > col');
				var noOfCols = ui.placeholder.colDefs.length; 
				var phColHtml = "<td></td>".repeat(noOfCols);
				ui.placeholder.html(phColHtml);
				ui.helper.find("*").each(function(){$(this).addClass("sorting");});
			},
			change: function(event, ui) {
				var prev = ui.placeholder.prevAll("tr:visible").first();
				if (prev.hasClass("movedChildNode")) {
					$('body').addClass("notAllowed");
					ui.helper.addClass("dropNotAllowed").removeClass("sorting");
				} else {
					$('body').removeClass("notAllowed");
					ui.helper.removeClass("dropNotAllowed").addClass("sorting");
				}
			},
			beforeStop: function(event, ui) {
				ui.helper.find("*").each(function(){$(this).removeClass("sorting").removeClass("dropNotAllowed");});
			},
			stop: function(event, ui) {
    			$('#resultTable').find("tr").each(function(){
    				$(this).removeClass("movedChildNode");
    				});
			},
			update: function(event, ui) {
				var next = ui.item.nextAll('tr:visible').first();
				var prev = ui.item.prevAll('tr:visible').first();
				if (prev.hasClass("movedChildNode")){
	    			$('#resultTable').cancel();
				} else if (ui.item.data("previd") == prev.data("nodeid") && ui.item.data("nextid") == next.data("nodeid")) {
					<%-- have we been dropped at the logical position we started at? skip if so --%>
					$('#resultTable').cancel();
				}
				else if (!$('#resultTable').data("cancel")){
					$.get('tree.do', {
							sortItem : ui.item.data("nodeid"),
							afterItem : prev.data("nodeid"),
							beforeItem : next.data("nodeid")
						}
					)
					.always(function() {  self.document.forms[0].submit();  });
				}
				
    			$('#resultTable').data("cancel", false);
			}
		});
   		
   		<%-- for performance reasons --%>
   		<%-- only use placeholder styling when not on IE or if version of IE is at least 9 --%>
   		if (!$.browser.msie || ($.browser.msie && $.browser.version >= 9))
   			$('#resultTable').sortable("option", "placeholder", "sortable-placeholder");
  		
   		<%-- Init tree icons --%>
   		$("#resultTable td.showPM").each(function (index, elem) {
   			var prevCell = $(this).prev("td");
   			var isCollapsed = prevCell.closest("tr").data("collapsed");
   			prevCell.append("<span class='tree-icon pull-right'>&nbsp;</span>").find("span").click(function() {
   				$(this).toggleNode();
   			}).each(function() {
   				if (isCollapsed) {
   					$(this).addClass("collapsed-node");
   				}
   			});
   		});
    		
   		$("#collapseAllButton").click(function () {
   			$("#resultTable").collapseAllNodes();
   			$.get('tree.do', {collapseAll : true});
   		});
    		
   		$("#expandAllButton").click(function () {
   			$("#resultTable").expandAllNodes();
   			$.get('tree.do', {expandAll : true});
		});
   		
   		var reorderButton = $("#reorderToggleButton");
   		reorderButton.popover({
   			title: '<fmt:message key="button.treeview.reorder.popover.title" />',
   			content: '<fmt:message key="button.treeview.reorder.popover.text" />',
   			html: false,
   			placement: 'top',
   			trigger: 'hover',
   			delay: {hide: 200}
   		});
   		
    	$(document).keydown(function(e) {
    		if (e.keyCode == 27) {
    			$('#resultTable').cancel();
    		}
    	});
    	
    	<c:choose>
        	<c:when test="${not dialogMemory.treeViewHelper.reorderingEnabled}">
        		$("#resultTable").sortable("disable");
        	</c:when>
        	<c:otherwise>
        		$("#resultTable").sortable("enable");
        	</c:otherwise>
		</c:choose>
	});
  		
}(window.jQuery));
</script>
  
  <style>
  
  	body.notAllowed {
  		cursor: not-allowed !important;
  	}
  	body.sorting {
  		cursor: move !important;
  	}
  
  	tr.movedChildNode > * {
  		color: #cccccc;
  	}
  	
  	tr.movedChildNode i {
  		opacity: 0.2 !important;
  	}
  	
  	tr.dropNotAllowed * {
  		cursor: not-allowed !important;
  	}
  	
	tr.sorting * {
  		cursor: move !important;
  	}
  	
  	.partial-loading-placeholder {
  		cursor: pointer;
  	}
  
    #resultTable {
      table-layout: fixed;
    }
    
    #resultTable > colgroup > col.hierarchy-col {
      width: 35px;    
    }
    
    #resultTable > colgroup > col.firstPMCol {
      width: 15px;
    }
  
    #resultTable > tbody > tr > td.firstPMCell {
      padding-right: 0 !important;
    }
    
    tr.sortable-placeholder > td {
      border-top: 2px solid #AC007C !important;
      border-bottom: 2px solid #AC007C !important;
    }
    
    tr.sortable-placeholder > td:first-child {
      border-left: 2px solid #AC007C !important;
    }
    
    tr.sortable-placeholder > td:last-child {
      border-right: 2px solid #AC007C !important; 
    }
    
    col.actionsColumn {
      vertical-align: middle;
      white-space: nowrap;
      width: 65px;
    }
    
  </style>


<%-- init permissions --%>
<c:set var="updatePermissionType">
  <itera:write name="userContext"
    property="perms.userHasBbTypeUpdatePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
</c:set>
<c:set var="createPermissionType">
  <itera:write name="userContext"
    property="perms.userHasBbTypeCreatePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
</c:set>
<c:set var="deletePermissionType">
  <itera:write name="userContext"
    property="perms.userHasBbTypeDeletePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
</c:set>
<c:set var="hasPermissionForActions"
  value="${createPermissionType || updatePermissionType || deletePermissionType}" />


<div id="ResultTableModule" class="row-fluid module">
  <div class="module-heading-nopadding">
        <div class="pull-left" style="padding: 8px 0px;">
          <c:if test="${addManageShortcuts == true}">
            <c:if test="${hasPermissionForActions}">
              <c:set var="result_column_count" value="${result_column_count+1}" />
            </c:if>
          </c:if>
          <c:set var="found"><fmt:message key="search.found.header" /></c:set>
          <c:out value="${dialogMemory.treeViewHelper.resultCount} " />
          <fmt:message key="${entityNamePluralKey}" />
          <c:out value=" ${fn:toLowerCase(found)} " />
        </div>    
        <div class="pull-right" style="margin-right: 8px; margin-top: 4px; font-weight: normal;">
        
          <%-- Button to collapse all nodes --%>
          <span style="margin-left: 7px; margin-top: 4px;">
            <button id="collapseAllButton" class="btn" type="button">
              <i class="icon-resize-small"></i>
              <fmt:message key="button.collapseAll" />
            </button>
          </span>
          
          <%-- Button to expand all nodes --%>
          <span style="margin-left: 7px; margin-top: 4px;">
            <button id="expandAllButton" class="btn" type="button">
              <i class="icon-resize-full"></i>
              <fmt:message key="button.expandAll" />
            </button>
          </span>
          
          <%-- Button to sort by position --%>
          <c:if test="${not dialogMemory.treeViewHelper.orderedByPosition}">
            <span style="margin-left: 7px; margin-top: 4px;">
              <button class="btn" type="button" onclick="createHiddenField('sortByPosition', 'true'); self.document.forms[0].submit();">
                <fmt:message key="button.sortByPosition" />
              </button>
            </span>
          </c:if>

          <%-- Button to toggle reordering of elements --%>
          <c:choose>
            <c:when test="${dialogMemory.treeViewHelper.reorderingEnabled}">
              <fmt:message var="reorderButtonCaption" key="button.treeview.disableReordering" />
              <c:set var="isReorderActiveCss" value=" active" />
            </c:when>
            <c:otherwise>
              <fmt:message var="reorderButtonCaption" key="button.treeview.enableReordering" />
              <c:set var="isReorderActiveCss" value="" />
            </c:otherwise>
          </c:choose>
          <c:if test="${dialogMemory.treeViewHelper.reorderingPossible}">
            <span style="margin-left: 7px; margin-top: 4px;">
              <button id="reorderToggleButton" class="btn${isReorderActiveCss}" type="button" <c:if test="${not updatePermissionType}">disabled="disabled"</c:if> onclick="$(this).toggleReorderingEnabled();">
                <c:out value="${reorderButtonCaption}" />
              </button>
            </span>
          </c:if>
          
          <%-- Button to add new column --%>
          <c:if test="${fn:length(dialogMemory.tableState.availableColumnDefinitions) gt 0}">
              <span style="margin-left: 7px; margin-top: 4px;">
                  <button data-toggle="modal" href="#addColumnContainer" class="link btn">
                    <i class="icon-plus" style="margin-right: 3px;"></i>
                    <fmt:message key="reports.addColumn.button" />
                  </button>
              </span> 
          </c:if>
          
          <%-- Button to switch back to list view --%>
          <span style="margin-left: 7px; margin-top: 4px;">
            <button type="button" class="btn" onclick="createHiddenField('showTreeView', 'false'); self.document.forms[0].submit();">
              <i class="icon-eye-open"></i>
              <fmt:message key="reports.list" />
            </button>          
          </span>
          
        </div>
  </div>
  <div class="row-fluid">
    <div class="module-body-table">
      <div class="row-fluid"> 
        
        <table id="resultTable" class="table table-striped table-condensed tableInModule">
          <colgroup>
          
            <col class="firstPMCol"></col>
          
            <c:forEach begin="1" end="${dialogMemory.treeViewHelper.maxTreeLevel}" step="1">
              <col class="hierarchy-col"></col>
            </c:forEach>
            
            <c:forEach items="${resultColumnDefinitions}" var="columnProps" varStatus="s">
              <col></col>
            </c:forEach>
            
            <c:if test="${addManageShortcuts == true}">
              <c:if test="${hasPermissionForActions}">
                <col class="actionsColumn"></col>
              </c:if>
            </c:if>
            
          </colgroup>
          <thead>
            <tr>
            
              <c:forEach items="${resultColumnDefinitions}" var="columnProps" varStatus="s">
              
                <c:set var="calcColSpanTh">
                  <c:choose>
                    <c:when test="${s.first}">
                      <c:out value="${dialogMemory.treeViewHelper.maxTreeLevel + 2}" />
                    </c:when>
                    <c:otherwise>
                      <c:out value="1" />
                    </c:otherwise>
                  </c:choose>
                </c:set>

                <th colspan="${calcColSpanTh}" class="link sortable" onclick="sortByColumn('${s.count-1}')">
                      <span style="white-space: nowrap;">
                        <c:choose>
                          <c:when test="${columnProps.attributeType ne null}">
                            <c:out value="${columnProps.tableHeaderKey}" />                       
                          </c:when>                   
                          <c:otherwise>
                            <fmt:message key="${columnProps.tableHeaderKey}" />
                          </c:otherwise>
                        </c:choose>
                        <c:choose>
                          <c:when test="${resultList.sort.ascending == true && resultList.sort.property == columnProps.beanPropertyPath}">
                            &nbsp;<i class="icon-chevron-up"></i>
                          </c:when>
                          <c:when test="${resultList.sort.ascending == false && resultList.sort.property == columnProps.beanPropertyPath}">
                            &nbsp;<i class="icon-chevron-down"></i>
                          </c:when>
                        </c:choose>
                      </span>
                      
                      <span class="dontwrap" style="margin-left: 7px;">
                        <c:if test="${s.count gt 2}">
                        <img
                          src="<c:url value="/images/SortArrowLeft.gif"/>"
                          onclick="createHiddenField('colMoveIndex', '${s.count-1}'); createHiddenField('colMoveDirection', 'left'); self.document.forms[0].submit();"
                          class="link"
                          alt="<fmt:message key='tooltip.moveLeft'/>"
                          title="<fmt:message key='tooltip.moveLeft'/>" />
                        </c:if>
                        <c:if test="${s.count gt 1}">
                          <a class="link" href="#"
                            title="<fmt:message key='tooltip.remove'/>"
                            onclick="createHiddenField('colRemoveIndex', '${s.count-1}'); self.document.forms[0].submit();">
                            <i class="icon-remove"></i>
                          </a>
                        </c:if>
                        <c:if test="${s.count gt 1 and s.count lt fn:length(resultColumnDefinitions)}">
                          <img
                            src="<c:url value="/images/SortArrowRight.gif"/>"
                            onclick="createHiddenField('colMoveIndex', '${s.count-1}'); createHiddenField('colMoveDirection', 'right'); self.document.forms[0].submit();"
                            class="link"
                            alt="<fmt:message key='tooltip.moveRight'/>"
                            title="<fmt:message key='tooltip.moveRight'/>" />
                        </c:if>
                      </span>
                  </th>
                
              </c:forEach>
              <c:if test="${addManageShortcuts == true}">
                <c:if test="${hasPermissionForActions}">
                  <th><fmt:message
                      key="global.manage" /></th>
                </c:if>
              </c:if>
            </tr>
          </thead>
          
          <tbody>
            <c:forEach items="${dialogMemory.treeViewHelper.sortedNodes}" var="hierarchicalEntityNode">

              <c:choose> 
                <c:when test="${hierarchicalEntityNode.partialPlaceholder}">
                  <tiles:insertTemplate template="HierarchicalPlaceholder.jsp">
                    <tiles:putAttribute name="hierarchicalEntityNode" value="${hierarchicalEntityNode}" />
                    <tiles:putAttribute name="resultColumnDefinitions" value="${resultColumnDefinitions}" />
                    <tiles:putAttribute name="addManageShortcuts" value="${addManageShortcuts}" />
                    <tiles:putAttribute name="hasPermissionForActions" value="${hasPermissionForActions}" />
                  </tiles:insertTemplate>
                </c:when>
                
                <c:otherwise>
                  <tiles:insertTemplate template="HierarchicalRow.jsp">
                    <tiles:putAttribute name="hierarchicalEntityNode" value="${hierarchicalEntityNode}" />
                    <tiles:putAttribute name="resultColumnDefinitions" value="${resultColumnDefinitions}" />
                    <tiles:putAttribute name="addManageShortcuts" value="${addManageShortcuts}" />
                    <tiles:putAttribute name="hasPermissionForActions" value="${hasPermissionForActions}" />
                    <tiles:putAttribute name="createPermissionType" value="${createPermissionType}" />
                    <tiles:putAttribute name="updatePermissionType" value="${updatePermissionType}" />
                    <tiles:putAttribute name="deletePermissionType" value="${deletePermissionType}" />
                  </tiles:insertTemplate>
                </c:otherwise>
              </c:choose>
              
            </c:forEach>
          </tbody>
        </table>
        
      </div>
    </div>
  </div>
</div>

<div class="errorMsg">

  <form:errors path="*" />
</div>

<%-- (initially hidden) form for column adding --%>
<tiles:insertTemplate template="/jsp/common/ShowAddColumn.jsp">
  <tiles:putAttribute name="availableColumnDefinitions"
     value="${dialogMemory.tableState.availableColumnDefinitions}" />
 </tiles:insertTemplate> 

<div>
  <ul class="dropdown-menu" role="menu">
    <li><a tabindex="-1" href="#">Insert after</a></li>  
    <li><a tabindex="-1" href="#">Insert as child</a></li>  
    <li><a tabindex="-1" href="#">Cancel</a></li>  
  </ul>
</div>


<!--<div id="stickyTableHeaderBar" class="unvis">
  <c:forEach items="${resultColumnDefinitions}" var="columnProps" varStatus="s">
    <div id="stickyTableHeaderBar_${s.count}">
      <span style="white-space: nowrap;">
        <c:choose>
          <c:when test="${columnProps.attributeType ne null}">
            <c:out value="${columnProps.tableHeaderKey}" />
          </c:when>
          <c:otherwise>
            <fmt:message key="${columnProps.tableHeaderKey}" />
          </c:otherwise>
        </c:choose>
      </span>
    </div>
  </c:forEach>
  <c:if test="${addManageShortcuts == true}"> 
    <c:if test="${hasPermissionForActions}" >
      <div id="stickyTableHeaderBar_${fn:length(resultColumnDefinitions)+1}">
        <fmt:message key="global.manage" />
      </div>
    </c:if>
  </c:if>
</div> -->
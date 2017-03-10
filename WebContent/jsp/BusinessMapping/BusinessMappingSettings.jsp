<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="componentModel" />
<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="collapse" />

<c:set var="componentMode" value="${componentModel.componentMode}" scope="request" />
<c:set var="bbListForSelection" value="${componentModel.bbListForSelection}" scope="request" />

<fmt:message var="firstBBT" key="${componentModel.firstType}"/>
<fmt:message var="columnBBT" key="${componentModel.columnType}"/>
<fmt:message var="rowBBT" key="${componentModel.rowType}"/>
<fmt:message var="contentBBT" key="${componentModel.contentType}"/>

<c:set var="in" value="in"/>
<c:set var="collapseIcon" value="icon-resize-small"/>
<c:if test="${collapse}">
	<c:set var="in" value=""/>
	<c:set var="collapseIcon" value="icon-resize-full"/>
</c:if>

<script type="text/javascript">

	function createBbtContainerWithElement(containerId, elementLabel) {
		$('<div/>').text(elementLabel).addClass("bbtElement").appendTo('#' + containerId);
	}

	function swapElements(event, ui) {
		
		var dragged = ui.draggable;

		// swap divs for the ui
		var div1 = dragged.children("div").first();
		var div2 = $(this).children("div").first();
		dragged.children("div").remove();
		$(this).children("div").remove();
		dragged.append(div2);
		$(this).append(div1);
		
		// swap fields in the component model
		var sourceField = dragged.children("input").first();
		var targetField = $(this).children("input").first();
		var tmp = sourceField.attr('value');
		sourceField.attr('value', targetField.attr('value'));
		targetField.attr('value', tmp);
		
		<%-- this is necessary because when a clone is used its state is copied into the original
			 element adding a second value to the hidden input child, this fixes the bug --%>
		ui.helper.children("input").first().remove();
		
		if (($(this).attr('id') == 'bbtFirstContainer' || dragged.attr('id') == 'bbtFirstContainer')
				&& $(this).attr('id') != dragged.attr('id')) {
			$('select').val('-1');
			flowAction('changeSettings');
		}
	}

	function highlightTargets(event, ui) {
		$('#bbtFirstContainer').addClass('highlight');
		$('#bbtColumnContainer').addClass('highlight');
		$('#bbtRowContainer').addClass('highlight');
		$('#bbtContentContainer').addClass('highlight');
	}

	function unhighlightTargets(event, ui) {
		$('#bbtFirstContainer').removeClass('highlight');
		$('#bbtColumnContainer').removeClass('highlight');
		$('#bbtRowContainer').removeClass('highlight');
		$('#bbtContentContainer').removeClass('highlight');
	}

	function init() {
		createBbtContainerWithElement("bbtFirstContainer", "${firstBBT}");
		createBbtContainerWithElement("bbtColumnContainer", "${columnBBT}");
		createBbtContainerWithElement("bbtRowContainer", "${rowBBT}");
		createBbtContainerWithElement("bbtContentContainer", "${contentBBT}");
		
		var dragArgs = {
			helper : 'clone',
			containment : $('#businessMapping_settings'),
			cursor : 'move',
			revert : false,
			start : highlightTargets,
			stop : unhighlightTargets
		};
		
		var dropArgs = {
			tolerance : 'touch',
			drop : swapElements
		};
		
		$("#bbtFirstContainer").draggable(dragArgs);
		$("#bbtColumnContainer").draggable(dragArgs);
		$("#bbtRowContainer").draggable(dragArgs);
		$("#bbtContentContainer").draggable(dragArgs);
		
		$("#bbtFirstContainer").droppable(dropArgs);
		$("#bbtColumnContainer").droppable(dropArgs);
		$("#bbtRowContainer").droppable(dropArgs);
		$("#bbtContentContainer").droppable(dropArgs);

	};
	$(document).ready(init);
</script>

<div class="accordion" id="businessMapping_accordion">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#businessMapping_accordion" href="#businessMapping_settings"
					onclick="toggleIcon('collapseIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="collapseIcon" class="${collapseIcon}"></i>
				<fmt:message key="businessMapping.settingsHeader" />
			</a>
		</div>
		<div id="businessMapping_settings" class="accordion-body ${in} collapse">
			<div class="accordion-inner-table">
				<table class="table table-condensed tableInModule noHighlighting">
				    <tr>
						<td width="200" height="200"> 
				      		<div id="bbtFirstContainer" class="bbtContainer">
				      			<form:hidden id="hiddenFieldFirstBBT" path="${path_to_componentModel}.firstType"/>
							</div>
							<br />
							<form:select path="${path_to_componentModel}.selectedBbId" onchange="flowAction('changeSettings');">
						  		<form:option value="-1"> &lt;<fmt:message key="reports.selectAll" />&gt; </form:option>
					  			<c:forEach  var="bbName" items="${bbListForSelection}">
					    			<form:option value="${bbName.id}"><c:out value="${bbName.nonHierarchicalName}"/></form:option>
					  			</c:forEach>
							</form:select>
							<br />
							<c:if test="${componentModel.selectedBbId != null && componentModel.selectedBbId >= 0}">
						  		<input id="transactionShowBusinessMappings" type="button" class="btn" onclick="flowAction('sendBMRequest');" value="<fmt:message key="businessMapping.sendRequest" />" />		
							</c:if>
				      	</td>
				      	<td>
				      		<div id="bbtColumnContainer" class="bbtContainer">
				      			<form:hidden id="hiddenFieldColumnBBT" path="${path_to_componentModel}.columnType"/>
							</div>
				      	</td>
					</tr>
				    <tr>
				    	<td width="200" height="300">
				      		<div id="bbtRowContainer" class="bbtContainer">
				      			<form:hidden id="hiddenFieldRowBBT" path="${path_to_componentModel}.rowType"/>
							</div>
				      	</td>
				      	<td>
				        	<div id="bbtContentContainer" class="bbtContainer">
								<form:hidden id="hiddenFieldContentBBT" path="${path_to_componentModel}.contentType"/>
							</div>
				     	</td>
				    </tr>
				</table>
			</div>
		</div>
	</div>
</div>
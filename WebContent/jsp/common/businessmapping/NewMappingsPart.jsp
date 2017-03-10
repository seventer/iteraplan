<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:useAttribute name="path_to_componentModel" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
<itera:define id="topLevelName" name="memBean" property="${path_to_componentModel}.topLevelName" />
<c:set var="typeOfBuildingBlock" value="${memBean.componentModel.managedType.typeOfBuildingBlock}" />

<script type="text/javascript">

	$(document).ready(function() { 	
		moveTopLeftIfIsEmpty('isr', '${topLevelName}');
		moveTopLeftIfIsEmpty('bp', '${topLevelName}');
		moveTopLeftIfIsEmpty('businessUnit', '${topLevelName}');
		moveTopLeftIfIsEmpty('product', '${topLevelName}');
	 });
</script>

<input type="hidden" name="<c:out value="${path_to_componentModel}" />.action"/>
<div id="<c:out value="${html_id}_newBmForm"/>_Modul" class="row-fluid inner-module">
	<div class="inner-module-heading">
		<fmt:message key="informationSystemRelease.createBusinessMapping" />
	</div>
	<div class="row-fluid">
		<div class="inner-module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-att" />
						<col class="col-ico" />
						<col class="col-att" />
					</colgroup>
					<thead>
						<tr>
							<th><fmt:message key="global.selectedElements" /></th>
							<th>&nbsp;</th>
							<th><fmt:message key="global.availableElements" /></th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${typeOfBuildingBlock != 'INFORMATIONSYSTEMRELEASE'}">
							<tr>
								<td>
									<div style="height:32px;">
										<fmt:message key="informationSystemRelease.plural" />:
									</div>
									<form:select path="${path_to_componentModel}.isrToAdd" cssClass="nameforSelect" id="isr_connected" size="4" ondblclick="moveSelection(isr_connected, isr_available);">
										<itera:define id="connectedIsrs" name="memBean" property="${path_to_componentModel}.connectedIsrs"/>
										<form:options items="${connectedIsrs}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>
								</td>
								<td>
									<div style="height:32px;">
										&nbsp;
									</div>
									<a onclick="moveSelectionLeftTopExclusive('isr', '${topLevelName}');">
										<i class="icon-arrow-left" title="<fmt:message key="tooltip.add"/>" id="isr_add" ></i>
									</a>
									<br/>
									<br/>
									<a onclick="moveSelection2('isr_connected', 'isr_available');">
										<i class="icon-arrow-right" title="<fmt:message key="tooltip.remove"/>" id="isr_remove"></i>
									</a>
								</td>
								<td>
									<span class="pull-left" style="height:32px;">
										<fmt:message key="informationSystemRelease.plural" />:
									</span>
									<span class="pull-right">
										<fmt:message key="global.filter" />:
										<input type="text" value="" class="filter" id="<c:out value="${html_id}_filterText_isrAvailable" />" onkeyup="catchTabKeyAndFilterList(this, event, this.value, 'isr_available')" onchange="filterList(this.value, 'isr_available')" />
									</span>
									<form:select path="${path_to_componentModel}.bpForAdd" cssClass="nameforSelect" id="isr_available" size="4" ondblclick="moveSelectionLeftTopExclusive('isr', '${topLevelName}');">
										<itera:define id="availableIsrs" name="memBean" property="${path_to_componentModel}.availableIsrs" />
									<form:options items="${availableIsrs}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>
								</td>
							</tr>
						</c:if>
						<c:if test="${typeOfBuildingBlock != 'BUSINESSPROCESS'}">
							<tr>
								<td>
									<div style="height:32px;">
										<fmt:message key="businessProcess.plural" />:
									</div>
									<form:select path="${path_to_componentModel}.bpToAdd" cssClass="nameforSelect" id="bp_connected" size="4" ondblclick="moveSelection(bp_connected, bp_available);">
										<itera:define id="connectedBps" name="memBean" property="${path_to_componentModel}.connectedBps"/>
										<form:options items="${connectedBps}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>
								</td>
								<td>
									<div style="height:32px;">
										&nbsp;
									</div>
									<a onclick="moveSelectionLeftTopExclusive('bp', '${topLevelName}');">
										<i class="icon-arrow-left" title="<fmt:message key="tooltip.add"/>" id="bp_add" ></i>
									</a>
									<br/>
									<br/>
									<a onclick="moveSelection2('bp_connected', 'bp_available');">
										<i class="icon-arrow-right" title="<fmt:message key="tooltip.remove"/>" id="bp_remove"></i>
									</a>
								</td>
								<td>
									<span class="pull-left" style="height:32px;">
										<fmt:message key="businessProcess.plural" />:
									</span>
									<span class="pull-right">
										<fmt:message key="global.filter" />:
										<input type="text" value="" class="filter" id="<c:out value="${html_id}_filterText_bpForAdd" />" onkeyup="catchTabKeyAndFilterList(this, event, this.value, 'bp_available')" onchange="filterList(this.value, 'bp_available')" />
									</span>
									<form:select path="${path_to_componentModel}.bpForAdd" cssClass="nameforSelect" id="bp_available" size="4" ondblclick="moveSelectionLeftTopExclusive('bp', '${topLevelName}');">
										<itera:define id="availableBps" name="memBean" property="${path_to_componentModel}.availableBps" />
										<form:options items="${availableBps}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>
								</td>
							</tr>
						</c:if>
						<c:if test="${typeOfBuildingBlock != 'BUSINESSUNIT'}">
							<tr>
								<td>
									<div style="height:32px;">
										<fmt:message key="businessUnit.plural" />:
									</div>
									<form:select path="${path_to_componentModel}.buToAdd" cssClass="nameforSelect" id="businessUnit_connected" size="4" ondblclick="moveSelection(businessUnit_connected, businessUnit_available);">
										<itera:define id="connectedBusinessUnits" name="memBean" property="${path_to_componentModel}.connectedBusinessUnits"/>
										<form:options items="${connectedBusinessUnits}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>    
								</td>
								<td>
									<div style="height:32px;">
										&nbsp;
									</div>
									<a onclick="moveSelectionLeftTopExclusive('businessUnit', '${topLevelName}');">
										<i class="icon-arrow-left" title="<fmt:message key="tooltip.add"/>" id="businessUnit_add" ></i>
									</a>
									<br/>
									<br/>
									<a onclick="moveSelection2('businessUnit_connected', 'businessUnit_available');">
										<i class="icon-arrow-right" title="<fmt:message key="tooltip.remove"/>" id="businessUnit_remove" ></i>
									</a>
								</td>
								<td>
									<span class="pull-left" style="height:32px;">
										<fmt:message key="businessUnit.plural" />:
									</span>
									<span class="pull-right">
										<fmt:message key="global.filter" />:
										<input type="text" value="" class="filter" id="<c:out value="${html_id}_filterText_buForAdd" />" onkeyup="catchTabKeyAndFilterList(this, event, this.value, 'businessUnit_available')" onchange="filterList(this.value, 'businessUnit_available')" />
									</span>
									<form:select path="${path_to_componentModel}.buForAdd" cssClass="nameforSelect" id="businessUnit_available" size="4" ondblclick="moveSelectionLeftTopExclusive('businessUnit', '${topLevelName}');">
										<itera:define id="availableBusinessUnits" name="memBean" property="${path_to_componentModel}.availableBusinessUnits"/>
										<form:options items="${availableBusinessUnits}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>
								</td>
							</tr>
						</c:if>
						<c:if test="${typeOfBuildingBlock != 'PRODUCT'}">
							<tr>
								<td>
									<div style="height:32px;">
										<fmt:message key="global.products" />:
									</div>
									<form:select path="${path_to_componentModel}.prToAdd" cssClass="nameforSelect" id="product_connected" size="4" ondblclick="moveSelection(product_connected, product_available);">
										<itera:define id="connectedProducts" name="memBean" property="${path_to_componentModel}.connectedProducts"/>
										<form:options items="${connectedProducts}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>    
								</td>
								<td>
									<div style="height:32px;">
										&nbsp;
									</div>
									<a onclick="moveSelectionLeftTopExclusive('product', '${topLevelName}');">
										<i class="icon-arrow-left" title="<fmt:message key="tooltip.add"/>" id="product_add" ></i>
									</a>
									<br/>
									<br/>
									<a onclick="moveSelection2('product_connected', 'product_available');">
										<i class="icon-arrow-right" title="<fmt:message key="tooltip.remove"/>" id="product_remove" ></i>
									</a>
								</td>
								<td>
									<span class="pull-left" style="height:32px;">
										<fmt:message key="global.products" />:
									</span>
									<span class="pull-right">
										<fmt:message key="global.filter" />: 
										<input type="text" value="" class="filter" id="<c:out value="${html_id}_filterText_prForAdd" />" onkeyup="catchTabKeyAndFilterList(this, event, this.value, 'product_available')" onchange="filterList(this.value, 'product_available')" />
									</span>
									<form:select path="${path_to_componentModel}.prForAdd" cssClass="nameforSelect" id="product_available" size="4" ondblclick="moveSelectionLeftTopExclusive('product', '${topLevelName}');">
										<itera:define id="availableProducts" name="memBean" property="${path_to_componentModel}.availableProducts"/>
										<form:options items="${availableProducts}" itemLabel="hierarchicalName" itemValue="id" />
									</form:select>   
								</td>
							</tr>
						</c:if>
						<tr>
							<td colspan="2">
								<input type="button" id="reset" value="<fmt:message key="button.reset" />" class="btn" 
					          		onclick="setHiddenField('<c:out value="${path_to_componentModel}" />.action', 'resetMappings');
									removeOptions('isr_connected');
									removeOptions('bp_connected');
									removeOptions('businessUnit_connected');
									removeOptions('product_connected');
							      	flowAction('update');" />
								&nbsp;
								<input type="button" id="add" value="<fmt:message key="button.add" />" class="btn"
									onclick="setHiddenField('<c:out value="${path_to_componentModel}" />.action', 'addMappings');
									selectOptions('isr_connected');
									selectOptions('bp_connected');
									selectOptions('businessUnit_connected');
									selectOptions('product_connected');
									flowAction('update');" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
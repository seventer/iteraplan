<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<itera:define id="valuesModel" name="memBean" property="${path_to_componentModel}" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="connected_elements" name="memBean" property="${path_to_componentModel}.rangeValues"/>
<c:set       var="connected_elements_size" value="${fn:length(connected_elements)}" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
<itera:define id="maxRangeValue" name="memBean" property="${path_to_componentModel}.maxRangeValue" />

<c:set var="currentLocale" value="${userContext.locale}" />

<c:if test="${component_mode == 'READ'}">
  <c:set var="tdstyle" value="top margin" />
</c:if>

<input type="hidden" name="<c:out value="${path_to_componentModel}" />.selectedPosition"/>
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.action"/>

<div id="RangeValuesComponentListViewModul" class="row-fluid inner-module">
	<div class="inner-module-heading">
		<fmt:message key="manageAttributes.numberAT.manualRanges" />
	</div>
	<div class="row-fluid">
		<div class="inner-module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-ico" />
						<col class="col-name" />
					</colgroup>
					<c:choose>
						<c:when test="${component_mode != 'READ'}">
							<thead>
      							<tr>
      								<th>&nbsp;</th>
        							<th>
        								<fmt:message key="manageAttributes.rangeValue" />
        							</th>
      							</tr>
      						</thead>
      						<tbody>
      							<c:forEach items="${valuesModel.rangeValues}" var="rangeValues" varStatus="status">
      								<script type="text/javascript">
										$(function(){
											// validateNumber
											$("#${html_id}_${status.index}_nameText").validate({
												<%-- TODO: checkRegEx --%>
												expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
												<%-- TODO: checkErrorMessage --%>
												message: "<fmt:message key="errors.invalidValue"/>"
											});
										});
									</script>
									<tr>
										<td>
											<a id="<c:out value="${html_id}" />_<c:out value="${status.index}_remove" />" class="link" href="#"
									            title="<fmt:message key="tooltip.remove"/>" 
									            onclick="setHiddenField('<c:out value="${path_to_componentModel}" />.selectedPosition', <c:out value="${status.count}" />);
													setHiddenField('<c:out value="${path_to_componentModel}" />.action', 'delete');
													flowAction('update');" >
												<i class="icon-remove"></i>
								            </a>
										</td>
										<td>
											<div class="control-group">
												<div class="controls">
	          										<form:input id="${html_id}_${status.index}_nameText" path="${path_to_componentModel}.rangeValues[${status.index}].valueAsString" cssClass="input-large" lang="${currentLocale}" />
	          									</div>
											</div>
          								</td>
        							</tr>
      							</c:forEach>
    					</c:when>
    
						<c:otherwise>
							<thead>
								<c:choose>
									<c:when test="${connected_elements_size <= 0}">
										<thead>
											<tr>
	            								<td colspan="2">&nbsp;</td>
											</tr>
										</thead>
	        						</c:when>
	        						<c:otherwise>
	          								<tr>
	          									<td>
	            									<fmt:message key="global.number" />
	            								</td>
	            								<td>
	            									<fmt:message key="global.name" />
	            								</td>
	          								</tr>
	        						</c:otherwise>
	      						</c:choose>
      						</thead>
      						<tbody>
	      						<c:forEach items="${valuesModel.rangeValues}" var="rangeValues" varStatus="status">
		        					<tr>
										<td>
											<c:out value="${status.count}" />
										</td>
	            						<td>
											<c:out value="${rangeValues.valueAsString}" />
										</td>
									</tr>
	      						</c:forEach>
    					</c:otherwise>
  					</c:choose>
  
					<c:if test="${component_mode != 'READ' && connected_elements_size < maxRangeValue }" >
						<script type="text/javascript">
							$(function(){
								// validateNumber
								$("#${html_id}_valueToAdd").validate({
									<%-- TODO: checkRegEx --%>
									expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
									<%-- TODO: checkErrorMessage --%>
									message: "<fmt:message key="errors.invalidValue"/>"
								});
							});
						</script>
	    				<tr>
	    					<td>
								<a id="<c:out value="${html_id}" />_add" class="link" href="#"
									title="<fmt:message key="tooltip.add"/>" 
									onclick="setHiddenField('<c:out value="${path_to_componentModel}" />.action', 'new');
										flowAction('update');" >
									<i class="icon-plus"></i>
								</a>
							</td>
	            			<td>
	            				<div class="control-group">
									<div class="controls">
	        							<form:input id="${html_id}_valueToAdd" cssClass="input-large" path="${path_to_componentModel}.valueToAdd"	lang="${currentLocale}" />
	        						</div>
	        					</div>
	   						</td>
						</tr>
	  				</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
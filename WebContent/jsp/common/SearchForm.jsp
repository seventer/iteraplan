<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>



<tiles:useAttribute name="bbTypeHtmlId" ignore="true" />

<tiles:useAttribute name="entitySearchKey" ignore="true" />

<tiles:useAttribute name="searchCriteria" ignore="true" />

<tiles:useAttribute name="showSearchLabel" ignore="true" />

<tiles:useAttribute name="specialCharHintEscaped" ignore="true" />



<c:set var="specialCharHintEscaped">
  <fmt:message key="manageSearch.warning.specialcharacters" var="specialCharHint"/>
  <c:out value="${specialCharHint}"/>
</c:set>


<div id="searchForm">

  <div>
  <fieldset style="width: 100%;">
    <input type="hidden" name="resetAttributeSearch" id="resetAttributeSearch" value="false" />
    <input type="hidden" name="nextPageToShow" id="nextPageToShow" value="false" />
    <form:input type="hidden" path="activeSearchStrategy" />
    <form:input type="hidden" path="currentPageNumber" />
    <form:input type="hidden" path="queryId" />

    <legend class="searchheading">
      <c:if test="${not empty bbTypeHtmlId}">
        <span class="headerIcon icon_${bbTypeHtmlId}"> </span>
      </c:if>
      <fmt:message key="${entitySearchKey}" />
    </legend>

    <div class="tabbable tabs-top">
    
      <c:set var="showSavedQueries" value="${fn:length(dialogMemory.savedQueries) > 0}"/>
    
      <c:set var="fullTextSearchActive" value="active"/>
      <c:set var="queriesActive" value=""/>
      
      <c:if test="${guiContext.selectedTab == 'queries'}">
      <c:set var="fullTextSearchActive" value=""/>
        <c:set var="queriesActive" value="active"/>
      </c:if>
      
      <c:if test="${dialogMemory.queryForm != null}">
        <ul class="nav nav-tabs">
          
          <li class="${fullTextSearchActive}">
            <a href="#tab_fullTextSearch" data-toggle="tab" onclick="GuiService.setSelectedTab('fullTextSearch');">
              <fmt:message key="manageSearch.heading" />
            </a>
          </li>
          
          <li class="${queriesActive}">
            <a href="#tab_queries" data-toggle="tab" onclick="GuiService.setSelectedTab('queries');">
              <fmt:message key="global.queries" />
            </a>
          </li>
          
          <c:if test="${showSavedQueries}">
            <li class="dropdown">
              <a class="dropdown-toggle" href="#" data-toggle="dropdown">
                <fmt:message key="search.filterBy.savedQuery" />
                <b class="caret"></b>
              </a>
              
              <c:set var="queries" value="${dialogMemory.savedQueries}" />
              
              <ul class="dropdown-menu">
                <c:forEach var="savedQuery" items="${queries}">
                	<c:set var="queryLink" value="./search.do?queryId=${savedQuery.id}&activeSearchStrategy=SEARCH_BY_SAVED_QUERY&pageStart=0&currentPageNumber=0" />
                  <li>
                    <a href="${queryLink}">
                      <c:out value="${savedQuery.name}" escapeXml="true"/>
                    </a>
                  </li>
                </c:forEach>
              </ul>
            </li>
          </c:if>
        </ul>
      </c:if>
      
      <div class="tab-content">
        <div class="tab-pane ${fullTextSearchActive}" id="tab_fullTextSearch">
          
          <div class="searchCriteria">
            <c:forEach var="criterion" items="${searchCriteria}">
              <div class="field">
                <div class="input">
                  <c:if test="${showSearchLabel}">
                    <c:choose>
                      <c:when test="${fn:length(searchCriteria) > 1}">
                        <c:set var="labelClass" value="searchLabelBlock" />
                      </c:when>
                      <c:otherwise>
                        <c:set var="labelClass" value="searchLabelNoBlock" />
                      </c:otherwise>
                    </c:choose>
                    <label class="${labelClass}" for="crit_${criterion.name}">
                      <fmt:message key="${criterion.labelKey}" />:
                    </label>
                  </c:if>
                  <i class="icon-search"></i>
                  <form:input id="crit_${criterion.name}" path="${criterion.name}"
                    maxlength="40" />
                  &nbsp;
                  <form:errors path="${criterion.name}" cssClass="errorMsg"
                    htmlEscape="false" />
                  <br />
                  <script type="text/javascript">
                    $('#crit_${criterion.name}').tooltip({
                      trigger : 'manual',
                      placement : 'right',
                      title : '${specialCharHintEscaped}'
                    });
                    
                    $('#crit_${criterion.name}').keyup(function() {
                      if ($(this).val().match(/[^a-zA-Z0-9\\s*?À-ÿ]+/)) {
                        $(this).tooltip('show');
                      } else {
                        $(this).tooltip('hide');
                      }
                    });
                          
                          $(document).ready(function() {
                    // Add additional support: Enter-Key submits the form by clicking the Search-Button
                    $("#crit_${criterion.name}").keypress(function(evt) { onEnterClickButton('sendSearchquery', evt); setHiddenField('resetAttributeSearch', true); });
                            });
                  </script>
                </div>
              </div>
            </c:forEach>
          </div>
          
          <div class="btn-toolbar">
            <a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.sendQuery.tooltip'/>">
              <input type="submit" id="sendSearchquery" value="<fmt:message key="button.search"/>" onclick="setHiddenField('pageStart', 0);setHiddenField('currentPageNumber', 0);setHiddenField('activeSearchStrategy', 'SEARCH_BY_INDEX'); return true;" class="btn btn-primary" />
            </a>
          </div>
          
        </div>
        <c:if test="${dialogMemory.queryForm != null}">
          <div class="tab-pane ${queriesActive}" id="tab_queries">
            
            <c:set var="countFirstLevel" value="0" />
                  <table id="innerTesttabel">
                    <itera:define id="querySecondLevelsArray" name="dialogMemory" property="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels" />
                    <c:forEach items="${querySecondLevelsArray}" var="qPart" varStatus="countSecondLevelStatus">
                      <c:set var="countSecondLevel" value="${countSecondLevelStatus.index}" />
                      <tr>
                        <td><form:select
                            id="attributeDropDown_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].chosenAttributeStringId"
                            cssClass="attributeDropDown"
                            onchange="setHiddenField('activeSearchStrategy', 'SEARCH_ALL'); changeAttributeWithResets('operationDropDown_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}', 'attributeValueRadio_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}', 'attributeValueDropDown_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}', 'freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}');">
                            <itera:define id="availableAttributeArray"
                              name="dialogMemory" property="queryForm.availableAttributes" />
            
                            <c:forEach items="${availableAttributeArray}"
                              var="availableAttribute">
                              <c:if
                                test="${availableAttribute.id != 0 and availableAttribute.id != -11}">
                                <form:option value="${availableAttribute.stringId}">
                                  <c:choose>
                                    <c:when test="${availableAttribute.type == 'blank'}">
                                      <fmt:message key="${availableAttribute.name}" />
                                    </c:when>
                                    <c:when test="${availableAttribute.type == 'fixed'}">
                                      <fmt:message key="${availableAttribute.name}" />
                                    </c:when>
                                    <c:when test="${availableAttribute.type == 'fixedDate'}">
                                      <fmt:message key="${availableAttribute.name}" />
                                    </c:when>
                                    <c:when test="${availableAttribute.type == 'fixedSet'}">
                                      <fmt:message key="${availableAttribute.name}" />
                                    </c:when>
                                    <c:when test="${availableAttribute.type == 'fixedEnum'}">
                                      <fmt:message key="${availableAttribute.name}" />
                                    </c:when>
                                    <c:otherwise>
                                      <c:out value="${availableAttribute.name}" />
                                    </c:otherwise>
                                  </c:choose>
                                </form:option>
                              </c:if>
                            </c:forEach>
                          </form:select> <c:set var="chosenAttributeId">
                            <itera:write name="dialogMemory"
                              property="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].chosenAttributeStringId" escapeXml="false" />
                          </c:set> <c:set var="inputFieldsDisabled" value="false" /> <c:if
                            test="${chosenAttributeId == null || chosenAttributeId == '' || chosenAttributeId == 'blank_null_-1'}">
                            <c:set var="inputFieldsDisabled" value="true" />
                          </c:if>
                        </td>
                        <td>
                          <c:set var="nestedProperty2"
                            value="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].operators" />
                          <itera:define id="items" name="dialogMemory"
                            property="${nestedProperty2}" /> <form:select
                            id="operationDropDown_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].chosenOperationId"
                            cssClass="operationDropDown"
                            disabled="${inputFieldsDisabled}">
                            <c:forEach items="${items}" var="operator">
                              <c:if
                                test="${!(criticalExtension && operator.description == 'notOperation')}">
                                <c:choose>
                                  <c:when
                                    test="${chosenAttributeId == 'fixed_technicalComponent.availableForInterfaces_-12'}">
                                    <c:if
                                      test="${operator.id == 5 || operator.id == 6 || operator.id == 8}">
                                      <form:option value="${operator.id}">
                                        <fmt:message key="${operator.name}" />
                                      </form:option>
                                    </c:if>
                                  </c:when>
                                  <c:otherwise>
                                    <form:option value="${operator.id}">
                                      <fmt:message key="${operator.name}" />
                                    </form:option>
                                  </c:otherwise>
                                </c:choose>
                              </c:if>
                            </c:forEach>
                            <%-- show empty dummy option if select is disabled/empty, needed for W3C validation --%>
                            <c:if test="${inputFieldsDisabled}">
                              <form:option value=""></form:option>
                            </c:if>
                          </form:select>
                        </td>
            
                        <%-- Boolean Variable to decide whether the selection fields are disabled or not --%>
                        <itera:define id="selectFieldsDisabled" name="dialogMemory"
                          property="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].selectFieldsDisabled" />
            
                        <%-- The following code sets the list of available attribute values into a bean called attributeValueList, which is needed now and later on. --%>
                        <c:set var="nestedProperty3"
                          value="queryForm.availableAttributeValues(${qPart.chosenAttributeStringId})" />
                        <itera:define id="attributeValueList" name="dialogMemory"
                          property="${nestedProperty3}" />
            
                        <td>
                        <div class="attributeValueDropDown" style="white-space: nowrap;">
                        <c:set var="radiobuttonAndFirstFieldDisabled"
                            value="${inputFieldsDisabled || (empty attributeValueList) || selectFieldsDisabled}" />
                          <form:radiobutton
                            id="attributeValueRadio_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected"
                            value="false" disabled="${radiobuttonAndFirstFieldDisabled}" />
                        <form:select
                            id="attributeValueDropDown_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].existingCriteria"
                            disabled="${radiobuttonAndFirstFieldDisabled}"
                            onclick="switchRadioButton('queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected', 0);">
                            <c:if test="${not empty attributeValueList}">
                              <c:choose>
                                <c:when
                                  test="${chosenAttributeId == 'fixed_technicalComponent.availableForInterfaces_-12'}">
                                  <c:forEach items="${attributeValueList}"
                                    var="attributeValue">
                                    <c:choose>
                                      <c:when test="${attributeValue.name eq true}">
                                        <form:option value="${attributeValue.name}">
                                          <fmt:message key="global.yes" />
                                        </form:option>
                                      </c:when>
                                      <c:otherwise>
                                        <form:option value="${attributeValue.name}">
                                          <fmt:message key="global.no" />
                                        </form:option>
                                      </c:otherwise>
                                    </c:choose>
                                  </c:forEach>
                                </c:when>
                                <c:otherwise>
                                  <form:options items="${attributeValueList}"
                                    itemLabel="description" itemValue="name" />
                                </c:otherwise>
                              </c:choose>
                            </c:if>
                            <%-- show empty dummy option if select is disabled/empty, needed for W3C validation --%>
                            <c:if test="${inputFieldsDisabled}">
                              <form:option value=""></form:option>
                            </c:if>
                        </form:select>
                        </div>

                        <div class="attributeValueDropDown" style="white-space: nowrap;">
                        <form:radiobutton
                            id="attributeValueInputRadio_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected"
                            value="true" disabled="${radiobuttonAndFirstFieldDisabled}" />
                            
                          <c:set var="isDateAT">
                            <itera:write name="dialogMemory" property="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].dateATSelected" escapeXml="false" />
                          </c:set>
                          <c:set var="isNumberAT">
                            <itera:write name="dialogMemory" property="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].numberATSelected" escapeXml="false" />
                          </c:set>

                          <c:set var="datepickerClazzName">
                            <c:if test="${isDateAT}">datepicker</c:if>
                          </c:set>
                          <form:input path="queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteria"
                            disabled="${inputFieldsDisabled || selectFieldsDisabled}"
                            id="freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}"
                            onClick="switchRadioButton('queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected', 1);" cssClass="${datepickerClazzName}" /> 
                          
                          <c:if test="${isDateAT}">
                            <script type="text/javascript">
                              $(document).ready(function() {
                                          /* TODO springDecoration -> Spring.addDecoration(new Spring.ElementDecoration(
                                            {
                                              elementId: 'freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}',
                                              widgetType: 'dijit.form.DateTextBox',
                                              widgetAttrs: {
                                              datePattern: '<fmt:message key="calendar.dateFormat"/>',
                                              style: 'width: 21em; '
                                            }
                                            }));*/
                                          $("#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}").click(function() { switchRadioButton('queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected', 1); });
                                      });
                            </script>
                          </c:if>
                          
                          <c:if test="${isNumberAT}">
                            <script type="text/javascript">
                              $(document).ready(function() {
                                          // validateNumber
                                          $("#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}").validate({
                                  <%-- TODO: checkRegEx --%>
                                  expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
                                  <%-- TODO: checkErrorMessage --%>
                                  message: "<fmt:message key="errors.invalidValue"/>"
                                });
                                          $("#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}").click(function() { switchRadioButton('queryForm.queryUserInput.queryFirstLevels[${countFirstLevel}].querySecondLevels[${countSecondLevel}].freeTextCriteriaSelected', 1); });
                                      });    
                              </script>
                          </c:if>
                          
                          <script type="text/javascript">
                            $(document).ready(function() {
                              // Add additional support: Enter-Key submits the form by clicking the Search-Button
                              $('#freeTextCriteria_${currentReportFormId}_${countFirstLevel}_${countSecondLevel}').keypress(function(evt) { onEnterClickButton('sendSearchquery', evt); });
                                    });   
                          </script>
                          </div>
                        </td>
                        <td>
                          <div class="btn-toolbar">
                            <a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.sendQuery.tooltip'/>">
                              <input type="submit" id="sendSearchquery" value="<fmt:message key="button.search"/>" onclick="setHiddenField('pageStart', 0);setHiddenField('currentPageNumber', 0);setHiddenField('activeSearchStrategy', 'SEARCH_BY_QUERY_FORM'); return true;" class="btn btn-primary" />
                            </a>
                          </div>                        
                        </td>
                      </tr>
                    </c:forEach>
            
                  </table>
                  
          </div>
        </c:if>
        
      </div>
      
    </div>
  
    <div class="btn-toolbar">
      <div class="btn-group">
        <a href="#" onclick="changeLocation('resetSearch.do');" class="btn">
          <fmt:message key="button.resetAndShowAll"/>
        </a>
      </div>
    </div>
    
    <form:hidden path="pageStart" />
    <c:if test="${dialogMemory.filteredBySavedQuery}">
      <div>
        <fmt:message key="search.filteredBy.savedQuery" />
        <c:out value=" ${dialogMemory.savedQueryName}" />
      </div>            
    </c:if>
  </fieldset>
  </div>

</div>
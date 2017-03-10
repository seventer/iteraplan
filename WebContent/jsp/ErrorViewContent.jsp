<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%@ page isErrorPage="true"%>

<p>
	<strong> <fmt:message key="errorview.text.technicalError" />
	</strong>
</p>

<form:errors path="*" cssClass="errorInline" />
<div class="alert alert-error">
	<div class="errorHeader">
	<%-- if an unexpected exception occurs, rather than displaying an empty box, a general exception message will be shown --%>
		<c:choose>
			<c:when
				test="${(fn:length(flowRequestContext.messageContext.allMessages) > 0) or (not empty _iteraplan_exception_message)}">
				<c:forEach var="error"
					items="${flowRequestContext.messageContext.allMessages}">
					<span id="*.errors"><c:out value="${error.text}"
							escapeXml="false" /></span>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<span><fmt:message key="GENERAL_TECHNICAL_ERROR" /></span>
			</c:otherwise>
		</c:choose>
		<%-- insert an exception message provided by one of our exception handlers --%>
		<c:if test="${not empty _iteraplan_exception_message}">
			<span id="*.errors"><c:out
					value="${_iteraplan_exception_message}" escapeXml="false" /></span>
		</c:if>
	</div>
</div>
<p>
	<fmt:message key="errorview.text.helpOne.partOne" />
</p>
<ul>
	<li><fmt:message key="errorview.text.helpOne.partTwo" /></li>
	<li><fmt:message key="errorview.text.helpOne.partThree" /></li>
	<li><fmt:message key="errorview.text.helpOne.partFour" /></li>
</ul>

<p>
	<fmt:message key="errorview.text.helpTwo.partOne" />
</p>


<%-- Problem reports: --%>
<c:if test="${not empty _iteraplan_problem_report_enabled}">      
  <c:catch>
  
    <hr/>
    
    <c:set var="storedReports" value="${sessionScope['_stored_problem_reports']}" />
    <c:set var="currentKey" value="${requestScope['_iteraplan_problem_report_key']}" />
    
    <p>
      <strong><fmt:message key="problemreport.caption" />:</strong>
    </p>
    
    <c:if test="${userContext.perms.userIsAdministrator}">
      <p>
        <a href="${storedReports[currentKey].downloadLink}" class="btn btn-primary">
          <i class="icon-download-alt"></i>
          <fmt:message key="problemreport.download.button.text"></fmt:message>
        </a>
      </p>
    </c:if>
    
    <c:if test="${not empty _iteraplan_problem_report_display_gui}">
      <div class="accordion" id="problemMessageParts">
        <c:set var="reportParts" value="${requestScope['_iteraplan_problem_report_parts']}" />
        
        <c:forEach items="${reportParts}" var="entry">
          <c:set var="partKey" value="${entry.key}" />
          <div class="accordion-group">
            <div class="accordion-heading">
              <a class="accordion-toggle" data-toggle="collapse" data-parent="#problemMessageParts" href="#collapse_${partKey}">
               <span style="text-transform: uppercase;">${entry.key}</span> information
              </a>
            </div>
            <div id="collapse_${partKey}" class="accordion-body collapse">
              <div class="accordion-inner">
                <pre class="prettyprint">
<c:out value="${reportParts[entry.key]}" default="*empty*" escapeXml="false" />
                </pre>
              </div>
            </div>
          </div>
        </c:forEach>
      
      </div>
    </c:if>
    
    <script type="text/javascript">
    $(function() {
   	
      $('#generateMailtoLink').click(function() {
    	var clickedElem = $(this);
        $.ajax({
          url: '${pageContext.request.contextPath}/miscellaneous/generateProblemReportLink.do',
          data: $.param({
          	reportKey: '${currentKey}'
          }),
          type: 'GET'
        }).done(function (data, textStatus, jqXHR) {
			window.location = data;
        }).fail(function (jqXHR, textStatus, errorThrown) {
			clickedElem.text('<fmt:message key="problemreport.generate.button.failed"></fmt:message>').removeClass('btn-primary').addClass('disabled').unbind('click');
        });
        
      });
    });
    </script>
    
    <p>
      <button type="button" id="generateMailtoLink" class="btn btn-primary">
        <i class="icon-briefcase"></i> <fmt:message key="problemreport.generate.button.text"></fmt:message>
      </button>
    </p>
    
  </c:catch>
</c:if>

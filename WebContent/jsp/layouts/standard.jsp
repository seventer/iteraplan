<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
  // disable caching, in order to avoid that inconsistent menu states are presented to the user.
			response.setHeader("Expires", "Tue, 15 Nov 1994 12:45:26 GMT");
			response.setHeader("Last-Modified", "Tue, 15 Nov 1994 12:45:26 GMT");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control",
					"private, no-cache, must-revalidate");
%>

<tiles:useAttribute name="title" />
<tiles:useAttribute name="title_style" ignore="true" />
<tiles:useAttribute name="header" />
<tiles:useAttribute name="navigation" />
<tiles:useAttribute name="breadcrumb" />
<tiles:useAttribute name="menu" />
<tiles:useAttribute name="contents" ignore="true" />
<tiles:useAttribute name="footer" />

<tiles:useAttribute name="form_model" ignore="true" />
<tiles:useAttribute name="form_action" ignore="true" />
<tiles:useAttribute name="form_id" ignore="true" />
<tiles:useAttribute name="form_method" ignore="true" />
<tiles:useAttribute name="form_enctype" ignore="true" />

<fmt:setLocale value="${userContext.locale}" scope="request" />

<spring:eval var="auditLogging"
	expression="@applicationProperties.getProperty('audit.logging.enabled')"
	scope="application" />
<spring:eval var="lastmodificationLogging"
	expression="@applicationProperties.getProperty('lastmodification.logging.enabled')"
	scope="application" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%--Inside the following <c:choose /> is the logic to change the page title. It is
		controlled by the var title_style which is set in the building block's views.xml --%>

<c:set var="path_to_ISRMB"
	value="de.iteratec.iteraplan.presentation.dialog.InformationSystem.InformationSystemReleaseMemBean" />
<c:set var="path_to_ISIMB"
	value="de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.InformationSystemInterfaceMemBean" />
<c:set var="path_to_TCRMB"
	value="de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.TechnicalComponentReleaseMemBean" />
<c:set var="path_to_UMB"
	value="de.iteratec.iteraplan.presentation.dialog.User.UserMemBean" />
<c:set var="path_to_UGMB"
	value="de.iteratec.iteraplan.presentation.dialog.UserGroup.UserGroupMemBean" />
<c:set var="path_to_ORPMB"
	value="de.iteratec.iteraplan.presentation.dialog.ObjectRelatedPermission.ObjectRelatedPermissionMemBean" />

<c:set var="path_to_name_releaseNameModel"
	value="componentModel.releaseNameModel.name" />
<c:set var="path_to_name_nameModel"
	value="componentModel.nameModel.name" />
<c:set var="path_to_name_selectModel"
	value="componentModel.selectModel.name" />
<c:set var="path_to_name_loginNameModel"
	value="componentModel.loginNameModel.current" />
<c:set var="path_to_name_nameModel_StringCM"
	value="componentModel.nameModel.current" />
<c:set var="path_to_name_ORPMB"
	value="dto.currentUserEntity.identityString" />

<title><fmt:message key="${title}" /> <c:choose>
		<c:when
			test="${title_style == 'global.overview' || title_style == 'global.new'}">
					: <fmt:message key="${title_style}" />
		</c:when>
		<c:when
			test="${title_style == 'details' || title_style == 'global.edit'}">
					: 
					<c:choose>
				<c:when
					test="${memBean['class'].name == path_to_ISRMB || memBean['class'].name == path_to_TCRMB}">
					<itera:write name="memBean"
						property="${path_to_name_releaseNameModel}" escapeXml="true" />
				</c:when>
				<c:when test="${memBean['class'].name == path_to_ISIMB}">
					<itera:write name="memBean" property="${path_to_name_selectModel}"
						escapeXml="true" />
				</c:when>
				<c:when test="${memBean['class'].name == path_to_UMB}">
					<itera:write name="memBean"
						property="${path_to_name_loginNameModel}" escapeXml="true" />
				</c:when>
				<c:when test="${memBean['class'].name == path_to_UGMB}">
					<itera:write name="memBean"
						property="${path_to_name_nameModel_StringCM}" escapeXml="true" />
				</c:when>
				<c:when test="${memBean['class'].name == path_to_ORPMB}">
					<itera:write name="memBean" property="${path_to_name_ORPMB}"
						escapeXml="true" />
				</c:when>
				<c:otherwise>
					<itera:write name="memBean" property="${path_to_name_nameModel}"
						escapeXml="true" />
				</c:otherwise>
			</c:choose>
			<c:if test="${id != null}"> (${id})</c:if>
			<c:if test="${title_style == 'global.edit'}">
						[<fmt:message key="${title_style}" />]
					</c:if>

		</c:when>
	</c:choose> - <fmt:message key="global.applicationname" /></title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=8,9,10" />
<meta http-equiv="refresh"
	content="<%=session.getMaxInactiveInterval() - 10%>;url=<c:url value="/jsp/logout.jsp"/>" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link rel="icon" href="<c:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>"
	type="image/x-icon" />

<jwr:style media="all" src="/bundles/iteraplanMain.css" />
<jwr:style media="all" src="/ui/custom/custom.css" />

<jwr:script src="/bundles/iteraplanMain.js" />

<script type="text/javascript">
	$(document).ready(function() {
		$(".defaultSearchBoxText").focus(function(srcc) {
			if ($(this).val() == $(this)[0].title) {
				$(this).removeClass("defaultTextActive");
				$(this).val("");
			}
		});

		$(".defaultSearchBoxText").blur(function() {
			if ($(this).val() == "") {
				$(this).addClass("defaultTextActive");
				$(this).val($(this)[0].title);
			}
		});

		$(".defaultSearchBoxText").blur();
	});

	$("nav-collapse").click(function() {
		$(this).addClass(active);
	});
</script>

<script type="text/javascript"
	src="<c:url value="/javascript/javascript.js.do" />"></script>

</head>

<body>



	<%-- Initialize formating string bootstrap-datepicker: --%>
	<fmt:message key="DATE_FORMAT" var="dateformatForJava" />
	<c:set var="dateformatForDatepicker"
		value="${fn:toLowerCase(dateformatForJava)}" />

	<%-- Initialize bootstrap-datepicker --%>
	<script type="text/javascript">
		function datepickerInit() {
			$('.datepicker').datepicker({
				format : '${dateformatForDatepicker}'
			}).on('changeDate', function(ev) {
				$(this).trigger('blur'); // this is to trigger possibly attached validation
			});
		}

		$(document).ready(function() {
			datepickerInit();
		});
	</script>

	<%-- vertically fixes the context menu, allows horizontal scrolling, uses the scrollToFixed-plugin --%>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#contextMenu').scrollToFixed({
				// depends on the header width
				marginTop : 125,
				// CUSTOM: offset for deactivating the window
				heightOffset : 110
			});
		});
	</script>

	<div id="hiddenCalendarDiv"
		style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>

	<form:form id="${form_id}" method="${form_method}"
		modelAttribute="${form_model}" action="${form_action}"
		enctype="${form_enctype}" class="form-horizontal">

		<input type="hidden" name="pagePositionX" id="pagePositionXId"
			value="<c:out value="${memBean.pagePositionX}" />" />
		<input type="hidden" name="pagePositionY" id="pagePositionYId"
			value="<c:out value="${memBean.pagePositionY}" />" />

		<div class="navbar navbar-fixed-top" style="margin-bottom:">
			<div id="iteraplanHeader">

				<a href="<c:url value="/start/start.do" />"> <img
					id="iteraplan_header_image"
					src="<c:url value="/images/blank.gif" />" alt="iteraplanLogo"
					border="0" />
				</a>

				<c:set var="functionalPermissionSearch"
					value="${userContext.perms.userHasFuncPermSearch}" scope="request" />
				<c:if test="${functionalPermissionSearch}">
					<div id="searchHeader" class="pull-right" style="padding-top: 6px">

						<i class="icon-search" style="margin-top: 3px;"></i> <input
							name="globalSearchBox" class="defaultSearchBoxText"
							title="<fmt:message key="menu.quicksearch.inputlabel" />"
							type="text"
							onkeypress="return onEnterClickButton('sendGlobalSearchquery', event);" />

						<input type="submit" style="margin-bottom: 5px;"
							id="sendGlobalSearchquery" class="btn"
							value="<fmt:message key="button.sendSearchquery" />"
							onclick="submitForm('<c:url value="/search/globalsearch.do" />', 'GET');" />
					</div>
				</c:if>

			</div>
			<tiles:insertAttribute name="navigation" />
			<tiles:insertAttribute name="breadcrumb" />
		</div>

		<tiles:insertTemplate template="/jsp/common/Combobox.jsp" />

		<div class="page">

			<div class="container-fluid" style="padding: 0 10px;">
				<div class="row-fluid">
					<%-- hide on the start page --%>
					<c:set var="hiddenMenu" value="" />
					<c:set var="contentStyle" value="" />
					<c:if test="${empty guiContext.activeDialogName}">
						<c:set var="startPageStyle"
							value="style=\"margin: auto; width: 100%;\"" />
						<c:set var="hiddenMenu" value="hidden" />
					</c:if>

					<c:if test="${hiddenMenu!='hidden'}">
						<div class="span2" id="contextMenu">
							<tiles:insertAttribute name="menu" />
						</div>
					</c:if>
					<div class="span10" id="mainContent" ${startPageStyle}>
						<tiles:insertAttribute name="header" />

						<%-- Display error messages --%>
						<spring:hasBindErrors name="${form_model}">
							<script type="text/javascript">
							<!--
								resetScrollCoordinates();
								scrollToCoordinates();
							// -->
							</script>
							<div class="alert alert-error">
								<a class="close" data-dismiss="alert">×</a> <span
									class="errorHeader"><fmt:message key="errors.header" /></span><br />
								<form:errors path="*" htmlEscape="false" />

								<%-- <c:forEach var="error" items="${messageContext.allMessages}">
					      <c:out value="Text: ${error.text} }" />
					    </c:forEach>--%>
							</div>
						</spring:hasBindErrors>
						
						<%-- Include Template for SaveSuccessfullInfoBox --%>
						<%@include file="/jsp/SaveSuccessfulInfoBox.jsp" %>
						
						<%-- Include Template for Download Trigger --%>
						<%@include file="/jsp/DownloadEventTriggeredInfoBox.jsp" %>

						<c:catch var="jsp_exception">
							<c:if test="${not empty contents}">
								<c:forEach items="${contents}" var="content">
									<tiles:insertAttribute value="${content}" flush="false" />
								</c:forEach>
							</c:if>

							<!-- Used on building block detail sites for Timeseries edit dialog -->
							<tiles:insertAttribute name="timeseriesDetailDialog"
								ignore="true" />
						</c:catch>

						<%-- if rendering the content tile(s) failed, display the error view --%>
						<c:if test="${jsp_exception!=null}">
							<tiles:insertTemplate template="../ErrorViewContent.jsp">
								<tiles:putAttribute name="onlyResetable" value="true" />
							</tiles:insertTemplate>
							<%-- And log the exception to the iteraplan logger --%>
							<itera:log
								message="Exception occured while inserting a tiles template: "
								category="error" />
							<itera:log exception="${jsp_exception}" category="error"
								logRootException="true" />
						</c:if>

					</div>
				</div>
			</div>
		</div>

	</form:form>

	<hr class="footer-separation">
		<div id="footer">
			<div id="footer-left">
				<div id="footer-right">
					<tiles:insertAttribute name="footer" />
				</div>
			</div>
		</div>

		<tiles:insertTemplate template="/jsp/ModalDialog.jsp" flush="true" />
</body>
<script type="text/javascript">
	if ($("[rel=tooltip]").length > 0) {
		$("[rel=tooltip]").tooltip({
			'placement' : 'right',
			'html' : 'true'
		});
		var shiftKeyImageSrc = "<img src='<c:url value="/images/shift-key_invert.png"/>'/>";

		$("[rel=tooltip]").each(
				function(index) {
					$(this).attr(
							'data-original-title',
							function() {
								return ($(this).attr('data-original-title')
										.replace("<img/>", shiftKeyImageSrc));
							});
				});
	}
</script>

<script type="text/javascript">
	$(window).unload(function() {
		doUnloadProcessing();
	});
	$(document).ready(function() {
		scrollToCoordinates();
		decorateTextInputsOnKeyPress();
	});
</script>
</html>

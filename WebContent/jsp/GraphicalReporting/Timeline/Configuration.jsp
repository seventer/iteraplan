<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<jwr:script src="/bundles/vbb.js" />
<jwr:style src="/bundles/vbb.css" />


<script type="text/javascript">
/* <![CDATA[ */
$(function() {
	$("#runtimeElementFillColor").colorPicker({
		colors: [<c:forEach var="color" items="${memBean.graphicalOptions.colorOptionsBean.availableColors}">"#${color}",</c:forEach>]
	});
	
	var config = new Configuration('timeline', $('#generateExport'));
	config.addSubstantialType($('#runtimeElementContainer'), $('#runtimeElementDroppableContainer'), $('#runtimeElement'));
	//config.addSubstantialType($('#subRuntimeElementContainer'), $('#subRuntimeElementDroppableContainer'), $('#subRuntimeElement'));
	config.addFeature($('#runtimePeriod'), [$('#runtimeElement')]);
	//config.addFeature($('#subRuntimePeriod'), [$('#subRuntimeElement')]);
	//config.addFeature($('#elementRelationship'), [$('#runtimeElement'), $('#subRuntimeElement')]);
	config.init();
});
/* ]]> */
</script>

<style type="text/css">
	/* <![CDATA[ */
	/* Formatting for VBB configuration */
	#configurationContainer { width: 970px; margin: 3em auto 3em auto; overflow: auto; }
	
	#selectionContainer { float: left; margin-right: 4em; position: relative; }
	#runtimeElementDroppableContainer    { width: 27em; padding: 1em; margin: 0 0 1em 0; height: 8em; border: 1px solid #999; font-weight: bold; text-align: center; vertical-align: middle; }
	#subRuntimeElementDroppableContainer { width: 27em; padding: 1em; margin: 1em 0 0 70px; height: 8em; border: 1px solid #999; font-weight: bold; text-align: center; vertical-align: middle; }
	#relationContainer { width: 29em; }
	#relationContainer p { margin: 0.5em 0; }
	
	#recommendationContainer { width: 35em; float: right; font-weight: bold; }
	#runtimeElementContainer { padding: 1em; margin-bottom: 1em; border: 1px solid #999; overflow: hidden; }
	#subRuntimeElementContainer { padding: 1em; border: 1px solid #999; overflow: hidden; }
	
	#runtimePeriod, #subRuntimePeriod, #elementRelationship { width: 100%; }
	
	.attributeContainer { margin-left: 40px;}
	
	.optionContainer { margin-bottom: 1em; overflow: auto; }
	.optionChooser { float: left; cursor: pointer; border: 1px solid #999999; margin-right: 0.3em; }
	
	/* jQuery Drag&Drop */
	/*.hover { border-color: #cccccc; }*/
	.active { background: #dddddd; }
	#selectionContainer .ui-droppable .ui-draggable { margin: 0; font-size: 120%; }
	/* ]]> */
</style>

<div id="configurationContainer">
	<div id="selectionContainer">
		<div id="runtimeElementDroppableContainer">
			<div class="optionContainer">
				<form:hidden path="graphicalOptions.viewpointConfigMap['runtimeElement.fillColor']" id="runtimeElementFillColor" />
				<div class="attributeContainer">
					<form:select path="graphicalOptions.viewpointConfigMap['runtimeElement.runtimePeriod']" id="runtimePeriod" />
				</div>
			</div>
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['runtimeElement.name']" name="graphicalOptions.viewpointConfigMap['runtimeElement.name']" value="name" />
			<form:hidden path="graphicalOptions.viewpointConfigMap['runtimeElement']" id="runtimeElement" />
		</div>
		<%--
		<div id="relationContainer">
			<p><fmt:message key="graphicalExport.timeline.configuration.selectAttribute" /></p>
			<p><form:select path="graphicalOptions.viewpointConfigMap['runtimeElement.elementRelationship']" id="elementRelationship" /></p>
		</div>
		<div id="subRuntimeElementDroppableContainer">
			<div class="optionContainer">
				<div class="attributeContainer">
					<form:select path="graphicalOptions.viewpointConfigMap['subRuntimeElement.subRuntimePeriod']" id="subRuntimePeriod" />
				</div>
			</div>
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['subRuntimeElement.name']" name="graphicalOptions.viewpointConfigMap['subRuntimeElement.name']" value="name" />
			<form:hidden path="graphicalOptions.viewpointConfigMap['subRuntimeElement']" id="subRuntimeElement" />
		</div>
		--%>
		<p><fmt:message key="graphicalExport.timeline.configuration.startDate" />: <form:input class="small datepicker" type="text" path="graphicalOptions.viewpointConfigMap['start']" id="start" /></p>
		<p><fmt:message key="graphicalExport.timeline.configuration.endDate" />: <form:input class="small datepicker" type="text" path="graphicalOptions.viewpointConfigMap['end']" id="end" /></p>
	</div>
	
	<%-- <input name="graphicalOptions.viewpointConfigMap['runtimeElement.fillColor']" value="#ffffff" type="hidden" /> --%>
	<%-- <input name="graphicalOptions.viewpointConfigMap['runtimeElement.runtimePeriod.fillColor']" value="#b100af" type="hidden" /> --%>
	
	<div id="recommendationContainer">
		<div id="runtimeElementContainer">
			<h2><fmt:message key="graphicalExport.timeline.configuration.candidates" /></h2>
		</div>
		<%--
		<div id="subRuntimeElementContainer">
			<h2><fmt:message key="graphicalExport.timeline.configuration.candidates" /></h2>
		</div>
		--%>
	</div>
</div>
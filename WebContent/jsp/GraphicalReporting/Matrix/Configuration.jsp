<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<jwr:script src="/bundles/vbb.js" />
<jwr:style src="/bundles/vbb.css" />


<script type="text/javascript">
/* <![CDATA[ */
$(function() {
	$("#columnFillColor").colorPicker({
		colors: [<c:forEach var="color" items="${memBean.graphicalOptions.colorOptionsBean.availableColors}">"#${color}",</c:forEach>]
	});
	$("#rowFillColor").colorPicker({
		colors: [<c:forEach var="color" items="${memBean.graphicalOptions.colorOptionsBean.availableColors}">"#${color}",</c:forEach>]
	});
	
	var config = new Configuration('binarymatrix', $('#generateExport'));
	config.addSubstantialType($('#columnTagContainer'), $('#columnDroppableContainer'), $('#column'));
	config.addSubstantialType($('#rowTagContainer'), $('#rowDroppableContainer'), $('#row'));
	config.addFeature($('#column2row'), [$('#column'), $('#row')]);
	config.addSwitch($('#row'), $('#column'), $('#switchTypes'));
	
	config.init();
});
/* ]]> */
</script>

<style type="text/css">
	/* <![CDATA[ */
	/* Formatting for VBB configuration */
	#configurationContainer { width: 970px; margin: 3em auto 3em auto; overflow: auto; }
	
	#selectionContainer { float: left; margin-right: 4em; position: relative; }
	#switchTypes { width: 50px; height: 100px; cursor: pointer; position: absolute; top: 50%; margin-top: -50px; }
	#columnDroppableContainer { width: 27em; padding: 1em; margin: 0 0 1em 70px; height: 8em; border: 1px solid #999; font-weight: bold; text-align: center; vertical-align: middle; }
	#rowDroppableContainer { width: 27em; padding: 1em; margin: 1em 0 0 70px; height: 8em; border: 1px solid #999; font-weight: bold; text-align: center; vertical-align: middle; }
	#nestingRelationContainer { margin: 0 0 0 70px; width: 29em; }
	#nestingRelationContainer p { margin: 0.5em 0; }
	
	#recommendationContainer { width: 35em; float: right; font-weight: bold; }
	#columnTagContainer { padding: 1em; margin-bottom: 1em; border: 1px solid #999; overflow: hidden; }
	#rowTagContainer { padding: 1em; border: 1px solid #999; overflow: hidden; }
	
	#column2row { width: 100%; }
	
	#select { margin-left: 40px;}
	
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
		<img id="switchTypes" src="<c:url value="/images/flipArrow.gif" />" alt="Switch" />
		<div id="columnDroppableContainer">
			<div class="optionContainer">
				<form:hidden path="graphicalOptions.viewpointConfigMap['column.fillColor']" id="columnFillColor" />
			</div>
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['column.id']" name="graphicalOptions.viewpointConfigMap['column.id']" value="id" />
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['column.name']" name="graphicalOptions.viewpointConfigMap['column.name']" value="name" />
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['column.description']" name="graphicalOptions.viewpointConfigMap['column.description']" value="description" />
			<form:hidden path="graphicalOptions.viewpointConfigMap['column']" id="column" />
		</div>
		<div id="nestingRelationContainer">
			<p><fmt:message key="graphicalExport.matrixDiagram.configuration.selectRelation" /></p>
			<p><form:select path="graphicalOptions.viewpointConfigMap['column.column2row']" id="column2row" /></p>
		</div>
		<div id="rowDroppableContainer">
			<div class="optionContainer">
				<form:hidden path="graphicalOptions.viewpointConfigMap['row.fillColor']" id="rowFillColor" />
			</div>
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['row.id']" name="graphicalOptions.viewpointConfigMap['row.id']" value="id" />
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['row.name']" name="graphicalOptions.viewpointConfigMap['row.name']" value="name" />
			<input type="hidden" id="graphicalOptions.viewpointConfigMap['row.description']" name="graphicalOptions.viewpointConfigMap['row.description']" value="description" />
			<form:hidden path="graphicalOptions.viewpointConfigMap['row']" id="row" />
		</div>
	</div>
	
	<div id="recommendationContainer">
		<div id="columnTagContainer">
			<h2><fmt:message key="graphicalExport.matrixDiagram.configuration.candidates.xAxis" /></h2>
		</div>
		<div id="rowTagContainer">
			<h2><fmt:message key="graphicalExport.matrixDiagram.configuration.candidates.yAxis" /></h2>
		</div>
	</div>
</div>
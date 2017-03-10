<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<a id="importStrategyDescription" rel="popover" href="#" data-original-title="" title="">
	<i class="icon-info-sign"></i>
</a>

<script type="text/javascript">	
	$("#importStrategyDescription").popover(
					{
						title: 		'<fmt:message key="excel.import.strategy.title" />',
						content : 	'<b><fmt:message key="excel.import.strategy.description.header" /></b>' +
									'<ul><li><b><fmt:message key="excel.import.strategy.cud" />:</b> <fmt:message key="excel.import.strategy.cud.description" /></li>' +
									// '<li><b><fmt:message key="excel.import.strategy.update" />:</b> <fmt:message key="excel.import.strategy.update.description" /></li>' +
									'<li><b><fmt:message key="excel.import.strategy.additive" />:</b> <fmt:message key="excel.import.strategy.additive.description" /></li></ul>',
						html : 		'true',
						trigger : 	'focus',
						container : 'body',
						delay : { show : '200', hide : '400' }
					});
</script>
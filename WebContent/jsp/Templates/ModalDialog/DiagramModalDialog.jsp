<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<script type="text/javascript">
	$("#DiagramModalDialog").ready(function() {	
		$('#DiagramModalDialog').modal({
	  		backdrop: 'static',
	  		keyboard: false
		});

		$('#DiagramModalDialog').modal('hide');
	});
	
</script>

<div class="modal hide modalWideMode" id="DiagramModalDialog">
	<div class="modal-header">
		<a class="close" data-dismiss="modal" onclick="rollback()">×</a>
		<h3>
			<fmt:message key="customDashboard.template.modalDialog.choiceDiagram.title" />
		</h3>
	</div>
	<div class="modal-body">
		<tiles:insertTemplate
			template="/jsp/Templates/ModalDialog/ShowSavedDiagram.jsp">
		</tiles:insertTemplate>
	</div>
	<div class="modal-footer">
		<a onclick="rollback()" class="btn" data-dismiss="modal"> <fmt:message
				key="button.cancel" />
		</a>
	</div>
</div>
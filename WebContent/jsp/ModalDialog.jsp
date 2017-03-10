<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript">
/* <![CDATA[ */
$(function() {

	/* closes modal on enter */
    $('#modalDialog').on('shown', function () {
    	var close = $("#close");
    	$(document).on('keyup.confirm.modal', function ( e ) {
            e.which == 13 && close.click();
          });
	});    
});
/* ]]> */
</script>

<div class="modal hide" id="modalDialog">
  	<div class="modal-header">
    	<a class="close" data-dismiss="modal">×</a>
    	<h3 id="modalDialogTitle"></h3>
 	</div>
  	<div class="modal-body">
    	<p id="modalDialogContent"></p>
  	</div>
  	<div class="modal-footer hide" id="modalFooterContainer">

    	<a id="modalFooterOK" href="#" class="btn btn-primary" data-dismiss="modal">
    		<i class="icon-ok icon-white"></i>
    		<fmt:message key="button.ok" />
    		<input id="close" class="hide" type ="submit"></input>
    	</a>
    	<a id="modalFooterCancel" href="#" class="btn" data-dismiss="modal">
    		<fmt:message key="button.cancel" />
 
    	</a>
  </div>
</div>
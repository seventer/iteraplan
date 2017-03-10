<%
		
        String redirectURL = request.getContextPath() + "/start/start.do";
        if (! response.isCommitted()) {
          response.sendRedirect(redirectURL);
        }
%>

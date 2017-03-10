<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<%-- Display error messages from GuiController.addErrorMessage(String), if existing. --%>

<c:if test="${not empty requestScope['iteraplanMvcErrorMessages']}">
  <div class="alert alert-error">
    <a data-dismiss="alert" class="close">×</a> 
    <span><fmt:message key="errors.header" /></span>
    <div>
      <c:forEach var="error" items="${requestScope['iteraplanMvcErrorMessages']}">
        <c:out value="${error}" />
        <br />
      </c:forEach>
    </div>
  </div>
</c:if>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="value" rtexprvalue="true" %>
<%@ attribute name="length" %>
<%@ attribute name="style" %>
<%@ attribute name="styleClass" %>

<c:choose>
	<c:when test="${value.length()>length}">
		<span title="${value}" style="${style}" class="${styleClass}">${fn:substring(value,0,length-2)}...</span>
	</c:when>
	<c:otherwise>
		${value}
	</c:otherwise>
</c:choose>
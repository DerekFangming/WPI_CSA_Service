<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" href="http://example.com" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          ${user.name}
        </a>
    <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
    	<c:if test="${user.emailConfirmed}">
    	<a class="dropdown-item" href="#"><i class="icon-ok-sign" style="color:green"></i>&nbsp;Email verified</a>
    	</c:if>
    	<c:if test="${!user.emailConfirmed}">
    	<div><a class="dropdown-item" href="#"><i class="icon-remove" style="color:red"></i>&nbsp;Email not verified. Re-send</a></div>
    	</c:if>
        <a class="dropdown-item" href="./logout"><i class="icon-signout"></i>&nbsp;Log out</a>
    </div>
</li>
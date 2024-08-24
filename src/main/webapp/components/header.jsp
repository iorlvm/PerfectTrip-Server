<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- Navbar -->
<nav class="navbar navbar-expand-md navbar-light bg-light" style="padding:0 0 0 0.75em; border-bottom: 1px solid #0000002a">
    <div class="navbar-brand">${pageTitle}</div>
    <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
</nav>

<!-- Offcanvas -->
<div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel" style="background-color: #343a40; color: #fff;">
    <div class="offcanvas-header">
        <h5 id="offcanvasNavbarLabel">PerfectTrip 管理系統</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body">
        <ul class="nav flex-column sidebar">
            <%@ include file="/components/nav-items.jsp" %>
        </ul>
    </div>
</div>

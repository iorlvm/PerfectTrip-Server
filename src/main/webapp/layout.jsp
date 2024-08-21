<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>
<body>

<div class="container-fluid row" style="--bs-gutter-x: 0">
    <%--側欄--%>
    <%@ include file="/components/sidebar.jsp" %>
    <div class="col-md-9 ms-sm-auto col-lg-10">
        <%--導覽列--%>
        <%@ include file="/components/header.jsp" %>

        <%--主要視圖--%>
        <main>
            <jsp:include page="${content}"/>
        </main>

        <%--底部--%>
        <%@ include file="/components/footer.jsp" %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ"
        crossorigin="anonymous"></script>
<script src="../js/script.js"></script>
</body>
</html>
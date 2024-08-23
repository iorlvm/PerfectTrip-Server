<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th scope="col">商家編號</th>
            <th scope="col">商家名稱</th>
            <th scope="col">狀態</th>
            <th scope="col">操作</th>
        </tr>
        </thead>
        <tbody>
        <!-- Example row -->
        <tr>
            <th scope="row">M002</th>
            <td>XYZ商店</td>
            <td><span class="badge bg-warning">待審核</span></td>
            <td>
                <button class="btn btn-sm btn-success">批准</button>
                <button class="btn btn-sm btn-danger">拒絕</button>
            </td>
        </tr>
        </tbody>
    </table>
</div>